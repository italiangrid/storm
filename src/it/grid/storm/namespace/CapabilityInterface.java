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
