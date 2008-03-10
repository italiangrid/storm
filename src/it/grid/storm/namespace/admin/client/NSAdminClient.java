package it.grid.storm.namespace.admin.client;

import java.io.*;
import java.util.logging.*;

import it.grid.storm.namespace.admin.common.*;

/**
 * <p>This class encapsulates the communication with the namespace
 * server, hiding the raw protocol and providing a simple and
 * convenient API for Java applications.
 */

public class NSAdminClient {

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

    public NSAdminClient() {
    }

    /**
     * Example:
     *   1. list VFS
     *   2. list VFS --name cnaf
     *   3. list MAPRULE
     *   4. list VFS --detail 0
     *   5. list VFS --detail 5
     *   6. list VO-VIEW
     *   7. list VFS --name cnaf --detail 3
     *   8. list
     *
     * @param element String
     * @param restriction String
     * @throws IOException
     * @throws CommandException
     */
    public void list(String element, String name, int detailLevel) throws IOException, CommandException {

    }

    public void reload() throws IOException, CommandException {

    }

    public void status() throws IOException, CommandException {

    }

    public void quit() throws IOException, CommandException {

    }

    public static void main(String[] args) {
        NSAdminClient nsadminclient = new NSAdminClient();
    }
}
