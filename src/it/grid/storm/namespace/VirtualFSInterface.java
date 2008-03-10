package it.grid.storm.namespace;

import java.util.*;

import it.grid.storm.filesystem.*;
import it.grid.storm.filesystem.swig.*;
import it.grid.storm.griduser.*;
import it.grid.storm.namespace.model.*;
import it.grid.storm.srm.types.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface VirtualFSInterface {

    public String getFSType() throws NamespaceException;

    public String getSpaceTokenDescription() throws NamespaceException;

    public StorageClassType getStorageClassType() throws NamespaceException;

    public String getRootPath() throws NamespaceException;

    public StoRI getRoot() throws NamespaceException;

    public String getAliasName() throws NamespaceException;

    public Class getFSDriver() throws NamespaceException;

    public genericfs getFSDriverInstance() throws NamespaceException;

    public Filesystem getFilesystem() throws NamespaceException;

    public Class getSpaceSystemDriver() throws NamespaceException;

    public SpaceSystem getSpaceSystemDriverInstance() throws NamespaceException;

    public String getAuthorizationSource() throws NamespaceException;

    public boolean isApproachableByUser(GridUserInterface user) throws NamespaceException;

    public DefaultValuesInterface getDefaultValues() throws NamespaceException;

    public CapabilityInterface getCapabilities() throws NamespaceException;

    public PropertyInterface getProperties() throws NamespaceException;

    public List getProtocols() throws NamespaceException;

    public List getMappingRules() throws NamespaceException;

    /**************************************************
     *    Methods used to retrieve the Status of VFS
     **************************************************/

    public TSizeInBytes getUsedNearlineSpace() throws NamespaceException;

    public TSizeInBytes getUsedOnlineSpace() throws NamespaceException;

    public TSizeInBytes getAvailableOnlineSpace() throws NamespaceException;

    public TSizeInBytes getAvailableNearlineSpace() throws NamespaceException;

    /**************************************************
     *    Methods used to create new FILE
     **************************************************/

    public StoRI createFile(String relativePath) throws NamespaceException;

    public StoRI createFile(String relativePath, StoRIType type) throws NamespaceException;

    /**************************************************
     *    Methods Used for implicit space reservation
     **************************************************/

    /**
     * Associate the file with a Space with the presumed size and StoRI name
     * (PURE implicit reservation)
     */
    public void makeSilhouetteForFile(StoRI stori, TSizeInBytes presumedSize) throws NamespaceException;

    //Use for the "not-so-implicit" space reservation the space file specified by token
    public void useSpaceForFile(TSpaceToken token, StoRI file, TSizeInBytes sizePresumed) throws ExpiredSpaceTokenException, NamespaceException;

    //Use for the "not-so-implicit" space reservation the space file specified by token
    public void useAllSpaceForFile(TSpaceToken token, StoRI file) throws ExpiredSpaceTokenException, NamespaceException;

    /**  NOT USED!!
       //Associate the file with a space file
       public void bindSpaceToFile(StoRI space, StoRI file) throws NamespaceException;
     **/

    /**************************************************
     *    Methods Used for EXPLICIT space reservation
     **************************************************/


    public StoRI createSpace(String relativePath, long guaranteedSize, long totalSize) throws NamespaceException;

    public StoRI createSpace(String relativePath, long totalSize) throws NamespaceException;

    public StoRI createSpace(long guarSize, long totalSize) throws NamespaceException;

    public StoRI createSpace(long totalSize) throws NamespaceException;

    public StoRI createSpace() throws NamespaceException;

    /**************************************************
     *    Methods Used as utility for SPACE management
     **************************************************/

    //Return a StoRI representing a new Space
    public TSizeInBytes splitSpace(StoRI spaceOrig, StoRI file, long sizePresumed) throws NamespaceException;

    /**************************************************
     *    Methods Used for Default use
     **************************************************/

    public StoRI createDefaultStoRI() throws NamespaceException;

    public long getCreationTime();

}
