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
     */
    public TTURL getTURL(TURLPrefix prefixOfAcceptedTransferProtocols) throws
        InvalidGetTURLNullPrefixAttributeException;

    public TTURL getTURL(Protocol prot);

    public List getTURLs();

    public TSURL getSURL();

    public PFN getPFN();

    public StFN getStFN();

    public String getRelativePath();
    
    public String getRelativeStFN();

    public TLifeTimeInSeconds getFileLifeTime();
    
    public Date getFileStartTime();

    public StoRIType getStoRIType();

    public boolean isSURLBusy();

    public Space getSpace();

    public void setSpace(Space space);

    public LocalFile getLocalFile();

    public VirtualFSInterface getVirtualFileSystem();

    public String getStFNRoot();
    
    public String getStFNPath();
    
    public String getFilename();

    public void setStFNRoot(String stfnRoot);

    public void setMappingRule(MappingRule winnerRule);

    public MappingRule getMappingRule();

    /*****************************************************************************
     *  BUSINESS METHODs
     ****************************************************************************/

    public ArrayList getChildren(GridUserInterface gUser, TDirOption dirOption) throws
        InvalidDescendantsPathRequestException, InvalidDescendantsAuthRequestException,
        InvalidDescendantsFileRequestException, InvalidDescendantsEmptyRequestException;

    public ArrayList getChildren(TDirOption dirOption) throws
        InvalidDescendantsEmptyRequestException, InvalidDescendantsAuthRequestException,
        InvalidDescendantsPathRequestException, InvalidDescendantsFileRequestException;

    public String getAbsolutePath();

    public boolean hasJustInTimeACLs();

    /**
     * Method that returns an ordered list of parent StoRI objects, starting from
     * the root and excluding the StoRI itself. If no parents are present, an empty
     * List is returned instead.
     */
    public List getParents();

    public void allotSpaceForFile(TSizeInBytes totSize) throws ReservationException;

    public void allotSpaceByToken(TSpaceToken token) throws ReservationException, ExpiredSpaceTokenException;

    public void allotSpaceByToken(TSpaceToken token, TSizeInBytes totSize) throws ReservationException, ExpiredSpaceTokenException;

}
