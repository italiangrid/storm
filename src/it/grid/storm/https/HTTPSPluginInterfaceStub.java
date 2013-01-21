/**
 * 
 */
package it.grid.storm.https;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.LocalUser;

/**
 * @author Michele Dibenedetto
 *
 */
public class HTTPSPluginInterfaceStub implements HTTPSPluginInterface
{

    private static final Logger log = LoggerFactory.getLogger(HTTPSPluginInterfaceStub.class);
    
    @Override
    public void grantServiceGroupPermission(LocalFile localFile, FilesystemPermission permission)
    {
        log.info("Granted group permission " + permission.toString() +  " on file " + localFile.toString() + " to service group");
    }
    
    /* (non-Javadoc)
     * @see it.grid.storm.https.HTTPSPluginInterface#grantGroupPermission(it.grid.storm.filesystem.LocalFile, it.grid.storm.griduser.LocalUser, it.grid.storm.filesystem.FilesystemPermission)
     */
    @Override
    public void grantGroupPermission(LocalFile localFile, LocalUser localUser, FilesystemPermission permission)
    {
        log.info("Granted group permission " + permission.toString() +  " on file " + localFile.toString() + " to group " + localUser.toString());
    }
    
    @Override
    public void grantServiceUserPermission(LocalFile localFile, FilesystemPermission permission)
    {
        log.info("Granted user permission " + permission.toString() +  " on file " + localFile.toString() + " to service user");
    }

    /* (non-Javadoc)
     * @see it.grid.storm.https.HTTPSPluginInterface#grantUserPermission(it.grid.storm.filesystem.LocalFile, it.grid.storm.griduser.LocalUser, it.grid.storm.filesystem.FilesystemPermission)
     */
    @Override
    public void grantUserPermission(LocalFile localFile, LocalUser localUser, FilesystemPermission permission)
    {
        log.info("Granted user permission " + permission.toString() +  " on file " + localFile.toString() + " to user " + localUser.toString());

    }
    
    /* (non-Javadoc)
     * @see it.grid.storm.https.HTTPSPluginInterface#removeGroupPermission(it.grid.storm.filesystem.LocalFile, it.grid.storm.griduser.LocalUser)
     */
    @Override
    public void removeGroupPermission(LocalFile localFile, LocalUser localUser)
    {
        log.info("Removed group permission from file " + localFile.toString() + " to group " + localUser.toString());

    }

    /* (non-Javadoc)
     * @see it.grid.storm.https.HTTPSPluginInterface#removeUserPermission(it.grid.storm.filesystem.LocalFile, it.grid.storm.griduser.LocalUser)
     */
    @Override
    public void removeUserPermission(LocalFile localFile, LocalUser localUser)
    {
        log.info("Removed user permission from file " + localFile.toString() + " to user " + localUser.toString());

    }

    /* (non-Javadoc)
     * @see it.grid.storm.https.HTTPSPluginInterface#revokeGroupPermission(it.grid.storm.filesystem.LocalFile, it.grid.storm.griduser.LocalUser, it.grid.storm.filesystem.FilesystemPermission)
     */
    @Override
    public void revokeGroupPermission(LocalFile localFile, LocalUser localUser,
            FilesystemPermission permission)
    {
        log.info("Revoked group permission " + permission.toString() +  " from file " + localFile.toString() + " to group " + localUser.toString());

    }

    /* (non-Javadoc)
     * @see it.grid.storm.https.HTTPSPluginInterface#revokeUserPermission(it.grid.storm.filesystem.LocalFile, it.grid.storm.griduser.LocalUser, it.grid.storm.filesystem.FilesystemPermission)
     */
    @Override
    public void revokeUserPermission(LocalFile localFile, LocalUser localUser, FilesystemPermission permission)
    {
        log.info("Revoked user permission " + permission.toString() +  " from file " + localFile.toString() + " to user " + localUser.toString());

    }

    /* (non-Javadoc)
     * @see it.grid.storm.https.HTTPSPluginInterface#setGroupPermission(it.grid.storm.filesystem.LocalFile, it.grid.storm.griduser.LocalUser, it.grid.storm.filesystem.FilesystemPermission)
     */
    @Override
    public void setGroupPermission(LocalFile localFile, LocalUser localUser, FilesystemPermission permission)
    {
        log.info("Setted group permission " + permission.toString() +  " on file " + localFile.toString() + " to group " + localUser.toString());

    }

    /* (non-Javadoc)
     * @see it.grid.storm.https.HTTPSPluginInterface#setUserPermission(it.grid.storm.filesystem.LocalFile, it.grid.storm.griduser.LocalUser, it.grid.storm.filesystem.FilesystemPermission)
     */
    @Override
    public void setUserPermission(LocalFile localFile, LocalUser localUser, FilesystemPermission permission)
    {
        log.info("Setted user permission " + permission.toString() +  " on file " + localFile.toString() + " to user " + localUser.toString());

    }

    @Override
    public void removeAllPermissions(LocalFile localFile)
    {
        log.info("Removing all permissions from file " + localFile.toString());
        
    }

    @Override
    public void moveAllPermissions(LocalFile fromLocalFile, LocalFile toLocalFile)
    {
        log.info("Moving all permissions from file " + fromLocalFile.toString() + " to file " + toLocalFile.toString());
        
    }

    @Override
    public String mapLocalPath(String hostname, String localAbsolutePath)
    {
        log.info("Mapping local absolute path \'" + localAbsolutePath + "\' to \'itIsTheSame" + File.separatorChar + localAbsolutePath + "\'");
        return  File.separatorChar + "itIsTheSame" + localAbsolutePath;
    }

    @Override
    public ServiceStatus getServiceStatus(String hostname, int port, Protocol protocol)
            throws HTTPSPluginException
    {
        return ServiceStatus.RUNNING;
    }

}
