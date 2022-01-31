package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.GPFS_QUOTA_REFRESH_PERIOD;
import static it.grid.storm.config.ConfigurationDefaults.HTTP_TURL_PREFIX;
import static it.grid.storm.config.ConfigurationDefaults.MAX_LOOP;
import static it.grid.storm.config.ConfigurationDefaults.PING_VALUES_PROPERTIES_FILENAME;
import static it.grid.storm.config.ConfigurationDefaults.PTG_SKIP_ACL_SETUP;
import static it.grid.storm.config.ConfigurationDefaults.SANITY_CHECK_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.SERVER_POOL_STATUS_CHECK_TIMEOUT;

import java.net.InetAddress;
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

  private String version;
  private List<Endpoint> srmEndpoints;
  private DatabaseConnection db;
  private RestServer rest;
  private XmlRpcServer xmlrpc;
  private SecuritySettings security;
  private DiskUsageService du;
  private InProgressRequestsAgent inprogressRequestsAgent;
  private ExpiredSpacesAgent expiredSpacesAgent;
  private CompletedRequestsAgent completedRequestsAgent;
  private RequestsPickerAgent requestsPickerAgent;
  private RequestsScheduler requestsScheduler;
  private PtpScheduler ptpScheduler;
  private PtgScheduler ptgScheduler;
  private BolScheduler bolScheduler;
  private boolean sanityChecksEnabled;
  private ExtraslashesSettings extraslashes;
  private SynchLsSettings synchLs;
  private PinlifetimeSettings pinlifetime;
  private boolean skipPtgAclSetup;
  private AdvancedFileSettings files;
  private AdvancedDirectorySettings directories;
  private HearthbeatSettings hearthbeat;
  private int infoQuotaRefreshPeriod;
  private String httpTurlPrefix;
  private long serverPoolStatusCheckTimeout;
  private int abortMaxloop;
  private String pingPropertiesFilename;

  private Site site;

  @JsonCreator
  public StormProperties(@JsonProperty(value = "version", required = true) String version)
      throws UnknownHostException {
    this.version = version;
    srmEndpoints = Lists.newArrayList(new Endpoint(InetAddress.getLocalHost().getHostName()));
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
    setSite(new Site());
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

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public List<Endpoint> getSrmEndpoints() {
    return srmEndpoints;
  }

  public void setSrmEndpoints(List<Endpoint> srmEndpoints) {
    this.srmEndpoints = srmEndpoints;
  }

  public DatabaseConnection getDb() {
    return db;
  }

  public void setDb(DatabaseConnection db) {
    this.db = db;
  }

  public RestServer getRest() {
    return rest;
  }

  public void setRest(RestServer rest) {
    this.rest = rest;
  }

  public XmlRpcServer getXmlrpc() {
    return xmlrpc;
  }

  public void setXmlrpc(XmlRpcServer xmlrpc) {
    this.xmlrpc = xmlrpc;
  }

  public SecuritySettings getSecurity() {
    return security;
  }

  public void setSecurity(SecuritySettings security) {
    this.security = security;
  }

  public DiskUsageService getDu() {
    return du;
  }

  public void setDu(DiskUsageService du) {
    this.du = du;
  }

  public InProgressRequestsAgent getInprogressRequestsAgent() {
    return inprogressRequestsAgent;
  }

  public void setInprogressRequestsAgent(InProgressRequestsAgent inprogressRequestsAgent) {
    this.inprogressRequestsAgent = inprogressRequestsAgent;
  }

  public ExpiredSpacesAgent getExpiredSpacesAgent() {
    return expiredSpacesAgent;
  }

  public void setExpiredSpacesAgent(ExpiredSpacesAgent expiredSpacesAgent) {
    this.expiredSpacesAgent = expiredSpacesAgent;
  }

  public CompletedRequestsAgent getCompletedRequestsAgent() {
    return completedRequestsAgent;
  }

  public void setCompletedRequestsAgent(CompletedRequestsAgent completedRequestsAgent) {
    this.completedRequestsAgent = completedRequestsAgent;
  }

  public RequestsPickerAgent getRequestsPickerAgent() {
    return requestsPickerAgent;
  }

  public void setRequestsPickerAgent(RequestsPickerAgent requestsPickerAgent) {
    this.requestsPickerAgent = requestsPickerAgent;
  }

  public RequestsScheduler getRequestsScheduler() {
    return requestsScheduler;
  }

  public void setRequestsScheduler(RequestsScheduler requestsScheduler) {
    this.requestsScheduler = requestsScheduler;
  }

  public PtpScheduler getPtpScheduler() {
    return ptpScheduler;
  }

  public void setPtpScheduler(PtpScheduler ptpScheduler) {
    this.ptpScheduler = ptpScheduler;
  }

  public PtgScheduler getPtgScheduler() {
    return ptgScheduler;
  }

  public void setPtgScheduler(PtgScheduler ptgScheduler) {
    this.ptgScheduler = ptgScheduler;
  }

  public BolScheduler getBolScheduler() {
    return bolScheduler;
  }

  public void setBolScheduler(BolScheduler bolScheduler) {
    this.bolScheduler = bolScheduler;
  }

  public boolean isSanityChecksEnabled() {
    return sanityChecksEnabled;
  }

  public void setSanityChecksEnabled(boolean sanityChecksEnabled) {
    this.sanityChecksEnabled = sanityChecksEnabled;
  }

  public ExtraslashesSettings getExtraslashes() {
    return extraslashes;
  }

  public void setExtraslashes(ExtraslashesSettings extraslashes) {
    this.extraslashes = extraslashes;
  }

  public SynchLsSettings getSynchLs() {
    return synchLs;
  }

  public void setSynchLs(SynchLsSettings synchLs) {
    this.synchLs = synchLs;
  }

  public PinlifetimeSettings getPinlifetime() {
    return pinlifetime;
  }

  public void setPinlifetime(PinlifetimeSettings pinlifetime) {
    this.pinlifetime = pinlifetime;
  }

  public boolean isSkipPtgAclSetup() {
    return skipPtgAclSetup;
  }

  public void setSkipPtgAclSetup(boolean skipPtgAclSetup) {
    this.skipPtgAclSetup = skipPtgAclSetup;
  }

  public AdvancedFileSettings getFiles() {
    return files;
  }

  public void setFiles(AdvancedFileSettings files) {
    this.files = files;
  }

  public AdvancedDirectorySettings getDirectories() {
    return directories;
  }

  public void setDirectories(AdvancedDirectorySettings directories) {
    this.directories = directories;
  }

  public HearthbeatSettings getHearthbeat() {
    return hearthbeat;
  }

  public void setHearthbeat(HearthbeatSettings hearthbeat) {
    this.hearthbeat = hearthbeat;
  }

  public int getInfoQuotaRefreshPeriod() {
    return infoQuotaRefreshPeriod;
  }

  public void setInfoQuotaRefreshPeriod(int infoQuotaRefreshPeriod) {
    this.infoQuotaRefreshPeriod = infoQuotaRefreshPeriod;
  }

  public String getHttpTurlPrefix() {
    return httpTurlPrefix;
  }

  public void setHttpTurlPrefix(String httpTurlPrefix) {
    this.httpTurlPrefix = httpTurlPrefix;
  }

  public long getServerPoolStatusCheckTimeout() {
    return serverPoolStatusCheckTimeout;
  }

  public void setServerPoolStatusCheckTimeout(long serverPoolStatusCheckTimeout) {
    this.serverPoolStatusCheckTimeout = serverPoolStatusCheckTimeout;
  }

  public int getAbortMaxloop() {
    return abortMaxloop;
  }

  public void setAbortMaxloop(int abortMaxloop) {
    this.abortMaxloop = abortMaxloop;
  }

  public String getPingPropertiesFilename() {
    return pingPropertiesFilename;
  }

  public void setPingPropertiesFilename(String pingPropertiesFilename) {
    this.pingPropertiesFilename = pingPropertiesFilename;
  }

  public Site getSite() {
    return site;
  }

  public void setSite(Site site) {
    this.site = site;
  }

}
