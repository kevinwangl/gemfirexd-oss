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
package com.gemstone.gemfire.internal;

import java.io.Serializable;

/**
 * Describes where the value of a configuration attribute came from.
 * 
 * @author darrel
 * @since 7.0
 */
public class ConfigSource implements Serializable {
  private static final long serialVersionUID = -4097017272431018553L;
  public enum Type {API, SYSTEM_PROPERTY, FILE, SECURE_FILE, XML, RUNTIME, LAUNCHER};
  private final Type type;
  private final String description;
  
  private ConfigSource(Type t, String d) {
    this.type = t;
    this.description = d;
  }
  /**
   * @return returns the type of this source
   */
  public Type getType() {
    return this.type;
  }
  public String getDescription() {
    String result = this.description;
    if (result == null) {
      switch (getType()) {
      case API: result = "api"; break;
      case SYSTEM_PROPERTY: result = "system property"; break;
      case FILE: result = "file"; break;
      case SECURE_FILE: result = "secure file"; break;
      case XML: result = "cache.xml"; break;
      case RUNTIME: result = "runtime modification"; break;
      case LAUNCHER: result = "launcher"; break;
      }
    }
    return result;
  }

  private static final ConfigSource API_SINGLETON = new ConfigSource(Type.API, null);
  private static final ConfigSource SYSPROP_SINGLETON = new ConfigSource(Type.SYSTEM_PROPERTY, null);
  private static final ConfigSource XML_SINGLETON = new ConfigSource(Type.XML, null);
  private static final ConfigSource RUNTIME_SINGLETON = new ConfigSource(Type.RUNTIME, null);
  private static final ConfigSource LAUNCHER_SINGLETON = new ConfigSource(Type.LAUNCHER, null);
  
  public static ConfigSource api() {return API_SINGLETON;}
  public static ConfigSource sysprop() {return SYSPROP_SINGLETON;}
  public static ConfigSource xml() {return XML_SINGLETON;}
  public static ConfigSource runtime() {return RUNTIME_SINGLETON;}
  public static ConfigSource file(String fileName, boolean secure) {return new ConfigSource(secure ? Type.SECURE_FILE : Type.FILE, fileName);}
  public static ConfigSource launcher() {return LAUNCHER_SINGLETON;}
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ConfigSource other = (ConfigSource) obj;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (type != other.type)
      return false;
    return true;
  }
}