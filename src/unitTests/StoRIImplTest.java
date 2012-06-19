package unitTests;


import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.LinkedList;
import it.grid.storm.balancer.BalancingStrategyType;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.namespace.InvalidGetTURLNullPrefixAttributeException;
import it.grid.storm.namespace.InvalidGetTURLProtocolException;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRIImpl;
import it.grid.storm.namespace.TURLBuildingException;
import it.grid.storm.namespace.model.Authority;
import it.grid.storm.namespace.model.Capability;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.PoolMember;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.namespace.model.ProtocolPool;
import it.grid.storm.namespace.model.StoRIType;
import it.grid.storm.namespace.model.TransportProtocol;
import it.grid.storm.namespace.model.VirtualFS;
import org.junit.Test;

public class StoRIImplTest
{

    @Test
    public final void testGetTURL()
    {
        VirtualFS vfs = new VirtualFS(true);
        try
        {
            vfs.setRoot("/ciao/banane");
        } catch(NamespaceException e)
        {
            fail("Unable to set root to VFS " + e);
        }
        Capability cap = null;
        try
        {
            cap = new Capability();
        } catch(NamespaceException e)
        {
            fail("Unable to create the capabilities " + e);
        }
        
        ArrayList<PoolMember> poolMembers = new ArrayList<PoolMember>();
        String hostname;
        int port;
        Authority auth;
        TransportProtocol trans;
        hostname = "omii003-vm01.cnaf.infn.it";
        port = 2811;
        auth = new Authority(hostname, port);
        trans = new TransportProtocol(Protocol.GSIFTP, auth);
        poolMembers.add(new PoolMember(0, trans));
        
        hostname = "omii005-vm02.cnaf.infn.it";
        port = 2811;
        auth = new Authority(hostname, port);
        trans = new TransportProtocol(Protocol.GSIFTP, auth);
        poolMembers.add(new PoolMember(1, trans));
    
        hostname = "etics-06-vm01.cnaf.infn.it";
        port = 2811;
        auth = new Authority(hostname, port);
        trans = new TransportProtocol(Protocol.GSIFTP, auth);
        poolMembers.add(new PoolMember(2, trans));
        
        try
        {
            cap.addProtocolPoolBySchema(Protocol.GSIFTP, new ProtocolPool(BalancingStrategyType.SMART_RR,
                                                                          poolMembers));
        } catch(NamespaceException e)
        {
            fail("Unable to add protocol pool to capabilities: NamespaceException " + e.getMessage());
        }
        cap.addTransportProtocolByScheme(Protocol.GSIFTP, trans);
        
        vfs.setCapabilities(cap);
        MappingRule winnerRule = null;
        String relativeStFN = "mamma/mia";
        StoRIType type = null;
        StoRIImpl s = new StoRIImpl(vfs, winnerRule, relativeStFN, type);
        TURLPrefix pref = new TURLPrefix();
        pref.addProtocol(Protocol.GSIFTP);
        for (int i = 0; i < 3; i++)
        {
            try
            {
                System.err.println("Built TURL = " + s.getTURL(pref));
            } catch(InvalidGetTURLNullPrefixAttributeException e)
            {
                fail("Unable to get the turl " + e);
            } catch(InvalidGetTURLProtocolException e)
            {
                fail("Unable to get the turl " + e);
            } catch(TURLBuildingException e)
            {
                fail("Unable to get the turl " + e);
            }
        }
    }

    @Test
    public final void testGetTURLEmptyPool()
    {
        VirtualFS vfs = new VirtualFS(true);
        try
        {
            vfs.setRoot("/ciao/banane");
        } catch(NamespaceException e)
        {
            fail("Unable to set root to VFS " + e);
        }
        Capability cap = null;
        try
        {
            cap = new Capability();
        } catch(NamespaceException e)
        {
            fail("Unable to create the capabilities " + e);
        }
        try
        {
            cap.addProtocolPoolBySchema(Protocol.GSIFTP, new ProtocolPool(Protocol.GSIFTP, BalancingStrategyType.SMART_RR, new LinkedList<PoolMember>()));
            fail("it should fail");
        } catch(NamespaceException e)
        {
        }
    }
    
    @Test
    public final void testGetTURLMultipleProtocols()
    {
        VirtualFS vfs = new VirtualFS(true);
        try
        {
            vfs.setRoot("/ciao/banane");
        } catch(NamespaceException e)
        {
            fail("Unable to set root to VFS " + e);
        }
        Capability cap = null;
        try
        {
            cap = new Capability();
        } catch(NamespaceException e)
        {
            fail("Unable to create the capabilities " + e);
        }
        
        ArrayList<PoolMember> poolMembers = new ArrayList<PoolMember>();
        String hostname;
        int port;
        Authority auth;
        TransportProtocol trans;
        hostname = "omii003-vm01.cnaf.infn.it";
        port = 2811;
        auth = new Authority(hostname, port);
        trans = new TransportProtocol(Protocol.GSIFTP, auth);
        poolMembers.add(new PoolMember(0, trans));
        
        try
        {
            cap.addProtocolPoolBySchema(Protocol.GSIFTP, new ProtocolPool(BalancingStrategyType.SMART_RR,
                                                                          poolMembers));
        } catch(NamespaceException e)
        {
            fail("Unable to add protocol pool to capabilities: NamespaceException " + e.getMessage());
        }
        cap.addTransportProtocolByScheme(Protocol.GSIFTP, trans);
        cap.addTransportProtocol(trans);
        
        hostname = "omii003-vm01.cnaf.infn.it";
        port = 8088;
        auth = new Authority(hostname, port);
        trans = new TransportProtocol(Protocol.FILE, auth);
        cap.addTransportProtocolByScheme(Protocol.FILE, trans);
        cap.addTransportProtocol(trans);
        
        vfs.setCapabilities(cap);
        MappingRule winnerRule = null;
        String relativeStFN = "mamma/mia";
        StoRIType type = null;
        StoRIImpl s = new StoRIImpl(vfs, winnerRule, relativeStFN, type);
        TURLPrefix pref = new TURLPrefix();
        pref.addProtocol(Protocol.GSIFTP);
        pref.addProtocol(Protocol.FILE);
        for (int i = 0; i < 3; i++)
        {
            try
            {
                System.err.println("Built TURL = " + s.getTURL(pref));
            } catch(InvalidGetTURLNullPrefixAttributeException e)
            {
                fail("Unable to get the turl " + e);
            } catch(InvalidGetTURLProtocolException e)
            {
                fail("Unable to get the turl " + e);
            } catch(TURLBuildingException e)
            {
                fail("Unable to get the turl " + e);
            }
        }
    }
    
}
