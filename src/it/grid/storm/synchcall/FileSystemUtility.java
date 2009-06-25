package it.grid.storm.synchcall;

import it.grid.storm.filesystem.Filesystem;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.swig.genericfs;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;

import org.slf4j.Logger;


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
public class FileSystemUtility {

    private static Logger log = NamespaceDirector.getLogger();

    public static LocalFile getLocalFileByAbsolutePath(String absolutePath) throws NamespaceException {
        LocalFile file = null;
        VirtualFSInterface vfs = null;
        genericfs fsDriver = null;
        Filesystem fs = null;
        try {
            vfs = NamespaceDirector.getNamespace().resolveVFSbyAbsolutePath(absolutePath);
        }
        catch (NamespaceException ex) {
            log.error("Unable to retrieve VFS by Absolute Path", ex);
        }
        if (vfs == null) {
            throw new NamespaceException("No VFS found in StoRM for this file :'" + absolutePath + "'");
        }

        try {
            fsDriver = (genericfs) (vfs.getFSDriver()).newInstance();
            fs = new Filesystem(fsDriver);
            file = new LocalFile(absolutePath, fs);
        }
        catch (NamespaceException ex1) {
            log.error("Error while retrieving FS driver", ex1);
        }
        catch (IllegalAccessException ex1) {
            log.error("Error while using reflection in FS Driver", ex1);
        }
        catch (InstantiationException ex1) {
            log.error("Error while instancing new FS driver", ex1);
        }

        return file;
    }







}
