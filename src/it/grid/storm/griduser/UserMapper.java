package it.grid.storm.griduser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserMapper {

    private static final Logger log = LoggerFactory.getLogger(UserMapper.class);

    private static long delay = 1; // delay for 5 sec.
    private static long period = 18000; // repeat every 5 minuti.

    private static String mappingFileName = "mapping.properties";
    private static String mappingFile;

    private static UserMapper istance = null;

    private Properties mapping;



    /**
     * Empty Constructor;
     */
    private UserMapper() {
        mapping = readMapping();
    }

    public static UserMapper getIstance() {
        if (istance == null) {
            istance = new UserMapper();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                //ex.printStackTrace();
                log.error("Unable to load/parse mapping file",ex);
            }

        }
        return istance;
    }

    /**
     *  ONLY FOR DEMO !!!
     */
    public String getFixedUID(String userDN) {
        String result; //= "storm"; //Default value

        String DN = userDN.toLowerCase();
        result = DN;
        if (DN.equals("joda")) {
            result = "tiziana";
        }
        if (DN.equals("RiTZ")) {
            result = "ritz";
        }

        if (DN.indexOf("flavia.donno") != -1) {
            result = "flavia";
        }
        return result;
    }


    /**
     * @todo: manca la gestione del caso in cui lo user non è presente nel
     * file di mapping..
     *
     * --> generazione di una eccezione.
     */

    public String getUID(String subject) {
        String result = "undef";

        String patternStr = "/";
        String[] tokens = subject.split(patternStr);

        //System.out.println("fields = "+tokens.length);
        if (tokens.length > 1) {
            //Mapping based with CN field within FQDN.
            String cn = search("CN=", tokens);
            log.debug("CN chiave di ricerca = "+cn);
            result = mapping.getProperty(cn);
            log.debug("RESULT = "+result);
        } else {
            //Mapping based with no FQDN.
            result = mapping.getProperty(subject);
        }
        if (result==null) {
            log.warn("Mapping does not found with grid user = "+subject);
            result = "storm";
        }
        return result;
    }


    /**
     * Every time period (at least 5 minutes is suggested) the task read the
     * user mapping files
     */
    public Properties readMapping() {

        Timer timer = new Timer();
        mapping = new Properties();
        char sep = File.separatorChar;
        System.out.println("Mapping File Name");
        mappingFile = System.getProperty("user.dir") + sep + "config" + sep + mappingFileName;

        log.debug(mappingFile);

        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                try {
                    mapping.load(new FileInputStream(mappingFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        timer.scheduleAtFixedRate(tt, delay, period);
        return mapping;
    }

    /**
     *
     *
     * @param subject String
     * @return String
     */
    public String getGridUserName(String subject) {
        String patternStr = "/";
        String[] fields = subject.split(patternStr);
        String cn = null;
        for (int i = 0; i < fields.length; i++) {
            log.debug("fields " + i + " = " + fields[i]);
            if (fields[i].startsWith("CN".toUpperCase())) {
                cn = fields[i].substring(3, fields[i].length());
            }
        }
        if (cn != null) {
            log.debug("cn estratto = '" + cn + "'");
            cn = (cn.replaceAll(" ", "_")).toLowerCase();
            log.debug("cn elab = '" + cn + "'");
        }
        else {
            int end = 8;
            if ( subject.length()<8 ) {
                end = subject.length();
            }
            cn = subject.substring(0,end);
        }
        return cn;
    }

    /**
     *
     */
    public void storeProperties() {
        mapping = new Properties();
        char sep = File.separatorChar;
        mappingFile = System.getProperty("user.dir") + sep + "config" + sep + mappingFileName;

        mapping.setProperty("Ciccio Ciccio", "RitZ");

        try {
            mapping.store(new FileOutputStream(mappingFile), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param start String
     * @param fields String[]
     * @return String
     */
    public String search(String start, String[] fields) {
        String result = null;
        String patternStr = "=";
        String[] parts;
        for (String field : fields) {
            if (field.startsWith(start)) {
                parts = field.split(patternStr);
                result = parts[1];
            }
        }
        return result;
    }

    public static void main(String[] args) {


        UserMapper map = UserMapper.getIstance();
        String res;
        res = map.getUID(args[0]);
        System.out.println("Grid User  = " + args[0]);
        System.out.println("Local User = " + res);

        System.out.println("---------------");

        String subject = "subject=/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Riccardo Zappi/emailAddress=riccardo.zappi@cnaf.infn.it";
        res = map.getUID(subject);
        System.out.println("Grid User  = " + subject);
        System.out.println("Local User = " + res);

    }

}
