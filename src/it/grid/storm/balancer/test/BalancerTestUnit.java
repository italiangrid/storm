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
