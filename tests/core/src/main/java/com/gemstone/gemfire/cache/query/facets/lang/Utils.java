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

package com.gemstone.gemfire.cache.query.facets.lang;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.cache.AttributesFactory;
import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionAttributes;
import com.gemstone.gemfire.cache.query.CacheUtils;
import com.gemstone.gemfire.cache.query.QueryService;
import java.util.*;



class Utils {
  private static final Random RANDOM = new Random();
  private static Cache _cache;
  
  static{
    try{
      init();
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  
  
  static void init() throws Exception{
    CacheUtils.startCache();
    _cache = CacheUtils.getCache();
  }
  
  static void installObserver()
  throws Exception {
    //         Class holderClass = Class.forName("com.gemstone.persistence.query.internal.query.QueryObserverHolder");
    //         Method meth = holderClass.getMethod(null, "getInstance", null);
    
  }
  
  
  static QueryService getQueryService() {
    return _cache.getQueryService();
  }
  
  static Region createRegion(String regionName, Class valueConstraint)
  throws Exception {
    AttributesFactory attributesFactory = new AttributesFactory();
    attributesFactory.setValueConstraint(valueConstraint);
    RegionAttributes regionAttributes = attributesFactory.create();
    
    Region region = _cache.createRegion(regionName, regionAttributes);
    return region;
  }
  
  static LogWriter getLogger(){
    return _cache.getLogger();
  }
  
  static void log(Object message){
    System.out.println(message);
  }
  
  static void setSeed(long seed) {
    RANDOM.setSeed(seed);
  }
  
  
  static List toTokens(String s) {
    StringTokenizer tokenizer = new StringTokenizer(s, " ");
    List l = new ArrayList();
    while(tokenizer.hasMoreTokens())
      l.add(tokenizer.nextToken());
    return l;
  }
  
  /*
  static void checkpoint(GsSession gsSession)
  throws Exception {
    try {
      gsSession.commit();
   
    } catch (GsTransactionConflictException ex) {
      gsSession.abort();
      throw new GsException("Commit Failed");
    }
   
    gsSession.begin();
  }
   
  static void canonicalize()
  throws Exception {
    // this step won't be necessary when we have a query layer filein
    GsSession gsSession = SessionManagerImpl.getDefault().createSession();
    gsSession.begin();
    // load the QueryService interface
    Class.forName("com.gemstone.persistence.query.QueryService");
    com.gemstone.persistence.vm.VMFactory.getCurrentVM().commitReposCanonicalObjs(gsSession);
    try {
      gsSession.commit();
   
    } catch (GsTransactionConflictException ex) {
      System.err.println("Commit Failed");
      return;
    }
    ((GsSession)gsSession).shutdown();
  }
   */
  static int randomInt(int low, int high) {
    int range = high - low + 1;
    return (int)(range * RANDOM.nextDouble()) + low;
  }
  
  static char randomAlphabetic() {
    return (char)randomInt('A', 'z');
  }
  
  
}
