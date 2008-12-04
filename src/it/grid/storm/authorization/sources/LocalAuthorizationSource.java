package it.grid.storm.authorization.sources;

import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.authorization.AuthorizationQueryInterface;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.namespace.StoRI;

import org.apache.log4j.Logger;

/**
 *
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 *
 *
 * Authors:
 *     @author alvaro@IFIC
 *
 * @date = Dec 4, 2008
 *
 */


public class LocalAuthorizationSource implements AuthorizationQueryInterface  {
 

    // --- private --- //

    static private Logger log = Logger.getLogger("LocalAuthorizationSource");


    // examine parent or file itself
    private final static boolean PARENT = true;
    private final static boolean THIS = false;


    /** The <code>AuthorizationDecision</code> instance to return as a
     * result for all queries. */
    private final AuthorizationDecision __response = AuthorizationDecision.Permit;


    private AuthorizationDecision ExamineAuthorization(final String whoCalls,
            final VomsGridUser gridUser, 
            final StoRI fileOrDirectory, 
            final FilesystemPermission queryPerm,
            final boolean ancestor) {

        log.debug("Local Authorization Source: " + whoCalls + " invoked for " + gridUser.getDn()
                  + " on SURL " + fileOrDirectory.toString());

        LocalUser lu;
        try {
            lu = gridUser.getLocalUser();
            int uid = lu.getUid();
            int gid = lu.getPrimaryGid();
            log.info("Local Authorization Source: uid: "+uid+"  gid: "+gid);
        } catch (CannotMapUserException e) {
            log.error("Local Authorization Source: unable to get local user");
            return AuthorizationDecision.Deny;
        }

        LocalFile lf = fileOrDirectory.getLocalFile();
        if ( ancestor ) {
            if ( ! lf.parentExists() ) {
                log.error("JSZ Local Authorization Source: parent does not exist "+fileOrDirectory.toString());
                // WARNING: return permit. Parent does not exist, so further operations will fail besides authorization.
                return AuthorizationDecision.Permit;
            }
            lf = lf.getParentFile();
            if ( lf == null ) {
                log.error("JSZ Local Authorization Source: parent does not exist "+fileOrDirectory.toString());
                // WARNING: return permit. Parent does not exist, so further operations will fail besides authorization.
                return AuthorizationDecision.Permit;
            }
            try {
                if ( ! lf.isDirectory() ) {
                    log.error("JSZ Local Authorization Source: parent is not a directory  "+fileOrDirectory.toString());
                    // WARNING: return permit. Parent does not exist, so further operations will fail besides authorization.
                    return AuthorizationDecision.Permit;
                }
            } catch (SecurityException e) {
                log.error("Local Authorization Source: unable to get directory status");
                return AuthorizationDecision.Deny;
            }
        }

        // localfile lf must exist
        if ( ! lf.exists() ) {
            log.error("JSZ Local Authorization Source: file does not exist "+fileOrDirectory.toString());
            // WARNING: return permit. File does not exist, so further operations will fail besides authorization.
            return AuthorizationDecision.Permit;
        }

        boolean auth;
        try {
            auth = lf.canAccess(lu, queryPerm);
        } catch (CannotMapUserException e) {
            log.error("Local Authorization Source: unable to get local user in canAccess");
            return AuthorizationDecision.Deny;
        }

        log.debug("Local Authorization Source: canAccess gives "+auth);
        if ( auth )
            return AuthorizationDecision.Permit;

        return AuthorizationDecision.Deny;
    }



    // --- public --- //

    public AuthorizationDecision canUseStormAtAll(final VomsGridUser gridUser) {
        return __response;
    }

    public AuthorizationDecision canReadFile(final VomsGridUser gridUser, final StoRI file) {

        return ExamineAuthorization("canReadFile", gridUser, file, 
                                    FilesystemPermission.Read, THIS);

    }

    public AuthorizationDecision canWriteFile(final VomsGridUser gridUser, final StoRI existingFile) {

        return ExamineAuthorization("canWriteFile", gridUser, existingFile, 
                                    FilesystemPermission.Write, THIS);

    }

    public AuthorizationDecision canCreateNewFile(final VomsGridUser gridUser, final StoRI targetFile) {
        // puedo crear un fichero si puedo escribir en el directorio
        //

        return ExamineAuthorization("canCreateNewFile", gridUser, targetFile, 
                                    FilesystemPermission.Write, PARENT);

    }

    public AuthorizationDecision canChangeAcl(final VomsGridUser gridUser, final StoRI fileOrDirectory) {
        return __response;
    }

    public AuthorizationDecision canGiveaway(final VomsGridUser gridUser, final StoRI fileOrDirectory) {
        return __response;
    }

    public AuthorizationDecision canListDirectory(final VomsGridUser gridUser, final StoRI targetDirectory) {
        return ExamineAuthorization("canListDirectory", gridUser, targetDirectory, 
                                    FilesystemPermission.List, THIS);

    }

    public AuthorizationDecision canTraverseDirectory(final VomsGridUser gridUser, final StoRI targetDirectory) {

        return ExamineAuthorization("canTraverseDirectory", gridUser, targetDirectory, 
                                    FilesystemPermission.Traverse, THIS);

    }

    public AuthorizationDecision canRename(final VomsGridUser gridUser, final StoRI file) {
        // renombrar un fichero podemos implementarlo como:
        // 1. poder leer la entrada del directorio
        // 2. poder escribir la entrada del directorio

        return ExamineAuthorization("canRename", gridUser, file,
                                    FilesystemPermission.ReadWrite, PARENT);

    }

    public AuthorizationDecision canDelete(final VomsGridUser gridUser, final StoRI file) {
        // para que se pueda borrar un objeto, miro si puedo leer y escribir en el padre
        // 

        return ExamineAuthorization("canDelete", gridUser, file,
                                    FilesystemPermission.ReadWrite, PARENT);

    }

    public AuthorizationDecision canMakeDirectory(final VomsGridUser gridUser, final StoRI targetDirectory) {

        return ExamineAuthorization("canMakeDirectory", gridUser, targetDirectory,
                                    FilesystemPermission.Write, PARENT);

    }

}
