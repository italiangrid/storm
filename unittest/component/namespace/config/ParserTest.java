package component.namespace.config;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.PropertyConfigurator;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.config.xml.XMLNamespaceLoader;
import java.util.TimerTask;
import it.grid.storm.namespace.config.xml.XMLReloadingStrategy;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.*;
import java.util.Timer;
import it.grid.storm.namespace.config.NamespaceLoader;
import it.grid.storm.namespace.config.xml.XMLParserUtil;
import it.grid.storm.namespace.config.xml.XMLNamespaceParser;
import org.apache.commons.configuration.XMLConfiguration;


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

  private static Log log = LogFactory.getLog(ParserTest.class);

  private void init() {


    boolean jdk14Logger = (log instanceof Jdk14Logger);
    boolean log4jlog = (log instanceof Log4JLogger);

    if (jdk14Logger) {
      System.out.println("Using Jdk14Logger = " + jdk14Logger);
    }
    if (log4jlog) {
      System.out.println("Using Log14Logger = " + log4jlog);
      String logConfigFile = System.getProperty("user.dir") + File.separator +
          "unittest" + File.separator + "log4j_for_testing.properties";
      System.out.println("config file = " + logConfigFile);
      PropertyConfigurator.configure(logConfigFile);
    }


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
