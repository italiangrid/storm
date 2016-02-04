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

import it.grid.storm.balancer.BalancingStrategy;
import it.grid.storm.balancer.Node;
import it.grid.storm.filesystem.FilesystemIF;
import it.grid.storm.filesystem.SpaceSystem;
import it.grid.storm.filesystem.swig.genericfs;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.model.ApproachableRule;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.namespace.model.SAAuthzType;
import it.grid.storm.namespace.model.StoRIType;
import it.grid.storm.namespace.model.StorageClassType;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;

import java.util.List;

public interface VirtualFSInterface {

	public String getFSType();

	public String getSpaceTokenDescription();

	public StorageClassType getStorageClassType();

	public String getRootPath();

	public StoRI getRoot() throws NamespaceException;

	public String getAliasName();

	public Class getFSDriver() throws NamespaceException;

	public genericfs getFSDriverInstance() throws NamespaceException;

	public FilesystemIF getFilesystem() throws NamespaceException;

	public Class getSpaceSystemDriver() throws NamespaceException;

	public SpaceSystem getSpaceSystemDriverInstance() throws NamespaceException;

	public boolean isApproachableByUser(GridUserInterface user);

	public DefaultValuesInterface getDefaultValues();

	public CapabilityInterface getCapabilities();

	public PropertyInterface getProperties();

	public List<MappingRule> getMappingRules() throws NamespaceException;

	public List<ApproachableRule> getApproachableRules()
		throws NamespaceException;

	public TSizeInBytes getUsedNearlineSpace() throws NamespaceException;

	public TSizeInBytes getUsedOnlineSpace() throws NamespaceException;

	public TSizeInBytes getAvailableOnlineSpace() throws NamespaceException;

	public TSizeInBytes getAvailableNearlineSpace() throws NamespaceException;

	public StoRI createFile(String relativePath) throws NamespaceException;

	public StoRI createFile(String relativePath, StoRIType type);

	public void makeSilhouetteForFile(StoRI stori, TSizeInBytes presumedSize)
		throws NamespaceException;

	public void useSpaceForFile(TSpaceToken token, StoRI file,
		TSizeInBytes sizePresumed) throws ExpiredSpaceTokenException,
		NamespaceException;

	public void useAllSpaceForFile(TSpaceToken token, StoRI file)
		throws ExpiredSpaceTokenException, NamespaceException;


	public StoRI createSpace(String relativePath, long guaranteedSize,
		long totalSize) throws NamespaceException;

	public StoRI createSpace(String relativePath, long totalSize)
		throws NamespaceException;

	public StoRI createSpace(long guarSize, long totalSize)
		throws NamespaceException;

	public StoRI createSpace(long totalSize) throws NamespaceException;

	public StoRI createSpace() throws NamespaceException;

	public TSizeInBytes splitSpace(StoRI spaceOrig, StoRI file, long sizePresumed)
		throws NamespaceException;

	public StorageSpaceData getSpaceByAlias(String alias)
		throws NamespaceException;

	public void storeSpaceByToken(StorageSpaceData spaceData)
		throws NamespaceException;

	public StoRI createDefaultStoRI() throws NamespaceException;

	public long getCreationTime();

	public TSpaceToken getSpaceToken() throws NamespaceException;

	public SAAuthzType getStorageAreaAuthzType() throws NamespaceException;

	public String getStorageAreaAuthzDB() throws NamespaceException;

	public String getStorageAreaAuthzFixed() throws NamespaceException;

	public boolean isPoolDefined(Protocol protocol) throws NamespaceException;

	public BalancingStrategy<? extends Node> getProtocolBalancingStrategy(
		Protocol protocol);

	public boolean isApproachableByAnonymous();

	public boolean isHttpWorldReadable();

	public void setProperties(PropertyInterface prop);
	
	public boolean increaseUsedSpace(long size);
	
	public boolean decreaseUsedSpace(long size);

}
