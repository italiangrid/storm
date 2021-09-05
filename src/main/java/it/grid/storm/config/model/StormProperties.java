package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.GPFS_QUOTA_REFRESH_PERIOD;
import static it.grid.storm.config.ConfigurationDefaults.HTTP_TURL_PREFIX;
import static it.grid.storm.config.ConfigurationDefaults.MAX_LOOP;
import static it.grid.storm.config.ConfigurationDefaults.PING_VALUES_PROPERTIES_FILENAME;
import static it.grid.storm.config.ConfigurationDefaults.PTG_SKIP_ACL_SETUP;
import static it.grid.storm.config.ConfigurationDefaults.SANITY_CHECK_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.SERVER_POOL_STATUS_CHECK_TIMEOUT;

import java.net.UnknownHostException;
import java.util.List;

import org.slf4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jersey.repackaged.com.google.common.collect.Lists;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StormProperties {

  public static final String VERSION = "v2";
  public static final String UNRECOGNIZED_VERSION = "unknown";
  
  public String version;
  public List<Endpoint> srmEndpoints;
  public DatabaseConnection db;
  public RestServer rest;
  public XmlRpcServer xmlrpc;
  public SecuritySettings security;
  public DiskUsageService du;
  public InProgressRequestsAgent inprogressRequestsAgent;
  public ExpiredSpacesAgent expiredSpacesAgent;
  public CompletedRequestsAgent completedRequestsAgent;
  public RequestsPickerAgent requestsPickerAgent;
  public RequestsScheduler requestsScheduler;
  public PtpScheduler ptpScheduler;
  public PtgScheduler ptgScheduler;
  public BolScheduler bolScheduler;
  public boolean sanityChecksEnabled;
  public ExtraslashesSettings extraslashes;
  public SynchLsSettings synchLs;
  public PinlifetimeSettings pinlifetime;
  public boolean skipPtgAclSetup;
  public AdvancedFileSettings files;
  public AdvancedDirectorySettings directories;
  public HearthbeatSettings hearthbeat;
  public int infoQuotaRefreshPeriod;
  public String httpTurlPrefix;
  public long serverPoolStatusCheckTimeout;
  public int abortMaxloop;
  public String pingPropertiesFilename;  

  public StormProperties() throws UnknownHostException {
    version = UNRECOGNIZED_VERSION;
    srmEndpoints = Lists.newArrayList(Endpoint.DEFAULT());
    db = new DatabaseConnection();
    rest = new RestServer();
    xmlrpc = new XmlRpcServer();
    security = new SecuritySettings();
    du = new DiskUsageService();
    inprogressRequestsAgent = new InProgressRequestsAgent();
    expiredSpacesAgent = new ExpiredSpacesAgent();
    completedRequestsAgent = new CompletedRequestsAgent();
    requestsPickerAgent = new RequestsPickerAgent();
    requestsScheduler = new RequestsScheduler();
    ptpScheduler = new PtpScheduler();
    ptgScheduler = new PtgScheduler();
    bolScheduler = new BolScheduler();
    sanityChecksEnabled = SANITY_CHECK_ENABLED;
    extraslashes = new ExtraslashesSettings();
    synchLs = new SynchLsSettings();
    pinlifetime = new PinlifetimeSettings();
    skipPtgAclSetup = PTG_SKIP_ACL_SETUP;
    files = new AdvancedFileSettings();
    directories = new AdvancedDirectorySettings();
    hearthbeat = new HearthbeatSettings();
    infoQuotaRefreshPeriod = GPFS_QUOTA_REFRESH_PERIOD;
    httpTurlPrefix = HTTP_TURL_PREFIX;
    serverPoolStatusCheckTimeout = SERVER_POOL_STATUS_CHECK_TIMEOUT;
    abortMaxloop = MAX_LOOP;
    pingPropertiesFilename = PING_VALUES_PROPERTIES_FILENAME;
  }

  public boolean hasVersion() {
    return !UNRECOGNIZED_VERSION.equals(version);
  }
  
  public void log(Logger log) {

    log.info("version: {}", version);
    for (int i = 0; i < srmEndpoints.size(); i++) {
      log.info("srm_endpoints.{}.host: {}", i+1, srmEndpoints.get(i).host);
      log.info("srm_endpoints.{}.port: {}", i+1, srmEndpoints.get(i).port);
    }
    db.log(log, "db");
    rest.log(log, "rest");
    xmlrpc.log(log, "xmlrpc");
    security.log(log, "security");
    du.log(log, "du");
    inprogressRequestsAgent.log(log, "inprogress_requests_agent");
    expiredSpacesAgent.log(log, "expired_spaces_agent");
    completedRequestsAgent.log(log, "completed_requests_agent");
    requestsPickerAgent.log(log, "requests_picker_agent");
    requestsScheduler.log(log, "requests_scheduler");
    ptpScheduler.log(log, "ptp_scheduler");
    ptgScheduler.log(log, "ptg_scheduler");
    bolScheduler.log(log, "bol_scheduler");
    log.info("sanity_checks_enabled: {}", sanityChecksEnabled);
    extraslashes.log(log, "extraslashes");
    synchLs.log(log, "synch_ls");
    pinlifetime.log(log, "pinlifetime");
    log.info("skip_ptg_acl_setup: {}", skipPtgAclSetup);
    files.log(log, "files");
    directories.log(log, "directories");
    hearthbeat.log(log, "hearthbeat");
    log.info("info_quota_refresh_period: {}", infoQuotaRefreshPeriod);
    log.info("http_turl_prefix: {}", httpTurlPrefix);
    log.info("server_pool_status_check_timeout: {}", serverPoolStatusCheckTimeout);
    log.info("abort_max_loop: {}", abortMaxloop);
    log.info("ping_properties_filename: {}", pingPropertiesFilename);
  }

}
