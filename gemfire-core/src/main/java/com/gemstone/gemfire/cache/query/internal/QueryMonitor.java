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

package com.gemstone.gemfire.cache.query.internal;

import java.util.concurrent.atomic.AtomicBoolean;

import com.gemstone.gemfire.cache.query.Query;
import com.gemstone.gemfire.cache.query.QueryExecutionLowMemoryException;
import com.gemstone.gemfire.cache.query.QueryExecutionTimeoutException;
import com.gemstone.gemfire.cache.query.internal.QueryExecutionCanceledException;
import com.gemstone.gemfire.i18n.LogWriterI18n;
import com.gemstone.gemfire.internal.cache.GemFireCacheImpl;
import com.gemstone.gemfire.internal.concurrent.CFactory;
import com.gemstone.gemfire.internal.concurrent.CLQ;
import com.gemstone.gemfire.internal.concurrent.CM;
import com.gemstone.gemfire.internal.i18n.LocalizedStrings;

/**
 * QueryMonitor class, monitors the query execution time. 
 * Instantiated based on the system property MAX_QUERY_EXECUTION_TIME. At most 
 * there will be one query monitor-thread that cancels the long running queries.
 * 
 * The queries to be monitored is added into the ordered queue, ordered based
 * on its start/arrival time. The first one in the Queue is the older query 
 * that will be canceled first.
 * 
 * The QueryMonitor cancels a query-execution thread if its taking more than
 * the max time. 
 * 
 * @since 6.0
 * @author agingade
 */
public class QueryMonitor implements Runnable {

  /**
   * Holds the query execution status for the thread executing the query.
   * FALSE if the query is not canceled due to max query execution timeout.
   * TRUE it the query is canceled due to max query execution timeout timeout.
   */
  private static ThreadLocal<AtomicBoolean> queryExecutionStatus = new ThreadLocal<AtomicBoolean>() {
    @Override 
    protected AtomicBoolean initialValue() {
      return new AtomicBoolean(Boolean.FALSE);
    }    
  };
 
  private final LogWriterI18n i18nLogger;

  private final long maxQueryExecutionTime;

  private static final CLQ queryThreads = CFactory.createCLQ();

  private Thread monitoringThread;
  private final AtomicBoolean stopped = new AtomicBoolean(Boolean.FALSE);

  /** For DUnit test purpose */
  private CM queryMonitorTasks = null;
  
  //Variables for cancelling queries due to low memory
  private volatile static Boolean LOW_MEMORY = Boolean.FALSE;
  private volatile static long LOW_MEMORY_USED_BYTES = 0;
  
  public QueryMonitor(LogWriterI18n i18nLogger, long maxQueryExecutionTime) {
    this.maxQueryExecutionTime = maxQueryExecutionTime;
    this.i18nLogger = i18nLogger;
  }

  /**
   * Add query to be monitored.
   * @param queryThread Thread executing the query.
   * @param query Query.
   */
  public void monitorQueryThread(Thread queryThread, Query query) {
    if (LOW_MEMORY) {
      String reason = LocalizedStrings.QueryMonitor_LOW_MEMORY_CANCELED_QUERY.toLocalizedString(LOW_MEMORY_USED_BYTES);
      ((DefaultQuery) query).setCanceled(true, new QueryExecutionLowMemoryException(reason));
      throw new QueryExecutionLowMemoryException(reason);
    }
    QueryThreadTask queryTask = new QueryThreadTask(queryThread, query, queryExecutionStatus.get());
    synchronized (queryThreads){
      queryThreads.add(queryTask);
      queryThreads.notify();
    }

    if (this.i18nLogger.fineEnabled()) {
      this.i18nLogger.fine("Adding thread to QueryMonitor. QueryMonitor size is:" 
          + queryThreads.size() + ", Thread (id): " + queryThread.getId() 
          + " query: " + query.getQueryString() + "thread is : " + queryThread);
    }

    /** For dunit test purpose */
    if (GemFireCacheImpl.getInstance() != null && GemFireCacheImpl.getInstance().TEST_MAX_QUERY_EXECUTION_TIME > 0) {
      if (this.queryMonitorTasks == null){
        this.queryMonitorTasks = CFactory.createCM();
      }
      this.queryMonitorTasks.put(queryThread, queryTask);
    }    
  }
  
  /**
   * Stops monitoring the query.
   * Removes the passed thread from QueryMonitor queue.
   * @param queryThread
   */
  public void stopMonitoringQueryThread(Thread queryThread, Query query) {
    // Re-Set the queryExecution status on the LocalThread.
    QueryExecutionTimeoutException testException = null;
    DefaultQuery q = (DefaultQuery)query;
    boolean[] queryCompleted = q.getQueryCompletedForMonitoring();
    
    synchronized(queryCompleted) {
      queryExecutionStatus.get().getAndSet(Boolean.FALSE);

      // START - DUnit Test purpose.
      if (GemFireCacheImpl.getInstance() != null && GemFireCacheImpl.getInstance().TEST_MAX_QUERY_EXECUTION_TIME > 0){
        long maxTimeSet = GemFireCacheImpl.getInstance().TEST_MAX_QUERY_EXECUTION_TIME;
        QueryThreadTask queryTask = (QueryThreadTask)queryThreads.peek();

        // This is to check if the QueryMonitoring thread slept longer than the expected time.
        // Its seen that in some cases based on OS thread scheduling the thread can sleep much
        // longer than the specified time.
        if (queryTask != null) {
          if ((System.currentTimeMillis() - queryTask.StartTime) > maxTimeSet){
            // The sleep() is unpredictable.
            testException = new QueryExecutionTimeoutException("The QueryMonitor thread may be sleeping longer than" +
                " the set sleep time. This will happen as the sleep is based on OS thread scheduling," +
            " verify the time spent by the executor thread.");
          }
        }

        // This is to see if query finished before it could get canceled, this could happen because
        // of a faster machine.
        // Check if the query finished before the max_query_execution time.
        queryTask = (QueryThreadTask)this.queryMonitorTasks.get(queryThread);
        if (queryTask != null){
          this.queryMonitorTasks.remove(queryThread);
          if (!GemFireCacheImpl.getInstance().TEST_MAX_QUERY_EXECUTION_TIME_OVERRIDE_EXCEPTION && testException == null && ((System.currentTimeMillis() - queryTask.StartTime) < 
              (maxTimeSet + 10 /* 10ms buffer */))){    
            testException = new QueryExecutionTimeoutException("The Query completed sucessfully before it got canceled.");          
          }
        }
      }
      // END - DUnit Test purpose.

      q.setQueryCompletedForMonitoring(true);
      // Remove the query task from the queue.
      queryThreads.remove(new QueryThreadTask(queryThread, null, null));
    }
    
    if (this.i18nLogger.fineEnabled()) {
      this.i18nLogger.fine("Removed thread from QueryMonitor. QueryMonitor size is:" 
          + queryThreads.size() + ", Thread (id): " + queryThread.getId() 
          + " thread is : " + queryThread);
    }
    
    if (testException != null){
      throw testException;
    }
  }

  /**
   * This method is called to check if the query execution is canceled.
   * The QueryMonitor cancels the query execution if it takes more than 
   * the max query execution time set or in low memory situations where
   * critical heap percentage has been set on the resource manager
   *  
   * The max query execution time is set using the system property 
   * gemfire.Cache.MAX_QUERY_EXECUTION_TIME 
   */  
  public static void isQueryExecutionCanceled(){    
    if (queryExecutionStatus.get() != null && queryExecutionStatus.get().get()){
      throw new QueryExecutionCanceledException();
    }
  }
 
  /**
   * Stops query monitoring.
   */
  public void stopMonitoring(){
    //synchronized in the rare case where query monitor was created but not yet run
    synchronized(stopped) {
      if (this.monitoringThread != null) {
        this.monitoringThread.interrupt();
      }
      stopped.set(Boolean.TRUE);
    }
  }

  /**
   * Starts monitoring the query.
   * If query runs longer than the set MAX_QUERY_EXECUTION_TIME, interrupts the
   * thread executing the query.
   */
  public void run(){
    //if the query monitor is stopped before run has been called, we should not run
    synchronized (stopped) {
      if (stopped.get()) {
        queryThreads.clear();
        return;
      }
      this.monitoringThread = Thread.currentThread();
    }
    try {
      QueryThreadTask queryTask = null;
      long sleepTime = 0;
      while(true){
        // Get the first query task from the queue. This query will have the shortest 
        // remaining time that needs to canceled first.
        queryTask = (QueryThreadTask)queryThreads.peek();
        if (queryTask == null){
          // Empty queue. 
          synchronized (this.queryThreads){
            this.queryThreads.wait();
          }
          continue;
        }
        if (DefaultQuery.testHook != null) {
          DefaultQuery.testHook.doTestHook(6);
        }
        long currentTime = System.currentTimeMillis();

        // Check if the sleepTime is greater than the remaining query execution time. 
        if ((currentTime - queryTask.StartTime) < this.maxQueryExecutionTime){
          sleepTime = this.maxQueryExecutionTime - (currentTime - queryTask.StartTime);
          // Its been noted that the sleep is not guaranteed to wait for the specified 
          // time (as stated in Suns doc too), it depends on the OSs thread scheduling
          // behavior, hence thread may sleep for longer than the specified time. 
          // Specifying shorter time also hasn't worked.
          Thread.sleep(sleepTime);
          continue;
        }

        // Query execution has taken more than the max time, Set queryExecutionStatus flag 
        // to canceled (TRUE).
        boolean[] queryCompleted = ((DefaultQuery)queryTask.query).getQueryCompletedForMonitoring();
        synchronized(queryCompleted) {
          if (!queryCompleted[0]) { // Check if the query is already completed.
            ((DefaultQuery)queryTask.query).setCanceled(true, new QueryExecutionTimeoutException(LocalizedStrings.QueryMonitor_LONG_RUNNING_QUERY_CANCELED.toLocalizedString(
                GemFireCacheImpl.MAX_QUERY_EXECUTION_TIME)));
            queryTask.queryExecutionStatus.set(Boolean.TRUE);
            // Remove the task from queue.
            queryThreads.poll();
          }
        }
        
        this.i18nLogger.info(LocalizedStrings.GemFireCache_LONG_RUNNING_QUERY_EXECUTION_CANCELED, 
            new Object[] {queryTask.query.getQueryString(), queryTask.queryThread.getId()});

        if (this.i18nLogger.fineEnabled()){
          this.i18nLogger.fine("Query Execution for the thread " + queryTask.queryThread + " got canceled.");
        }
      }
    } catch (InterruptedException ex) {
      if (this.i18nLogger.fineEnabled()){
        this.i18nLogger.fine("Query Monitoring thread got interrupted.");
      }
    } finally {
      this.queryThreads.clear();
    }
  }
  
  //Assumes LOW_MEMORY will only be set if query monitor is enabled
  public static boolean isLowMemory() {
    return LOW_MEMORY;
  }
  
  public static long getMemoryUsedDuringLowMemory() {
    return LOW_MEMORY_USED_BYTES;
  }
  
  public static void setLowMemory(boolean lowMemory, long usedBytes) {
    if (GemFireCacheImpl.getInstance() != null && !GemFireCacheImpl.getInstance().isQueryMonitorDisabledForLowMemory()) {
      QueryMonitor.LOW_MEMORY_USED_BYTES = usedBytes;
      QueryMonitor.LOW_MEMORY = lowMemory;
    }
  }
  
  public static void clearLowMemory() {
    QueryMonitor.LOW_MEMORY_USED_BYTES = 0;
    QueryMonitor.LOW_MEMORY = false;
  }

  public void cancelAllQueriesDueToMemory() {
    synchronized (this.queryThreads) {
      QueryThreadTask queryTask = (QueryThreadTask) queryThreads.poll();
      while (queryTask != null) {
        cancelQueryDueToLowMemory(queryTask, LOW_MEMORY_USED_BYTES);
        queryTask = (QueryThreadTask) queryThreads.poll();
      }
      queryThreads.clear();
      queryThreads.notify();
    }
  }
  
  private void cancelQueryDueToLowMemory(QueryThreadTask queryTask, long memoryThreshold) {
    boolean[] queryCompleted = ((DefaultQuery) queryTask.query).getQueryCompletedForMonitoring();
    synchronized (queryCompleted) {
      if (!queryCompleted[0]) { //cancel if query is not completed
        String reason = LocalizedStrings.QueryMonitor_LOW_MEMORY_CANCELED_QUERY.toLocalizedString(memoryThreshold);
        ((DefaultQuery) queryTask.query).setCanceled(true, new QueryExecutionLowMemoryException(reason));
        queryTask.queryExecutionStatus.set(Boolean.TRUE);
      }
    }
  }
  
  // FOR TEST PURPOSE
  public int getQueryMonitorThreadCount() {
    return this.queryThreads.size();
  }

  /**
   * Query Monitoring task, placed in the queue.
   * @author agingade
   *
   */
  private class QueryThreadTask {

    private final long StartTime;

    private final Thread queryThread;

    private final Query query;

    private final AtomicBoolean queryExecutionStatus;
    
    
    QueryThreadTask(Thread queryThread, Query query, AtomicBoolean queryExecutionStatus){
      this.StartTime = System.currentTimeMillis();
      this.queryThread = queryThread;
      this.query = query;
      this.queryExecutionStatus = queryExecutionStatus;
    }

    @Override
    public int hashCode() {
      assert this.queryThread != null;
      return this.queryThread.hashCode();
    }
    
    /**
     * The query task in the queue is identified by the thread.
     * To remove the task in the queue using the thread reference.
     */
    @Override
    public boolean equals(Object other){
      if (!(other instanceof QueryThreadTask)) {
        return false;
      }
      QueryThreadTask o = (QueryThreadTask)other;
      return this.queryThread.equals(o.queryThread);
    }

    @Override
    public String toString(){
      return new StringBuffer()
      .append("QueryThreadTask[StartTime:").append(this.StartTime)
      .append(", queryThread:").append(this.queryThread)
      .append(", threadId:").append(this.queryThread.getId())
      .append(", query:").append(this.query.getQueryString())
      .append(", queryExecutionStatus:").append(this.queryExecutionStatus)
      .append("]").toString();
    }
    
  }
}
