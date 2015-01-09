package it.grid.storm.https.impl;

import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.https.HTTPSPluginException;
import it.grid.storm.https.HTTPSPluginInterface;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHTTPPlugin implements HTTPSPluginInterface {

  public static final Logger LOGGER = LoggerFactory
    .getLogger(DefaultHTTPPlugin.class);

  public DefaultHTTPPlugin() {

  }

  @Override
  public void grantGroupPermission(LocalFile localFile, LocalUser localUser,
    FilesystemPermission permission) {

  }

  @Override
  public void grantUserPermission(LocalFile localFile, LocalUser localUser,
    FilesystemPermission permission) {

  }

  @Override
  public void removeGroupPermission(LocalFile localFile, LocalUser localUser) {

  }

  @Override
  public void removeUserPermission(LocalFile localFile, LocalUser localUser) {

  }

  @Override
  public void revokeGroupPermission(LocalFile localFile, LocalUser localUser,
    FilesystemPermission permission) {

  }

  @Override
  public void revokeUserPermission(LocalFile localFile, LocalUser localUser,
    FilesystemPermission permission) {

  }

  @Override
  public void setGroupPermission(LocalFile localFile, LocalUser localUser,
    FilesystemPermission permission) {

  }

  @Override
  public void setUserPermission(LocalFile localFile, LocalUser localUser,
    FilesystemPermission permission) {

  }

  @Override
  public void removeAllPermissions(LocalFile localFile) {

  }

  @Override
  public void moveAllPermissions(LocalFile fromLocalFile, LocalFile toLocalFile) {

  }

  @Override
  public String mapLocalPath(String hostname, String localAbsolutePath)
    throws HTTPSPluginException {

    return null;
  }

  private boolean isReachable(String hostname, int port) {

    Socket socket = null;
    boolean reachable = false;

    try {
      socket = new Socket(hostname, port);
      reachable = true;
    } catch (UnknownHostException e) {
      LOGGER.warn("Unknown host while checking HTTP service status: {}",
        e.getMessage());
      LOGGER.debug(e.getMessage(), e);

    } catch (IOException e) {
      LOGGER.warn("IOException while checking HTTP service status: {}",
        e.getMessage());
      LOGGER.debug(e.getMessage(), e);
    } finally {
      if (socket != null) {
        try {
          socket.close();
        } catch (IOException e) {
        }
      }
    }

    return reachable;
  }

  @Override
  public ServiceStatus getServiceStatus(String hostname, int port,
    Protocol protocol) throws HTTPSPluginException {

    if (isReachable(hostname, port)) {
      return ServiceStatus.RUNNING;
    }
    
    LOGGER.info("{} endpoint {}:{} is not reachable", protocol.name(), 
      hostname, port );
    
    return ServiceStatus.NOT_RESPONDING;
  }

  @Override
  public void grantServiceGroupPermission(LocalFile localFile,
    FilesystemPermission permission) {

  }

  @Override
  public void grantServiceUserPermission(LocalFile localFile,
    FilesystemPermission permission) {

  }

}
