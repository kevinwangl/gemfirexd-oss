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
/**
 * 
 */
package com.gemstone.gemfire.internal.cache.tier.sockets.command;

import com.gemstone.gemfire.internal.cache.LocalRegion;
import com.gemstone.gemfire.internal.cache.tier.CachedRegionHelper;
import com.gemstone.gemfire.internal.cache.tier.Command;
import com.gemstone.gemfire.internal.cache.tier.InterestType;
import com.gemstone.gemfire.internal.cache.tier.MessageType;
import com.gemstone.gemfire.internal.cache.tier.sockets.*;
import com.gemstone.gemfire.internal.i18n.LocalizedStrings;
import com.gemstone.gemfire.internal.security.AuthorizeRequest;
import com.gemstone.gemfire.cache.DynamicRegionFactory;
import com.gemstone.gemfire.cache.InterestResultPolicy;
import com.gemstone.gemfire.cache.operations.RegisterInterestOperationContext;
import com.gemstone.org.jgroups.util.StringId;

import java.io.IOException;
import java.util.List;

/**
 * 
 * All keys of the register interest list are being sent as a single part since
 * 6.6. There is no need to send no keys as a separate part.In earlier versions 
 * {@link RegisterInterestList61} number of keys & each individual key was sent 
 * as a separate part.
 * 
 * @author sbhokare
 * 
 * @since 6.6
 */
public class RegisterInterestList66 extends BaseCommand {

  private final static RegisterInterestList66 singleton = new RegisterInterestList66();

  public static Command getCommand() {
    return singleton;
  }

  private RegisterInterestList66() {
  }

  @Override
  public void cmdExecute(Message msg, ServerConnection servConn, long start)
      throws IOException, InterruptedException {
    Part regionNamePart = null, keyPart = null;// numberOfKeysPart = null;
    String regionName = null;
    Object key = null;
    InterestResultPolicy policy;
    List keys = null;
    CachedRegionHelper crHelper = servConn.getCachedRegionHelper();
    int numberOfKeys = 0, partNumber = 0;
    servConn.setAsTrue(REQUIRES_RESPONSE);
    servConn.setAsTrue(REQUIRES_CHUNKED_RESPONSE);
    ChunkedMessage chunkedResponseMsg = servConn.getRegisterInterestResponseMessage();

    // bserverStats.incLong(readDestroyRequestTimeId,
    // DistributionStats.getStatTime() - start);
    // bserverStats.incInt(destroyRequestsId, 1);
    // start = DistributionStats.getStatTime();
    // Retrieve the data from the message parts
    regionNamePart = msg.getPart(0);
    regionName = regionNamePart.getString();

    // Retrieve the InterestResultPolicy
    try {
      policy = (InterestResultPolicy)msg.getPart(1).getObject();
    }
    catch (Exception e) {
      writeChunkedException(msg, e, false, servConn);
      servConn.setAsTrue(RESPONDED);
      return;
    }
    boolean isDurable = false ;
    try {
      Part durablePart = msg.getPart(2);
      byte[] durablePartBytes = (byte[])durablePart.getObject();
      isDurable = durablePartBytes[0] == 0x01;
    }
    catch (Exception e) {
      writeChunkedException(msg, e, false, servConn);
      servConn.setAsTrue(RESPONDED);
      return;
    }
//  region data policy
    byte[] regionDataPolicyPartBytes;
    try {
      Part regionDataPolicyPart = msg.getPart(msg.getNumberOfParts()-1);
      regionDataPolicyPartBytes = (byte[])regionDataPolicyPart.getObject();
    }
    catch (Exception e) {
      writeChunkedException(msg, e, false, servConn);
      servConn.setAsTrue(RESPONDED);
      return;
    }
   
    partNumber = 3;
    Part list = msg.getPart(partNumber);    
    try {
      keys = (List)list.getObject();
    }
    catch (Exception e) {
      writeChunkedException(msg, e, false, servConn);
      servConn.setAsTrue(RESPONDED);
      return;
    }

    boolean sendUpdatesAsInvalidates = false;
    try {
      Part notifyPart = msg.getPart(partNumber + 1);
      byte[] notifyPartBytes = (byte[])notifyPart.getObject();
      sendUpdatesAsInvalidates = notifyPartBytes[0] == 0x01;
    }
    catch (Exception e) {
      writeChunkedException(msg, e, false, servConn);
      servConn.setAsTrue(RESPONDED);
      return;
    }

    if (logger.fineEnabled()) {
      logger.fine(servConn.getName() + ": Received register interest 66 request ("
          + msg.getPayloadLength() + " bytes) from "
          + servConn.getSocketString() + " for the following " + numberOfKeys
          + " keys in region " + regionName + ": " + keys);
    }
    
    /*
    AcceptorImpl acceptor = servConn.getAcceptor();
    
    //  Check if the Server is running in NotifyBySubscription=true mode.
    if (!acceptor.getCacheClientNotifier().getNotifyBySubscription()) {
      // This should have been taken care at the client.
      String err = LocalizedStrings.RegisterInterest_INTEREST_REGISTRATION_IS_SUPPORTED_ONLY_FOR_SERVERS_WITH_NOTIFYBYSUBSCRIPTION_SET_TO_TRUE.toLocalizedString();
      writeChunkedErrorResponse(msg, MessageType.REGISTER_INTEREST_DATA_ERROR,
          err, servConn);
      servConn.setAsTrue(RESPONDED);  return;
    }
    */

    // Process the register interest request
    if (keys.isEmpty() || regionName == null) {
      StringId errMessage = null;
      if (keys.isEmpty() && regionName == null) {
        errMessage = LocalizedStrings.RegisterInterestList_THE_INPUT_LIST_OF_KEYS_IS_EMPTY_AND_THE_INPUT_REGION_NAME_IS_NULL_FOR_THE_REGISTER_INTEREST_REQUEST;
      } else if (keys.isEmpty()) {
        errMessage = LocalizedStrings.RegisterInterestList_THE_INPUT_LIST_OF_KEYS_FOR_THE_REGISTER_INTEREST_REQUEST_IS_EMPTY;
      } else if (regionName == null) {
        errMessage = LocalizedStrings.RegisterInterest_THE_INPUT_REGION_NAME_FOR_THE_REGISTER_INTEREST_REQUEST_IS_NULL;
      }
      if (logger.warningEnabled()) {
        logger.warning(
          LocalizedStrings.TWO_ARG_COLON,
          new Object[] {servConn.getName(), errMessage.toLocalizedString()});
      }
      writeChunkedErrorResponse(msg, MessageType.REGISTER_INTEREST_DATA_ERROR,
          errMessage.toLocalizedString(), servConn);
      servConn.setAsTrue(RESPONDED);
    }
    else { // key not null

      LocalRegion region = (LocalRegion)crHelper.getRegion(regionName);
      if (region == null) {
        if (logger.infoEnabled()) {
          logger.info(
            LocalizedStrings.RegisterInterestList_0_REGION_NAMED_1_WAS_NOT_FOUND_DURING_REGISTER_INTEREST_LIST_REQUEST,
            new Object[]{servConn.getName(), regionName});
        }
        // writeChunkedErrorResponse(msg,
        // MessageType.REGISTER_INTEREST_DATA_ERROR, message);
        // responded = true;
      } // else { // region not null
      try {
        AuthorizeRequest authzRequest = servConn.getAuthzRequest();
        if (authzRequest != null) {
          // TODO SW: This is a workaround for DynamicRegionFactory
          // registerInterest calls. Remove this when the semantics of
          // DynamicRegionFactory are cleaned up.
          if (!DynamicRegionFactory.regionIsDynamicRegionList(regionName)) {
            RegisterInterestOperationContext registerContext = authzRequest
                .registerInterestListAuthorize(regionName, keys, policy);
            keys = (List)registerContext.getKey();
          }
        }
        // Register interest
        servConn.getAcceptor().getCacheClientNotifier()
            .registerClientInterest(regionName, keys, servConn.getProxyID(),
                isDurable, sendUpdatesAsInvalidates,
                true, regionDataPolicyPartBytes[0],true);
      }
      catch (Exception ex) {
        // If an interrupted exception is thrown , rethrow it
        checkForInterrupt(servConn, ex);
        // Otherwise, write an exception message and continue
        writeChunkedException(msg, ex, false, servConn);
        servConn.setAsTrue(RESPONDED);
        return;
      }

      // Update the statistics and write the reply
      // bserverStats.incLong(processDestroyTimeId,
      // DistributionStats.getStatTime() - start);
      // start = DistributionStats.getStatTime();

      boolean isPrimary = servConn.getAcceptor().getCacheClientNotifier()
          .getClientProxy(servConn.getProxyID()).isPrimary();
      if (!isPrimary) {
        chunkedResponseMsg.setMessageType(MessageType.RESPONSE_FROM_SECONDARY);
        chunkedResponseMsg.setTransactionId(msg.getTransactionId());
        chunkedResponseMsg.sendHeader();
        chunkedResponseMsg.setLastChunk(true);
        if (logger.fineEnabled()) {
          String str = servConn.getName()
              + ": Sending register interest response chunk from secondary for region: "
              + regionName + " for key: " + key + " chunk=<"
              + chunkedResponseMsg + ">";
          logger.fine(str);
        }
        chunkedResponseMsg.sendChunk(servConn);
      }
      else { // isPrimary
        // Send header which describes how many chunks will follow
        chunkedResponseMsg.setMessageType(MessageType.RESPONSE_FROM_PRIMARY);
        chunkedResponseMsg.setTransactionId(msg.getTransactionId());
        chunkedResponseMsg.sendHeader();

        // Send chunk response
        try {
          fillAndSendRegisterInterestResponseChunks(region, keys,
              InterestType.KEY, policy, servConn);
          servConn.setAsTrue(RESPONDED);
        }
        catch (Exception e) {
          // If an interrupted exception is thrown , rethrow it
          checkForInterrupt(servConn, e);

          // otherwise send the exception back to client
          writeChunkedException(msg, e, false, servConn);
          servConn.setAsTrue(RESPONDED);
          return;
        }

        if (logger.fineEnabled()) {
          // logger.fine(getName() + ": Sent chunk (1 of 1) of register interest
          // response (" + chunkedResponseMsg.getBufferLength() + " bytes) for
          // region " + regionName + " key " + key);
          logger.fine(servConn.getName()
              + ": Sent register interest response for the following "
              + numberOfKeys + " keys in region " + regionName + ": " + keys);
        }
        // bserverStats.incLong(writeDestroyResponseTimeId,
        // DistributionStats.getStatTime() - start);
        // bserverStats.incInt(destroyResponsesId, 1);
      } // isPrimary
      // } // region not null
    } // key not null
  }

}