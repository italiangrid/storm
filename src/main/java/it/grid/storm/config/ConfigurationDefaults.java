package it.grid.storm.config;

public class ConfigurationDefaults {

  /* SRM oublic info */
  public static final int SERVICE_SRM_PUBLIC_PORT = 8444;

  /* Database */
  public static final int DB_PORT = 3306;
  public static final String DB_USERNAME = "storm";
  public static final String DB_PASSWORD = "storm";
  public static final String DB_PROPERTIES = "serverTimezone=UTC&autoReconnect=true";

  /* Database connection pool */
  public static final int DB_POOL_SIZE = -1;
  public static final int DB_POOL_MIN_IDLE = 10;
  public static final int DB_POOL_MAX_WAIT_MILLIS = 5000;
  public static final boolean DB_POOL_TEST_ON_BORROW = true;
  public static final boolean DB_POOL_TEST_WHILE_IDLE = true;
  
  /* REST service */
  public static final int REST_SERVICES_PORT = 9998;
  public static final int REST_SERVICES_MAX_THREADS = 100;
  public static final int REST_SERVICES_MAX_QUEUE_SIZE = 1000;

  /* Sanity check enabled */
  public static final boolean SANITY_CHECK_ENABLED = true;

  /* XMLRPC */
  public static final int XMLRPC_MAX_THREADS = 256;
  public static final int XMLRPC_MAX_QUEUE_SIZE = 1000;
  public static final int XMLRPC_SERVER_PORT = 8080;
  public static final boolean XMLRPC_SECURITY_ENABLED = false;

  /* Disk usage service */
  public static final Boolean DISKUSAGE_SERVICE_ENABLED = false;
  public static final int DISKUSAGE_SERVICE_INITIAL_DELAY = 0;
  public static final int DISKUSAGE_SERVICE_TASKS_INTERVAL = 604800;
  public static final boolean DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED = false;


  public static final long CLEANING_INITIAL_DELAY = 10L;
  public static final long CLEANING_TIME_INTERVAL = 300L;

  public static final long FILE_DEFAULT_SIZE = 1000000L;
  public static final long FILE_LIFETIME_DEFAULT = 3600L;
  public static final long PIN_LIFETIME_DEFAULT = 259200L;
  public static final long PIN_LIFETIME_MAXIMUM = 1814400L;
  public static final long TRANSIT_INITIAL_DELAY = 10L;
  public static final long TRANSIT_TIME_INTERVAL = 300L;
  public static final long PICKING_INITIAL_DELAY = 1L;
  public static final long PICKING_TIME_INTERVAL = 2L;
  public static final int PICKING_MAX_BATCH_SIZE = 100;

  public static final int LS_MAX_NUMBER_OF_ENTRY = 2000;
  public static final boolean LS_DEFAULT_ALL_LEVEL_RECURSIVE = false;
  public static final int LS_DEFAULT_NUM_OF_LEVELS = 1;
  public static final int LS_OFFSET = 0;

  public static final int PTP_CORE_POOL_SIZE = 50;
  public static final int PTP_MAX_POOL_SIZE = 200;
  public static final int PTP_QUEUE_SIZE = 1000;

  public static final int PTG_CORE_POOL_SIZE = 50;
  public static final int PTG_MAX_POOL_SIZE = 200;
  public static final int PTG_QUEUE_SIZE = 2000;

  public static final int BOL_CORE_POOL_SIZE = 50;
  public static final int BOL_MAX_POOL_SIZE = 200;
  public static final int BOL_QUEUE_SIZE = 2000;

  public static final int CORE_POOL_SIZE = 10;
  public static final int MAX_POOL_SIZE = 50;
  public static final int QUEUE_SIZE = 2000;

  public static final boolean AUTOMATIC_DIRECTORY_CREATION = false;
  public static final boolean ENABLE_WRITE_PERM_ON_DIRECTORY = false;

  public static final String DEFAULT_OVERWRITE_MODE = "N";
  public static final String DEFAULT_FILE_STORAGE_TYPE = "V";

  public static final int PURGE_BATCH_SIZE = 800;

  public static final long EXPIRED_REQUEST_TIME = 604800L;
  public static final int REQUEST_PURGER_DELAY = 10;
  public static final int REQUEST_PURGER_PERIOD = 600;
  public static final boolean EXPIRED_REQUEST_PURGING = true;
  public static final long EXPIRED_INPROGRESS_PTP_TIME = 2592000L;

  public static final String EXTRA_SLASHES_FOR_FILE_TURL = "";
  public static final String EXTRA_SLASHES_FOR_RFIO_TURL = "";
  public static final String EXTRA_SLASHES_FOR_GSIFTP_TURL = "/";
  public static final String EXTRA_SLASHES_FOR_ROOT_TURL = "/";

  public static final String PING_VALUES_PROPERTIES_FILENAME = "ping-values.properties";

  public static final int HEARTHBEAT_PERIOD = 60;
  public static final int PERFORMANCE_GLANCE_TIME_INTERVAL = 15;
  public static final int PERFORMANCE_LOGBOOK_TIME_INTERVAL = 15;
  public static final boolean PERFORMANCE_MEASURING = false;
  public static final boolean BOOK_KEEPING_ENABLED = false;

  public static final int MAX_LOOP = 10;

  public static final int GPFS_QUOTA_REFRESH_PERIOD = 900;

  public static final long SERVER_POOL_STATUS_CHECK_TIMEOUT = 20000;

  public static final boolean PTG_SKIP_ACL_SETUP = false;

  public static final String HTTP_TURL_PREFIX = ""; // was "/filetransfer"

}
