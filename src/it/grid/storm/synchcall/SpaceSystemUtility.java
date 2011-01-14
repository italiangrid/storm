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

package it.grid.storm.synchcall;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.PFN;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.model.StoRIType;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.TSpaceToken;

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
public class SpaceSystemUtility {

    private static Logger log = NamespaceDirector.getLogger();

    public static StoRI getSpaceByToken(TSpaceToken token) throws NamespaceException {

        NamespaceInterface namespace = NamespaceDirector.getNamespace();
        //Retrieve Storage Space from Persistence
        ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();
        StorageSpaceData spaceData = catalog.getStorageSpace(token);
        PFN pfn = spaceData.getSpaceFileName();
        StoRI stori = namespace.resolveStoRIbyPFN(pfn);
        stori.setStoRIType(StoRIType.SPACE);
        return stori;
    }



}
