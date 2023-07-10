/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace;

import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.StFN;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.ReservationException;
import it.grid.storm.filesystem.Space;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.StoRIType;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TTURL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface StoRI {

  public void setStoRIType(StoRIType type);

  public TTURL getTURL(TURLPrefix prefixOfAcceptedTransferProtocols)
      throws IllegalArgumentException, InvalidGetTURLProtocolException, TURLBuildingException;

  public TSURL getSURL();

  public PFN getPFN();

  public StFN getStFN();

  public StFN getStFNFromMappingRule();

  public String getRelativePath();

  public String getRelativeStFN();

  public TLifeTimeInSeconds getFileLifeTime();

  public Date getFileStartTime();

  public StoRIType getStoRIType();

  public Space getSpace();

  public void setSpace(Space space);

  public LocalFile getLocalFile();

  public VirtualFS getVirtualFileSystem();

  public String getStFNRoot();

  public String getStFNPath();

  public String getFilename();

  public void setStFNRoot(String stfnRoot);

  public void setMappingRule(MappingRule winnerRule);

  public MappingRule getMappingRule();

  public ArrayList<StoRI> getChildren(TDirOption dirOption)
      throws InvalidDescendantsEmptyRequestException, InvalidDescendantsPathRequestException,
          InvalidDescendantsFileRequestException;

  public String getAbsolutePath();

  public boolean hasJustInTimeACLs();

  public List<StoRI> getParents();

  public void allotSpaceForFile(TSizeInBytes totSize) throws ReservationException;

  public void allotSpaceByToken(TSpaceToken token)
      throws ReservationException, ExpiredSpaceTokenException;

  public void allotSpaceByToken(TSpaceToken token, TSizeInBytes totSize)
      throws ReservationException, ExpiredSpaceTokenException;
}
