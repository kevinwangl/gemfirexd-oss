/*
 * Copyright (c) 2010-2015 Pivotal Software, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package com.gemstone.gemfire.internal.cache.xmlcache;

import com.gemstone.gemfire.i18n.LogWriterI18n;
import com.gemstone.gemfire.internal.cache.GemFireCacheImpl;
import com.gemstone.gemfire.internal.cache.PartitionAttributesImpl;
import com.gemstone.gemfire.internal.i18n.LocalizedStrings;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.io.*;

import com.gemstone.gemfire.cache.*;
import com.gemstone.gemfire.cache.query.*;
import com.gemstone.gemfire.cache.query.internal.index.IndexCreationData;
import com.gemstone.gemfire.cache.snapshot.RegionSnapshotService;
import com.gemstone.gemfire.internal.cache.LocalRegion;
import com.gemstone.gemfire.internal.cache.snapshot.RegionSnapshotServiceImpl;

/**
 * Represents a {@link Region} that is created declaratively.  Notice
 * that it implements the {@link Region} interface so that this class
 * must be updated when {@link Region} is modified.  This class is
 * public for testing purposes.
 *
 * @author David Whitlock
 *
 * @since 3.0
 */
public class RegionCreation implements Region {

//  /** An <code>AttributesFactory</code> for creating default
//   * <code>RegionAttribute</code>s */
//  private static final AttributesFactory defaultFactory =
//    new AttributesFactory();

  ///////////////////////  Instance Fields  ///////////////////////

  /** The name of this region */
  private final String name;

  /** The id of the region-attributes this regions uses by default.
   *
   * @since 6.5 */
  private String refid;

  /**
   * If true then someone explicitly added region attributes to this region
   * @since 6.5
   */
  private boolean hasAttributes;

  /** The full path to this region */
  private final String fullPath;

  /** The attributes of this region */
  private RegionAttributesCreation attrs;

  /** This region's subregions keyed on name */
  private Map subregions = new LinkedHashMap();

  /** The key/value pairs in this region */
  private Map values = new HashMap();

  /** List of IndexCreationData objects. A region can contain
   * multiple indexes defined */
  private List indexes = new ArrayList();

  /** The cache in which this region will reside */
  private final CacheCreation cache;

  ///////////////////////  Constructors  ///////////////////////

  /**
   * Creates a new <code>RegionCreation</code> with the given name and
   * with the default <code>RegionAttributes</code>.
   */
  public RegionCreation(CacheCreation cache, RegionCreation parent,
                        String name, String refid) {
    this.cache = cache;
    if (parent != null) {
      this.fullPath = parent.getFullPath() + SEPARATOR + name;

    } else {
      this.fullPath = SEPARATOR + name;
    }
    this.name = name;
    this.refid = refid;
    this.attrs = new RegionAttributesCreation(this.cache);
    if (refid != null) {
      this.attrs.setRefid(refid);
      this.attrs.inheritAttributes(cache);
    }
  }

  public RegionCreation(CacheCreation cache, String name, String refid) {
    this(cache, null, name, refid);
  }

  public RegionCreation(CacheCreation cache, String name) {
    this(cache, null, name, null);
  }

  //////////////////////  Instance Methods  //////////////////////

  public Object put(Object key, Object value)
    throws TimeoutException, CacheWriterException
  {
    return this.values.put(key, value);
  }

  /**
   * Fills in the state (that is, adds entries and creates subregions)
   * of a given <code>Region</code> based on the description provided
   * by this <code>RegionCreation</code>.
   *
   * @throws TimeoutException
   * @throws CacheWriterException
   * @throws RegionExistsException
   */
  private void fillIn(Region region)
    throws TimeoutException, CacheWriterException,
           RegionExistsException {

    for (Iterator iter = this.values.entrySet().iterator();
         iter.hasNext(); ) {
      Map.Entry entry = (Map.Entry) iter.next();
      region.put(entry.getKey(), entry.getValue());
    }

    for (Iterator iter = this.subregions.values().iterator();
         iter.hasNext(); ) {
      RegionCreation sub = (RegionCreation) iter.next();
      sub.create(region);
    }
  }

  /**
   * Sets the mutable attributes of the given region based on the
   * attributes of this <code>RegionCreation</code>.  This allows us
   * to modify the attributes of an existing region using a cache.xml
   * file.
   *
   * @see AttributesMutator
   */
  private void setMutableAttributes(Region region) {
    AttributesMutator mutator = region.getAttributesMutator();

    RegionAttributesCreation attrs = this.attrs;

    if (attrs.hasCacheListeners()) {
      mutator.initCacheListeners(attrs.getCacheListeners());
    }

    if (attrs.hasCacheLoader()) {
      mutator.setCacheLoader(attrs.getCacheLoader());
    }

    if (attrs.hasCacheWriter()) {
      mutator.setCacheWriter(attrs.getCacheWriter());
    }

    if (attrs.hasEntryIdleTimeout()) {
      mutator.setEntryIdleTimeout(attrs.getEntryIdleTimeout());
    }
    if (attrs.hasCustomEntryIdleTimeout()) {
      mutator.setCustomEntryIdleTimeout(attrs.getCustomEntryIdleTimeout());
    }

    if (attrs.hasEntryTimeToLive()) {
      mutator.setEntryTimeToLive(attrs.getEntryTimeToLive());
    }
    if (attrs.hasCustomEntryTimeToLive()) {
      mutator.setCustomEntryTimeToLive(attrs.getCustomEntryTimeToLive());
    }

    if (attrs.hasRegionIdleTimeout()) {
      mutator.setRegionIdleTimeout(attrs.getEntryIdleTimeout());
    }

    if (attrs.hasRegionTimeToLive()) {
      mutator.setRegionTimeToLive(attrs.getRegionTimeToLive());
    }
    
    if(attrs.hasCloningEnabled()){
      mutator.setCloningEnabled(attrs.getCloningEnabled());
    }
  }

  /**
   * Creates a root {@link Region} in a given <code>Cache</code>
   * based on the description provided by this
   * <code>RegionCreation</code>.
   *
   * @throws TimeoutException
   * @throws CacheWriterException
   * @throws RegionExistsException
   */
  void createRoot(Cache cache)
    throws TimeoutException, CacheWriterException,
           RegionExistsException {
    Region root = null;

    // Validate the attributes before creating the root region
    this.attrs.inheritAttributes(cache);
    this.attrs.setIndexes(this.indexes);
    this.attrs.prepareForValidation();
    
    try {
      root = ((GemFireCacheImpl)cache).basicCreateRegion(this.name, new AttributesFactory(this.attrs).create());
    } catch (RegionExistsException ex) {
      root = ex.getRegion();
      setMutableAttributes(root);
    }
    fillIn(root);
  }
  /**
   * Called by CacheXmlParser to add the IndexCreationData object
   * to the list. It is called when functional element is encounetered
   * @param icd
   */
  void addIndexData(IndexCreationData icd) {
    this.indexes.add(icd);
  }

  /**
   * Creates a {@link Region} with the given parent using the
   * description provided by this <code>RegionCreation</code>.
   *
   * @throws TimeoutException
   * @throws CacheWriterException
   * @throws RegionExistsException
   * @throws IllegalStateException
   */
  void create(Region parent)
    throws TimeoutException, CacheWriterException,
           RegionExistsException {

    // Validate the attributes before creating the sub-region
    this.attrs.inheritAttributes(parent.getCache());
    this.attrs.prepareForValidation();
    this.attrs.setIndexes(this.indexes);

    Region me = null;
    try {
      me = parent.createSubregion(this.name, new AttributesFactory(this.attrs).create());
    } catch (RegionExistsException ex) {
      me = ex.getRegion();
      setMutableAttributes(me);
    }

    // Register named region attributes
    String id = this.attrs.getId();
    if (id != null) {
      RegionAttributes realAttrs = me.getAttributes();
      me.getCache().setRegionAttributes(id, realAttrs);
    }

    fillIn(me);
  }

  /**
   * Returns whether or not this <code>RegionCreation</code> is
   * equivalent to another <code>Region</code>.
   */
  public boolean sameAs(Region other) {
    if (other == null) {
      return false;
    }

    if (!this.getName().equals(other.getName())) {
      throw new RuntimeException(LocalizedStrings.RegionCreation_REGION_NAMES_DIFFER_THIS_0_OTHER_1.toLocalizedString(new Object[] {this.getName(), other.getName()}));
    }

    if (!this.attrs.sameAs(other.getAttributes())) {
      throw new RuntimeException(LocalizedStrings.RegionCreation_REGION_ATTRIBUTES_DIFFER_THIS_0_OTHER_1.toLocalizedString(new Object[] {this.attrs, other.getAttributes()}));
    }

    Collection myEntries = this.basicEntries(false);
    Collection otherEntries = ((LocalRegion)other).basicEntries(false);
    if (myEntries.size() != otherEntries.size()) {
      return false;
    }

    for (Iterator iter = myEntries.iterator(); iter.hasNext(); ) {
      Region.Entry myEntry = (Region.Entry) iter.next();
      Region.Entry otherEntry = other.getEntry(myEntry.getKey());
      if (otherEntry == null) {
        return false;

      } else if (!myEntry.getValue().equals(otherEntry.getValue())) {
        return false;
      }
    }

    return true;
  }

  public String getName() {
    return this.name;
  }

  /**
   * Sets the attributes of this region
   */
  public void setAttributes(RegionAttributes attrs) {
    setAttributes(attrs, true);
  }

  /**
   * Note: hydra invokes this with setRefid=false.
   */
  public void setAttributes(RegionAttributes attrs, boolean setRefid) {
    this.hasAttributes = true;
    if (attrs instanceof RegionAttributesCreation) {
      this.attrs = (RegionAttributesCreation) attrs;
    } else {
      this.attrs = new RegionAttributesCreation(this.cache, attrs, false);
    }
    if ((setRefid && (this.attrs.getRefid() == null))) {
      this.attrs.setRefid(getRefid());
    }
    if (attrs.getPartitionAttributes() != null) {
      ((PartitionAttributesImpl) attrs.getPartitionAttributes()).computeLocalMaxMemory();
        
      if (attrs.getEvictionAttributes() != null
          && attrs.getEvictionAttributes().getAlgorithm().isLRUMemory()
          && attrs.getPartitionAttributes().getLocalMaxMemory() != 0
          && attrs.getEvictionAttributes().getMaximum() != attrs.getPartitionAttributes().getLocalMaxMemory()) {
        
        getCache().getLoggerI18n().warning(
            LocalizedStrings.Mem_LRU_Eviction_Attribute_Reset,
            new Object[] { this.getName(),
                attrs.getEvictionAttributes().getMaximum(),
                attrs.getPartitionAttributes().getLocalMaxMemory() });
        this.attrs.setEvictionAttributes(attrs.getEvictionAttributes().createLRUMemoryAttributes(
            attrs.getPartitionAttributes().getLocalMaxMemory(),
            attrs.getEvictionAttributes().getObjectSizer(),
            attrs.getEvictionAttributes().getAction()));
      }
    }
  }

  public RegionAttributes getAttributes() {
    return this.attrs;
  }



  public Region getSubregion(String regionName){
    return (Region) this.subregions.get(regionName);
  }

  /**
   * Adds a subregion with the given name to this region
   *
   *
   * @throws RegionExistsException
   *         If a subregion with <code>name</code> already exists
   */
  void addSubregion(String name, RegionCreation region)
    throws RegionExistsException {
    
    if (this.subregions.containsKey(name)) {
      RegionCreation existing =
        (RegionCreation) this.subregions.get(name);
      throw new RegionExistsException(existing);

    } else {
      this.subregions.put(name, region);
    }
  }

  public Set subregions(boolean recursive) {
    if (recursive) {
      throw new UnsupportedOperationException(LocalizedStrings.RegionCreation_GETTING_SUBREGIONS_RECURSIVELY_IS_NOT_SUPPORTED.toLocalizedString());
    }

    return new HashSet(this.subregions.values());
  }

  public void writeToDisk() {
    throw new UnsupportedOperationException(LocalizedStrings.RegionCreation_WRITING_A_REGIONCREATION_TO_DISK_IS_NOT_SUPPORTED.toLocalizedString());
  }

  public void registerInterest(Object key) throws CacheWriterException {
    throw new UnsupportedOperationException(LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }
  
  public void registerInterest(Object key, boolean isDurable) throws CacheWriterException {
    throw new UnsupportedOperationException(
      LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }
  
  public void registerInterest(Object key, boolean isDurable,
      boolean receiveValues) throws CacheWriterException {
    throw new UnsupportedOperationException(
      LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }

  public void registerInterestRegex(String regex) throws CacheWriterException {
    throw new UnsupportedOperationException(LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }
  
  public void registerInterestRegex(String regex, boolean isDurable) throws CacheWriterException {
    throw new UnsupportedOperationException(
      LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }
  
  public void registerInterestRegex(String regex, boolean isDurable,
      boolean receiveValues) throws CacheWriterException {
    throw new UnsupportedOperationException(
      LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }

  public void registerInterest(Object key, InterestResultPolicy policy) throws CacheWriterException {
    throw new UnsupportedOperationException(LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }
  
  public void registerInterest(Object key, InterestResultPolicy policy, boolean isDurable) throws CacheWriterException {
    throw new UnsupportedOperationException(
      LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }
  
  public void registerInterest(Object key, InterestResultPolicy policy,
      boolean isDurable, boolean receiveValues) throws CacheWriterException {
    throw new UnsupportedOperationException(
      LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }


  public void registerInterestRegex(String regex, InterestResultPolicy policy) throws CacheWriterException {
    throw new UnsupportedOperationException(LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }

  public void registerInterestRegex(String regex, InterestResultPolicy policy, boolean isDurable) throws CacheWriterException {
    throw new UnsupportedOperationException(
      LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }
  
  public void registerInterestRegex(String regex, InterestResultPolicy policy,
      boolean isDurable, boolean receiveValues) throws CacheWriterException {
    throw new UnsupportedOperationException(
      LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }

  public void unregisterInterest(Object key) throws CacheWriterException {
    throw new UnsupportedOperationException(LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }
  
  public void unregisterInterest(Object key, boolean isDurable) throws CacheWriterException {
    throw new UnsupportedOperationException(
      LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }

  public void unregisterInterestRegex(String regex) throws CacheWriterException {
    throw new UnsupportedOperationException(LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }
  
  public void unregisterInterestRegex(String regex, boolean isDurable) throws CacheWriterException {
    throw new UnsupportedOperationException(
      LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }


  public List getInterestList() throws CacheWriterException {
    throw new UnsupportedOperationException(LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }

  public List getDurableInterestList() throws CacheWriterException {
    throw new UnsupportedOperationException(
      LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }

  public List getInterestListRegex() throws CacheWriterException {
    throw new UnsupportedOperationException(LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }

  public Set keySetOnServer() throws CacheWriterException {
    throw new UnsupportedOperationException(LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }

  public boolean containsKeyOnServer(Object key) throws CacheWriterException {
    throw new UnsupportedOperationException(LocalizedStrings.UNSUPPORTED_AT_THIS_TIME.toLocalizedString());
  }

  static class Entry implements Region.Entry {
    private Object key;
    private Object value;

    Entry(Object key, Object value) {
      this.key = key;
      this.value = value;
    }
    
    public boolean isLocal() {
      return false;
    }

    public Object getKey() {
      return key;
    }

    public Object getValue() {
      return value;
    }

    public Region getRegion() {
      throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
    }

    public CacheStatistics getStatistics() {
      throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
    }

    public Object getUserAttribute() {
      throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
    }

    public Object setUserAttribute(Object userAttribute) {
      throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
    }

    public boolean isDestroyed() {
      throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
    }

    public Object setValue(Object arg0) {
      throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
    }
  }

  public Region.Entry getEntry(Object key) {
    Object value = this.values.get(key);
    if (value == null) {
      return null;

    } else {
      return new Entry(key, value);
    }
  }

  public Set entries(boolean recursive) {
    return basicEntries(recursive);
  }
  
  public Set basicEntries(boolean recursive) {
    if (recursive) {
      throw new UnsupportedOperationException(LocalizedStrings.RegionCreation_GETTING_ENTRIES_RECURSIVELY_IS_NOT_SUPPORTED.toLocalizedString());
    }

    Set set = new HashSet();
    for (Iterator iter = this.values.entrySet().iterator();
         iter.hasNext(); ) {
      final Map.Entry entry = (Map.Entry) iter.next();
      set.add(new Entry(entry.getKey(), entry.getValue()));
    }

    return set;
  }

  public String getFullPath() {
    return this.fullPath;
  }

  //////////  Inherited methods that don't do anything  //////////

  public Region getParentRegion() {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public AttributesMutator getAttributesMutator(){
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public CacheStatistics getStatistics(){
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void invalidateRegion() throws TimeoutException{
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void invalidateRegion(Object aCallbackArgument)
    throws TimeoutException {
      throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void localInvalidateRegion(){
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void localInvalidateRegion(Object aCallbackArgument) {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }


  public void destroyRegion()
    throws CacheWriterException, TimeoutException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void destroyRegion(Object aCacheWriterParam)
    throws CacheWriterException, TimeoutException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void localDestroyRegion(){
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void localDestroyRegion(Object aCallbackArgument){
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void close() {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public Region createSubregion(String subregionName,
                                RegionAttributes attrs)
    throws RegionExistsException, TimeoutException {
    RegionCreation subregion =
      new RegionCreation(this.cache, this, subregionName, null);
    subregion.setAttributes(attrs);
    this.addSubregion(subregionName, subregion);
    return subregion;
  }

  public Object get(Object key)
    throws CacheLoaderException, TimeoutException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public Object get(Object key, Object aCallbackArgument)
    throws TimeoutException, CacheLoaderException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public Object put(Object key, Object value, Object aCacheWriterParam)
    throws TimeoutException, CacheWriterException
  {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void create(Object key, Object value)
    throws TimeoutException, EntryExistsException,
    CacheWriterException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void create(Object key, Object value,
                     Object aCacheWriterParam)
    throws TimeoutException, EntryExistsException,
    CacheWriterException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void invalidate(Object key)
    throws TimeoutException, EntryNotFoundException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void invalidate(Object key, Object callbackArgument)
    throws TimeoutException, EntryNotFoundException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void localInvalidate(Object key)
    throws EntryNotFoundException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void localInvalidate(Object key,Object callbackArgument)
    throws EntryNotFoundException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }


  public Object destroy(Object key)
    throws TimeoutException, EntryNotFoundException,
    CacheWriterException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public Object destroy(Object key, Object aCacheWriterParam)
    throws TimeoutException, EntryNotFoundException,
    CacheWriterException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void localDestroy(Object key) throws EntryNotFoundException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void localDestroy(Object key,Object callbackArgument)
    throws EntryNotFoundException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());

  }

  public Set keys() {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public Collection values() {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public Cache getCache() {
    return this.cache;
  }

  public RegionService getRegionService() {
    return this.cache;
  }

  public Object getUserAttribute() {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void setUserAttribute(Object value) {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public boolean isDestroyed() {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public boolean containsValueForKey(Object key) {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public boolean containsKey(Object key) {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public Lock getRegionDistributedLock() {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public Lock getDistributedLock(Object key) {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public boolean existsValue(String predicate) {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public SelectResults query(String predicate) {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public Object selectValue(String predicate) {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void loadSnapshot(InputStream inputStream)
  throws IOException, ClassNotFoundException, CacheWriterException, TimeoutException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public void saveSnapshot(OutputStream outputStream) throws IOException {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }
  public void becomeLockGrantor() {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }
  public int size() {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }
  public void clear() {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
    
  }
  public boolean isEmpty() {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }
  public boolean containsValue(Object arg0) {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }
  public void putAll(Map arg0) {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString()); 
  }
  public Map getAll(Collection keys) {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }
  public Set entrySet() {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }
  public Set keySet() {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }
 
  public Object remove(Object arg0) {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }

  public Set entrySet(boolean recursive) {
    if (recursive) {
      throw new UnsupportedOperationException(LocalizedStrings.RegionCreation_GETTING_ENTRIES_RECURSIVELY_IS_NOT_SUPPORTED.toLocalizedString());
    }

    Set set = new HashSet();
    for (Iterator iter = this.values.entrySet().iterator();
         iter.hasNext(); ) {
      final Map.Entry entry = (Map.Entry) iter.next();
      set.add(new Entry(entry.getKey(), entry.getValue()));
    }

    return set;
  }

  public void localClear() {
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());    
  }    
  
  public void forceRolling(){
    throw new UnsupportedOperationException(LocalizedStrings.SHOULDNT_INVOKE.toLocalizedString());
  }
  
  public boolean forceCompaction(){
    throw new UnsupportedOperationException("Shouldn't be invoked");
  }

  /* (non-Javadoc)
   * @see java.util.concurrent.ConcurrentMap#putIfAbsent(java.lang.Object, java.lang.Object)
   */
  public Object putIfAbsent(Object key, Object value) {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see java.util.concurrent.ConcurrentMap#remove(java.lang.Object, java.lang.Object)
   */
  public boolean remove(Object key, Object value) {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object, java.lang.Object)
   */
  public Object replace(Object key, Object value) {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object, java.lang.Object, java.lang.Object)
   */
  public boolean replace(Object key, Object oldValue, Object newValue) {
    throw new UnsupportedOperationException();
  }

  /**
   * Sets the refid of the region attributes being created
   *
   * @since 6.5
   */
  public void setRefid(String refid) {
    this.refid = refid;
  }

  /**
   * Returns the refid of the region attributes being created
   *
   * @since 6.5
   */
  public String getRefid() {
    return this.refid;
  }
  /**
   * Returns true if someone explicitly added region attributes to this region.
   */
  public boolean hasAttributes() {
    return this.hasAttributes;
  }
  
  public RegionSnapshotService<?, ?> getSnapshotService() {
    throw new UnsupportedOperationException();
  }
}
