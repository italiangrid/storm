package it.grid.storm.namespace.model;

import it.grid.storm.namespace.NamespaceDirector;
import org.apache.commons.logging.Log;

public class Quota {

  private Log log = NamespaceDirector.getLogger();

  private boolean defined = false;
  private boolean enabled = false;
  private String propertiesFile = null;
  private String device = null;
  private QuotaType quotaID = null;

  public Quota() {
    super();
  }

  public Quota(boolean enabled, String device, QuotaType quotaID) {
    this.defined = true;
    this.enabled = enabled;
    this.device = device;
    this.quotaID = quotaID;
    this.propertiesFile = null;
  }

  public Quota(boolean enabled, String propertiesFile) {
    this.defined = true;
    this.enabled = enabled;
    this.propertiesFile = propertiesFile;
    parsingPropertiesFile();
  }

  /**
   * READ ONLY PROPERTY
   * @return boolean
   */
  public boolean getDefined() {
    return defined;
  }

  public boolean getEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getDevice() {
    return this.device;
  }

  public void setDevice(String device) {
    this.device = device;
  }

  public QuotaType getQuotaID() {
    return this.quotaID;
  }

  public void setQuotaID(QuotaType quotaID){
    this.quotaID = quotaID;
  }

  /**
   * Method used to retrieve Quota Parameters
   * @todo
   */
  private void parsingPropertiesFile(){
     log.warn("WARNING! Parsing of Quota Properties File is not enabled in this version of StoRM!");
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append("Quota : [ Defined:'"+this.defined+"' ");
    result.append("Enabled:'"+this.enabled+"' ");
    if (this.propertiesFile==null) {
      result.append("device:'"+this.device+"', ");
      result.append("quotaID:'"+this.quotaID+" ");
    } else {
      result.append("Property-file:'"+this.propertiesFile+"' --> [");
      result.append("device:'"+this.device+"', ");
      result.append("quotaID:'"+this.quotaID+"' ]");
    }
    result.append("]");
    return result.toString();
  }

}
