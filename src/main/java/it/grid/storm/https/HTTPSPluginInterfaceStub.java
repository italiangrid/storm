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
public class HTTPSPluginInterfaceStub implements HTTPSPluginInterface {

	private static final Logger log = LoggerFactory
		.getLogger(HTTPSPluginInterfaceStub.class);

	@Override
	public void grantServiceGroupPermission(LocalFile localFile,
		FilesystemPermission permission) {
	  
	  throw new UnsupportedOperationException("Unimplemented. "
	    + "Please check documentation on how to configure "
	    + "the HTTPs plugin properly");

	}

	@Override
	public void grantGroupPermission(LocalFile localFile, LocalUser localUser,
		FilesystemPermission permission) {

	  throw new UnsupportedOperationException("Unimplemented. "
	    + "Please check documentation on how to configure "
	    + "the HTTPs plugin properly");
	}

	@Override
	public void grantServiceUserPermission(LocalFile localFile,
		FilesystemPermission permission) {

	  throw new UnsupportedOperationException("Unimplemented. "
	    + "Please check documentation on how to configure "
	    + "the HTTPs plugin properly");
	}

	@Override
	public void grantUserPermission(LocalFile localFile, LocalUser localUser,
		FilesystemPermission permission) {

	  throw new UnsupportedOperationException("Unimplemented. "
	    + "Please check documentation on how to configure "
	    + "the HTTPs plugin properly");
	}

	@Override
	public void removeGroupPermission(LocalFile localFile, LocalUser localUser) {

	  throw new UnsupportedOperationException("Unimplemented. "
	    + "Please check documentation on how to configure "
	    + "the HTTPs plugin properly");

	}

	@Override
	public void removeUserPermission(LocalFile localFile, LocalUser localUser) {

	  throw new UnsupportedOperationException("Unimplemented. "
	    + "Please check documentation on how to configure "
	    + "the HTTPs plugin properly");
	}

	@Override
	public void revokeGroupPermission(LocalFile localFile, LocalUser localUser,
		FilesystemPermission permission) {

	  throw new UnsupportedOperationException("Unimplemented. "
	    + "Please check documentation on how to configure "
	    + "the HTTPs plugin properly");

	}

	@Override
	public void revokeUserPermission(LocalFile localFile, LocalUser localUser,
		FilesystemPermission permission) {

	  throw new UnsupportedOperationException("Unimplemented. "
	    + "Please check documentation on how to configure "
	    + "the HTTPs plugin properly");

	}

	@Override
	public void setGroupPermission(LocalFile localFile, LocalUser localUser,
		FilesystemPermission permission) {

	  throw new UnsupportedOperationException("Unimplemented. "
	    + "Please check documentation on how to configure "
	    + "the HTTPs plugin properly");
	}

	@Override
	public void setUserPermission(LocalFile localFile, LocalUser localUser,
		FilesystemPermission permission) {

	  throw new UnsupportedOperationException("Unimplemented. "
	    + "Please check documentation on how to configure "
	    + "the HTTPs plugin properly");
	}

	@Override
	public void removeAllPermissions(LocalFile localFile) {

	  throw new UnsupportedOperationException("Unimplemented. "
	    + "Please check documentation on how to configure "
	    + "the HTTPs plugin properly");
	}

	@Override
	public void moveAllPermissions(LocalFile fromLocalFile, LocalFile toLocalFile) {

	  throw new UnsupportedOperationException("Unimplemented. "
	    + "Please check documentation on how to configure "
	    + "the HTTPs plugin properly");

	}

	@Override
	public String mapLocalPath(String hostname, String localAbsolutePath) {

	  throw new UnsupportedOperationException("Unimplemented. "
	    + "Please check documentation on how to configure "
	    + "the HTTPs plugin properly");

	}

	@Override
	public ServiceStatus getServiceStatus(String hostname, int port,
		Protocol protocol) throws HTTPSPluginException {

	  throw new UnsupportedOperationException("Unimplemented. "
	    + "Please check documentation on how to configure "
	    + "the HTTPs plugin properly");
	}
}