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

import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.StFN;
import it.grid.storm.filesystem.Space;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TTURL;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description
 * </p>
 * This interface represents the main entry point for namespace operation. The
 * results of retrived informations depends on namespace configuration. The
 * namespace configuration is read back every time that it changes, so it is
 * possible mutates the namespace configuration at run time.
 * 
 * 
 * The namespace configuration is divided in three main section: 1) Virtual File
 * System definition 2) Mapping rules definition 3) Approachable rules
 * definition
 * 
 * 
 * VFS Configuration
 * 
 * 
 * 
 * Mapping Rule
 * 
 * 
 * 
 * Approachable Rule
 * 
 * 
 * 
 * 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: INFN-CNAF and ICTP/eGrid project
 * </p>
 * 
 * @author Riccardo Zappi
 * @version 1.0
 */
public interface NamespaceInterface {

	/**
	 * getAllDefinedVFS
	 * 
	 * @return List<VirtualFSInterface> : Return a List of VirtualFS cointaing all
	 *         the instances defined within Namespace
	 * @throws NamespaceException
	 */
	public Collection<VirtualFSInterface> getAllDefinedVFS()
		throws NamespaceException;

	/**
	 * 
	 * 
	 * 
	 * @param user
	 *          GridUserInterface : Represents the principal
	 * @return List : Return a List of VirtualFS instances
	 * @throws NamespaceException
	 *           : Occur when
	 */
	public List<VirtualFSInterface> getApproachableVFS(GridUserInterface user)
		throws NamespaceException;

	/**
	 * 
	 * @param user
	 *          GridUserInterface
	 * @return VirtualFSInterface
	 * @throws NamespaceException
	 */
	public VirtualFSInterface getDefaultVFS(GridUserInterface user)
		throws NamespaceException;

	/**
	 * 
	 * @param storageResource
	 *          StoRI
	 * @param gridUser
	 *          GridUserInterface
	 * @return boolean
	 * @throws NamespaceException
	 */
	public boolean isApproachable(StoRI storageResource,
		GridUserInterface gridUser) throws NamespaceException;

	/**
	 * 
	 * @param surl
	 *          TSURL
	 * @param user
	 *          GridUserInterface
	 * @return StoRI
	 * @throws NamespaceException
	 * @throws UnapprochableSurlException
	 * @throws InvalidSURLException 
	 */
	public StoRI resolveStoRIbySURL(TSURL surl, GridUserInterface user)
		throws IllegalArgumentException, UnapprochableSurlException, NamespaceException, InvalidSURLException;

	/**
	 * 
	 * @param surl
	 *          TSURL
	 * @return StoRI
	 * @throws IllegalArgumentException 
	 * @throws NamespaceException
	 * @throws InvalidSURLException 
	 */
	public StoRI resolveStoRIbySURL(TSURL surl) throws UnapprochableSurlException, IllegalArgumentException, NamespaceException, InvalidSURLException;

	/**
	 * 
	 * @param surl
	 *          TSURL
	 * @param user
	 *          GridUserInterface
	 * @return VirtualFSInterface
	 * @throws NamespaceException
	 * @throws UnapprochableSurlException
	 * @throws InvalidSURLException 
	 * @throws IllegalArgumentException 
	 */
	public VirtualFSInterface resolveVFSbySURL(TSURL surl, GridUserInterface user)
		throws UnapprochableSurlException, IllegalArgumentException, InvalidSURLException, NamespaceException;

	/**
	 * 
	 * @param absolutePath
	 *          String
	 * @param user
	 *          GridUserInterface
	 * @return StoRI
	 * @throws NamespaceException
	 */
	public StoRI resolveStoRIbyAbsolutePath(String absolutePath,
		GridUserInterface user) throws NamespaceException;

	/**
	 * 
	 * @param absolutePath
	 *          String
	 * @return StoRI
	 * @throws NamespaceException
	 */
	public StoRI resolveStoRIbyAbsolutePath(String absolutePath)
		throws NamespaceException;

	/**
	 * 
	 * @param absolutePath
	 *          String
	 * @param user
	 *          GridUserInterface
	 * @return VirtualFSInterface
	 * @throws NamespaceException
	 */
	public VirtualFSInterface resolveVFSbyAbsolutePath(String absolutePath,
		GridUserInterface user) throws NamespaceException;

	/**
	 * 
	 * @param absolutePath
	 *          String
	 * @return VirtualFSInterface
	 * @throws NamespaceException
	 */
	public VirtualFSInterface resolveVFSbyAbsolutePath(String absolutePath)
		throws NamespaceException;

	/**
	 * 
	 * @param file
	 *          LocalFile
	 * @param user
	 *          GridUserInterface
	 * @return StoRI
	 * @throws NamespaceException
	 */
	public StoRI resolveStoRIbyLocalFile(it.grid.storm.filesystem.LocalFile file,
		GridUserInterface user) throws NamespaceException;

	/**
	 * 
	 * @param file
	 *          LocalFile
	 * @return StoRI
	 * @throws NamespaceException
	 */
	public StoRI resolveStoRIbyLocalFile(it.grid.storm.filesystem.LocalFile file)
		throws NamespaceException;

	/**
	 * 
	 * @param file
	 *          LocalFile
	 * @param user
	 *          GridUserInterface
	 * @return VirtualFSInterface
	 * @throws NamespaceException
	 */
	public VirtualFSInterface resolveVFSbyLocalFile(
		it.grid.storm.filesystem.LocalFile file, GridUserInterface user)
		throws NamespaceException;

	/**
	 * 
	 * @param file
	 *          LocalFile
	 * @return VirtualFSInterface
	 * @throws NamespaceException
	 */
	public VirtualFSInterface resolveVFSbyLocalFile(
		it.grid.storm.filesystem.LocalFile file) throws NamespaceException;

	/**
	 * 
	 * @param stfn
	 *          StFN
	 * @param user
	 *          GridUserInterface
	 * @return StoRI
	 * @throws NamespaceException
	 */
	public StoRI resolveStoRIbyStFN(StFN stfn, GridUserInterface user)
		throws NamespaceException;

	/**
	 * 
	 * @param stfn
	 *          StFN
	 * @return StoRI
	 * @throws NamespaceException
	 */
	public StoRI resolveStoRIbyStFN(StFN stfn) throws NamespaceException;

	/**
	 * 
	 * @param stfn
	 *          StFN
	 * @param user
	 *          GridUserInterface
	 * @return VirtualFSInterface
	 * @throws NamespaceException
	 */
	public VirtualFSInterface resolveVFSbyStFN(StFN stfn, GridUserInterface user)
		throws NamespaceException;

	/**
	 * 
	 * @param stfn
	 *          StFN
	 * @return VirtualFSInterface
	 * @throws NamespaceException
	 */
	public VirtualFSInterface resolveVFSbyStFN(StFN stfn)
		throws NamespaceException;

	/**
	 * 
	 * @param pfn
	 *          PFN
	 * @param user
	 *          GridUserInterface
	 * @return StoRI
	 * @throws NamespaceException
	 */
	public StoRI resolveStoRIbyPFN(PFN pfn, GridUserInterface user)
		throws NamespaceException;

	/**
	 * 
	 * @param pfn
	 *          PFN
	 * @return StoRI
	 * @throws NamespaceException
	 */
	public StoRI resolveStoRIbyPFN(PFN pfn) throws NamespaceException;

	/**
	 * 
	 * @param pfn
	 *          PFN
	 * @param user
	 *          GridUserInterface
	 * @return VirtualFSInterface
	 * @throws NamespaceException
	 */
	public VirtualFSInterface resolveVFSbyPFN(PFN pfn, GridUserInterface user)
		throws NamespaceException;

	/**
	 * 
	 * @param pfn
	 *          PFN
	 * @return VirtualFSInterface
	 * @throws NamespaceException
	 */
	public VirtualFSInterface resolveVFSbyPFN(PFN pfn) throws NamespaceException;

	/**
	 * 
	 * @param turl
	 *          TTURL
	 * @param user
	 *          GridUserInterface
	 * @return StoRI
	 * @throws NamespaceException
	 */
	public StoRI resolveStoRIbyTURL(TTURL turl, GridUserInterface user)
		throws NamespaceException;

	/**
	 * 
	 * @param turl
	 *          TTURL
	 * @return StoRI
	 * @throws NamespaceException
	 */
	public StoRI resolveStoRIbyTURL(TTURL turl) throws NamespaceException;

	/**
	 * 
	 * @param turl
	 *          TTURL
	 * @param user
	 *          GridUserInterface
	 * @return VirtualFSInterface
	 * @throws NamespaceException
	 */
	public VirtualFSInterface resolveVFSbyTURL(TTURL turl, GridUserInterface user)
		throws NamespaceException;

	/**
	 * 
	 * @param turl
	 *          TTURL
	 * @return VirtualFSInterface
	 * @throws NamespaceException
	 */
	public VirtualFSInterface resolveVFSbyTURL(TTURL turl)
		throws NamespaceException;

	/**
	 * 
	 * @param user
	 *          GridUserInterface
	 * @return StoRI
	 * @throws NamespaceException
	 */
	public StoRI getDefaultSpaceFileForUser(GridUserInterface user)
		throws NamespaceException;

	/**
	 * Method that retrieves a previously reserved Space as identified by the
	 * SpaceToken, for the given new size. If null or Empty TSizeInBytes are
	 * supplied, a Space object built off deafult values is returned instead.
	 * 
	 * 
	 * @param totSize
	 *          TSizeInBytes
	 * @param token
	 *          TSpaceToken
	 * @return Space
	 */

	public Space retrieveSpaceByToken(TSizeInBytes totSize, TSpaceToken token);

	/**
	 * 
	 * @param user
	 *          GridUserInterface
	 * @return String
	 * @throws NamespaceException
	 */
	public String makeSpaceFileURI(GridUserInterface user)
		throws NamespaceException;

	/**
	 * @param fileName
	 * @return
	 * @throws IllegalArgumentException
	 */
	public boolean isSpaceFile(String fileName) throws IllegalArgumentException;

	public String getNamespaceVersion() throws NamespaceException;

	/**
	 * @param absolutePath
	 * @return
	 * @throws NamespaceException
	 */
	public VirtualFSInterface resolveVFSbyRoot(String absolutePath)
		throws NamespaceException;

	/**
	 * @param spaceToken
	 * @return
	 * @throws NamespaceException
	 */
	public VirtualFSInterface resolveVFSbySpaceToken(TSpaceToken spaceToken)
		throws NamespaceException;


}
