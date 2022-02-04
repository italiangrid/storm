package it.grid.storm.rest.info.namespace.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AclEntry {

  private String groupName;
  private PermissionType permissions;

  @JsonProperty("groupName")
  public String getGroupName() {
    return groupName;
  }

  @JsonProperty("groupName")
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  @JsonProperty("permissions")
  public PermissionType getPermissions() {
    return permissions;
  }

  @JsonProperty("permissions")
  public void setPermissions(PermissionType permissions) {
    this.permissions = permissions;
  }


}
