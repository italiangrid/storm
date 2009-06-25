package component.namespace.config;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.config.xml.XMLNamespaceLoader;
import it.grid.storm.namespace.config.xml.XMLNamespaceParser;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.utiltest.SetUpTest;




/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public class ParserTest {

    private NamespaceInterface namespace;
    private XMLNamespaceParser parser;
    private XMLNamespaceLoader loader;
    private XMLConfiguration config;

    private static Logger log = LoggerFactory.getLogger(ParserTest.class);

    private void init() {
        System.out.println("Using SLF4J and LogBack");
        String unitTestPath = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        String logConfigFile = unitTestPath + "logging-test.xml";
        SetUpTest.init(logConfigFile);

        this.parser = (XMLNamespaceParser) NamespaceDirector.getNamespaceParser(false,true);
        this.loader = (XMLNamespaceLoader) NamespaceDirector.getNamespaceLoader(false,true);
        this.config = (XMLConfiguration) loader.getConfiguration();


    }

    private void testReload(){
        Peeper peeper = new Peeper();
        Timer timer = new Timer();
        timer.schedule(peeper, 1, 3000);

    }


    public static void main(String[] args) {
        ParserTest test = new ParserTest();
        test.init();
        test.testReload();
    }


    /**
     *
     * <p>Title: </p>
     *
     * <p>Description: </p>
     *
     * <p>Copyright: Copyright (c) 2006</p>
     *
     * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
     *
     * @author Riccardo Zappi
     * @version 1.0
     */
    private class Peeper extends TimerTask {


        public Peeper() {

        }


        @Override
        public void run() {
            String root = null;
            try {
                root = parser.getVFS("cnaf-FS").getRootPath();
                log.debug("      ROOT = "+root);
            }
            catch (NamespaceException ex) {
                ex.printStackTrace();
            }
        }

    }


}
