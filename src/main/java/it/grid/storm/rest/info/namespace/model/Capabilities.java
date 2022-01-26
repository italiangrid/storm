package it.grid.storm.rest.info.namespace.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Capabilities {

  private AclMode aclMode;
  private List<DefaultAcl> defaultAcl;
  private List<Quota> quota;
  private List<Prot> transProt;
  private Pool pool;

  @JsonProperty("aclMode")
  public AclMode getAclMode() {
    return aclMode;
  }

  @JsonProperty("aclMode")
  public void setAclMode(AclMode aclMode) {
    this.aclMode = aclMode;
  }

  @JsonProperty("defaultAcl")
  public List<DefaultAcl> getDefaultAcl() {
    return defaultAcl;
  }

  @JsonProperty("default-acl")
  public void setDefaultAcl(List<DefaultAcl> defaultAcl) {
    this.defaultAcl = defaultAcl;
  }

  @JsonProperty("quota")
  public List<Quota> getQuota() {
    return quota;
  }

  @JsonProperty("quota")
  public void setQuota(List<Quota> quota) {
    this.quota = quota;
  }

  @JsonProperty("transferProtocols")
  public List<Prot> getTransProt() {
    return transProt;
  }

  @JsonProperty("trans-prot")
  public void setTransProt(List<Prot> transProt) {
    this.transProt = transProt;
  }

  @JsonProperty("pool")
  public Pool getPool() {
    return pool;
  }

  @JsonProperty("pool")
  public void setPool(Pool pool) {
    this.pool = pool;
  }


}
