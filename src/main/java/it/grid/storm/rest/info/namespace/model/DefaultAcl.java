package it.grid.storm.rest.info.namespace.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DefaultAcl {

  private List<AclEntry> aclEntry;

  @JsonProperty("aclEntries")
  public List<AclEntry> getAclEntry() {
    return aclEntry;
  }

  @JsonProperty("acl-entry")
  public void setAclEntry(List<AclEntry> aclEntry) {
    this.aclEntry = aclEntry;
  }

  
}
