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
package com.gemstone.gemfire.distributed.internal;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.admin.AdminDistributedSystem;
import com.gemstone.gemfire.admin.AdminDistributedSystemFactory;
import com.gemstone.gemfire.admin.Alert;
import com.gemstone.gemfire.admin.AlertLevel;
import com.gemstone.gemfire.admin.AlertListener;
import com.gemstone.gemfire.admin.DistributedSystemConfig;
import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheListener;
import com.gemstone.gemfire.cache.DataPolicy;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionEvent;
import com.gemstone.gemfire.cache.RegionFactory;
import com.gemstone.gemfire.cache.Scope;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;
import com.gemstone.gemfire.distributed.DistributedMember;
import com.gemstone.gemfire.distributed.DistributedSystem;
import com.gemstone.gemfire.distributed.internal.membership.InternalDistributedMember;
import com.gemstone.gemfire.distributed.internal.membership.jgroup.JGroupMembershipManager;
import com.gemstone.gemfire.distributed.internal.membership.jgroup.MembershipManagerHelper;
import com.gemstone.gemfire.internal.AvailablePort;
import com.gemstone.gemfire.internal.tcp.MemberShunnedException;
import com.gemstone.gemfire.internal.tcp.Stub;
import com.gemstone.org.jgroups.protocols.pbcast.GMS;

import dunit.DistributedTestCase;
import dunit.Host;
import dunit.SerializableCallable;
import dunit.SerializableRunnable;
import dunit.VM;

import java.net.InetAddress;
import java.util.Properties;

import management.operations.ops.cli.executors.RemoveExecutor;
import junit.framework.Assert;

/**
 * This class tests the functionality of the {@link
 * DistributionManager} class.
 */
public class DistributionManagerTest extends DistributedTestCase {

  /**
   * Clears the exceptionInThread flag in the given distribution
   * manager. 
   */
  public static void clearExceptionInThreads(DistributionManager dm) {
    dm.clearExceptionInThreads();
  }

  public DistributionManagerTest(String name) {
    super(name);
  }
  
  @Override
  public void setUp() throws Exception {
    disconnectAllFromDS();
    super.setUp();
  }

  ////////  Test Methods

  protected static class ItsOkayForMyClassNotToBeFound 
    extends SerialDistributionMessage {

    public int getDSFID() {
      return NO_FIXED_ID;
    }
    
    @Override
    protected void process(DistributionManager dm) {
      // We should never get here
    }
  };
  
  public static DistributedSystem ds;
  
  public void testGetDistributionVMType() {
    DM dm = getSystem().getDistributionManager();
    InternalDistributedMember ipaddr = dm.getId();
    assertEquals(DistributionManager.NORMAL_DM_TYPE, ipaddr.getVmKind());
  }
  
  /**
   * Send the distribution manager a message it can't deserialize
   */
  public void testExceptionInThreads() throws InterruptedException {
    DM dm = getSystem().getDistributionManager();
    String p1 = "ItsOkayForMyClassNotToBeFound";
    dm.getLoggerI18n().convertToLogWriter().info(
        "<ExpectedException action=add>"
      + p1 + "</ExpectedException>");
    DistributionMessage m = new ItsOkayForMyClassNotToBeFound();
    dm.putOutgoing(m);
    Thread.sleep(1 * 1000);
    dm.getLoggerI18n().convertToLogWriter().info(
        "<ExpectedException action=remove>"
        + p1 + "</ExpectedException>");
//    assertTrue(dm.exceptionInThreads());
//    dm.clearExceptionInThreads();
//    assertTrue(!dm.exceptionInThreads());
  }

  public void _testGetDistributionManagerIds() {
    int systemCount = 0;
    for (int h = 0; h < Host.getHostCount(); h++) {
      Host host = Host.getHost(h);
      systemCount += host.getSystemCount();
    }

    DM dm = getSystem().getDistributionManager();
    systemCount += 1;

    assertEquals(systemCount, dm.getNormalDistributionManagerIds().size());
  }
  
  


  /**
   * Demonstrate that a new UDP port is used when an attempt is made to
   * reconnect using a shunned port
   */
  public void testConnectAfterBeingShunned() {
    InternalDistributedSystem sys = getSystem();
    JGroupMembershipManager mgr = MembershipManagerHelper.getMembershipManager(sys);
    InternalDistributedMember idm = mgr.getLocalMember();
    System.setProperty("gemfire.jg-bind-port", ""+idm.getPort());
    try {
      sys.disconnect();
      sys = getSystem();
      mgr = MembershipManagerHelper.getMembershipManager(sys);
      sys.disconnect();
      InternalDistributedMember idm2 = mgr.getLocalMember();
      getLogWriter().info("original ID=" + idm +
          " and after connecting=" + idm2);
      assertTrue("should not have used a different udp port",
          idm.getPort() == idm2.getPort());
    } finally {
      System.getProperties().remove("gemfire.jg-bind-port");
    }
  }

  /**
   * Test the handling of "surprise members" in the membership manager.
   * Create a DistributedSystem in this VM and then add a fake member to
   * its surpriseMember set.  Then ensure that it stays in the set when
   * a new membership view arrives that doesn't contain it.  Then wait
   * until the member should be gone and force more view processing to have
   * it scrubbed from the set.
   **/ 
  public void testSurpriseMemberHandling() {
    VM vm0 = Host.getHost(0).getVM(0);

    InternalDistributedSystem sys = getSystem();
    JGroupMembershipManager mgr = MembershipManagerHelper.getMembershipManager(sys);

    try {
      InternalDistributedMember mbr = new InternalDistributedMember(
        DistributedTestCase.getIPLiteral(), 12345);

      // first make sure we can't add this as a surprise member (bug #44566)
      
      // if the view number isn't being recorded correctly the test will pass but the
      // functionality is broken
      Assert.assertTrue("expected view ID to be greater than zero", mgr.getView().getViewNumber() > 0);

      int oldViewId = mbr.getVmViewId();
      mbr.setVmViewId((int)mgr.getView().getViewNumber()-1);
      getLogWriter().info("current membership view is " + mgr.getView());
      getLogWriter().info("created ID " + mbr + " with view ID " + mbr.getVmViewId());
      sys.getLogWriter().info("<ExpectedException action=add>attempt to add old member</ExpectedException>");
      sys.getLogWriter().info("<ExpectedException action=add>Removing shunned GemFire node</ExpectedException>");
      try {
        boolean accepted = mgr.addSurpriseMember(mbr, new Stub());
        Assert.assertTrue("member with old ID was not rejected (bug #44566)", !accepted);
      } finally {
        sys.getLogWriter().info("<ExpectedException action=remove>attempt to add old member</ExpectedException>");
        sys.getLogWriter().info("<ExpectedException action=remove>Removing shunned GemFire node</ExpectedException>");
      }
      mbr.setVmViewId(oldViewId);

      // now forcibly add it as a surprise member and show that it is reaped
      long gracePeriod = 5000;
      long startTime = System.currentTimeMillis();
      long birthTime = startTime - mgr.getSurpriseMemberTimeout() + gracePeriod;
      MembershipManagerHelper.addSurpriseMember(sys, mbr, birthTime);
      assertTrue("Member was not a surprise member", mgr.isSurpriseMember(mbr));
      
      // force a real view change
      SerializableRunnable connectDisconnect = new SerializableRunnable() {
        public void run() {
          getSystem().disconnect();
        }
      };
      vm0.invoke(connectDisconnect);
      
      if (birthTime < (System.currentTimeMillis() - mgr.getSurpriseMemberTimeout())) {
        return; // machine is too busy and we didn't get enough CPU to perform more assertions
      }
      assertTrue("Member was incorrectly removed from surprise member set",
          mgr.isSurpriseMember(mbr));
      
      try {
        Thread.sleep(gracePeriod);
      }
      catch (InterruptedException e) {
        fail("test was interrupted");
      }
      
      vm0.invoke(connectDisconnect);
      assertTrue("Member was not removed from surprise member set",
          !mgr.isSurpriseMember(mbr));
      
    }
    catch (java.net.UnknownHostException e) {
      fail("unable to resolve localhost - test needs some attention");
    }
    finally {
      if (sys != null && sys.isConnected()) {
        sys.disconnect();
      }
    }
  }

  /**
   * Test the verifyMember interface of the membership manager
   **/ 
  public void testVerifyMember() {
    VM memberVM = Host.getHost(0).getVM(1);

    int port1 = AvailablePort.getRandomAvailablePort(AvailablePort.SOCKET);

    final String locators = getIPLiteral() + "[" + port1 + "]";
    final Properties properties = new Properties();
    properties.put(DistributionConfig.MCAST_PORT_NAME, "0");
    properties.put(DistributionConfig.START_LOCATOR_NAME, locators);
    properties.put(DistributionConfig.DISABLE_AUTO_RECONNECT_NAME, "true");
    properties.put(DistributionConfig.LOG_LEVEL_NAME, getDUnitLogLevel());

    system = (InternalDistributedSystem)DistributedSystem.connect(properties);
    JGroupMembershipManager mgr = MembershipManagerHelper.getMembershipManager(system);
    
    try {
      properties.remove(DistributionConfig.START_LOCATOR_NAME);
      properties.put(DistributionConfig.LOCATORS_NAME, locators);
      properties.put(DistributionConfig.NAME_NAME, "John Doe");
      DistributedMember id = (DistributedMember)memberVM.invoke(new SerializableCallable() {
        public Object call() {
          DistributedSystem system = DistributedSystem.connect(properties);
          return system.getDistributedMember();
        }
      });
      
      assertTrue(id.getName().equals("John Doe"));
      
      // a new view won't be installed for 20 seconds
      GMS.TEST_HOOK_SLOW_VIEW_CASTING=20;
      
      // show we can reconnect even though the old ID for this member is still
      // in the membership view.  Disconnecting will shut down the old DistributedSystem
      // in memberVM and while the old ID is still in the view
      memberVM.invoke(new SerializableRunnable("disconnect and reconnect") {
        public void run() {
          DistributedSystem sys = InternalDistributedSystem.getAnyInstance();
          DistributedMember oldID = sys.getDistributedMember();
          crashDistributedSystem(sys);
          sys = DistributedSystem.connect(properties);
          getLogWriter().info("initial view in new system is " + 
            MembershipManagerHelper.getMembershipManager(sys).getView());
          // the old ID should be in the new view
          if (MembershipManagerHelper.getMembershipManager(sys)
              .memberExists((InternalDistributedMember)oldID)) {
            // the old DistributedSystem should not be reachable
            assertFalse(MembershipManagerHelper.getMembershipManager(sys)
                .verifyMember(oldID, "old member has disconnected for this test"));
          }
        }
      });
    }
    finally {
      memberVM.invoke(new SerializableRunnable("disconnect") {
        public void run() {
          DistributedSystem sys = InternalDistributedSystem.getAnyInstance();
          if (sys != null) {
            sys.disconnect();
          }
        }
      });
      if (system != null && system.isConnected()) {
        system.disconnect();
      }
    }
  }

  /**
   * vm1 stores its cache in this static variable in
   * testAckSeverAllertThreshold
   */
  static Cache myCache;
  
  /**
   * Tests that a severe-level alert is generated if a member does not respond
   * with an ack quickly enough.  vm0 and vm1 create a region and set
   * ack-severe-alert-threshold.  vm1 has a cache listener in its
   * region that sleeps when notified, forcing the operation to take longer
   * than ack-wait-threshold + ack-severe-alert-threshold
   */
  public void testAckSevereAlertThreshold() throws Exception {
    disconnectAllFromDS();
    Host host = Host.getHost(0);
//    VM vm0 = host.getVM(0);
    VM vm1 = host.getVM(1);
    
    // in order to set a small ack-wait-threshold, we have to remove the
    // system property established by the dunit harness
    String oldAckWait = (String)System.getProperties()
              .remove("gemfire." + DistributionConfig.ACK_WAIT_THRESHOLD_NAME);

    try {
      final Properties props = getDistributedSystemProperties();
      props.setProperty("mcast-port", "0");
      props.setProperty(DistributionConfig.ACK_WAIT_THRESHOLD_NAME, "5");
      props.setProperty(DistributionConfig.ACK_SEVERE_ALERT_THRESHOLD_NAME, "5");
      props.setProperty(DistributionConfig.NAME_NAME, "putter");
  
      getSystem(props);
      Region rgn = (new RegionFactory())
                        .setScope(Scope.DISTRIBUTED_ACK)
                        .setEarlyAck(false)
                        .setDataPolicy(DataPolicy.REPLICATE)
                        .create("testRegion");
      system.getLogWriter().info("<ExpectedException action=add>"
          + "elapsed while waiting for replies</ExpectedException>");

      vm1.invoke(new SerializableRunnable("Connect to distributed system") {
        public void run() {
          props.setProperty(DistributionConfig.NAME_NAME, "sleeper");
          getSystem(props);
          RegionFactory rf = new RegionFactory();
          Region r = rf.setScope(Scope.DISTRIBUTED_ACK)
            .setDataPolicy(DataPolicy.REPLICATE)
            .setEarlyAck(false)
            .addCacheListener(getSleepingListener(false))
            .create("testRegion");
          myCache = r.getCache();
          try {
            createAlertListener();
          }
          catch (Exception e) {
            throw new RuntimeException("failed to create alert listener", e);
          }
        }
      });
      
      // now we have two caches set up, each having an alert listener.  Vm1
      // also has a cache listener that will sleep for longer than the
      // ack-severe-alert-threshold.
      
      rgn.put("bomb", "pow!");

      system.getLogWriter().info("<ExpectedException action=remove>"
          + "elapsed while waiting for replies</ExpectedException>");
      rgn.getCache().close();
      system.disconnect();

      vm1.invoke(new SerializableRunnable("disconnect from ds") {
        public void run() {
          if (!myCache.isClosed()) {
            if (system.isConnected()) {
              system.disconnect();
            }
            myCache = null;
          }
          if (system.isConnected()) {
             system.disconnect();
          }
          synchronized(alertGuard) {
            assertTrue("alert listener was not invoked for event in remote VM", alertReceived);
          }
        }
      });

    }
    finally {
      if (oldAckWait != null) {
        System.setProperty("gemfire." + DistributionConfig.ACK_WAIT_THRESHOLD_NAME, oldAckWait);
      }
    }
  }
  
  static volatile boolean regionDestroyedInvoked;

  static CacheListener getSleepingListener(final boolean playDead) {
    regionDestroyedInvoked = false;
    
    return new CacheListenerAdapter() {
      @Override
      public void afterCreate(EntryEvent event) {
        try {
          if (playDead) {
            MembershipManagerHelper.playDead(system);
          }
          Thread.sleep(15000);
        }
        catch (InterruptedException ie) {
          fail("interrupted");
        }
      }
      @Override
      public void afterRegionDestroy(RegionEvent event) {
        LogWriter logger = myCache.getLogger();
        logger.info("afterRegionDestroyed invoked in sleeping listener");
        logger.info("<ExpectedException action=remove>service failure</ExpectedException>");
        logger.info("<ExpectedException action=remove>com.gemstone.gemfire.ForcedDisconnectException</ExpectedException>");
        regionDestroyedInvoked = true;
        }
    };
  }
  
  static AdminDistributedSystem adminSystem;
  static Object alertGuard = new Object();
  static boolean alertReceived;
  
  static void createAlertListener() throws Exception {
    DistributedSystemConfig config = 
      AdminDistributedSystemFactory.defineDistributedSystem(system, null);
    adminSystem =
      AdminDistributedSystemFactory.getDistributedSystem(config);
    adminSystem.setAlertLevel(AlertLevel.SEVERE);
    adminSystem.addAlertListener(new AlertListener() {
      public void alert(Alert alert) {
        final LogWriter lw = adminSystem.getLogWriter();
        try {          
          lw.info("alert listener invoked for alert originating in " + alert.getConnectionName());
          lw.info("  alert text = " + alert.getMessage());
          lw.info("  systemMember = " + alert.getSystemMember());
        }
        catch (Exception e) {
          lw.severe("exception trying to use alert object", e);
        }
        synchronized(alertGuard) {
          alertReceived = true;
        }
      }
    });
    adminSystem.connect();
    assertTrue(adminSystem.waitToBeConnected(5 * 1000));
  }

  /**
   * Tests that a sick member is kicked out
   */
  public void testKickOutSickMember() throws Exception {
    disconnectAllFromDS();
    Host host = Host.getHost(0);
//    VM vm0 = host.getVM(0);
    VM vm1 = host.getVM(1);
    
    // in order to set a small ack-wait-threshold, we have to remove the
    // system property established by the dunit harness
    String oldAckWait = (String)System.getProperties()
              .remove("gemfire." + DistributionConfig.ACK_WAIT_THRESHOLD_NAME);

    try {
      final Properties props = getDistributedSystemProperties();
      props.setProperty(DistributionConfig.MCAST_PORT_NAME, "0"); // loner
      props.setProperty(DistributionConfig.ACK_WAIT_THRESHOLD_NAME, "5");
      props.setProperty(DistributionConfig.ACK_SEVERE_ALERT_THRESHOLD_NAME, "5");
      props.setProperty(DistributionConfig.NAME_NAME, "putter");
  
      getSystem(props);
      Region rgn = (new RegionFactory())
                        .setScope(Scope.DISTRIBUTED_ACK)
                        .setDataPolicy(DataPolicy.REPLICATE)
                        .create("testRegion");
      system.getLogWriter().info("<ExpectedException action=add>sec have elapsed while waiting for replies</ExpectedException>");
      
      vm1.invoke(new SerializableRunnable("Connect to distributed system") {
        public void run() {
          props.setProperty(DistributionConfig.NAME_NAME, "sleeper");
          getSystem(props);
          LogWriter log = system.getLogWriter();
          log.info("<ExpectedException action=add>service failure</ExpectedException>");
          log.info("<ExpectedException action=add>com.gemstone.gemfire.ForcedDisconnectException</ExpectedException>");
          RegionFactory rf = new RegionFactory();
          Region r = rf.setScope(Scope.DISTRIBUTED_ACK)
            .setDataPolicy(DataPolicy.REPLICATE)
            .addCacheListener(getSleepingListener(true))
            .create("testRegion");
          myCache = r.getCache();
        }
      });
      
      // now we have two caches set up, each having an alert listener.  Vm1
      // also has a cache listener that will turn off its ability to respond
      // to "are you dead" messages and then sleep
      
      rgn.put("bomb", "pow!");
      
      
      rgn.getCache().close();
      system.getLogWriter().info("<ExpectedException action=remove>sec have elapsed while waiting for replies</ExpectedException>");
      system.disconnect();
      
      vm1.invoke(new SerializableRunnable("wait for forced disconnect") {
        public void run() {
          // wait a while for the DS to finish disconnecting
          WaitCriterion ev = new WaitCriterion() {
            public boolean done() {
              return !system.isConnected();
            }
            public String description() {
              return null;
            }
          };
          // if this fails it means the sick member wasn't kicked out and something is wrong
          DistributedTestCase.waitForCriterion(ev, 60 * 1000, 200, true);
          
          ev = new WaitCriterion() {
            public boolean done() {
              return myCache.isClosed();
            }
            public String description() {
              return null;
            }
          };
          DistributedTestCase.waitForCriterion(ev, 20 * 1000, 200, false);
          
          if (!myCache.isClosed()) {
            if (system.isConnected()) {
              system.disconnect();
            }
            myCache = null;
            throw new RuntimeException("Test Failed - vm1's cache is not closed");
          }
          if (system.isConnected()) {
             system.disconnect();
             throw new RuntimeException("Test Failed - vm1's system should have been disconnected");
          }
          
          WaitCriterion wc = new WaitCriterion() {
            public boolean done() {
              return regionDestroyedInvoked;
            }
            public String description() {
              return "vm1's listener should have received afterRegionDestroyed notification";
            }
          };
          DistributedTestCase.waitForCriterion(wc, 30 * 1000, 1000, true);
          
        }
      });

    }
    finally {
      if (oldAckWait != null) {
        System.setProperty("gemfire." + DistributionConfig.ACK_WAIT_THRESHOLD_NAME, oldAckWait);
      }
    }
  }

  /**
   * test use of a bad bind-address for bug #32565
   * @throws Exception
   */
  public void testBadBindAddress() throws Exception {
    disconnectAllFromDS();

    final Properties props = getDistributedSystemProperties();
    props.setProperty(DistributionConfig.MCAST_PORT_NAME, "0"); // loner
    // use a valid address that's not proper for this machine
    props.setProperty(DistributionConfig.BIND_ADDRESS_NAME, "www.yahoo.com");
    props.setProperty(DistributionConfig.ACK_WAIT_THRESHOLD_NAME, "5");
    props.setProperty(DistributionConfig.ACK_SEVERE_ALERT_THRESHOLD_NAME, "5");
    try {
      getSystem(props);
    } catch (IllegalArgumentException e) {
      getLogWriter().info("caught expected exception (1)", e);
    }
    // use an invalid address
    props.setProperty(DistributionConfig.BIND_ADDRESS_NAME, "bruce.schuchardt");
    try {
      getSystem(props);
    } catch (IllegalArgumentException e) {
      getLogWriter().info("caught expected exception (2_", e);
    }
    // use a valid bind address
    props.setProperty(DistributionConfig.BIND_ADDRESS_NAME, InetAddress.getLocalHost().getCanonicalHostName());
    getSystem().disconnect();
  }
}