package it.grid.storm.asynch;

import org.apache.log4j.Logger;

import it.grid.storm.config.Configuration;

import java.util.*;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.scheduler.Scheduler;
import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.SchedulerStatus;
import it.grid.storm.scheduler.SchedulerException;
import it.grid.storm.scheduler.CrusherScheduler;
import it.grid.storm.scheduler.ChunkScheduler;

/**
 * This class is a factory for making the schedulers of StoRM
 *
 * @author  Ezio Corso
 * @author  EGRID - ICTP Trieste
 * @date    April 25th, 2005
 * @version 1.0
 */
public class SchedulerFactory {

    /**
     * Method that returns the Scheduler in charge of handling Feeder tasks.
     */
    public static Scheduler crusherSched() {
      if (Configuration.getInstance().getSerialScheduler()) {
        return new Scheduler() {
          public void schedule(Delegable t) throws SchedulerException {
            t.doIt();
          }

          public SchedulerStatus getStatus(int type) {
            return null;
          }

          public void abort(Delegable task) throws SchedulerException {
          }

          public void suspend(Delegable task) throws SchedulerException {
          }

        };
      }
      else
        return CrusherScheduler.getInstance();
    }

    /**
     * Method that returns the Scheduler in charge of Chunk tasks.
     */
    public static Scheduler chunkSched() {
      if (Configuration.getInstance().getSerialScheduler()) {
        return new Scheduler() {
          public void schedule(Delegable t) throws SchedulerException {
            t.doIt();
          }

          public SchedulerStatus getStatus(int type) {
            return null;
          }

          public void abort(Delegable task) throws SchedulerException {
          }

          public void suspend(Delegable task) throws SchedulerException {
          }
        };
      }
      else
        return ChunkScheduler.getInstance();
    }
  }


