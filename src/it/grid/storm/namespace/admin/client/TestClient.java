package it.grid.storm.namespace.admin.client;

import java.io.*;
import java.net.*;
import java.util.logging.*;

public class TestClient {

    private static final Logger logger = Logger.getLogger("it.grid.storm.namespace.admin.client");
    static {
        System.setProperty("java.util.logging.config.file", "./etc/adminserver-log.properties");
        try {
            LogManager.getLogManager().readConfiguration();
        }
        catch (Exception e) {
            System.err.println("Could not read logging configuration: " + e.getMessage());
            System.err.println("Proceeding...");
        }
    }

    private static InetAddress address;
    private static int port;
    NSServerConnection server;

    public TestClient() {
        try {
            server = new NSServerConnection(true);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void connect() {
        if (server != null) {
            try {
                server.connect();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TestClient testclient = new TestClient();
        testclient.connect();
    }

}
