/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.scheduler.ChunkScheduler;
import it.grid.storm.scheduler.CrusherScheduler;

/**
 * This is a Facade to the Schedulers.
 *
 * @author Ezio Corso
 * @author EGRID - ICTP Trieste
 * @date April 25th, 2005
 * @version 1.0
 */
public class SchedulerFacade {

  private static SchedulerFacade sf = new SchedulerFacade();

  // Scheduler that manages Feeder tasks
  private CrusherScheduler crusherSched = SchedulerFactory.crusherSched();
  // Scheduler that manages Chunck tasks
  private ChunkScheduler chunkSched = SchedulerFactory.chunkSched();

  private SchedulerFacade() {}

  /** Method that returns the only instance of SchedulerFacade. */
  public static SchedulerFacade getInstance() {

    return sf;
  }

  /** Method that returns the Scheduler in charge of handling Chunk. */
  public ChunkScheduler chunkScheduler() {

    return chunkSched;
  }

  /** Method that returns the Scheduler in charge of handling Feeder */
  public CrusherScheduler crusherScheduler() {

    return crusherSched;
  }
}
