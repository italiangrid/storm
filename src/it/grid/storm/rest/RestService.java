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
 * 
 */
package it.grid.storm.rest;

import it.grid.storm.config.Configuration;
import it.grid.storm.info.InfoService;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.grizzly.http.SelectorThread;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;

/**
 * @author zappi
 */
public class RestService {

    private static final Logger log = LoggerFactory.getLogger(RestService.class);
    
    private static Configuration config = Configuration.getInstance();
    
    public static URI BASE_URI =  getBaseURI();
    
    private static SelectorThread httpThreadSelector;
       
    @SuppressWarnings("serial")
	private static final HashSet<String> resources = new HashSet<String>(){ 
    	{
    		add("it.grid.storm.tape.recalltable.resources");
    		add(InfoService.getResourcePackage());
    		add("it.grid.storm.authz.remote.resource");
    		add("it.grid.storm.namespace.remote.resource");
    	}
    };
 
    
    private static int getPort() {
        int restServicePort = config.getRestServicesPort();
        log.debug("Rest Service Port =  " + restServicePort);
        return restServicePort;
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost/").port(RestService.getPort()).build();
    }
    
 
    /**
     * Build the required initialization parameters Map for Grizzly adding the ';' separated package names
     * containing rest resources
     * 
     * @return the produced map
     */
    private static Map<String, String> buildInitParams()
    {
        Map<String, String> initParams = new HashMap<String, String>();
        String key = PackagesResourceConfig.PROPERTY_PACKAGES;
        String value = "";
        for(String resource : resources)
        {
            value += resource + ";"; 
        }
        initParams.put(key, value);
        return initParams;
    }


    /**
     * @throws IOException
     */
    public static void startServer() throws IOException
    {
        log.info("Starting Grizzly Web Server ... ");
        SelectorThread threadSelector = GrizzlyWebContainerFactory.create(BASE_URI, buildInitParams());
        log.info(" ... started!");
        httpThreadSelector = threadSelector;
        return;
    }

    /**
     * NOTE: never used
     * @throws IOException
     */
    public static void startWithAdapter() throws IOException {
        GrizzlyWebServer ws = new GrizzlyWebServer(config.getRestServicesPort());
        ServletAdapter jerseyAdapter = new ServletAdapter();
        jerseyAdapter.setServletInstance(new com.sun.jersey.spi.container.servlet.ServletContainer());
        Map<String,String> initParam = buildInitParams();
        String key = PackagesResourceConfig.PROPERTY_PACKAGES;
        jerseyAdapter.addInitParameter(key, initParam.get(key));
        ws.addGrizzlyAdapter(jerseyAdapter, new String[] { "/" });
        ws.getSelectorThread().enableMonitoring();
        ws.start();
    }

    /**
     * @throws IOException
     */
    public static void stop() throws IOException {
        httpThreadSelector.stopEndpoint();
    }
}
