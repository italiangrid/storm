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

/**
 * This class represents the Synchronous Call xmlrpc Server .
 * This class hava a set of Handler that manage the FE call invoking the right
 * BackEnd manager.
 *
 * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.xmlrpc;

import it.grid.storm.common.HostLookup;
import it.grid.storm.config.Configuration;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLRPCHttpServer {
    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(XMLRPCHttpServer.class);
    private int _port; //Default port for xmlRpc Server
    private int _secure_port; //Default port for SecureXmlRpc Server
    private List fe_hostname_array; //FE machines hostname array. In case of distributed FE-BE installation
    private List fe_IP_array; //FE machines IPs array. Needed in case of distributed FE-BE installation with dynamic DNS.
    private Configuration config;

    public XMLRPCHttpServer() {
        config = Configuration.getInstance();
        //Default value
        _port = 8080;
        _secure_port = 8081;
    };


    /* (non-Javadoc)
     * @see it.grid.storm.xmlrpc.XMLRPCServerInterface#createServer()
     */
    public void createServer() {
        try {

            //
            // WebServer is a sample http server
            // Used to provide xmlRpcServer on http
            // XmlRpc serve is multithreded, one thred for each
            // Connection requested.
            // Default MAX_NUMBER of thread is 100, but can be changed.
            //

            // TODO
            // Default port is 8080
            // Getting the port from Confiuration file!
            _port = config.getXmlRpcServerPort();

            //Invoke me as <http://localhost:port/RPC2>
            log.info("[xmlrpc server] Starting server on port: " + _port);
            WebServer xmlrpcWebServer = new WebServer(_port);

            //Add Client Filtering
            log.info("[xmlrpc server] IP requests filtering enabled.");
            //xmlrpcWebServer.setParanoid(true);

            //By default the localhost interface is enabled.
            //Accept Client only from loopback inerface
            log.info("[xmlrpc server] Accepting requests from: 127.0.0.1");
            //xmlrpcWebServer.acceptClient("127.0.0.1");

            //Get List of machine names that host a FE service
            //for this StoRM instance

            fe_hostname_array = config.getListOfMachineNames();
            //For each host name set the IP as authorized
            HostLookup hl = new HostLookup();
            for (Iterator it = fe_hostname_array.iterator(); it.hasNext(); ) {
                String hostname = (String) it.next();
                try {
                    String IP = hl.lookup(hostname);
                    log.info("[xmlrpc server] Accepting requests from " + IP + " (" + hostname + ")");
                    //xmlrpcWebServer.acceptClient(IP);
                }
                catch (UnknownHostException ex) {
                    log.warn("Synchserver: IP for hostname: " + hostname + " cannot be resolved.");
                }
            }

            /**
             * Dynamic DNS for the FE machine.
             * Where a dynamic DNS is used to load balancing on the FE machines,
             * the hostname IP lookup above cannot be used.
             * In that case the configuration properties files must contains
             * the set of the specific IPs for the frontend machines.
             * In that way, the xmlrpc server can be configured to
             * accept request from each particulare FE machine.
             */

            fe_IP_array = config.getListOfMachineIPs();
            //For each host name set the IP as authorized
            for (Iterator it = fe_IP_array.iterator(); it.hasNext(); ) {
                String IP = (String) it.next();
                log.info("[xmlrpc server] Accepting requests from " + IP);
                //xmlrpcWebServer.acceptClient(IP);
            }

            XmlRpcServer xmlRpcServer = xmlrpcWebServer.getXmlRpcServer();
            PropertyHandlerMapping phm = new PropertyHandlerMapping();

            //use reflection for (dynamic) mapping
            //DynamicHandlerMapping dhm = new DynamicHandlerMapping(new  TypeConverterFactoryImpl(), true);

            //xmlrpcWebServer.getXmlRpcServer().setMaxThreads(arg0);

            // Add handler
            //xmlrpcWebServer.addHandler("synchcall", new SynchCallServer());
            phm.addHandler("synchcall", XMLRPCMethods.class);

            //Set the number of thread to use
            //xmlrpcWebServer.
            xmlRpcServer.setHandlerMapping(phm);

            //Set Number of thread
            //Get from configuration FILE
            int num_thread = config.getMaxXMLRPCThread();
            xmlRpcServer.setMaxThreads(num_thread);

            // Starting xmlrpc server
            xmlrpcWebServer.start();
            //Insecure...
            log.info("[xmlrpc server] Server running...");

            //
            // Secure WebServer is a WebServer with SSL support.
            // As server certificate can be used the host certificate.
            // (or can be requestes a service certifcate, but is not necessary...)
            //

            //TODO SECURE SERVER
            //Default port for secure version is 80443
            //Get port number from configuration file
            /*			_secure_port = config.getSecureXmlRpcServerPort();
       log.debug("Starting Secure Server server on port: "+_secure_port);
       SecureWebServer xmlrpcServer_secure = new SecureWebServer(_secure_port);

       //Debug Print for Secure Server Attributes

       log.debug("Working Directory:"+System.getProperty("user.dir"));
       log.debug("getKeyManagerType(): "+SecurityTool.getKeyManagerType());
       log.debug("getKeyStore(): "+SecurityTool.getKeyStore());
       SecurityTool.setKeyStore("fake_cert");
       //Setting path fpor the host cert
       //SecurityTool.setKeyStore("hostcert");

       log.debug("After Setting: getKeyStore(): "+SecurityTool.getKeyStore());

       log.debug("getKeyStorePassword(): "+SecurityTool.getKeyStorePassword() );
       SecurityTool.setKeyStorePassword("fake_cert");


       log.debug("After Setting getKeyStorePassword(): "+SecurityTool.getKeyStorePassword() );


       log.debug("getKeyStoreType(): "+SecurityTool.getKeyStoreType() );
       log.debug("getProtocolHandlerPackages(): "+SecurityTool.getProtocolHandlerPackages());
       log.debug("getSecurityProtocol(): "+SecurityTool.getSecurityProtocol() );
       log.debug("getSecurityProviderClass(): "+SecurityTool.getSecurityProviderClass());

       log.debug("getTrustStore() : "+SecurityTool.getTrustStore() );
       //SecurityTool.setTrustStore("");
       log.debug("After Setting:getTrustStore() : "+SecurityTool.getTrustStore() );

       log.debug("getTrustStorePassword() : "+SecurityTool.getTrustStorePassword() );
       //SecurityTool.setTrustStorePassword("ciccio");
       log.debug("After Setting:getTrustStorePassword() : "+SecurityTool.getTrustStorePassword());

       log.debug("getTrustStoreType() : "+SecurityTool.getTrustStoreType() );



       //
       // Adding handler on SecureServer
       //

       xmlrpcServer_secure.addHandler("synchcall", new SynchCallServer());
       //Starting secure Server
       xmlrpcServer_secure.start();
       log.debug("server secure running...");

             */

        }
        catch (Exception ServerStartError) {
            log.error("SynchCallServer " + ServerStartError.toString());
        }
    }

    /* (non-Javadoc)
     * @see it.grid.storm.xmlrpc.XMLRPCServerInterface#logExecution(it.grid.storm.health.OperationType, java.lang.String, long, long, boolean)
     */
    /**
  public void logExecution(OperationType opType, String dn, long startTime, long duration, boolean successResult) {
    BookKeeper bk = HealthDirector.getHealthMonitor().getBookKeeper();
    LogEvent event = new LogEvent(opType, dn, startTime, duration, successResult);
    bk.addLogEvent(event);
  }

  public static void main(String[] args) {
    XMLRPCHttpServer s = new XMLRPCHttpServer();
    s.createServer();
  }

     **/
}
