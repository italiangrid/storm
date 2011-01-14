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
package it.grid.storm.tape.recalltable;

import it.grid.storm.config.Configuration;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
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
public class RecallTableService {

    private static final Logger log = LoggerFactory.getLogger(RecallTableService.class);
    private static Configuration config = Configuration.getInstance();
    private static SelectorThread httpThreadSelector;
    public static final URI BASE_URI = RecallTableService.getBaseURI();

    private static int getPort() {
        int recallTableServicePort = config.getRecallTableServicePort();
        log.debug("TableRecall Service Port =  " + recallTableServicePort);
        return recallTableServicePort;
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost/").port(RecallTableService.getPort()).build();
    }

    protected static SelectorThread startServer() throws IOException {
        final Map<String, String> initParams = new HashMap<String, String>();
        initParams.put(PackagesResourceConfig.PROPERTY_PACKAGES, "it.grid.storm.tape.recalltable.resources");

        log.info("Starting RecallTable Servive ... ");
        SelectorThread threadSelector = GrizzlyWebContainerFactory.create(BASE_URI, initParams);
        log.info(" ... started!");
        return threadSelector;
    }

    public static void startWithAdapter() throws IOException {
        GrizzlyWebServer ws = new GrizzlyWebServer(RecallTableService.getPort());
        ServletAdapter jerseyAdapter = new ServletAdapter();
        jerseyAdapter.setServletInstance(new com.sun.jersey.spi.container.servlet.ServletContainer());

        jerseyAdapter.addInitParameter(PackagesResourceConfig.PROPERTY_PACKAGES,
                                       "it.grid.storm.tape.recalltable.resources");
        System.out.println("Property packages : " + PackagesResourceConfig.PROPERTY_PACKAGES);
        ws.addGrizzlyAdapter(jerseyAdapter, new String[] { "/" });
        ws.getSelectorThread().enableMonitoring();
        ws.start();
    }

    public static void start() throws IOException {

        httpThreadSelector = startServer();

    }

    public static void stop() throws IOException {
        httpThreadSelector.stopEndpoint();
    }

}
