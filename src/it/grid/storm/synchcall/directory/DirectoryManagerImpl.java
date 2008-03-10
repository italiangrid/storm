package it.grid.storm.synchcall.directory;

import org.apache.log4j.Logger;
import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.srm.types.TReturnStatus;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public class DirectoryManagerImpl implements DirectoryManager
{

    static final Logger   log           = Logger.getLogger("synch");
    NamespaceInterface    namespace;
    static Configuration  config        = Configuration.getInstance();

    private Functionality functionality = null;

    private LSExecutor    ls            = null;
    private MkdirExecutor mkdir         = null;
    private RmExecutor    rm            = null;
    private RmdirExecutor rmdir         = null;
    private MvExecutor    mv            = null;

    public DirectoryManagerImpl(Functionality func) {
        namespace = NamespaceDirector.getNamespace();
        functionality = func;
        switch (func.getFuncId()) {
        case DirectoryManager.LS_Id:
            ls = new LSExecutor();
            break;
        case DirectoryManager.MKDIR_Id:
            mkdir = new MkdirExecutor();
            break;
        case DirectoryManager.RM_Id:
            rm = new RmExecutor();
            break;
        case DirectoryManager.RMDIR_Id:
            rmdir = new RmdirExecutor();
            break;
        case DirectoryManager.MV_Id:
            mv = new MvExecutor();
            break;
        default:
            log.error("Unable to instanciate DirectoryManager. Please select the supported functionality.");
        }
    }

    /**
     * Method that provide ls functionality.
     *
     * @param inputData Contain information about data OF SrmLS request.
     * @return LSOutputData that contain all SRM return parameter.
     * @todo Implement this it.grid.storm.synchcall.directory.DirectoryManager
     *   method
     */
    public LSOutputData ls(LSInputData inputData)
    {
        if (ls != null) {
            return ls.doit(inputData);
        } else {
            log.error("Directory Manager instanciate for " + functionality.toString());
            return null;
        }
    }

    /**
     * Method that provide SrmMkdir functionality.
     *
     * @param inputData Contains information about input data for Mkdir request.
     * @return TReturnStatus Contains output data
     * @todo Implement this it.grid.storm.synchcall.directory.DirectoryManager
     *   method
     */
    public TReturnStatus mkdir(MkdirInputData inputData)
    {
        if (mkdir != null) {
            return mkdir.doit(inputData);
        } else {
            log.error("Directoty Manager instanciate for " + functionality.toString());
            return null;
        }

    }

    /**
     * Method that provide SrmRm functionality.
     *
     * @param inputData Contains information about input data for rm request.
     * @return RmOutputData Contains output data
     * @todo Implement this it.grid.storm.synchcall.directory.DirectoryManager
     *   method
     */
    public RmOutputData rm(RmInputData inputData)
    {
        if (rm != null) {
            return rm.doit(inputData);
        } else {
            log.error("Directoty Manager instanciate for " + functionality.toString());
            return null;
        }

    }

    /**
     * Method that provide SrmRmdir functionality.
     *
     * @param inputData Contains information about input data for Rmdir request.
     * @return TReturnStatus Contains output data
     * @todo Implement this it.grid.storm.synchcall.directory.DirectoryManager
     *   method
     */
    public TReturnStatus rmdir(RmdirInputData inputData)
    {
        if (rmdir != null) {
            return rmdir.doit(inputData);
        } else {
            log.error("Directoty Manager instanciate for " + functionality.toString());
            return null;
        }

    }

    /**
     * Method that provide SrmMv functionality.
     *
     * @param inputData Contains information about input data for Rmdir request.
     * @return outputData Contains output data
     * @todo 
     */
    public MvOutputData mv(MvInputData inputData)
    {
        if (mv != null) {
            return mv.doit(inputData);
        } else {
            log.error("Directoty Manager instanciate for " + functionality.toString());
            return null;
        }
    }
}
