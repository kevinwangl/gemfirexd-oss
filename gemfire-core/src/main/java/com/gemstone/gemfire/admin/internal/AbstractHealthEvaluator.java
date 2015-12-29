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
package com.gemstone.gemfire.admin.internal;

import com.gemstone.gemfire.i18n.LogWriterI18n;
import com.gemstone.gemfire.internal.i18n.LocalizedStrings;
import com.gemstone.gemfire.admin.GemFireHealth;
import com.gemstone.gemfire.admin.GemFireHealthConfig;
import com.gemstone.gemfire.distributed.internal.DM;
import java.util.List;

/**
 * The abstract superclass of all GemFire health evaluators.
 * Basically, this class specifies what the health evaluators need and
 * what they should do.
 *
 * <P>
 *
 * Note that evaluators never reside in the administration VM, they
 * only in member VMs.  They are not <code>Serializable</code> and
 * aren't meant to be.
 *
 * @author David Whitlock
 *
 * @since 3.5
 * */
public abstract class AbstractHealthEvaluator  {

  /** The number of times this evaluator has been evaluated.  Certain
   * checks are not made the first time an evaluation occurs.  */
  private int numEvaluations;

  /** Logger to write messages to */
  protected LogWriterI18n logger;

  //////////////////////  Constructors  //////////////////////

  /**
   * Creates a new <code>AbstractHealthEvaluator</code> with the given
   * <code>GemFireHealthConfig</code> and
   * <code>DistributionManager</code>.  
   *
   * Originally, this method took an
   * <code>InternalDistributedSystem</code>, but we found there were
   * race conditions during initialization.  Namely, that a
   * <code>DistributionMessage</code> can be processed before the
   * <code>InternalDistributedSystem</code>'s
   * <code>DistributionManager</code> is set.
   */
  protected AbstractHealthEvaluator(GemFireHealthConfig config,
                                    DM dm)
  {
    this.numEvaluations = 0;
    this.logger = dm.getLoggerI18n();
  }

  /////////////////////  Instance Methods  /////////////////////

  /**
   * Evaluates the health of a component of a GemFire distributed
   * system. 
   *
   * @param status
   *        A list of {@link AbstractHealthEvaluator.HealthStatus
   *        HealthStatus} objects that is populated when ill health is
   *        detected.
   */
  public final void evaluate(List status) {
    this.numEvaluations++;
    check(status);
  }

  /**
   * Checks the health of a component of a GemFire distributed
   * system. 
   *
   * @see #evaluate
   */
  protected abstract void check(List status);

  /**
   * Returns whether or not this is the first evaluation
   */
  protected final boolean isFirstEvaluation() {
    return this.numEvaluations <= 1;
  }

  /**
   * A factory method that creates a {@link
   * AbstractHealthEvaluator.HealthStatus HealthStats} with
   * {@linkplain GemFireHealth#OKAY_HEALTH okay} status.
   */
  protected HealthStatus okayHealth(String diagnosis) {
    logger.info(LocalizedStrings.AbstractHealthEvaluator_OKAY_HEALTH__0, diagnosis);
    return new HealthStatus(GemFireHealth.OKAY_HEALTH, diagnosis);
  }

  /**
   * A factory method that creates a {@link
   * AbstractHealthEvaluator.HealthStatus HealthStats} with
   * {@linkplain GemFireHealth#POOR_HEALTH poor} status.
   */
  protected HealthStatus poorHealth(String diagnosis) {
    logger.info(LocalizedStrings.AbstractHealthEvaluator_POOR_HEALTH__0, diagnosis);
    return new HealthStatus(GemFireHealth.POOR_HEALTH, diagnosis);
  }

  /**
   * Returns a <code>String</code> describing the component whose
   * health is evaluated by this evaluator.
   */
  protected abstract String getDescription();

  /**
   * Closes this evaluator and releases all of its resources
   */
  abstract void close();

  ///////////////////////  Inner Classes  //////////////////////

  /**
   * Represents the health of a GemFire component.
   */
  public class HealthStatus  {
    /** The health of a GemFire component */
    private GemFireHealth.Health healthCode;

    /** The diagnosis of the illness */
    private String diagnosis;

    //////////////////////  Constructors  //////////////////////

    /**
     * Creates a new <code>HealthStatus</code> with the give
     * <code>health</code> code and <code>dianosis</code> message.
     *
     * @see GemFireHealth#OKAY_HEALTH
     * @see GemFireHealth#POOR_HEALTH
     */
    HealthStatus(GemFireHealth.Health healthCode, String diagnosis) {
      this.healthCode = healthCode;
      this.diagnosis =
        "[" + AbstractHealthEvaluator.this.getDescription() + "] " +
        diagnosis;
    }

    /////////////////////  Instance Methods  /////////////////////

    /**
     * Returns the health code
     *
     * @see GemFireHealth#OKAY_HEALTH
     * @see GemFireHealth#POOR_HEALTH
     */
    public GemFireHealth.Health getHealthCode() {
      return this.healthCode;
    }

    /**
     * Returns the diagnosis prepended with a description of the
     * component that is ill.
     */
    public String getDiagnosis() {
      return this.diagnosis;
    }

  }

}