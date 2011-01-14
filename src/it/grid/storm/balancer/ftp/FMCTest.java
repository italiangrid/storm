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

package it.grid.storm.balancer.ftp;

    
 // ############################################################################
 // $Log: CurrentProcs.java,v $
 // Revision 1.2  2008/11/04 14:35:19  galli
 // convert IP address if needed
 //
 // Revision 1.1  2008/11/04 08:18:54  galli
 // Initial revision
 // ############################################################################

import java.net.InetAddress;
import java.net.UnknownHostException;

//import dim.DimCurrentInfo;
//import dim.DimInfo;

 // ############################################################################
/**
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * Authors:
 * 		@author lucamag
 *
 */
public class FMCTest {


    public int getN(String hostName)
    {
        InetAddress addr;
        String baseName;
        try
        {
            addr=InetAddress.getByName(hostName);
        }
        catch(UnknownHostException e)
        {
            System.err.println("Unknown host: \""+hostName+"\"!");
            return -2;
        }
        catch(SecurityException e)
        {
            System.err.println("Security exception: "+e.getMessage()+"!");
            return -3;
        }
        //baseName=addr.getHostName();
        baseName="devrb.cnaf.infn.it";
        int dotPos=baseName.indexOf('.');
        if(dotPos!=-1)baseName=baseName.substring(0,dotPos);
        
        String svcName="/FMC/"+baseName.toUpperCase()+"/ps/ntasks";
        //String svcName=baseName.toUpperCase();

        /* Removed linjdim.so and jdim.jar from build path because useless. If needed u can found them in revisions previous the 06/08/2010.*/
//        return new DimCurrentInfo(svcName,-1).getInt();
        return -1;

        //DimInfo srv = new DimInfo("TEST_IT_INT", -1);
        //return srv.getQuality();

    }
    
//    public static void main(String[] args) {
//        
//        FMCTest t = new FMCTest();
//        System.out.println(System.getProperty("java.library.path"));
//        System.out.println("process: "+t.getN("localhost"));
//    }

 }

