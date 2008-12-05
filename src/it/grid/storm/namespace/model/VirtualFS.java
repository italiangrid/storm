package it.grid.storm.namespace.model;

import java.lang.reflect.*;
import java.util.*;

import org.apache.commons.logging.*;
import it.grid.storm.catalogs.*;
import it.grid.storm.common.*;
import it.grid.storm.common.types.*;
import it.grid.storm.config.*;
import it.grid.storm.filesystem.*;
import it.grid.storm.filesystem.swig.*;
import it.grid.storm.griduser.*;
import it.grid.storm.namespace.*;
import it.grid.storm.namespace.naming.*;
import it.grid.storm.srm.types.*;
import it.grid.storm.balancer.Balancer;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public class VirtualFS implements VirtualFSInterface {

    private Log log = NamespaceDirector.getLogger();

    String aliasName = null;
    String type = null;

    StoRI storiRoot = null;
    String spaceTokenDescription = null;
    String rootPath = null;
    Class fsDriver = null;
    Class spaceSystemDriver = null;
    DefaultValuesInterface defValue = null;
    CapabilityInterface capabilities = null;
    PropertyInterface properties = null;
    String authorizationSourceClassName = null;
    Hashtable protocols = null;
    genericfs genericFS = null;
    SpaceSystem spaceSystem = null;
    Filesystem fsWrapper = null;
    List mappingRules = new ArrayList();
    Configuration config;
    StorageClassType storageClass = null;

    //For debug purpose only
    public long creationTime = System.currentTimeMillis();
    public boolean testingMode = false;

    public VirtualFS(boolean testingMode) {
        this.testingMode = testingMode;
    }

    public VirtualFS(String aliasName, String type, String rootPath, String spaceTokenDescription,
                     StorageClassType storageClass, Class fsDriver,
                     Class spaceDriver, PropertyInterface properties, DefaultValuesInterface defaultValue,
                     CapabilityInterface capabilities) throws NamespaceException {
      this.aliasName = aliasName;
      this.type = type;
      this.rootPath = rootPath;
      this.spaceTokenDescription = spaceTokenDescription;
      this.storageClass = storageClass;
      this.fsDriver = fsDriver;
      this.spaceSystemDriver = spaceDriver;
      this.defValue = defaultValue;
      this.capabilities = capabilities;
      this.properties = properties;
      buildStoRIRoot(rootPath);

    }

    /*****************************************************************************
     *  BUILDING METHODs
     ****************************************************************************/

    public void setAliasName(String name) {
        this.aliasName = name;
    }

    public void setFSType(String type) {
        this.type = type;
    }

    public void setFSDriver(Class fsDriver) throws NamespaceException {
        this.fsDriver = fsDriver;
        if (testingMode) {
            this.genericFS = null;
        }
        else {
            this.genericFS = makeFSInstance();
        }

        this.fsWrapper = new Filesystem(getFSDriverInstance());
    }

    public void setSpaceTokenDescription(String spaceTokenDescription) {
      this.spaceTokenDescription = spaceTokenDescription;
    }


    public void setStorageClassType(StorageClassType storageClass) {
      this.storageClass = storageClass;
    }

    public void setProperties(PropertyInterface prop) {
      this.properties = prop;
    }


    public void setAuthZSource(String authorizationSource) {
      this.authorizationSourceClassName = authorizationSource;
    }

    public void setSpaceSystemDriver(Class spaceDriver) {
        this.spaceSystemDriver = spaceDriver;
    }

    public void setDefaultValues(DefaultValuesInterface defValue) {
        this.defValue = defValue;
    }

    public void setCapabilities(CapabilityInterface cap) {
        this.capabilities = cap;
    }

    public void setRoot(String rootPath) throws NamespaceException {
        this.rootPath = rootPath;
        buildStoRIRoot(rootPath);
    }

    public void addMappingRule(MappingRule mappingRule) {
        mappingRules.add(mappingRule);
    }


    private void buildStoRIRoot(String rootPath) throws NamespaceException {
        /**
         * @todo
         */

        //storiRoot = new StoRIImpl(this, rootPath, StoRIType.FOLDER);
    }



    /*****************************************************************************
     *  READ METHODs
     ****************************************************************************/

    public String getFSType() throws NamespaceException {
      return this.type;
    }

    public String getSpaceTokenDescription() throws NamespaceException {
      return this.spaceTokenDescription;
    }

    public StorageClassType getStorageClassType() throws NamespaceException {
      return this.storageClass;
    }

    public String getAuthorizationSource() throws NamespaceException {
      return this.authorizationSourceClassName;
    }

    public PropertyInterface getProperties() throws NamespaceException {
      return this.properties;
    }


    public TSizeInBytes getUsedOnlineSpace() throws NamespaceException {
      TSizeInBytes result = TSizeInBytes.makeEmpty();
      /**
     * @todo : This method must contact Space Manager (or who for him) to
     * retrieve the real situation
     *
     * @todo : Contact Space Catalog to retrieve the logical space occupied.
     * This space must to be equal to space occupied in underlying FS.
       */
      return result;
    }

    public TSizeInBytes getAvailableOnlineSpace() throws NamespaceException {
      TSizeInBytes result = TSizeInBytes.makeEmpty();
    /**
   * @todo : This method must contact Space Manager (or who for him) to
   * retrieve the real situation
   *
   * @todo : Contact Space Catalog to retrieve the logical space occupied.
   * This space must to be equal to space occupied in underlying FS.
     */
    return result;

    }

    public TSizeInBytes getUsedNearlineSpace() throws NamespaceException {
      TSizeInBytes result = TSizeInBytes.makeEmpty();
      return result;
    }


    public TSizeInBytes getAvailableNearlineSpace() throws NamespaceException {
      return properties.getTotalNearlineSize();
  }

    public String getAliasName() throws NamespaceException {
        return this.aliasName;
    }

    public Class getFSDriver() throws NamespaceException {
        return this.fsDriver;
    }

    public genericfs getFSDriverInstance() throws NamespaceException {
        if ( (this.genericFS == null) && (!testingMode)) {
            this.genericFS = makeFSInstance();
        }
        return this.genericFS;
    }

    public List getMappingRules() throws NamespaceException {
        if (this.mappingRules.isEmpty()) {
            throw new NamespaceException("No one MAPPING RULES bound with this VFS (" + aliasName + "). ");
        }
        return this.mappingRules;
    }

    /**
     * makeFSInstance
     *
     * @return genericfs
     */
    private genericfs makeFSInstance() throws NamespaceException {
        genericfs fs = null;
        if (fsDriver == null) {
            throw new NamespaceException("Cannot build FS Driver istance without a valid Driver Class!");
        }

        Class fsArgumentsClass[] = new Class[1];
        fsArgumentsClass[0] = String.class;
        Object[] fsArguments = new Object[] {
            this.rootPath};
        Constructor fsConstructor = null;
        try {
            fsConstructor = fsDriver.getConstructor(fsArgumentsClass);
        }
        catch (SecurityException ex) {
            log.error("Unable to retrieve the FS Driver Constructor. Security problem.", ex);
            throw new NamespaceException("Unable to retrieve the FS Driver Constructor. Security problem.", ex);
        }
        catch (NoSuchMethodException ex) {
            log.error("Unable to retrieve the FS Driver Constructor. Security problem.", ex);
            throw new NamespaceException("Unable to retrieve the FS Driver Constructor. No such constructor.", ex);
        }
        try {
            fs = (genericfs) fsConstructor.newInstance(fsArguments);
        }
        catch (InvocationTargetException ex1) {
            log.error("Unable to instantiate the FS Driver. Wrong target.", ex1);
            //TOREMOVE
            log.debug("VFS: Ex message: " + ex1.getMessage());
            log.debug("VFS Ex Stack: ");
            ex1.printStackTrace();

            throw new NamespaceException("Unable to instantiate the FS Driver. ", ex1);
        }
        catch (IllegalArgumentException ex1) {
            log.error("Unable to instantiate the FS Driver. Using wrong argument.", ex1);
            throw new NamespaceException("Unable to instantiate the FS Driver. Using wrong argument.", ex1);
        }
        catch (IllegalAccessException ex1) {
            log.error("Unable to instantiate the FS Driver. Illegal Access.", ex1);
            throw new NamespaceException("Unable to instantiate the FS Driver. Illegal Access.", ex1);
        }
        catch (InstantiationException ex1) {
            log.error("Unable to instantiate the FS Driver. Generic problem..", ex1);
            throw new NamespaceException("Unable to instantiate the FS Driver. Generic problem..", ex1);
        }

        return fs;
    }

    public Filesystem getFilesystem() throws NamespaceException {
        if (fsWrapper == null) {
            fsWrapper = new Filesystem(getFSDriverInstance());
        }
        return this.fsWrapper;
    }

    public Class getSpaceSystemDriver() throws NamespaceException {
        return this.spaceSystemDriver;
    }

    public SpaceSystem getSpaceSystemDriverInstance() throws NamespaceException {
        if ( (this.spaceSystem == null) && (!testingMode)) {
            this.spaceSystem = makeSpaceSystemInstance();
        }
        return this.spaceSystem;
    }

    /**
     * makeSpaceSystemInstance
     *
     * @return SpaceSystem
     */
    private SpaceSystem makeSpaceSystemInstance() throws NamespaceException {

        SpaceSystem ss = null;

        if (spaceSystemDriver == null) {
            throw new NamespaceException("Cannot build Space Driver istance without a valid Driver Class!");
        }

        //Check if SpaceSystem is GPFSSpaceSystem used for GPFS FS
        //Check if SpaceSystem is MockSpaceSystem used for Posix FS
        //Check if SpaceSystem is MockSpaceSystem used for XFS FS
        if ( (this.spaceSystemDriver.getName().equals(GPFSSpaceSystem.class.getName())) ||
            (this.spaceSystemDriver.getName().equals(MockSpaceSystem.class.getName())) ||
            (this.spaceSystemDriver.getName().equals(XFSSpaceSystem.class.getName()))) {

            //The class type argument is the mount point of GPFS file system
            Class ssArgumentsClass[] = new Class[1];
            ssArgumentsClass[0] = String.class;
            Object[] ssArguments = new Object[] {
                this.rootPath};

            Constructor ssConstructor = null;
            try {
                ssConstructor = spaceSystemDriver.getConstructor(ssArgumentsClass);
            }
            catch (SecurityException ex) {
                log.error("Unable to retrieve the FS Driver Constructor. Security problem.", ex);
                throw new NamespaceException("Unable to retrieve the FS Driver Constructor. Security problem.", ex);
            }
            catch (NoSuchMethodException ex) {
                log.error("Unable to retrieve the FS Driver Constructor. Security problem.", ex);
                throw new NamespaceException("Unable to retrieve the FS Driver Constructor. No such constructor.", ex);
            }

            try {
                ss = (SpaceSystem) ssConstructor.newInstance(ssArguments);
            }
            catch (InvocationTargetException ex1) {
                log.error("Unable to instantiate the SpaceSystem Driver. Wrong target.", ex1);
                throw new NamespaceException("Unable to instantiate the FS Driver. ", ex1);
            }
            catch (IllegalArgumentException ex1) {
                log.error("Unable to instantiate the SpaceSystem Driver. Using wrong argument.", ex1);
                throw new NamespaceException("Unable to instantiate the FS Driver. Using wrong argument.", ex1);
            }
            catch (IllegalAccessException ex1) {
                log.error("Unable to instantiate the SpaceSystem Driver. Illegal Access.", ex1);
                throw new NamespaceException("Unable to instantiate the FS Driver. Illegal Access.", ex1);
            }
            catch (InstantiationException ex1) {
                log.error("Unable to instantiate the SpaceSystem Driver. Generic problem..", ex1);
                throw new NamespaceException("Unable to instantiate the FS Driver. Generic problem..", ex1);
            }

        }
        else {
            log.error("None Space System Driver built");
            /**
             * @todo : Perhaps a "genericSpaceSystem" could be more disederable rather than NULL
             */
            ss = null;
        }

        return ss;
    }

    public DefaultValuesInterface getDefaultValues() throws NamespaceException {
        return this.defValue;
    }

    public CapabilityInterface getCapabilities() throws NamespaceException {
        return this.capabilities;
    }

    public List getProtocols() throws NamespaceException {
        return ( (this.capabilities).getManagedProtocols());
    }

    public String getRootPath() throws NamespaceException {
        return this.rootPath;
    }

    public StoRI getRoot() throws NamespaceException {
        return storiRoot;
    }

    /*****************************************************************************
     *  BUSINESS METHODs
     ****************************************************************************/


    public boolean isApproachableByUser(GridUserInterface user) throws NamespaceException {
        return true;
    }

    public StoRI createFile(String relativePath) throws NamespaceException {
        /**
         * @todo Check if relativePath is a valid path for a file.
         */
        StoRIType type = StoRIType.UNKNOWN;
        //log.debug("CREATING STORI BY RELATIVE PATH : "+relativePath);
        StoRI stori = new StoRIImpl(this, (MappingRule) mappingRules.get(0), relativePath, type);
        return stori;
    }

    public StoRI createFile(String relativePath, StoRIType type) throws NamespaceException {
        /**
         * @todo Check if relativePath is a valid path for a file.
         */
        log.debug("VFS Class - Relative Path : " + relativePath);
        StoRI stori = new StoRIImpl(this, (MappingRule) mappingRules.get(0), relativePath, type);
        return stori;
    }

    /****************************************************************
     * Methods used by StoRI to perform IMPLICIT SPACE RESERVATION
     *****************************************************************/

    /**
     *  Workaround to manage the DEFAULT SPACE TOKEN defined per Storage Area.
     *  This workaround simply give the possibility to define a list of DEFAULT SPACE
     *  TOKENs by the StoRM configuration file.
     *  If the token specified into the PrepareToPut request belongs to the list of default space token,
     *  the space file is not used (since it does not exists into the space catalog) and a simple allocation of blocks is performed
     *  for the file

     *  Return true if the space token specified is a DEAFULT SPACE TOKENS.
     *
     */

    private Boolean isVOSAToken(TSpaceToken token) {
        ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();

        StorageSpaceData ssd = catalog.getStorageSpace(token);

        if ((ssd!=null) && (ssd.getSpaceType().equals(TSpaceType.VOSPACE)))
            return true;
        else
            return false;
    }

    public void makeSilhouetteForFile(StoRI stori, TSizeInBytes presumedSize) throws NamespaceException {

        //Check if StoRI is a file
        if (! (stori.getStoRIType().equals(StoRIType.FILE))) {
            log.error("Unable to associate a Space to the StoRI with type: " + stori.getStoRIType());
            throw new NamespaceException("Unable to associate a Space to the StoRI with type: " + stori.getStoRIType());
        }

        //Retrieve the instance of the right Space System
        SpaceSystem spaceSystem = getSpaceSystemDriverInstance();

        //Retrieve the Local File
        LocalFile localFile = stori.getLocalFile();

        TSizeInBytes guarSize = defValue.getDefaultGuaranteedSpaceSize();

        //Space space = createSpace(guarSize, presumedSize, localFile, spaceSystem);
        Space space = createSpace(presumedSize, presumedSize, localFile, spaceSystem);
        stori.setSpace(space);

    }

    /*
     * THis method is synchronized to avoid multiple execution from different thread.
     * In such condition, the SpaceData is token at the same time from both thread , and then modified
     * and updated. This means that one of the two update will be overwritten from the other thread!
     */

    public synchronized void useSpaceForFile(TSpaceToken token, StoRI file, TSizeInBytes sizePresumed) throws NamespaceException, ExpiredSpaceTokenException {
        //Check if StoRI is a file
        if (! (file.getStoRIType().equals(StoRIType.FILE))) {
            log.error("Unable to associate a Space to the StoRI with type: " + file.getStoRIType());
            throw new NamespaceException("Unable to associate a Space to the StoRI with type: " + file.getStoRIType());
        }

        //Get the default space size
        TSizeInBytes defaultFileSize = null;
        try {
        	defaultFileSize = TSizeInBytes.make(Configuration.getInstance().getFileDefaultSize(), SizeUnit.BYTES );
	    }
	    catch (it.grid.storm.srm.types.InvalidTSizeAttributesException e) {
	        log.debug("Invalid size created.");
	    }


        /**
         * Verify if the token specified is a DEFAULT SPACE TOKENS used to identify the Storage Area
         */
        Boolean found =  isVOSAToken(token);

	    /**
	     * In case of DEFAULT SPACE TOKENspecified
	     * do nothing and create a simple silhouette for the file...
	     */

        if(found) {
        	//The minimum size between the one specifed and the default.
        	//makeSilhouetteForFile(file, ((sizePresumed.value()<defaultFileSize.value())?sizePresumed:defaultFileSize));
        	try {
				file.allotSpaceForFile(sizePresumed);
			} catch (ReservationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return;
        }

        /**
         * Token for Dynamic space reservation specified.
         * Go ahead in the old way, look into the space reservation catalog, ...
         */


        //Use of Reserve Space Manager
        StorageSpaceData spaceData = getSpaceByToken(token);

        if (spaceData == null) {
            throw new NamespaceException("No Storage Space stored with this token :" + token);
        }

        //      Check here if Space Reservation is expired
        if (spaceData.isExpired()) {
        	throw new ExpiredSpaceTokenException("Space Token Expired :" + token);
        }

        //Compute space to use
        TSizeInBytes totalSpaceSize = spaceData.getTotalSize();
        log.debug("Total Space : " + totalSpaceSize);
        TSizeInBytes guarSpaceSize = spaceData.getGuaranteedSize();
        log.debug("Guaranteed Space : " + guarSpaceSize);
        TSizeInBytes usedSpaceSize = spaceData.getActualUsedSpace();
        log.debug("Used Space : " + usedSpaceSize);
        TSizeInBytes unusedSpaceSize = spaceData.getUnusedSizes();
        log.debug("Unused Space : " + unusedSpaceSize);

        //The unusedSpaceSize should have the same size of Space File
        /**
         * @todo : compare Real size and Unused Space size.
         */

        //Verify that Size retrieved from DB are not null.
        if (unusedSpaceSize == null) {
            unusedSpaceSize = TSizeInBytes.makeEmpty();
        }
        if (usedSpaceSize == null) {
            usedSpaceSize = TSizeInBytes.makeEmpty();
        }

        if (! (sizePresumed.isEmpty())) {

            log.debug("Presumed file size : " + sizePresumed);
            //consumeSize measures the amount of Space that will be consumed.
            long consumeSize = -1;

            if (sizePresumed.value() > unusedSpaceSize.value()) {
                consumeSize = unusedSpaceSize.value();
            }
            else {
                consumeSize = sizePresumed.value(); //unusedSpaceSize.value() - sizePresumed.value();
            }

            //remainingSize measures the amount of Space free after reservation.
            long remainingSize = unusedSpaceSize.value() - consumeSize; //Greater or equal to zero for construction

            //Retrieve the Space File
            PFN pfn = spaceData.getSpaceFileName();

            long totalSize = spaceData.getTotalSize().value();
            log.debug("VFS: PFN name "+pfn);

            //Create Space StoRI
            StoRI spaceFile = retrieveSpaceFileByPFN(pfn, totalSize);

            if( (!(spaceFile.getLocalFile().exists())) || (spaceFile.getLocalFile().isDirectory()) ) {
            	log.error("Unable to get  the correct space file!spaceFile does not exsists or it is a directory.");
            	return;
            }

            /**
             * Splitting the Space File.
             * In this first version the original space file is truncated at
             * the original size minus the new ptp file size presumed, and a new space pre_allocation,
             * bound with the new ptp file, is done.
             * @todo In the final version, if the new size requested is greater then the half of the
             * original space file, the original spacefile is renamed to the desired ptp file name and then truncated to the
             * requested size. A new space pre_allocation is perfored and bound with the old original space file name.
             *
             */

            TSizeInBytes returnedSize = splitSpace(spaceFile, file, sizePresumed.value());

            spaceFile.setStoRIType(StoRIType.SPACE_BOUND);
            file.setSpace(spaceFile.getSpace());

            /**
             * Log ANY data HERE
             */

            //Update Storage Space to new values of size
            TSizeInBytes newUsedSpaceSize = TSizeInBytes.makeEmpty();
            TSizeInBytes newUnusedSpaceSize = TSizeInBytes.makeEmpty();
            try {
                newUsedSpaceSize = TSizeInBytes.make(totalSpaceSize.value() - remainingSize, SizeUnit.BYTES);
                newUnusedSpaceSize = TSizeInBytes.make(remainingSize,SizeUnit.BYTES);
            }
            catch (InvalidTSizeAttributesException ex) {
                log.error("Unable to create Used Space Size, so use EMPTY size ", ex);
            }

            //Update the space data with new value
            spaceData.setActualUsedSpace(newUsedSpaceSize);
            //spaceData.setUnusedSize(newUnusedSize);
            spaceData.setUnusedSize(newUnusedSpaceSize);
            //Update the catalogs
            storeSpaceByToken(spaceData);
        }

        else { //Case presumedSize is empty

            log.warn(" --- Here there is a call with a empty presumed size!--- ");
            useAllSpaceForFile(token, file);
        }

    }

    /*
     * This mehod should be Synchronized?
     * Yes...:
     * From the last internal discussion we had, we decide that use the entire available space for
     * a single PtP request is not the right behaviour.
     * The correct behaviour is that, if the presumed size is not specified as input parameter in the PtP request,
     * only a part of the available spacefile is used.
     * The size is the minimum between the default file size for the StoRM configuration file and the half size of the
     * available spaceFile.
     * TODO
     *
     */
    public synchronized void useAllSpaceForFile(TSpaceToken token, StoRI file) throws NamespaceException, ExpiredSpaceTokenException {
        //Check if StoRI is a file
        if (! (file.getStoRIType().equals(StoRIType.FILE))) {
            log.error("Unable to associate a Space to the StoRI with type: " + file.getStoRIType());
            throw new NamespaceException("Unable to associate a Space to the StoRI with type: " + file.getStoRIType());
        }


        //Get the default space size
        TSizeInBytes defaultFileSize = null;
        try {
        	defaultFileSize = TSizeInBytes.make(Configuration.getInstance().getFileDefaultSize(), SizeUnit.BYTES );
	    }
	    catch (it.grid.storm.srm.types.InvalidTSizeAttributesException e) {
	        log.debug("Invalid size created.");
	    }





        //Use of Reserve Space Manager
        StorageSpaceData spaceData = getSpaceByToken(token);

        if (spaceData == null) {
            throw new ExpiredSpaceTokenException("No Storage Space stored with this token :" + token);
        }

        /**
	     * First of all, Check if it's default or not
	     */

	    //if(spaceData.getSpaceType()== StorageSpaceData.DEFAULT) {
	    if(isVOSAToken(token)) {
	    	//ADD HERE THE LOGIC TO MANAGE DEFAULT SPACE RESERVATION
	        /**
		     * Check if a DEFAULT SPACE TOKEN is specified.
		     * IN that case do nothing and create a simple silhouette for the file...
		     *
		     *
		     * TOREMOVE. The space data will contains this information!!!
		     * i METADATA non venfgono agrgiornati, sara fatta una funzionalita' nella getspacemetadatacatalog che in
		     * caso di query sul defaulr space token vada a vedre la quota sul file system.
		     *
		     */
            //WARNING, This double check have to be removed, the firs should be fdone on teh space type
		    Boolean found = isVOSAToken(token);
		    if(found) {
		    	try {
					file.allotSpaceForFile(defaultFileSize);
				} catch (ReservationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	return;
	        }


	    } else {
	    	 /**
	         * Token for Dynamic space reservation specified.
	         * Go ahead in the old way, look into the space reservation catalog, ...
	         */


	        // Check here if Space Reservation is expired
	        //Add control if it is default?

	        if (spaceData.isExpired()) {
	        	throw new NamespaceException("Space Token Expired :" + token);
	        }


	        //Compute space to use
	        TSizeInBytes totalSpaceSize = spaceData.getTotalSize();
	        log.debug("Total Space : " + totalSpaceSize);
	        TSizeInBytes guarSpaceSize = spaceData.getGuaranteedSize();
	        log.debug("Guaranteed Space : " + guarSpaceSize);
	        TSizeInBytes usedSpaceSize = spaceData.getActualUsedSpace();
	        log.debug("Used Space : " + usedSpaceSize);
	        TSizeInBytes unusedSpaceSize = spaceData.getUnusedSizes();
	        log.debug("Unused Space : " + unusedSpaceSize);

		    if(defaultFileSize.value() <= (unusedSpaceSize.value()/2)) {
		    	log.debug("UseAllSpaceForFile size:"+defaultFileSize);
		    	useSpaceForFile(token, file, defaultFileSize );
		    } else {
		    	TSizeInBytes fileSizeToUse = null;
		    	try {
		    		fileSizeToUse = TSizeInBytes.make(unusedSpaceSize.value()/2, SizeUnit.BYTES );
			    }
			    catch (it.grid.storm.srm.types.InvalidTSizeAttributesException e) {
			        log.debug("Invalid size created.");
			    }
			    log.debug("UseAllSpaceForFile size:"+fileSizeToUse );
		    	useSpaceForFile(token, file, fileSizeToUse);
		    }
	/*
	        //Retrieve the Space File
	        PFN pfn = spaceData.getSpaceFileName();
	        StoRI spaceFile = retrieveSpaceFileByPFN(pfn, totalSpaceSize.value());
	        spaceFile.setStoRIType(StoRIType.SPACE_BOUND);

	        //Assign spaceFile to new File
	        //Rename Space to the File Name to the name of LocalFile in file
	        spaceFile.getLocalFile().renameTo(file.getAbsolutePath());

	        //file.setSpace(spaceFile.getSpace());


	        //Update Storage Space to new values of size
	        //-1 is not correct , change here and add check on SpaceData
	        TSizeInBytes newUnusedSpaceSize = TSizeInBytes.makeEmpty();
	        try {
	            newUnusedSpaceSize = TSizeInBytes.make(0, SizeUnit.BYTES);
	        }
	        catch (it.grid.storm.srm.types.InvalidTSizeAttributesException e) {
	            log.debug("Invalid size created.");
	        }

	        spaceData.setActualUsedSpace(totalSpaceSize);
	        spaceData.setUnusedSize(newUnusedSpaceSize);

	        //Update the catalogs
	        storeSpaceByToken(spaceData);
	*/



	    }


    }

    /**
      public void bindSpaceToFile(StoRI space, StoRI file) throws NamespaceException {
        file.setSpace(space.getSpace());
      }
     **/

    /****************************************************************
     * Methods used by StoRI to perform EXPLICIT SPACE RESERVATION
     *****************************************************************/

    public StoRI createSpace(String relativePath, long guaranteedSize, long totalSize) throws NamespaceException {
        StoRIType type = StoRIType.SPACE;
        /*
         * TODO Mapping rule should be choosen from the appropriate app-rule presents in the namespace.xml file...
         */
        StoRI stori = new StoRIImpl(this, (MappingRule) mappingRules.get(0), relativePath, type);

        //Retrieve the instance of the right Space System
        SpaceSystem spaceSystem = getSpaceSystemDriverInstance();

        TSizeInBytes guarSize = TSizeInBytes.makeEmpty();
        try {
            guarSize = TSizeInBytes.make(guaranteedSize, SizeUnit.BYTES);
        }
        catch (InvalidTSizeAttributesException ex1) {
            log.error("Unable to create Guaranteed Size, so use EMPTY size ", ex1);
        }

        TSizeInBytes totSize = TSizeInBytes.makeEmpty();
        try {
            totSize = TSizeInBytes.make(totalSize, SizeUnit.BYTES);
        }

        catch (InvalidTSizeAttributesException ex2) {
            log.error("Unable to create Total Size, so use EMPTY size", ex2);
        }

        Space space = createSpace(guarSize, totSize, stori.getLocalFile(), spaceSystem);

        stori.setSpace(space);
        return stori;
    }

    public StoRI createSpace(String relativePath, long totalsize) throws NamespaceException {
        StoRI stori = createSpace(relativePath, totalsize, totalsize);
        return stori;
    }

    /**
     * This method is used to split the specified spaceFile to the desired PtP file.
     * The operations performed depends on the input parameters.
     * If the desired new size is minor then the half of the total reserved space size,
     * the original space file is truncated to new size : (original size - new PtP file presumed size),
     * then a new  space_preallocation, of the new PtP file presumed size,  is bound to the requested file.
     *
     * If the presumed size is greater then the half fo the global space available, the original space file is renamed to
     * the new PtP file and truncated to the presumed size.
     * A new space_preallocation is done to recreate the remaining original space file
     *
     * @param spaceOrig StoRI bounds to the original space file.
     * @param file StoRI bounds to the desired new PtP file.
     * @param long new PtP file size presumed.
     * @returns new Size
     */

    public TSizeInBytes splitSpace(StoRI spaceOrig, StoRI file, long sizePresumed) throws NamespaceException {

        //Update Storage Space to new values of size
        TSizeInBytes newSize = TSizeInBytes.makeEmpty();

        //Save the name of the current Space File
        String spacePFN = spaceOrig.getAbsolutePath();
        log.debug("VFS Split: spaceFileName:" + spacePFN);
        String relativeSpacePFN = NamespaceUtil.extractRelativePath(this.getRootPath(), spacePFN);
        /**
         * extractRelativePath seems not working in this case! WHY?
         * @todo Because the mapping rule choosen is always the same, for all StFNRoot...BUG to FIX..
         *
         */
        log.debug("Looking for root:" + this.getRootPath());
        int index = spacePFN.indexOf(this.getRootPath());
        boolean failure = false;
        if (index != -1) {
            relativeSpacePFN = spacePFN.substring(index);
        }
        else {
            failure = true;
            log.debug("Root NotFound!");
        }

        log.debug("VFS Split: relativeSpacePFN:" + relativeSpacePFN);

        if (failure) {
            log.warn("SpacePFN does not refer to this VFS root! Something goes wrong in app-rule?");
            try {
                newSize = TSizeInBytes.make(sizePresumed, SizeUnit.BYTES);
                file = createSpace(NamespaceUtil.extractRelativePath(this.getRootPath(), file.getAbsolutePath()),
                                   sizePresumed);
                file.getSpace().allot();
            }
            catch (InvalidTSizeAttributesException ex) {
                log.error("Unable to create UNUsed Space Size, so use EMPTY size ", ex);
            }
            catch (it.grid.storm.filesystem.ReservationException e2) {
                log.error("Unable to create space into File System");
            }

        }
        else {

            //Compute the real size of Space File
            long realSize = spaceOrig.getLocalFile().getSize();


            /**
             * The next steps depends on the input parameters.
             * Case (1) : new PtP file size minor than the half of the available space file.
             * In this case the spaceFile is truncated, and a new file is created with the desired amount of preallocated blocks.
             * Case(2) : new PtP file size greater than the half of the available space file.
             * The spaceFile is renamed to the new PtP file, truncated to the presumed size and a new preallocation is done bound
             * to the original space file name.
             *
             */

            if(sizePresumed <= (realSize/2)) {
	           	log.debug("SplitSpace Case (1)");

	           	//Truncate
	           	log.debug("SplitSpace: "+spaceOrig.getAbsolutePath()+" truncating file to size:"+(realSize-sizePresumed));
	           	spaceOrig.getSpace().getSpaceFile().truncateFile((realSize-sizePresumed));

	            //Allocate space for file
	            try {
	                newSize = TSizeInBytes.make(sizePresumed, SizeUnit.BYTES);
	                file = createSpace(NamespaceUtil.extractRelativePath(this.getRootPath(), file.getAbsolutePath()),
	                                   sizePresumed);
	                file.getSpace().allot();
	            }
	            catch (InvalidTSizeAttributesException ex) {
	                log.error("Unable to create UNUsed Space Size, so use EMPTY size ", ex);
	            }
	            catch (it.grid.storm.filesystem.ReservationException e2) {
	                log.error("Unable to create space into File System");
	            }

            } else {
            	log.debug("SplitSpace Case (2)");


                //Truncate the orig space to the new file size, and rename it

            	//Truncate
             	spaceOrig.getSpace().getSpaceFile().truncateFile((sizePresumed));

             	//Rename Space to the File Name to the name of LocalFile in file
                spaceOrig.getLocalFile().renameTo(file.getAbsolutePath());
                log.debug("VFS  : fileabspath:" + file.getAbsolutePath());


                //Allocate space original
             	long remainingSize = realSize - sizePresumed;

                try {
                    newSize = TSizeInBytes.make(remainingSize, SizeUnit.BYTES);
                    //Create a new Space file with the old name and with the size computed.
                    spaceOrig = createSpace(NamespaceUtil.extractRelativePath(this.getRootPath(), spacePFN), newSize.value());
                    //Create the new SpaceFile into the file system
                    spaceOrig.getSpace().allot();
                }
                catch (InvalidTSizeAttributesException ex) {
                    log.error("Unable to create UNUsed Space Size, so use EMPTY size ", ex);
                }
                catch (it.grid.storm.filesystem.ReservationException e2) {
                    log.error("Unable to create space into File System");
                }

            }

        } // failure else

        return newSize;
    }

    /**************************************************
     * Methods used by Space Reservation Manager
     *************************************************/

    public StoRI createSpace(long guarSize, long totalSize) throws NamespaceException {
        //retrieve SPACE FILE NAME
        String relativePath = makeSpaceFilePath();
        StoRI stori = createSpace(relativePath, guarSize, totalSize);
        return stori;
    }

    public StoRI createSpace(long totalSize) throws NamespaceException {
        //retrieve SPACE FILE NAME
        String relativePath = makeSpaceFilePath();
        //retrieve DEFAULT GUARANTEED size
        TSizeInBytes guarSize = defValue.getDefaultGuaranteedSpaceSize();
        StoRI stori = createSpace(relativePath, guarSize.value(), totalSize);
        return stori;

    }

    public StoRI createSpace() throws NamespaceException {

        //retrieve SPACE FILE NAME
        String relativePath = makeSpaceFilePath();

        //DEFAULT VALUE MUST NOT BE HANDLED HERE!!!!

        //retrieve DEFAULT GUARANTEED size
        TSizeInBytes guarSize = defValue.getDefaultGuaranteedSpaceSize();
        //retrieve DEFAULT TOTAL size
        TSizeInBytes totalSize = defValue.getDefaultTotalSpaceSize();
        StoRI stori = createSpace(relativePath, guarSize.value(), totalSize.value());
        return stori;
    }

    public StoRI createDefaultStoRI() throws NamespaceException {
        /**
         * @todo: When is used this method?
         */
        return null;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        String sep = System.getProperty("line.separator");
        sb.append(sep + "++++++++++++++++++++++++++++++++" + sep);
        sb.append(" VFS Name         : '" + this.aliasName + "'" + sep);
        sb.append(" VFS root         : '" + this.rootPath + "'" + sep);
        sb.append(" VFS FS driver    : '" + this.fsDriver.getName() + "'" + sep);
        sb.append(" VFS Space driver : '" + this.spaceSystemDriver.getName() + "'" + sep);
        sb.append(" -- DEFAULT VALUES --" + sep);
        sb.append(this.defValue);
        sb.append(" -- CAPABILITY --" + sep);
        sb.append(this.capabilities + "" + sep);
        sb.append("++++++++++++++++++++++++++++++++" + sep);
        return sb.toString();
    }

    /****************************************************************************
     *              UTILITY METHODS
     ****************************************************************************/

    /*****************************************
     * Methods used for manage SPACE
     *****************************************/

    private String makeSpaceFilePath() throws NamespaceException {
        String result = "";
        String spacePath = NamingConst.ROOT_PATH;
        GUID guid = new GUID();
        String spaceFileName = "vfs" + getAliasName() + "_" + guid + ".space";
        result = spacePath + NamingConst.SEPARATOR + spaceFileName;
        return result;
    }

    /**
      private SpaceSystem retrieveSpaceSystem() throws NamespaceException {
        SpaceSystem ss = null;
        try {
          ss = (SpaceSystem) (this.getSpaceSystemDriver()).newInstance();
        }
        catch (NamespaceException ex) {
          log.error("Error while retrieving Space System Driver for VFS :" + this.aliasName, ex);
          throw new NamespaceException("Error while retrieving Space System Driver for VFS :" + this.aliasName, ex);
        }
        catch (IllegalAccessException ex) {
          log.error("Error while accessing Space System driver for VFS :" + this.aliasName, ex);
          throw new NamespaceException("Error while accessing Space System driver for VFS :" + this.aliasName, ex);
        }
        catch (InstantiationException ex) {
          log.error("Error while instancianging Space System driver for VFS :" + this.aliasName, ex);
     throw new NamespaceException("Error while instancianging Space System driver for VFS :" + this.aliasName, ex);
        }
        return ss;
      }
     **/

    private Space createSpace(TSizeInBytes guarSize,
                              TSizeInBytes totalSize,
                              LocalFile file,
                              SpaceSystem spaceSystem) throws NamespaceException {
        Space space = null;
        try {
            space = new Space(guarSize, totalSize, file, spaceSystem);
        }
        catch (InvalidSpaceAttributesException ex3) {
            log.error("Error while retrieving Space System Driver for VFS ", ex3);
            throw new NamespaceException("Error while retrieving Space System Driver for VFS ", ex3);
        }
        return space;
    }

    public StorageSpaceData getSpaceByToken(TSpaceToken token) throws NamespaceException {

        //Retrieve Storage Space from Persistence
        ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();
        StorageSpaceData spaceData = catalog.getStorageSpace(token);
        return spaceData;
    }

    public StorageSpaceData getSpaceByAlias(String desc) throws NamespaceException {

        //Retrieve Storage Space from Persistence
        ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();
        StorageSpaceData spaceData = catalog.getStorageSpaceByAlias(desc);
        return spaceData;
    }


    public void storeSpaceByToken(StorageSpaceData spaceData) throws NamespaceException {

        //Retrieve Storage Space from Persistence
        ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();
        catalog.setFreeSize(spaceData);
    }

    public StoRI retrieveSpaceFileByPFN(PFN pfn, long totalSize) throws NamespaceException {
        NamespaceInterface namespace = NamespaceDirector.getNamespace();
        StoRI stori = namespace.resolveStoRIbyPFN(pfn);
        stori.setStoRIType(StoRIType.SPACE);
        //Create the Space istance
        log.debug("VFS: retrieveSpace, relative "+stori.getRelativePath() +"-"+stori.toString());
        StoRI space = createSpace(stori.getRelativeStFN(), totalSize);
        //Assign this istance to StoRI created
        stori.setSpace(space.getSpace());
        return stori;
    }

    public long getCreationTime() {
        return creationTime;
    }

  /******************************************
     *           VERSION 1.4                  *
  *******************************************/

  public Balancer getProtocolBalancer(TSpaceToken token) throws NamespaceException {
        /** @todo IMPLEMENT */
    return null;
  }

  public Balancer getProtocolBalancer(TSpaceToken token, Protocol protocol) throws NamespaceException {
    /** @todo IMPLEMENT */
    return null;
  }

}
