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

package it.grid.storm.namespace;

import java.util.*;

import it.grid.storm.common.types.*;
import it.grid.storm.filesystem.*;
import it.grid.storm.griduser.*;
import it.grid.storm.namespace.model.*;
import it.grid.storm.srm.types.*;
import it.grid.storm.namespace.ExpiredSpaceTokenException;

public interface StoRI {

    /*****************************************************************************
     *  BUILDING METHODs
     ****************************************************************************/

    public void setStoRIType(StoRIType type);

    /*****************************************************************************
     *  READ METHODs
     ***************************************************************************/


    /**
     * getTURL
     *
     * Create new Transport URL selecting correct protocol between TransferProtocols
     * specified by input parameter and TransferProtocol assigned at Virtual Organization at Creation time.   *
     *
     * @param prefixOfAcceptedTransferProtocols TURLPrefix  Collection of @link TransferProcol specified in SRM Request.
     * @return TTURL : TransportURL for StoRI.
     * @throws InvalidGetTURLNullPrefixAttributeException
     * @throws Exception 
     */
    public TTURL getTURL(TURLPrefix prefixOfAcceptedTransferProtocols) throws
        InvalidGetTURLNullPrefixAttributeException, InvalidGetTURLProtocolException, TURLBuildingException;


    public TSURL getSURL();

    public PFN getPFN();

    public StFN getStFN();

    public String getRelativePath();

    public String getRelativeStFN();

    public TLifeTimeInSeconds getFileLifeTime();

    public Date getFileStartTime();

    public StoRIType getStoRIType();

    public Space getSpace();

    public void setSpace(Space space);

    public LocalFile getLocalFile();

    public VirtualFSInterface getVirtualFileSystem();

    public String getStFNRoot();

    public String getStFNPath();

    public String getFilename();

    public void setStFNRoot(String stfnRoot);

    public void setMappingRule(MappingRule winnerRule);
    
    public void setGroupTapeRead();
    
    public void setGroupTapeWrite();

    public MappingRule getMappingRule();

    /*****************************************************************************
     *  BUSINESS METHODs
     ****************************************************************************/

    @Deprecated
    public ArrayList<StoRI> getChildren(GridUserInterface gUser, TDirOption dirOption) throws
        InvalidDescendantsPathRequestException, InvalidDescendantsAuthRequestException,
        InvalidDescendantsFileRequestException, InvalidDescendantsEmptyRequestException, InvalidDescendantsTDirOptionRequestException;

    public ArrayList<StoRI> generateChildrenNoFolders(TDirOption dirOption) throws
        InvalidDescendantsEmptyRequestException, InvalidDescendantsAuthRequestException,
        InvalidDescendantsPathRequestException, InvalidDescendantsFileRequestException, InvalidDescendantsTDirOptionRequestException;

    public ArrayList<StoRI> getChildren(TDirOption dirOption) throws
       InvalidDescendantsEmptyRequestException, InvalidDescendantsAuthRequestException,
       InvalidDescendantsPathRequestException, InvalidDescendantsFileRequestException;

    public String getAbsolutePath();

    public boolean hasJustInTimeACLs();


    /**
     * Method that returns an ordered list of parent StoRI objects, starting from
     * the root and excluding the StoRI itself. If no parents are present, an empty
     * List is returned instead.
     */
    public List<StoRI> getParents();

    public void allotSpaceForFile(TSizeInBytes totSize) throws ReservationException;

    public void allotSpaceByToken(TSpaceToken token) throws ReservationException, ExpiredSpaceTokenException;

    public void allotSpaceByToken(TSpaceToken token, TSizeInBytes totSize) throws ReservationException, ExpiredSpaceTokenException;



}
