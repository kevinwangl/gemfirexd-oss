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
   
   
package com.gemstone.gemfire.internal.admin.remote;

import com.gemstone.gemfire.distributed.internal.*;
import com.gemstone.gemfire.internal.i18n.LocalizedStrings;
//import com.gemstone.gemfire.*;
//import com.gemstone.gemfire.internal.*;
import java.io.*;
//import java.util.*;

public final class FetchDistLockInfoRequest extends AdminRequest {
  /**
   * Returns a <code>FetchDistLockInfoRequest</code> to be sent to the specified recipient.
   */
  public static FetchDistLockInfoRequest create() {
    FetchDistLockInfoRequest m = new FetchDistLockInfoRequest();
    return m;
  }

  public FetchDistLockInfoRequest() {
    friendlyName = LocalizedStrings.FetchDistLockInfoRequest_LIST_DISTRIBUTED_LOCKS.toLocalizedString();
  }

  /**
   * Must return a proper response to this request.
   */
  @Override  
  protected AdminResponse createResponse(DistributionManager dm) {
    return FetchDistLockInfoResponse.create(dm, this.getSender()); 
  }

  public int getDSFID() {
    return FETCH_DIST_LOCK_INFO_REQUEST;
  }

  @Override  
  public void toData(DataOutput out) throws IOException {
    super.toData(out);
  }

  @Override  
  public void fromData(DataInput in)
    throws IOException, ClassNotFoundException {
    super.fromData(in);
  }

  @Override  
  public String toString() {
    return LocalizedStrings.FetchDistLockInfoRequest_FETCHDISTLOCKINFOREQUEST_FROM_0.toLocalizedString(this.getSender());
  }
}
