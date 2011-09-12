/**
 * 
 */
package it.grid.storm.acl;


import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.https.HTTPPluginManager;

/**
 * @author Michele Dibenedetto
 */
public class AclManagerFSAndHTTPS implements AclManager
{


// private static HTTPSPluginFactory factory;
    private static AclManagerFSAndHTTPS instance = new AclManagerFSAndHTTPS();
// private HTTPSPluginInterface httpsplugin = null;
    private boolean isHttpsEnabled = Configuration.getInstance().getGridhttpsEnabled();
    private final AclManagementInterface aclManagement;


// private AclManagerFSAndHTTPS(HTTPSPluginInterface httpsplugin)
    private AclManagerFSAndHTTPS()
    {
        if(isHttpsEnabled)
        {
            aclManagement = HTTPPluginManager.getHTTPSPluginInstance();
        }
        else
        {
            aclManagement = null;   
        }
            
    }

    /**
     * @return
     */
    public static AclManager getInstance()
    {
        return instance;
    }


    /*
     * (non-Javadoc)
     * @see it.grid.storm.acl.AclManager#grantGroupPermission(it.grid.storm.griduser.LocalUser,
     * it.grid.storm.filesystem.FilesystemPermission)
     */
    @Override
    public FilesystemPermission grantGroupPermission(LocalFile localFile, LocalUser localUser, FilesystemPermission permission)  throws IllegalArgumentException
    {
        if(localFile == null || localUser == null || permission == null)
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received null parameters: localFile=" + localFile
                                               + " localUser=" +localUser + " permission=" + permission);
        }
        if(!localFile.exists())
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received file parameter points to a non existent file");
        }
        FilesystemPermission newPermission = localFile.grantGroupPermission(localUser, permission);
        if (isHttpsEnabled)
        {
            aclManagement.grantGroupPermission(localFile, localUser, permission);
        }
        return newPermission;
    }


    /*
     * (non-Javadoc)
     * @see it.grid.storm.acl.AclManager#grantUserPermission(it.grid.storm.filesystem.LocalFile,
     * it.grid.storm.griduser.LocalUser, it.grid.storm.filesystem.FilesystemPermission)
     */
    @Override
    public FilesystemPermission grantUserPermission(LocalFile localFile, LocalUser localUser, FilesystemPermission permission) throws IllegalArgumentException
    {
        if(localFile == null || localUser == null || permission == null)
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received null parameters: localFile=" + localFile
                                               + " localUser=" +localUser + " permission=" + permission);
        }
        if(!localFile.exists())
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received file parameter points to a non existent file");
        }
        FilesystemPermission newPermission = localFile.grantUserPermission(localUser, permission);
        if (isHttpsEnabled)
        {
            aclManagement.grantUserPermission(localFile, localUser, permission);
        }
        return newPermission;
    }


    @Override
    public FilesystemPermission removeGroupPermission(LocalFile localFile, LocalUser localUser) throws IllegalArgumentException
    {
        if(localFile == null || localUser == null)
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received null parameters: localFile=" + localFile
                                               + " localUser=" +localUser );
        }
        if(!localFile.exists())
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received file parameter points to a non existent file");
        }
        FilesystemPermission newPermission = localFile.removeGroupPermission(localUser);
        if (isHttpsEnabled)
        {
            aclManagement.removeGroupPermission(localFile, localUser);
        }
        return newPermission;
    }


    @Override
    public FilesystemPermission removeUserPermission(LocalFile localFile, LocalUser localUser) throws IllegalArgumentException
    {
        if(localFile == null || localUser == null)
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received null parameters: localFile=" + localFile
                                               + " localUser=" +localUser );
        }
        if(!localFile.exists())
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received file parameter points to a non existent file");
        }
        FilesystemPermission newPermission = localFile.removeUserPermission(localUser);
        if (isHttpsEnabled)
        {
            aclManagement.removeUserPermission(localFile, localUser);
        }
        return newPermission;
    }


    @Override
    public FilesystemPermission revokeGroupPermission(LocalFile localFile, LocalUser localUser, FilesystemPermission permission) throws IllegalArgumentException
    {
        if(localFile == null || localUser == null || permission == null)
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received null parameters: localFile=" + localFile
                                               + " localUser=" +localUser + " permission=" + permission);
        }
        if(!localFile.exists())
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received file parameter points to a non existent file");
        }
        FilesystemPermission newPermission = localFile.revokeGroupPermission(localUser, permission);
        if (isHttpsEnabled)
        {
            aclManagement.revokeGroupPermission(localFile, localUser, permission);
        }
        return newPermission;
    }


    @Override
    public FilesystemPermission revokeUserPermission(LocalFile localFile, LocalUser localUser, FilesystemPermission permission) throws IllegalArgumentException
    {
        if(localFile == null || localUser == null || permission == null)
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received null parameters: localFile=" + localFile
                                               + " localUser=" +localUser + " permission=" + permission);
        }
        if(!localFile.exists())
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received file parameter points to a non existent file");
        }
        FilesystemPermission newPermission = localFile.revokeUserPermission(localUser, permission);
        if (isHttpsEnabled)
        {
            aclManagement.revokeUserPermission(localFile, localUser, permission);
        }
        return newPermission;
    }


    @Override
    public FilesystemPermission setGroupPermission(LocalFile localFile, LocalUser localUser, FilesystemPermission permission) throws IllegalArgumentException
    {
        if(localFile == null || localUser == null || permission == null)
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received null parameters: localFile=" + localFile
                                               + " localUser=" +localUser + " permission=" + permission);
        }
        if(!localFile.exists())
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received file parameter points to a non existent file");
        }
        FilesystemPermission newPermission = localFile.setGroupPermission(localUser, permission);
        if (isHttpsEnabled)
        {
            aclManagement.setGroupPermission(localFile, localUser, permission);
        }
        return newPermission;
    }


    @Override
    public FilesystemPermission setUserPermission(LocalFile localFile, LocalUser localUser, FilesystemPermission permission) throws IllegalArgumentException
    {
        if(localFile == null || localUser == null || permission == null)
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received null parameters: localFile=" + localFile
                                               + " localUser=" +localUser + " permission=" + permission);
        }
        if(!localFile.exists())
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received file parameter points to a non existent file");
        }
        FilesystemPermission newPermission = localFile.setUserPermission(localUser, permission);
        if (isHttpsEnabled)
        {
            aclManagement.setUserPermission(localFile, localUser, permission);
        }
        return newPermission;
    }


    @Override
    public void removeHttpsPermissions(LocalFile localFile) throws IllegalArgumentException
    {
        if(localFile == null)
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received file parameter is null");
        }
        if (isHttpsEnabled)
        {
            if(!localFile.exists())
            {
                throw new IllegalArgumentException("Unable to perform the operation. The received file parameter points to a non existent file");
            }
            aclManagement.removeAllPermissions(localFile);
        }
    }


    @Override
    public void grantHttpsUserPermission(LocalFile localFile, LocalUser localUser, FilesystemPermission permission) throws IllegalArgumentException
    {
        if(localFile == null || localUser == null || permission == null)
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received null parameters: localFile=" + localFile
                                               + " localUser=" +localUser + " permission=" + permission);
        }
        if (isHttpsEnabled)
        {
            if(!localFile.exists())
            {
                throw new IllegalArgumentException("Unable to perform the operation. The received file parameter points to a non existent file");
            }
            aclManagement.grantUserPermission(localFile, localUser, permission);
        }
    }


    @Override
    public void grantHttpsGroupPermission(LocalFile localFile, LocalUser localUser, FilesystemPermission permission) throws IllegalArgumentException
    {
        if(localFile == null || localUser == null || permission == null)
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received null parameters: localFile=" + localFile
                                               + " localUser=" +localUser + " permission=" + permission);
        }
        if (isHttpsEnabled)
        {
            if(!localFile.exists())
            {
                throw new IllegalArgumentException("Unable to perform the operation. The received file parameter points to a non existent file");
            }
            aclManagement.grantGroupPermission(localFile, localUser, permission);
        }
    }


    @Override
    public void moveHttpsPermissions(LocalFile fromLocalFile, LocalFile toLocalFile) throws IllegalArgumentException
    {
        if(fromLocalFile == null || toLocalFile == null)
        {
            throw new IllegalArgumentException("Unable to perform the operation. The received null parameters: fromLocalFile=" + fromLocalFile
                                               + " toLocalFile=" +toLocalFile);
        }
        if (isHttpsEnabled)
        {
            if(!fromLocalFile.exists())
            {
                throw new IllegalArgumentException("Unable to perform the operation. The received source file parameter points to a non existent file");
            }
            if(!toLocalFile.exists())
            {
                throw new IllegalArgumentException("Unable to perform the operation. The received destination file parameter points to a non existent file");
            }
            aclManagement.moveAllPermissions(fromLocalFile, toLocalFile);
        }
    }
}
