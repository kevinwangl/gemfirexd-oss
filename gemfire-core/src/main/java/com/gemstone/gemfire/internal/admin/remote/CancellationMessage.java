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
//import com.gemstone.gemfire.*;
//import com.gemstone.gemfire.internal.*;
//import com.gemstone.gemfire.internal.admin.*;
import java.io.*;
//import java.util.*;
import com.gemstone.gemfire.distributed.internal.membership.*;
import com.gemstone.gemfire.internal.i18n.LocalizedStrings;

/**
 * A message that is sent to a particular distribution manager to cancel an
 * admin request
 */
public final class CancellationMessage extends PooledDistributionMessage {
  //instance variables
  private int msgToCancel;

  public static CancellationMessage create(InternalDistributedMember recipient, int msgToCancel) {
    CancellationMessage m = new CancellationMessage();
    m.msgToCancel = msgToCancel;
    m.setRecipient(recipient);
    return m;
  }

  @Override
  public void process(DistributionManager dm) {
    CancellationRegistry.getInstance().cancelMessage(this.getSender(), msgToCancel);
  }

  public int getDSFID() {
    return CANCELLATION_MESSAGE;
  }

  @Override
  public void toData(DataOutput out) throws IOException {
    super.toData(out);
    out.writeInt(msgToCancel);
  }

  @Override
  public void fromData(DataInput in) throws IOException,
      ClassNotFoundException {
    super.fromData(in);
    msgToCancel = in.readInt();
  }

  @Override
  public String toString(){
    return LocalizedStrings.CancellationMessage_CANCELLATIONMESSAGE_FROM_0_FOR_MESSAGE_ID_1.toLocalizedString(new Object[] { this.getSender(), Integer.valueOf(msgToCancel)});
  }
}
