/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.scheduler.ChunkScheduler;
import it.grid.storm.scheduler.CrusherScheduler;

/**
 * This class is a factory for making the schedulers of StoRM
 * 
 * @author Ezio Corso
 * @author EGRID - ICTP Trieste
 * @date April 25th, 2005
 * @version 1.0
 */
public class SchedulerFactory {


  private SchedulerFactory() {}

  /**
   * Method that returns the Scheduler in charge of handling Feeder tasks.
   */
  public static CrusherScheduler crusherSched() {

    return CrusherScheduler.getInstance();
  }

  /**
   * Method that returns the Scheduler in charge of Chunk tasks.
   */
  public static ChunkScheduler chunkSched() {

    return ChunkScheduler.getInstance();
  }
}
