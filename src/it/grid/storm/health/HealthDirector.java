package it.grid.storm.health;

import it.grid.storm.config.Configuration;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Logger;
import java.util.Enumeration;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.log4j.Level;
import org.apache.log4j.DailyRollingFileAppender;
import java.util.Date;
import java.text.SimpleDateFormat;



public class HealthDirector {

  private static final Logger LOG =  Logger.getLogger("health");
  private static final Logger BOOKKEEPING = Logger.getLogger("bookkeeping");
  private static boolean initialized = false;
  private static HealthMonitor healthMonitorIstance = null;
  private static boolean bookKeepingConfigured = false;
  private static boolean bookKeepingEnabled = false;

  private static long bornInstant = -1L;
  private static String bornInstantStr = null;


  /**
   *
   * @param testingMode boolean
   */
  public static void initializeDirector(boolean testingMode) {

    configureHealthLog(testingMode);

    bookKeepingEnabled = Configuration.getInstance().getBookKeepingEnabled();
    if (bookKeepingEnabled) {
       configureBookKeeping(testingMode);
    }

    int statusPeriod = Configuration.getInstance().getHearthbeatPeriod();
    if (testingMode) {
      statusPeriod = 1;
    }

    //Record the born of StoRM instance
    bornInstant = System.currentTimeMillis();
    Date date = new Date(bornInstant) ;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
    bornInstantStr = formatter.format(date);

    healthMonitorIstance = new HealthMonitor(statusPeriod*2, statusPeriod);

    initialized = true;
  }

  /**
   *
   * @param testingMode boolean
   */
  private static void configureHealthLog(boolean testingMode) {
    FileAppender healthFileAppender = null;
    PatternLayout layout = new PatternLayout(getHealthPatternLayout());
    //Recovery of HEALTH File Name
    String healthFile = Configuration.getInstance().getHealthElectrocardiogramFile();
    try {
      healthFileAppender = new FileAppender(layout, healthFile);
    }
    catch (IOException ex) {
      System.out.println("Unable to configure Health log." + ex.getMessage());
      System.out.println("Retrieving ALL LOG appenders..");
      ArrayList<FileAppender> fileApp = getFileAppenders();
      if (! (fileApp.isEmpty())) {
        healthFileAppender = getFileAppenders().get(0);
      }
      else {
        System.out.println("Anyone FILE Appender found!!");
        System.exit(0);
      }
    }
    LOG.addAppender(healthFileAppender);
    LOG.setAdditivity(false);
    healthFileAppender.activateOptions();
    if (testingMode) {
      ArrayList<FileAppender> fileApp = getFileAppenders();
      for (int i = 0; i < fileApp.size(); i++) {
        LOG.debug(fileApp.get(i));
      }
      LOG.setLevel( (Level) Level.DEBUG);
    }
    else {
      LOG.setLevel( (Level) Level.INFO);
    }
  }

  /**
   *
   * @param testingMode boolean
   */
  private static void configureBookKeeping(boolean testingMode) {
    bookKeepingConfigured = true;
    DailyRollingFileAppender bookKeepingAppender = null;
    PatternLayout layout = new PatternLayout(getBookKeppingPatternLayout());
    //Recovery of BOOK KEEPING LOG File Name
    String bookKeepingFile = Configuration.getInstance().getBookKeepingFile();
    String datePattern = "'.'yyyy-MM-dd";
    try {
      bookKeepingAppender = new DailyRollingFileAppender(layout, bookKeepingFile, datePattern);
      BOOKKEEPING.addAppender(bookKeepingAppender);
      BOOKKEEPING.setAdditivity(false);
      bookKeepingAppender.activateOptions();
      if (testingMode) {
        BOOKKEEPING.setLevel( (Level) Level.DEBUG);
      }
      else {
        BOOKKEEPING.setLevel( (Level) Level.INFO);
      }
    }
    catch (IOException ex) {
      System.out.println("Unable to configure Book Keeping log." + ex.getMessage());
      bookKeepingConfigured = false;
    }
  }


  /**
   *
   * @return ArrayList
   */
  private static ArrayList<FileAppender> getFileAppenders() {
    ArrayList<FileAppender> fileAppenders = new ArrayList<FileAppender>();
    Enumeration en = LOG.getAllAppenders();
    int nbrOfAppenders = 0;
    while (en.hasMoreElements()) {
      nbrOfAppenders++;
      Object appender = en.nextElement();
      if (appender instanceof FileAppender) {
          fileAppenders.add((FileAppender)appender);
      }
    }
    return fileAppenders;
  }

  /**
   * http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html
   * @return String
   */
  private static String getHealthPatternLayout() {
    /**
     * @todo : Retrieve Patter Layout from Configuration ..
     */
    String pattern = "[%d{ISO8601}]: %m%n";
    return pattern;
  }

  /**
   * http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html
   * @return String
   */
  private static String getBookKeppingPatternLayout() {
    /**
     * @todo : Retrieve Patter Layout from Configuration ..
     */
    String pattern = "[%d{ISO8601}]: %m%n";
    return pattern;
  }

  /**
   *
   * @return Logger
   */
  public static Logger getHealthLogger() {
    return LOG;
  }


  public static boolean isBookKeepingConfigured() {
    return bookKeepingConfigured;
  }


  public static boolean isBookKeepingEnabled() {
    return bookKeepingEnabled;
  }

  /**
   *
   * @return Logger
   */
  public static Logger getBookKeepingLogger() {
    return BOOKKEEPING;
  }

  /**
   *
   * @return Namespace
   */
  public static HealthMonitor getHealthMonitor() {
    if (! (initialized)) {
      initializeDirector(false);
    }
    return healthMonitorIstance;
  }


  /**
   *
   * @return Namespace
   */
  public static HealthMonitor getHealthMonitor(boolean testingMode) {
    if (! (initialized)) {
      initializeDirector(testingMode);
    }
    return healthMonitorIstance;
  }


  public static long getBornInstant(boolean testingMode) {
    if (! (initialized)) {
      initializeDirector(testingMode);
    }
    return bornInstant;
  }

  public static String getBornInstantStr(boolean testingMode) {
    if (! (initialized)) {
      initializeDirector(testingMode);
    }
    return bornInstantStr;
  }

  public static long getBornInstant() {
    if (! (initialized)) {
      initializeDirector(false);
    }
    return bornInstant;
  }

  public static String getBornInstantStr() {
    if (! (initialized)) {
      initializeDirector(false);
    }
    return bornInstantStr;
  }

}
