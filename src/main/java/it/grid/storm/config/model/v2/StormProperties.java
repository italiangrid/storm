package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.GPFS_QUOTA_REFRESH_PERIOD;
import static it.grid.storm.config.ConfigurationDefaults.HTTP_TURL_PREFIX;
import static it.grid.storm.config.ConfigurationDefaults.MAX_LOOP;
import static it.grid.storm.config.ConfigurationDefaults.PING_VALUES_PROPERTIES_FILENAME;
import static it.grid.storm.config.ConfigurationDefaults.PTG_SKIP_ACL_SETUP;
import static it.grid.storm.config.ConfigurationDefaults.SANITY_CHECK_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.SERVER_POOL_STATUS_CHECK_TIMEOUT;

import java.net.UnknownHostException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jersey.repackaged.com.google.common.collect.Lists;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StormProperties {

  public static final String VERSION = "2.0";
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

  @JsonCreator
  public StormProperties(@JsonProperty(value = "version", required = true) String version)
      throws UnknownHostException {
    this.version = version;
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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("StormProperties [version=");
    builder.append(version);
    builder.append(", srmEndpoints=");
    builder.append(srmEndpoints);
    builder.append(", db=");
    builder.append(db);
    builder.append(", rest=");
    builder.append(rest);
    builder.append(", xmlrpc=");
    builder.append(xmlrpc);
    builder.append(", security=");
    builder.append(security);
    builder.append(", du=");
    builder.append(du);
    builder.append(", inprogressRequestsAgent=");
    builder.append(inprogressRequestsAgent);
    builder.append(", expiredSpacesAgent=");
    builder.append(expiredSpacesAgent);
    builder.append(", completedRequestsAgent=");
    builder.append(completedRequestsAgent);
    builder.append(", requestsPickerAgent=");
    builder.append(requestsPickerAgent);
    builder.append(", requestsScheduler=");
    builder.append(requestsScheduler);
    builder.append(", ptpScheduler=");
    builder.append(ptpScheduler);
    builder.append(", ptgScheduler=");
    builder.append(ptgScheduler);
    builder.append(", bolScheduler=");
    builder.append(bolScheduler);
    builder.append(", sanityChecksEnabled=");
    builder.append(sanityChecksEnabled);
    builder.append(", extraslashes=");
    builder.append(extraslashes);
    builder.append(", synchLs=");
    builder.append(synchLs);
    builder.append(", pinlifetime=");
    builder.append(pinlifetime);
    builder.append(", skipPtgAclSetup=");
    builder.append(skipPtgAclSetup);
    builder.append(", files=");
    builder.append(files);
    builder.append(", directories=");
    builder.append(directories);
    builder.append(", hearthbeat=");
    builder.append(hearthbeat);
    builder.append(", infoQuotaRefreshPeriod=");
    builder.append(infoQuotaRefreshPeriod);
    builder.append(", httpTurlPrefix=");
    builder.append(httpTurlPrefix);
    builder.append(", serverPoolStatusCheckTimeout=");
    builder.append(serverPoolStatusCheckTimeout);
    builder.append(", abortMaxloop=");
    builder.append(abortMaxloop);
    builder.append(", pingPropertiesFilename=");
    builder.append(pingPropertiesFilename);
    builder.append("]");
    return builder.toString();
  }


}
