package it.grid.storm.balancer.test;

import it.grid.storm.balancer.Balancer;
import it.grid.storm.balancer.StrategyFactory;
import it.grid.storm.balancer.StrategyType;
import it.grid.storm.balancer.ftp.FTPNode;

public class BalancerTestUnit {

    private StrategyFactory fact = new StrategyFactory();

    public static void main(String[] args) {

        Balancer<FTPNode> bl = new Balancer<FTPNode>();

        FTPNode nd1 = new FTPNode();
        nd1.setHostname("host1");

        FTPNode nd2 = new FTPNode();
        nd2.setHostname("host2");

        FTPNode nd3 = new FTPNode();
        nd3.setHostname("host3");

        bl.addElement(nd1);
        bl.addElement(nd2);
        bl.addElement(nd3);



        System.out.println("%%%%%%%%%%%%%%%%%%%%%");
        bl.setStrategy(StrategyType.ROUNDROBIN);
        System.out.println(bl);
        for(int i=0;i<10;i++)
            System.out.println("Node: "+bl.getNextElement().getHostName());

        System.out.println("%%%%%%%%%%%%%%%%%%%%%");
        bl.setStrategy(StrategyType.RANDOM);
        System.out.println(bl);

        for(int i=0;i<10;i++)
            System.out.println("Node: "+bl.getNextElement().getHostName());


        System.out.println("%%%%%%%%%%%%%%%%%%%%%");
        bl.setStrategy(StrategyType.WEIGHT);
        System.out.println(bl);

        for(int i=0;i<50;i++)
            System.out.println("Node: "+bl.getNextElement().getHostName());

        System.out.println("%%%%%%%%%%%%%%%%%%%%%");
        bl.setStrategy(StrategyType.GFTPLOAD);
        System.out.println(bl);

        for(int i=0;i<10;i++)
            System.out.println("Node: "+bl.getNextElement().getHostName());

    }

}
