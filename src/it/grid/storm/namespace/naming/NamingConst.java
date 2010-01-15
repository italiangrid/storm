package it.grid.storm.namespace.naming;

import it.grid.storm.config.Configuration;

import java.util.Properties;

public class NamingConst {

    /**
     * The separator character used in file paths.
     */
    public static final char SEPARATOR_CHAR = '/';

    /**
     * The separator used in file paths.
     */
    public static final String SEPARATOR = "/";

    /**
     * The absolute path of the root of a file system.
     */
    public static final String ROOT_PATH = "/";

    private static NamingConst instance = new NamingConst();
    private final Properties prop = new Properties();

    private final Configuration config;

    private NamingConst() {
        config = Configuration.getInstance();
    }

    private Properties getProperties() {
        return prop;
    }

    public static String getServiceDefaultHost() {
        return instance.config.getServiceHostname();
    }

    public static int getServicePort() {
        return instance.config.getServicePort();
    }

    public static String getServiceSFNQueryPrefix() {
        return "SFN";
        // return instance.config.getSFNQueryStringPrefix();
    }

    // public static boolean isWithQueryStringForm() {
    // return instance.config.getSURLInQueryForm();
    // }

    // public static String getServiceQueryPrefix() {
    // StringBuffer result = new StringBuffer("");
    // if (isWithQueryStringForm()) {
    // result.append(getServiceEndpoint());
    // result.append("?");
    // result.append(getServiceSFNQueryPrefix());
    // result.append("=");
    // }
    // return result.toString();
    // }

}
