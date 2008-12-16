/**
 * This class represent File System abstraction.
 * This class provide a set of functionality thath use underlaying file system wrapper .
 *  *
 */

package it.grid.storm.wrapper;

import java.io.*;

import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.*;
import it.grid.storm.common.types.*;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.config.Configuration;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.catalogs.InvalidSpaceDataAttributesException;

import org.apache.log4j.Logger;
import java.io.File;
import it.grid.storm.griduser.CannotMapUserException;

public class FileSystem {
    /**
     * Logger.
     * This Logger it's used to log information.
     */

    private static final Logger log = Logger.getLogger("wrapper");
    private Configuration config;
    private Passwd parser;
    /**
     * Singleton.
     */
    private static FileSystem ref;

    /**
     * Empty Constructor;
     */
    private FileSystem()
    {
	log.debug("<FileSystem>: Constructor");
	config = Configuration.getInstance();
	parser = new Passwd();
	parser.digestPasswd();
    }


    /**
     * Meothd that provide access to single instance of
     * FileSystem object.
     */
    public static FileSystem getInstance()
    {
	//Create unique instance of FIleSystem if it
	//does not exists.
	if (ref==null) {
	    ref = new FileSystem();
	}

	return ref;
    }


    /**
     * This method reuturn True if file/directory exists,
     * False otherwise.
     * @param PFN physycal file name containing directory/file path.
     * @return boolean value
     */

    public boolean exists(PFN pfn)
    {
	log.debug("<FileSystem>:exists start...");
	File file = new File(pfn.getValue());
	return file.exists();
    }


    /**
     * This method reuturn True if parent directory exists,
     * False otherwise.
     * @param PFN physycal file name of directory.
     * @return boolean value
     */

    public boolean existsParent(PFN pfn)
    {
	boolean res = false;
	log.debug("<FileSystem>:existsParent start...");
	//Verify if parent directory, if is not null, exists into file system
	File file = new File(pfn.getValue());
	File parent = file.getParentFile();
	res = parent.exists()||parent==null;
	return res;
    }


    /**
     * This method reuturn True if the PFN specified is a Directory,
     * False otherwise.
     * @param PFN physycal file name containing directory path.
     * @return boolean value
     */

    public boolean isDirectory(PFN pfn)
    {
	log.debug("<FileSystem>:isDirectory start...");
	File file = new File(pfn.getValue());
	return file.isDirectory();
    }


    /**
     * This method reuturn True if the PFN specified is a File,
     * False otherwise.
     * @param PFN physycal file name containing directory path.
     * @return boolean value
     */

    public boolean isFile(PFN pfn)
    {
	log.debug("<FileSystem>:isDirectory start...");
	File file = new File(pfn.getValue());
	return file.isFile();
    }


    /**
     * This method reuturn True if the PFN specified is an Empty Directory,
     * False otherwise.
     * @param PFN physycal file name containing directory path.
     * @return boolean value
     */

    public boolean isEmptyDir(PFN pfn)
    {
	log.debug("<FileSystem>:isEmptyDir start...");
	File dir = new File(pfn.getValue());
	if (dir.isDirectory()) {
	    //Verfy if Directory is empty
	    //dir.list return String[] that contains all file and dir, "." and ".." included (TO VERIFY)
	    if (dir.list().length==0) {
		return true;
	    }
	    else {
		return false;
	    }
	}
	else {
	    return false;
	}
    }


    /**
     * Low Level List Directory, only for deleteAll Directory Manager function.
     */

    public String[] list(PFN pfn)
    {
	log.debug("<FileSystem>:List start...");
	File dir = new File(pfn.getValue());

	if (dir.isDirectory()) {
	    return dir.list();
	}
	else {
	    return null;
	}

    }


    /**
     * This method reuturn a String[] thath represent file contained in specified directory.
     * The direcory is specified by PFN.
     * @param PFN physycal file name containing directory path.
     * @return String[] containing one entry for each file in direcory
     */

    public String[] ls(PFN pfn) throws InvalidLSRequestException
    {
	log.debug("<FileSystem>:ls start...");

	String[] res = null;
	File dir = new File(pfn.getValue());
	//check sui permessi???

	if (!dir.isDirectory()) {
	    throw new InvalidLSRequestException(pfn);
	}
	res = dir.list();
	return res;
    }


    /**
     * This method remove specified PFN from storage system.
     * Return TRUE only if file or empty directory are corretly removed ,FALSE otherwise.
     */
    public boolean rm(PFN fileToRemove)
    {
	//Path must refere to single file or to _empty_ directory
	File file = new File(fileToRemove.getValue());
	return file.delete();
    }


    /**
     * Method used to recursivly remove all file and directory.
     * MayBe WRONG place here. Should reside at Directoy manager Level.
     * RmDir function remove , if recursiveFlag is specified, all file and dir
     * contained in the dir specified.
     */
    /*	public void  deleteAll(PFN pfn,GridUserInterface guser) {
       deleteAll(pfn.getValue(),guser);
     }

     public void  deleteAll(String filename,GridUserInterface guser) {
      File fileOrDir = new File(filename);
      PFN pfn = null;
      try {
       pfn = PFN.make(filename);
      } catch (InvalidPFNAttributeException e) {
       log.debug("Exception: "+e);
      }

      //If is a normal FIle or an empty Directory, Remove it!
      if(fileOrDir.isFile()||(fileOrDir.isDirectory()&&(fileOrDir.list().length==0))) {
       //FIXME
       //Remove FROM DATABASE entry!!!!!!
       //!!!!!!!!!!!!!!!!!!!!
       log.debug("Fs:RemoveAll:Case FILE OR EMPTY DIR: "+fileOrDir);
       if(canRemoveFile(pfn,guser))
	fileOrDir.delete();

      } else if (fileOrDir.isDirectory()) {
       log.debug("Fs:RemoveAll:Case DIR: "+fileOrDir);
       //Not Empty Directory, recursive case
       String[] arrayOfEntry = fileOrDir.list();
       if(arrayOfEntry != null) {
	for(int i=0;i<arrayOfEntry.length;i++) {
	 log.debug("Entry "+i+"="+arrayOfEntry[i]);
	 if(filename.endsWith("/"))
	  ref.deleteAll(filename+arrayOfEntry[i],guser);
	 else
	  ref.deleteAll(filename+"/"+arrayOfEntry[i],guser);

	}
       } else {
	log.warn("Fs:deleteAll "+fileOrDir+"Empty !!!Not possible!");
       }
      }
     }
     */

    /**
     * This method create a newd directory specified by PFN .
     * Return TRUE only if directory are corretly created ,FALSE otherwise.
     */
    public boolean mkdir(PFN path)
    {
	File dir = new File(path.getValue());
	return dir.mkdir();
    }


    /**
     * Make Placeholder for SrmPrepareToPut functionality.
     * Create an empty file, a placeholder, on wich ACL can be enforced.
     */
    public void makePlaceHolder(PFN newFilePFN) throws InvalidFileException
    {
	File placeHolder = new File(newFilePFN.getValue());
	//Crete new empty File
	try {
	    //IF OverWrite is ALways we can delete file
	    //FIXME
	    //if(placeHolder.exists())
	    //	placeHolder.delete();

	    placeHolder.createNewFile();
	}
	catch (IOException e) {
	    log.warn("<FileSystem> makePlaceHolder trow Exception!");
	    throw new InvalidFileException(newFilePFN.getValue());
	}

    }


    /**
     * This method is used by SpaceReservationManager to reseve space for file.
     *
     */
    public void reserveSpaceForFile(PFN path, TSizeInBytes size) throws InvalidPathException,
	    InvalidPermissionOnFileException, InvalidSpaceResExecException
    {
	//GENERERIC RESERVATION FAILURE
	int ret;

	log.debug("<FileSystem>:reserveSpaceForFile for filename: "+path+" size: "+size.value());

	SpaceReservationWrapper spaceWrap = new SpaceReservationWrapper();

	ret = spaceWrap.reserveSpace(path.getValue(), size.value());

	if (ret==-1) {
	    log.warn("<FileSystem>:reserveSpaceForFile Throw InvPat EX");
	    throw new InvalidPathException(path.getValue());
	}
	if (ret==-2) {
	    log.warn("<FileSystem>:reserveSpaceForFile Throw InvPerm EX");
	    throw new InvalidPermissionOnFileException(path.getValue());
	}
	if (ret==-3) {
	    log.warn("<FileSystem>:reserveSpaceForFile Throw InvExec EX");
	    throw new InvalidSpaceResExecException(path.getValue());
	}
    }


    public void compactSpaceInFile(String pathToFile) throws InvalidPathException
    {
	int ret;
	CompactSpaceWrapper compWrap = new CompactSpaceWrapper();
	ret = compWrap.compactSpace(pathToFile);

	if (ret==-1) {
	    throw new InvalidPathException(pathToFile);
	}
    }


    public void addReadACL(PFN fileName, GridUserInterface guser) throws InvalidFileException,
	    InvalidExecCommandException, InvalidAclFormatException,
      CannotMapUserException {
	log.debug("<FileSystem>:addReadACL start...");
        String userName = guser.getLocalUser().getLocalUserName();
	this.addAcl(fileName, userName, ACL.R.toString());
    }


    public void addWriteACL(PFN fileName, GridUserInterface guser) throws InvalidFileException,
	    InvalidExecCommandException, InvalidAclFormatException,
      CannotMapUserException {
	log.debug("<FileSystem>:addWriteACL start...");
        String userName = guser.getLocalUser().getLocalUserName();
	this.addAcl(fileName, userName, ACL.W.toString());
    }


    public void addExecuteACL(PFN fileName, GridUserInterface guser) throws InvalidFileException,
	    InvalidExecCommandException, InvalidAclFormatException,
      CannotMapUserException {

	log.debug("<FileSystem>:addExecuteACL start...");
        String userName = guser.getLocalUser().getLocalUserName();
	this.addAcl(fileName, userName, ACL.X.toString());
    }


    public void addReadWriteACL(PFN fileName, GridUserInterface guser) throws InvalidFileException,
	    InvalidExecCommandException, InvalidAclFormatException,
      CannotMapUserException {
	log.debug("<FileSystem>:addReadWriteACL start...");
        String userName = guser.getLocalUser().getLocalUserName();
	this.addAcl(fileName, userName, ACL.RW.toString());
    }


    public void addReadWriteExecACL(PFN fileName, GridUserInterface guser) throws InvalidFileException,
	    InvalidExecCommandException, InvalidAclFormatException,
      CannotMapUserException {
	log.debug("<FileSystem>:addReadWriteExecACL start...");
        String userName = guser.getLocalUser().getLocalUserName();
	this.addAcl(fileName, userName, ACL.RWX.toString());
    }


    private void addAcl(PFN fileName, String user, String acl) throws InvalidFileException, InvalidExecCommandException,
	    InvalidAclFormatException
    {

	int ret = 0;
	String tempDir = null;

	log.debug("<FileSystem>:addAcl start...USER:"+user);

	AclWrapper aclWrap = new AclWrapper();

	/**
	 * TemporaryDirectory it's used to contatin a temporay File where old acl and new acl are managed.
	 */

	tempDir = config.getTempDir();
	//log.debug("<FileSystem>:addAcl TempDir:"+Thread.currentThread().getName());
	log.debug("<FileSystem>:addAcl TempDir:"+tempDir);
	//ret = aclWrap.addAcl(Thread.currentThread().getName(),fileName.getValue(),user,acl);
	if (user!=null) {
	    ret = aclWrap.addAcl(tempDir, fileName.getValue(), user, acl);
	}
	else {
	    ret = -1;
	}

	if (ret==-1) {
	    log.warn("<FileSystem>:addAcl Throws InvPath Ex");
	    throw new InvalidFileException(fileName.getValue());
	}
	if (ret==-2) {
	    log.warn("<FileSystem>:addAcl Throws InvExec Ex");
	    throw new InvalidExecCommandException(fileName.getValue());
	}
	if (ret==-3) {
	    log.warn("<FileSystem>:addAcl Throws InvACL Ex");
	    throw new InvalidAclFormatException(acl);
	}
    }


    public void removeAllUserAcls(PFN path, GridUserInterface guser) throws InvalidFileException,
	    InvalidExecCommandException, CannotMapUserException {

	int ret = 0;
	String tempDir;

	log.debug("<FileSystem>:removeAllUserAcls start...");

	RemoveAclWrapper removeAclWrap = new RemoveAclWrapper();

	/**
	 * TemporaryDirectory it's used to contatin a temporay File where old acl and new acl are managed.
	 */
	tempDir = config.getTempDir();

	//removeAclWrap.removeAcl(Thread.currentThread().getName(), path.getValue(), user.getLocalUserName());

        String userName = guser.getLocalUser().getLocalUserName();
	removeAclWrap.removeAcl(tempDir, path.getValue(), userName);

	if (ret==-1) {
	    log.warn("<FileSistem>:removeAllUserAcls throws InvFile EX");
	    throw new InvalidFileException(path.getValue());
	}
	if (ret==-2) {
	    log.warn("<FileSistem>:removeAllUserAcls throws InvExec EX");
	    throw new InvalidExecCommandException(path.getValue());
	}

    }


    public void truncateFile(String pathToFile, TSizeInBytes Size) throws InvalidFileException
    {

	int ret;
	log.debug("<FileSystem>:truncateFile start...");

	TruncateWrapper truncateWrap = new TruncateWrapper();
	ret = truncateWrap.truncateFile(pathToFile, Size.value());
	if (ret==-1) {
	    log.warn("<FileSistem>:removeAllUserAcls throws InvFile EX");
	    throw new InvalidFileException(pathToFile);
	}
    }


    /**
     * Can the user remove the File specified in PFN
     */
    public boolean canRemoveFile(PFN pfn, GridUserInterface guser)
    {
	return true;
	/*
	String aclForUser = null;
	String[] tmp = new String[3];
	try {
	    aclForUser = this.getAclForUser(pfn, guser);
	}
	catch (InvalidFileException e) {
	    log.debug("Exception: "+e);
	    return false;
	}

	if (aclForUser!=null) {
	    tmp = aclForUser.split(":");
		//Okkio produce BUG 101!!!! INTANTO COMMENTO TUTTO
	    log.debug("fs:User ACL:: "+aclForUser+" PERMISSION: "+tmp[2]);
	}

	if ( (aclForUser!=null)&& (tmp[2].indexOf("w")!=-1)) {
	    log.debug("fs:User CAN Remove File: "+tmp[2]);
	    return true;
	}
	else {
	    return false;
	}
	*/

    }


    public String getAclForUser(PFN pfn, GridUserInterface guser) throws InvalidFileException, CannotMapUserException {
	String acl_for_user = null;
	String complete_acl = null;
	String[] s_array;
	boolean OWNER = false;
	Integer uid = null;
	String localUserName = guser.getLocalUser().getLocalUserName();
	//TOREMOVE IT
	//PATH FOR OLD NATIVE LIBRARY!!!
	//if(localuserName.indexOf("100")!=-1)
	//	localuserName = localuserName.replaceFirst("100","stormdev");

	//Patch for get username from uid
	try {
	    uid = new Integer(localUserName);
	    localUserName = parser.getNameByUID(uid.intValue());
	}
	catch (NumberFormatException e) {
	    log.debug("Not Integer UID found.");
	}

	log.debug("<FileSystem>:getAclForUser start...");

	complete_acl = this.getAcl(pfn);

	log.debug("<FileSystem>:getAclForUser result: "+complete_acl);
	log.debug("<FS:>getLocalUserName: "+localUserName);

	if (complete_acl!=null) {
	    s_array = complete_acl.split("\n");
	    for (int i = 0; i<s_array.length; i++) {

		if ( (s_array[i].indexOf(localUserName)!=-1)&& (s_array[i].indexOf("owner")!=-1)) {
		    OWNER = true;
		    acl_for_user = s_array[i];
		}
		if (OWNER) {
		    if (s_array[i].indexOf("user::")!=-1) {
			acl_for_user = acl_for_user+":"+s_array[i];
		    }
		}

		if (s_array[i].indexOf(localUserName)!=-1) {
		    acl_for_user = s_array[i];
		}

	    }

	}
	else {
	    log.warn("<FileSystem>:getAclForUser reeturn NULL Acl!");
	}

	return acl_for_user;

    }


    public String getAcl(PFN pfn) throws InvalidFileException
    {
	String acl = null;
	String tempDir;
	/**
	 * TemporaryDirectory it's used to contatin a temporay File where old acl and new acl are managed.
	 */
	tempDir = config.getTempDir();

	GetAclWrapper aclWrap = new GetAclWrapper();

	log.debug("<FileSystem>:getAcl start... for file:"+pfn.getValue());

	//acl = aclWrap.getAcl(Thread.currentThread().getName(), pfn.getValue());
	acl = aclWrap.getAcl(tempDir, pfn.getValue());

	if (acl==null) {
	    log.warn("<FileSystem>:getAcl throws InvFile EX");
	    throw new InvalidFileException(pfn.getValue());
	}

	return acl;

    }


    /**
     * This method retrive free space on storage system for a storage area specified
     * The rootDir indicate wich area (i case of multiple partition...) have to be analyzed.
     *
     */

    public TSizeInBytes getFreeSpace(PFN rootDir) throws InvalidTSizeAttributesException
    {

	TSizeInBytes size = null;
	log.debug("<FileSystem>:getFreeSpace start...");

	StatWrapper statWrapper = new StatWrapper();
	System.out.println("DIR:_"+rootDir.getValue()+"_");
	//Specifie gpfs root source
	long s_size = statWrapper.statfs(rootDir.getValue());

	log.debug("<FileSystem>:getFreeSpace long size returned: "+s_size);

	size = TSizeInBytes.make(s_size, SizeUnit.BYTES);

	log.debug("<FileSystem>:getFreeSpace size: "+size+" rootDir: "+rootDir);

	log.debug("PFN Root Dir = "+rootDir.getValue());

	try {
	    size = TSizeInBytes.make(statWrapper.statfs(rootDir.getValue()), SizeUnit.BYTES);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    log.error(this, e);
	}
	//lsize  = statWrapper.statfs("/gpfs");
	//System.out.println("FILESYSTEM: Size: "+size);
	if (size==null) {
	    log.warn("<FileSystem>:getFreeSpace return NULL size!");
	}

	return size;

    }


    /**
     * This method retreive size of file specified.
     *
     */

    public TSizeInBytes getSize(PFN pfnFile) throws InvalidFileException, InvalidPathException, InvalidGetSizeRequestException
    {

	File fileTest = new File(pfnFile.getValue());
	TSizeInBytes size = null;

	log.debug("<FileSystem>:getSize start...");

	//Test if File specified exist, otherwisw throws Exception
	if (!fileTest.exists()) {
	    log.warn("<FileSystem>:getSize INVALID File Path specified !");
	    throw new InvalidFileException(pfnFile.getValue());
	}
	else if ( (fileTest.getParentFile()==null)|| (!fileTest.getParentFile().exists())) {
	    log.warn("<FileSystem>:getSize INVALID Parent Directory for file specified");
	    throw new InvalidPathException(pfnFile.getValue());
	}
	else {

	    //Stat Native Library
	    StatWrapper statWrapper = new StatWrapper();

	    try {
		//Call native method
		size = TSizeInBytes.make(statWrapper.stat(pfnFile.getValue()), SizeUnit.BYTES);

		//Test if Size return is null
		//FIXME This test it's really necessary?
		if (size==null) {
		    log.warn("<FileSystem>:getSize return NULL size!");
		    throw new InvalidGetSizeRequestException(pfnFile.getValue());

		}

		//Return valid Size
		return size;

	    }
	    catch (InvalidTSizeAttributesException e) {
		log.warn("<FileSystem>getSize Invalid TSizeInBytes Creation: "+e);
		throw new InvalidGetSizeRequestException(pfnFile.getValue());
	    }

	}

    }

}
