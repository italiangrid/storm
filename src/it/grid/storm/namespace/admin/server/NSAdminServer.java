package it.grid.storm.namespace.admin.server;

import java.io.*;

import org.quickserver.net.*;
import org.quickserver.net.server.*;
import it.grid.storm.common.resource.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
class NSAdminServer {

    public static String version = "1.0";

    static final int serverBundleHandle = BundleHandler.getBundleHandle("namespace-messages");

    private static NSAdminServer adminInstance = null;

    private String configFile;
    private QuickServer stormQuickServer;

    /**
     * private constructor
     */
    private NSAdminServer() {
        /**
         * Retrieve information from Configuration File
         */
        // 1. StoRM Admin Server config File Name
        String configFileName = "adminserver-config.xml";
        // 2. Config path
        System.out.println("User Dir " + System.getProperty("user.dir"));

        String configPath = "etc";
        // 3. Build relative Path of StoRM Admin
        configFile = configPath + File.separator + configFileName;
        // 4. Load configuration
        loadServerConfiguration(configFile);
        // 5. Start server
        // Done authomatically by Server Hook mechanism.
        //startServer();

    }

    /**
     * To retrieve the single istance of AdminServer
     *
     * @return AdminServer
     */
    public static NSAdminServer getAdminInstance() {
        if (adminInstance == null) {
            adminInstance = new NSAdminServer();
        }
        return adminInstance;
    }

    /**
     *
     * @param relativePath String
     */
    private void loadServerConfiguration(String relativePath) {

        try {
            stormQuickServer = QuickServer.load(configFile);
        }
        catch (AppException e) {
            System.out.println("Error in server : " + e);
            e.printStackTrace();
        }
    }

    /**
     * Start the Admin server
     *
     *   NOTE: if you use the Server Hook this method SHOULD NOT BE used!!
     */
    private void startServer() {
        /**
         * This parameter are included within the SERVER configuration file
         *
                 String cmdHandle = "namespace.NSCommandHandler";
                 String authHandle = "namespace.NSQuickAuthenticator";
                 String dataHandle = "namespace.NSClientData";
                 int port = 12434;
                 stormQuickServer = new QuickServer();
                 stormQuickServer.setClientCommandHandler(cmdHandle);
                 stormQuickServer.setPort(port);
                 stormQuickServer.setName("StoRM Admin Server, v" + version);
                 stormQuickServer.setClientAuthenticationHandler(authHandle);
                 stormQuickServer.setClientData(dataHandle);
         **/


        try {
            stormQuickServer.startServer();
        }
        catch (AppException e) {
            System.err.println("Error in server : " + e);
            e.printStackTrace();
            System.exit( -1);
        }

    }

    public static void main(String[] args) {
        System.out.println("User dir" + System.getProperty("user.dir"));
        NSAdminServer admin = NSAdminServer.getAdminInstance();

    }
}
