/**
 * 
 */
package it.grid.storm.tape.recalltable;

import it.grid.storm.config.Configuration;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.jersey.api.core.PackagesResourceConfig;

/**
 * @author zappi
 *
 */
public class RecallTableService {

    private static final Logger log = LoggerFactory.getLogger(RecallTableService.class);
    private static Configuration config = Configuration.getInstance();

    public static final URI BASE_URI = RecallTableService.getBaseURI();

    private static int getPort() {
        int recallTableServicePort = config.getRecallTableServicePort();
        log.debug("TableRecall Service Port =  " + recallTableServicePort);
        return recallTableServicePort;
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost/").port(RecallTableService.getPort()).build();
    }

    public static void start() throws IOException {
        GrizzlyWebServer ws = new GrizzlyWebServer(RecallTableService.getPort());
        ServletAdapter jerseyAdapter = new ServletAdapter();
        jerseyAdapter.setServletInstance(new com.sun.jersey.spi.container.servlet.ServletContainer());
        jerseyAdapter.addInitParameter(PackagesResourceConfig.PROPERTY_PACKAGES,
                "it.grid.storm.tape.recalltable.resources");
        ws.addGrizzlyAdapter(jerseyAdapter, new String[] { "/" });
        ws.getSelectorThread().enableMonitoring();
        ws.start();
    } 
    
}
