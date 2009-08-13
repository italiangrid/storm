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
 *
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

        System.out.println("Starting grizzly...");
        SelectorThread threadSelector = GrizzlyWebContainerFactory.create(BASE_URI, initParams);
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
