package it.grid.storm.namespace.model;

import org.apache.log4j.Logger;
import it.grid.storm.namespace.util.userinfo.UserInfoExecutor;
import it.grid.storm.namespace.util.userinfo.UserInfoException;
import it.grid.storm.filesystem.FilesystemPermission;

public class ACLEntry {

  private static final Logger LOG = Logger.getLogger("namespace");

  private String groupName;
  private int groupId = -1;
  private FilePermissionType permission;

  public ACLEntry(String groupName, String permissionString) throws PermissionException {

    //Digest the permission
    this.permission = FilePermissionType.getFilePermissionType(permissionString);
    if (permission.equals(FilePermissionType.UNKNOWN)) {
      LOG.error("Unble to understand permission '"+permissionString+"'");
      throw new PermissionException("Unble to understand permission '"+permissionString+"'");
    }

    //Digest the GroupName and Retrieve the GroupId
    this.groupName = groupName;
    try {
       this.groupId = UserInfoExecutor.retrieveGroupID(groupName);
       //this.groupId = UserInfoExecutor.retrieveGroupID_ENT(groupName);
    }
    catch (UserInfoException ex) {
      LOG.error("ACL Entry: ('"+groupName+"') --> "+ex);
      throw new PermissionException(ex.getMessage());
    }
  }

  public int getGroupID() {
    return groupId;
  }

  public String getFilePermissionString() {
    return permission.getPermissionString();
  }

  public FilesystemPermission getFilesystemPermission() {
    switch (permission.getOrdinalNumber()) {
      //READ 0 : FilePermissionType.READ.getOrdinalNumber()
      case 0 : return FilesystemPermission.Read;
      //READWRITE 1 : FilePermissionType.READWRITE.getOrdinalNumber()
      case 1 : return FilesystemPermission.ReadWrite;
      //WRITE 2 : FilePermissionType.WRITE.getOrdinalNumber()
      case 2 : return FilesystemPermission.Write;
      //DEFAULT VALUE (is it possible this case?) == READ
      default: return FilesystemPermission.Read;
    }
  }


  public String toString() {
    return "group: "+this.groupId+" ("+this.groupName+")" + " permission: "+this.permission;
  }



}
