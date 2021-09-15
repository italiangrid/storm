package it.grid.storm.config;

import it.grid.storm.config.model.v2.OverwriteMode;
import it.grid.storm.config.model.v2.StorageType;

public class ConfigurationDefaults {

  /* SRM port for endpoints */
  public static final int DEFAULT_SRM_PORT = 8444;

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

  /* Rest and XMLRPC security settings */
  public static final boolean SECURITY_ENABLED = true;
  public static final String SECURITY_TOKEN = "secret";

  /* Disk usage service */
  public static final boolean DISKUSAGE_SERVICE_ENABLED = false;
  public static final int DISKUSAGE_SERVICE_INITIAL_DELAY = 0;
  public static final long DISKUSAGE_SERVICE_TASKS_INTERVAL = 604800L;
  public static final boolean DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED = false;

  /* GC agents */
  public static final int INPROGRESS_REQUESTS_AGENT_DELAY = 50;
  public static final int INPROGRESS_REQUESTS_AGENT_INTERVAL = 300;
  public static final long INPROGRESS_REQUESTS_AGENT_PTP_EXPIRATION_TIME = 2592000L;

  public static final int EXPIRED_SPACES_AGENT_DELAY = 30;
  public static final int EXPIRED_SPACES_AGENT_INTERVAL = 300;

  public static final boolean COMPLETED_REQUESTS_AGENT_ENABLED = true;
  public static final int COMPLETED_REQUESTS_AGENT_DELAY = 10;
  public static final int COMPLETED_REQUESTS_AGENT_INTERVAL = 600;
  public static final long COMPLETED_REQUESTS_AGENT_PURGE_AGE = 604800L;
  public static final int COMPLETED_REQUESTS_AGENT_PURGE_SIZE = 800;

  /* schedulers */
  public static final int PTP_SCHEDULER_CORE_POOL_SIZE = 50;
  public static final int PTP_SCHEDULER_MAX_POOL_SIZE = 200;
  public static final int PTP_SCHEDULER_QUEUE_SIZE = 1000;

  public static final int PTG_SCHEDULER_CORE_POOL_SIZE = 50;
  public static final int PTG_SCHEDULER_MAX_POOL_SIZE = 200;
  public static final int PTG_SCHEDULER_QUEUE_SIZE = 2000;

  public static final int BOL_SCHEDULER_CORE_POOL_SIZE = 50;
  public static final int BOL_SCHEDULER_MAX_POOL_SIZE = 200;
  public static final int BOL_SCHEDULER_QUEUE_SIZE = 2000;

  public static final int REQUESTS_SCHEDULER_CORE_POOL_SIZE = 10;
  public static final int REQUESTS_SCHEDULER_MAX_POOL_SIZE = 50;
  public static final int REQUESTS_SCHEDULER_QUEUE_SIZE = 2000;

  public static final int REQUESTS_PICKER_AGENT_DELAY = 1;
  public static final int REQUESTS_PICKER_AGENT_INTERVAL = 2;
  public static final int REQUESTS_PICKER_AGENT_MAX_FETCHED_SIZE = 100;

  /* advanced */

  public static final long FILE_DEFAULT_SIZE = 1000000L;
  public static final long FILE_LIFETIME_DEFAULT = 259200L;
  public static final long PIN_LIFETIME_DEFAULT = 259200L;
  public static final long PIN_LIFETIME_MAXIMUM = 1814400L;

  public static final int LS_MAX_NUMBER_OF_ENTRY = 2000;
  public static final boolean LS_DEFAULT_ALL_LEVEL_RECURSIVE = false;
  public static final short LS_DEFAULT_NUM_OF_LEVELS = 1;
  public static final short LS_DEFAULT_OFFSET = 0;

  public static final boolean AUTOMATIC_DIRECTORY_CREATION = false;
  public static final boolean ENABLE_WRITE_PERM_ON_DIRECTORY = false;

  public static final String DEFAULT_OVERWRITE_MODE = OverwriteMode.N.name();
  public static final String DEFAULT_FILE_STORAGE_TYPE = StorageType.V.name();

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
