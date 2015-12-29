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

package com.gemstone.gemfire.internal.security;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.gemstone.gemfire.DataSerializable;
import com.gemstone.gemfire.DataSerializer;

public class ObjectWithAuthz implements DataSerializable {
  private static final long serialVersionUID = -9016665470672291858L;

  public static final byte CLASSID = (byte)57;

  private Object val;

  private Object authz;

  public ObjectWithAuthz() {
    this.val = null;
    this.authz = null;
  }

  public ObjectWithAuthz(Object val, Object authz) {

    this.val = val;
    this.authz = authz;
  }

  public Object getVal() {

    return this.val;
  }

  public Object getAuthz() {

    return this.authz;
  }

  public void fromData(DataInput in) throws IOException, ClassNotFoundException {

    this.val = DataSerializer.readObject(in);
    this.authz = DataSerializer.readObject(in);
  }

  public void toData(DataOutput out) throws IOException {

    DataSerializer.writeObject(this.val, out);
    DataSerializer.writeObject(this.authz, out);
  }

}