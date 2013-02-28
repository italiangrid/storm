package unitTests;


import static org.junit.Assert.fail;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import it.grid.storm.balancer.BalancingStrategy;
import it.grid.storm.balancer.BalancingStrategyException;
import it.grid.storm.balancer.BalancingStrategyFactory;
import it.grid.storm.balancer.BalancingStrategyType;
import it.grid.storm.balancer.ftp.FTPNode;
import org.junit.Before;
import org.junit.Test;

// @RunWith(Parameterized.class)
public class SmartRoundRobinStrategyTest
{

// @Parameterized.Parameters
// public static Collection<Object[]> data()
// {
// Object[][] prova = new Object[10][0];
// return Arrays.asList(prova);
// }

    public SmartRoundRobinStrategyTest()
    {

    }

    BalancingStrategy<FTPNode> balancingStrategy = null;

    int poolSize = 0;
    
    @Before
    public void setUp()
    {
        LinkedList<FTPNode> nodeList = new LinkedList<FTPNode>();
        
        String hostname = "omii005-vm02.cnaf.infn.it";
        int port = 2811;
        FTPNode ftpNode = new FTPNode(hostname, port);
        nodeList.add(ftpNode);
        
        hostname = "omii003-vm01.cnaf.infn.it";
        ftpNode = new FTPNode(hostname, port);
        nodeList.add(ftpNode);
        
        port = 9998;
        ftpNode = new FTPNode(hostname, port);
        nodeList.add(ftpNode);
        
        port = 8080;
        ftpNode = new FTPNode(hostname, port);
        nodeList.add(ftpNode);
        
        port = 4444;
        ftpNode = new FTPNode(hostname, port);
        nodeList.add(ftpNode);
        
        port = 8088;
        ftpNode = new FTPNode(hostname, port);
        nodeList.add(ftpNode);
        
        port = 8443;
        ftpNode = new FTPNode(hostname, port);
        nodeList.add(ftpNode);
        
        port = 8444;
        ftpNode = new FTPNode(hostname, port);
        nodeList.add(ftpNode);
        
        hostname = "banane.cnaf.infn.it";
        ftpNode = new FTPNode(hostname, port);
        nodeList.add(ftpNode);
        
        poolSize = nodeList.size();
        this.balancingStrategy = BalancingStrategyFactory.getBalancingStrategy(BalancingStrategyType.SMART_RR,
                                                                               nodeList);
    }

    @Test
    public void testGetNextElement()
    {
        FTPNode node = null;
        HashMap<FTPNode, Integer> map = new HashMap<FTPNode, Integer>();
        int[] counts = new int[poolSize]; 
        int nextIndex = 0;
        Object o =  new Object();
        for (int i = 0; i < (poolSize * 2); i++)
        {
            try
            {
                node = balancingStrategy.getNextElement();
                Integer index = map.get(node);
                if(index  == null)
                {
                    index = nextIndex;
                    map.put(node, index);
                    nextIndex++;
                    if(index >= poolSize)
                    {
                        fail("Pool size in count out of bounds");
                    }
                }
                counts[index] = counts[index] + 1;
                System.out.println("Node: " + node.toString());
                synchronized (o)
                {
                    try
                    {
                        o.wait(1000);
                    } catch(InterruptedException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            } catch(BalancingStrategyException e)
            {
                fail("getNextElement failed: BalancingStrategyException " + e.getMessage());
            }
        }
        for(Entry<FTPNode, Integer> e : map.entrySet())
        {
            System.out.println(e.getKey() + " " + counts[e.getValue()]);
        }
    }

}
