/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.space;

import it.grid.storm.acl.AclManager;
import it.grid.storm.acl.AclManagerFSAndHTTPS;
import it.grid.storm.catalogs.InvalidRetrievedDataException;
import it.grid.storm.catalogs.InvalidSpaceDataAttributesException;
import it.grid.storm.catalogs.MultipleDataEntriesException;
import it.grid.storm.catalogs.NoDataFoundException;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.ReservationException;
import it.grid.storm.filesystem.Space;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.naming.NamespaceUtil;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TSpaceType;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is part of the StoRM project.
 * Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 *
 * @author lucamag
 * @date May 30, 2008
 *
 */
public class SRMSpace {

    private static final Logger log = LoggerFactory.getLogger(SRMSpace.class);

    private Space space;
    private StoRI stori;
    private String spaceFN;
    private GridUserInterface user;
    private NamespaceInterface namespace = NamespaceDirector.getNamespace();
    private VirtualFSInterface vfs;
    private ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();
    private TSizeInBytes desiredSize;
    private TSizeInBytes guaranteedSize;
    private TLifeTimeInSeconds lifetime;
    private TSpaceToken spacetoken;
    private String spaceTokenAlias;

    public SRMSpace(TSpaceToken token) {
        //Retrieve from catalog

    }

    public SRMSpace(GridUserInterface user, TSizeInBytes desiredSize, TSizeInBytes guaranteedSize, TLifeTimeInSeconds lifetime, String spaceTokenAlias) throws IllegalSRMSpaceParameter {

        if (user != null) {
            this.user = user;
        } else {
            throw new IllegalSRMSpaceParameter(user, desiredSize, null);
        }

        try {
            spaceFN = namespace.makeSpaceFileURI(user);
            /**
             * This parsing it used to eliminate each double / present.
             */
            // while(spaceFN.indexOf("//")!=-1) {
            // spaceFN = spaceFN.replaceAll("//","/");
            // }
            log.debug(" Space FN : " + spaceFN);

        } catch (NamespaceException ex) {
            throw new IllegalSRMSpaceParameter(user, desiredSize, null);
        }

        try {
            vfs = namespace.resolveVFSbyAbsolutePath(spaceFN);
            log.debug("Space File belongs to VFS : " + vfs.getAliasName());
        } catch (NamespaceException ex2) {
            throw new IllegalSRMSpaceParameter(user, desiredSize, null);
        }

        String relativeSpaceFN = null;
        try {
            log.debug("ExtractRelativeSpace: root:" + vfs.getRootPath() + " spaceFN:" + spaceFN);
            relativeSpaceFN = NamespaceUtil.extractRelativePath(vfs.getRootPath(), spaceFN);
            log.debug("relativeSpaceFN:" + relativeSpaceFN);
        } catch (NamespaceException ex3) {
            log.warn("SRMSpace. Unable to extract the relative Space FN. Using the root '/'. "+ex3);
        }


        try {
            stori = vfs.createSpace(relativeSpaceFN, guaranteedSize.value(), desiredSize.value());
        } catch (NamespaceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new IllegalSRMSpaceParameter(user, desiredSize, null);
        }

        space = stori.getSpace();

        this.desiredSize = desiredSize;
        this.lifetime = lifetime;
        this.guaranteedSize = guaranteedSize;
        this.spaceTokenAlias = spaceTokenAlias;


    }



    private void permission() {

        // Call wrapper to set ACL on file created.
        /** Check for JiT or AoT */
        boolean hasJiTACL = stori.hasJustInTimeACLs();

        FilesystemPermission fp = FilesystemPermission.ReadWrite;

        if (hasJiTACL) {
            // JiT Case
            log.debug("<SpaceResManager>:reserveSpace AddACL for FIle: "
                    + spaceFN + "  " + "USER RW");
            try {
                AclManager manager = AclManagerFSAndHTTPS.getInstance();
                //TODO ACL manager
                LocalFile localFile = stori.getLocalFile();
                LocalUser localUser = user.getLocalUser();
                if (localFile == null || localUser == null)
                {
                    log.warn("Unable to setting up the ACL. Null value/s : LocalFile " + localFile
                            + " localUser " + localUser);
                }
                else
                {
                    try
                    {
                        manager.grantUserPermission(localFile, localUser, fp);
                    }
                    catch (IllegalArgumentException e)
                    {
                        log.error("Unable to grant user traverse permission on the file. IllegalArgumentException: " + e.getMessage());
                    }
                }
//                stori.getLocalFile().grantUserPermission(
//                        user.getLocalUser(), fp);
            } catch (CannotMapUserException ex5) {
                log.debug("Unable to setting up the ACL ", ex5);

            }
        } else {
            // AoT Case
            log.debug("<SpaceResManager>:reserveSpace AddACL for FIle: "
                    + spaceFN + "  " + "GROUP RW");
            try {
                AclManager manager = AclManagerFSAndHTTPS.getInstance();
                //TODO ACL manager
                LocalFile localFile = stori.getLocalFile();
                LocalUser localUser = user.getLocalUser();
                if (localFile == null || localUser == null)
                {
                    log.warn("Unable to setting up the ACL. Null value/s : LocalFile " + localFile
                            + " localUser " + localUser);
                }
                else
                {
                    try
                    {
                        manager.grantGroupPermission(localFile, localUser, fp);
                    }
                    catch (IllegalArgumentException e)
                    {
                        log.error("Unable to grant group permission on space file. IllegalArgumentException: " + e.getMessage());
                    }
                }
//                stori.getLocalFile().grantGroupPermission(
//                        user.getLocalUser(), fp);
            } catch (CannotMapUserException ex5) {
                log.debug("Unable to setting up the ACL ", ex5);

            }
        }

    }

    private void registerToCatalog() {

        /**
         * Create Storage Space in StoRM domain
         */
        log.debug("-- Creating Storage Space Data ...");
        StorageSpaceData spaceDt = null;

        /**
         * @todo REMOVE THIS AS SOON DB IS UPDATED!
         */
        TSpaceType spaceType = TSpaceType.PERMANENT;

        // FIXME: storageSystemInfo is passed as NULL. To fix managing the
        // new type ArrayOfTExtraInfo

        Date date = new Date();

        try {
            spaceDt = new StorageSpaceData(user, spaceType, spaceTokenAlias, desiredSize ,guaranteedSize, lifetime,
                    null, date, stori.getPFN());
            log.debug(spaceDt.toString());
            log.debug("-- Created Storage Space Data --");
        } catch (InvalidSpaceDataAttributesException ex7) {
            log.debug("Unable to create Storage Space Data", ex7);

        }

        /**
         * Add Storage Space in Catalog
         */

		catalog.addStorageSpace(spaceDt);

    }

    //TODO
    public void doReservation( ) {


        try {
            space.allot();
        } catch (ReservationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        permission();

        registerToCatalog();

    }




    public void update(){}
    public void purge() {}
    public void remove(){}

}
