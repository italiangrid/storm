/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.namespace;

import java.util.List;
import java.util.Map;

import it.grid.storm.common.types.PFN;
import it.grid.storm.filesystem.Space;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;

public interface NamespaceInterface {

  /**
   * getAllDefinedVFS
   * 
   * @return List<VirtualFSInterface> : Return a List of VirtualFS containing all the instances
   *         defined within Namespace
   * @throws NamespaceException
   */
  public List<VirtualFSInterface> getAllDefinedVFS();

  /**
   * getAllDefinedVFSAsDictionary
   *
   * @return Map<String, VirtualFSInterface> : Return a Map of all VirtualFS defined within
   *         Namespace, indexed by their root-paths
   * @throws NamespaceException
   */
  public Map<String, VirtualFSInterface> getAllDefinedVFSAsDictionary();

  /**
   * getVFSWithQuotaEnabled
   * 
   * @return Collection<VirtualFSInterface>: Return a collection of VirtualFS with fs type GPFS and
   *         quota enabled
   * @throws NamespaceException
   */
  public List<VirtualFSInterface> getVFSWithQuotaEnabled();

  /**
   * getAllDefinedMappingRules
   *
   * @return List<MappingRule> : Return a List of mapping rules containing all the instances defined
   *         within Namespace
   * @throws NamespaceException
   */
  public List<MappingRule> getAllDefinedMappingRules();

  /**
   * 
   * 
   * 
   * @param user GridUserInterface : Represents the principal
   * @return List : Return a List of VirtualFS instances
   * @throws NamespaceException : Occur when
   */
  public List<VirtualFSInterface> getApproachableVFS(GridUserInterface user)
      throws NamespaceException;

  /**
   * 
   * @return List : Return a List of readable and writable by anonymous users VirtualFS instances
   * @throws NamespaceException
   */
  public List<VirtualFSInterface> getApproachableByAnonymousVFS() throws NamespaceException;

  /**
   * 
   * @return List : Return a List of readable by anonymous users VirtualFS instances
   * @throws NamespaceException
   */
  public List<VirtualFSInterface> getReadableByAnonymousVFS() throws NamespaceException;

  /**
   * 
   * @return List : Return a List of readable or writable by anonymous users VirtualFS instances
   * @throws NamespaceException
   */
  public List<VirtualFSInterface> getReadableOrApproachableByAnonymousVFS()
      throws NamespaceException;

  /**
   * 
   * @param user GridUserInterface
   * @return VirtualFSInterface
   * @throws NamespaceException
   */
  public VirtualFSInterface getDefaultVFS(GridUserInterface user) throws NamespaceException;

  /**
   * 
   * @param storageResource StoRI
   * @param gridUser GridUserInterface
   * @return boolean
   * @throws NamespaceException
   */
  public boolean isApproachable(StoRI storageResource, GridUserInterface gridUser)
      throws NamespaceException;

  /**
   * 
   * @param surl TSURL
   * @param user GridUserInterface
   * @return StoRI
   * @throws NamespaceException
   * @throws UnapprochableSurlException
   * @throws InvalidSURLException
   */
  public StoRI resolveStoRIbySURL(TSURL surl, GridUserInterface user)
      throws UnapprochableSurlException, NamespaceException, InvalidSURLException;

  /**
   * 
   * @param surl TSURL
   * @return StoRI
   * @throws IllegalArgumentException
   * @throws NamespaceException
   * @throws InvalidSURLException
   */
  public StoRI resolveStoRIbySURL(TSURL surl)
      throws UnapprochableSurlException, NamespaceException, InvalidSURLException;

  /**
   * 
   * @param absolutePath String
   * @param user GridUserInterface
   * @return StoRI
   * @throws NamespaceException
   */
  public StoRI resolveStoRIbyAbsolutePath(String absolutePath, GridUserInterface user)
      throws NamespaceException;

  /**
   * 
   * @param absolutePath String
   * @param vfs VirtualFSInterface
   * @return StoRI
   * @throws NamespaceException
   */
  public StoRI resolveStoRIbyAbsolutePath(String absolutePath, VirtualFSInterface vfs)
      throws NamespaceException;

  /**
   * 
   * @param absolutePath String
   * @return StoRI
   * @throws NamespaceException
   */
  public StoRI resolveStoRIbyAbsolutePath(String absolutePath) throws NamespaceException;

  /**
   * 
   * @param absolutePath String
   * @param user GridUserInterface
   * @return VirtualFSInterface
   * @throws NamespaceException
   */
  public VirtualFSInterface resolveVFSbyAbsolutePath(String absolutePath, GridUserInterface user)
      throws NamespaceException;

  /**
   * 
   * @param absolutePath String
   * @return VirtualFSInterface
   * @throws NamespaceException
   */
  public VirtualFSInterface resolveVFSbyAbsolutePath(String absolutePath) throws NamespaceException;

  /**
   * 
   * @param pfn PFN
   * @return StoRI
   * @throws NamespaceException
   */
  public StoRI resolveStoRIbyPFN(PFN pfn) throws NamespaceException;

  /**
   * 
   * @param file LocalFile
   * @return VirtualFSInterface
   * @throws NamespaceException
   */
  public VirtualFSInterface resolveVFSbyLocalFile(it.grid.storm.filesystem.LocalFile file)
      throws NamespaceException;

  /**
   * 
   * @param pfn PFN
   * @return VirtualFSInterface
   * @throws NamespaceException
   */
  public VirtualFSInterface resolveVFSbyPFN(PFN pfn) throws NamespaceException;

  /**
   * 
   * @param user GridUserInterface
   * @return StoRI
   * @throws NamespaceException
   */
  public StoRI getDefaultSpaceFileForUser(GridUserInterface user) throws NamespaceException;

  /**
   * Method that retrieves a previously reserved Space as identified by the SpaceToken, for the
   * given new size. If null or Empty TSizeInBytes are supplied, a Space object built off deafult
   * values is returned instead.
   * 
   * 
   * @param totSize TSizeInBytes
   * @param token TSpaceToken
   * @return Space
   */

  public Space retrieveSpaceByToken(TSizeInBytes totSize, TSpaceToken token);

  /**
   * 
   * @param user GridUserInterface
   * @return String
   * @throws NamespaceException
   */
  public String makeSpaceFileURI(GridUserInterface user) throws NamespaceException;

  /**
   * @param fileName
   * @return
   * @throws IllegalArgumentException
   */
  public boolean isSpaceFile(String fileName);

  public String getNamespaceVersion() throws NamespaceException;

  /**
   * @param absolutePath
   * @return
   * @throws NamespaceException
   */
  public VirtualFSInterface resolveVFSbyRoot(String absolutePath) throws NamespaceException;

  /**
   * @param spaceToken
   * @return
   * @throws NamespaceException
   */
  public VirtualFSInterface resolveVFSbySpaceToken(TSpaceToken spaceToken)
      throws NamespaceException;

}
