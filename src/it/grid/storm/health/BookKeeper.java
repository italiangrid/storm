package it.grid.storm.health;

import java.util.ArrayList;
import org.apache.log4j.Logger;

public abstract class BookKeeper {

   protected Logger bookKeepingLog = HealthDirector.getBookKeepingLogger();
   protected ArrayList<LogEvent> logbook = new ArrayList<LogEvent>();

   /**
    *
    * @param logEvent LogEvent
    */
   public void addLogEvent(LogEvent logEvent) {
      logbook.add(logEvent);
      logDebug("Event is added to Log Book (item #"+(logbook.size()-1)+"");
      logInfo(logEvent.toString());
   }

   /**
    *
    */
   public void cleanLogBook() {
     logbook.clear();
   }

   /**
    *
    * @return int
    */
   public abstract int getNumberOfAsynchRequest();

   /**
    *
    * @return int
    */
   public abstract int getNumberOfSynchRequest();

   /**
    *
    * @param opType OperationType
    * @return int
    */
   public abstract int getNumberOfRequest(OperationType opType);

   /**
    *
    * @param opType OperationType
    * @return int
    */
   public abstract long getMeansDuration(OperationType opType);


   /**
    *
    * @param opType OperationType
    * @return int
    */
   public abstract int getNumberOfSuccess(OperationType opType);

////////////////////////////////////////////////////////////////

   /**
    *
    * @param msg String
    */
   protected void logDebug(String msg) {
     if ((HealthDirector.isBookKeepingConfigured())&&(HealthDirector.isBookKeepingEnabled())) {
       bookKeepingLog.debug("BK: " + msg);
     }
   }

   /**
    *
    * @param msg String
    */
   protected void logInfo(String msg) {
     if ((HealthDirector.isBookKeepingConfigured())&&(HealthDirector.isBookKeepingEnabled())) {
       bookKeepingLog.info(msg);
     }
   }


}
