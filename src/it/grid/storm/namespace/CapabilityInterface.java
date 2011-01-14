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

import it.grid.storm.namespace.model.Capability.*;
import it.grid.storm.namespace.model.Quota;
import it.grid.storm.namespace.model.DefaultACL;
import it.grid.storm.balancer.Balancer;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.namespace.model.TransportProtocol;
import it.grid.storm.balancer.Node;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface CapabilityInterface {

    public List<TransportProtocol> getManagedProtocolByScheme(Protocol protocol);

    public List<Protocol> getAllManagedProtocols();

    public boolean isPooledProtocol(Protocol protocol);

    public Balancer<? extends Node> getPoolByScheme(Protocol protocol);

    public TransportProtocol getProtocolByID(int id);

    public ACLMode getACLMode();

    //public Collection getManagedSpaceTypes();

    //public Collection getManagedFileTypes();

    //public TSizeInBytes getGuaranteedSpaceSizeMax();

    //public TLifeTimeInSeconds getSpaceLifeTimeMAX();

    //public TSizeInBytes getTotalSpaceSizeMAX();

    //public TLifeTimeInSeconds getFileLifeTimeMAX();

    //public boolean isAllowedFileType(TFileStorageType fileType);

    //public boolean isAllowedSpaceType(TSpaceType spaceType);

    public boolean isAllowedProtocol(String protocolName);

    public Quota getQuota();

    public DefaultACL getDefaultACL();


}
