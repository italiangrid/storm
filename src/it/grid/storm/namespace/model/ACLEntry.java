package it.grid.storm.namespace.model;

import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.namespace.util.userinfo.EtcGroupReader;
import it.grid.storm.namespace.util.userinfo.UserInfoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ACLEntry {

    private static final Logger LOG = LoggerFactory.getLogger(ACLEntry.class);

    private final String groupName;
    private int groupId = -1;
    private final FilePermissionType permission;

    public ACLEntry(String groupName, String permissionString) throws PermissionException {

        // Digest the permission
        permission = FilePermissionType.getFilePermissionType(permissionString);
        if (permission.equals(FilePermissionType.UNKNOWN)) {
            LOG.error("Unble to understand permission '" + permissionString + "'");
            throw new PermissionException("Unble to understand permission '" + permissionString + "'");
        }

        // Digest the GroupName and Retrieve the GroupId
        this.groupName = groupName;
        try {
            boolean isDefined = EtcGroupReader.isGroupDefined(groupName);
            if (!isDefined) {
                LOG.error("The groupName '" + groupName + "' does not exist!");
            } else {
                LOG.debug("Checking if groupName '" + groupName + "' is defined: " + isDefined);
            }
            groupId = EtcGroupReader.getGroupId(groupName);
            LOG.debug("GroupID of '" + groupName + "' = " + groupId);
            // this.groupId = UserInfoExecutor.retrieveGroupID(groupName);
            // this.groupId = UserInfoExecutor.retrieveGroupID_ENT(groupName);
        } catch (UserInfoException ex) {
            LOG.error("ACL Entry: ('" + groupName + "') --> " + ex);
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
            // READ 0 : FilePermissionType.READ.getOrdinalNumber()
            case 0:
                return FilesystemPermission.Read;
                // READWRITE 1 : FilePermissionType.READWRITE.getOrdinalNumber()
            case 1:
                return FilesystemPermission.ReadWrite;
                // WRITE 2 : FilePermissionType.WRITE.getOrdinalNumber()
            case 2:
                return FilesystemPermission.Write;
                // DEFAULT VALUE (is it possible this case?) == READ
            default:
                return FilesystemPermission.Read;
        }
    }

    @Override
    public String toString() {
        return "group: " + groupId + " (" + groupName + ")" + " permission: " + permission;
    }

}
