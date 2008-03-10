package component.namespace.main;


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
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.namespace.naming.NamespaceUtil;
import it.grid.storm.common.types.StFN;
import it.grid.storm.common.types.*;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.srm.types.*;
import java.net.URL;
import java.net.*;


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
public class StoRITest {

  private NamespaceInterface namespace;
  private static Log log = LogFactory.getLog(StoRITest.class);

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

    NamespaceDirector.initializeDirector(false,true);
    namespace = NamespaceDirector.getNamespace();

  }

  private void testReload(){
    Peeper peeper = new Peeper();
   Timer timer = new Timer();
   timer.schedule(peeper, 5, 100);

  }


  public StoRI getStoRI(String absolutePath) {
      StoRI stori = null;
      try {
          stori = namespace.resolveStoRIbyAbsolutePath(absolutePath);
      }
      catch (NamespaceException ex) {
          ex.printStackTrace();
      }
      return stori;
  }


  public StoRI createStoRI(String surl) {
      StoRI stori = null;
      try {
          TSURL tsurl = TSURL.makeFromString(surl);
          log.debug(" TSURL : "+tsurl);
          stori = namespace.resolveStoRIbySURL(tsurl);
      }
      catch (NamespaceException ex) {
          ex.printStackTrace();
      }
      catch (InvalidTSURLAttributesException ex) {
          /** @todo Handle this exception */
      }

        return stori;

  }

  public void testNamespaceUtil(String root, String absolutePath){
      String relative = NamespaceUtil.extractRelativePath(root,absolutePath);
      log.debug("Root : "+root);
      log.debug("Absolute Path : "+absolutePath );
      log.debug("Relative Path : "+relative);
  }


  public void testStFN(String stfn) {
    StFN stfnclass = null;
    StoRI stori = null;
    try {
        stfnclass = StFN.make(stfn);
    }
    catch (InvalidStFNAttributeException ex) {
       log.error(" STFN '"+stfn+"' is not valid",ex);
    }
    try {
        stori = namespace.resolveStoRIbyStFN(stfnclass);
    }
    catch (NamespaceException ex1) {
       log.error(" Unable to build StoRI from StFN '"+stfn+"'.",ex1);
    }
    if (stori!=null) {
        String absolutePath = stori.getAbsolutePath();
        log.debug("Absolute PATH : " + absolutePath);
    }
  }


  public void testNamespaceUtil(String stfn) {
      String filename = NamespaceUtil.getFileName(stfn);
      log.debug(" StFN : '"+stfn+"' has file name : "+filename);
      String filePath = NamespaceUtil.getStFNPath(stfn);
      log.debug(" StFN : '"+stfn+"' has path : "+filePath);

  }

  public void testMakeTTURL(String turl) {
    try {
        TTURL tturl = TTURL.makeFromString(turl);
        log.debug("turl : "+turl);
        log.debug("TTURL "+tturl);
    }
    catch (InvalidTTURLAttributesException ex) {
       log.error("Error in TTURL : "+turl,ex);
    }
  }

  public void testURL(){
    try {
        URL url = new URL("http", "testbed006.cnaf.infn.it", 4444, "//egrid/test1");
        log.debug("URL : "+url);
    }
    catch (MalformedURLException ex) {
        log.error("Error in URL "+ex);
    }
  }


  public void testWinnerRule() {
    // Create TSURL
    TSURL surl = TSURL.makeEmpty();
    try {
      surl = TSURL.makeFromStringValidate("srm://ibm139.cnaf.infn.it:8444/srmv2?SFN=/cnaf");
    }
    catch (InvalidTSURLAttributesException ex) {
      ex.printStackTrace();
    }
    log.debug(surl);
    String stfnStr = surl.sfn().stfn().toString();
    log.debug(stfnStr);
    String stfnPath = NamespaceUtil.getStFNPath(stfnStr);
    log.debug(stfnPath);

  }


  public static void main(String[] args) {
    StoRITest test = new StoRITest();
    test.init();
    //StoRI stori = test.getStoRI("mnt/gpfs/cnaf/test/prova1");
    //log.debug(stori);
    StoRI stori = test.createStoRI("srm://egrid-6.egrid.it/cnaf");
    //log.debug(stori);
    //log.debug("SFN : "+stori.getSURL().sfn());
    //log.debug("STFN : "+stori.getSURL().sfn().stfn());
    //Protocol prot = Protocol.FILE;
    //log.debug("TURL : "+stori.getTURL(prot));
    //srm://egrid-6.egrid.it/egrid/amessina/zero
    //test.testNamespaceUtil("/egrid","messina/zero");
    //test.testStFN("/egrid/messina/zero");
    //test.testNamespaceUtil("/egrid/test");
    //test.testNamespaceUtil("/egrid/test/");
    //file:///mnt/gpfs/cnaf/Pippo/file
    //test.testMakeTTURL("file:///mnt/gpfs/cnaf/Pippo/file");
    //test.testURL();
    log.debug("PFN = "+stori.getPFN());
    log.debug("StFN = "+stori.getStFN());
    log.debug("StFN Root = "+stori.getStFNRoot());
    log.debug("StFN Relative StFN Path = "+stori.getRelativePath());
    log.debug("Type = "+stori.getStoRIType());
    TURLPrefix prefix = new TURLPrefix();
    TransferProtocol transProt = TransferProtocol.FILE;
    //TransferProtocol transProt = TransferProtocol.RFIO;
    log.debug("Protocollo : "+transProt);
    prefix.addTransferProtocol(transProt);
    try {
      log.debug( "TURL : " + stori.getTURL( prefix ) );
    }
    catch ( InvalidGetTURLNullPrefixAttributeException ex ) {
      ex.printStackTrace();
    }

    transProt = TransferProtocol.GSIFTP;
    log.debug("Protocollo : "+transProt);
    prefix = new TURLPrefix();
    prefix.addTransferProtocol(transProt);
    try {
      log.debug( "TURL : " + stori.getTURL( prefix ) );
    }
    catch ( InvalidGetTURLNullPrefixAttributeException ex ) {
      ex.printStackTrace();
    }

    //int d = NamespaceUtil.computeDistanceFromPath("/cnaf", "/cnaf");
    //log.debug("Distance = "+d);

    //test.testWinnerRule();

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
    StoRI stori = null;
    try {
      stori = namespace.resolveStoRIbyAbsolutePath("mnt/gpfs/cnaf/test/prova1");
      log.debug(stori);
    }
    catch (NamespaceException ex) {
      ex.printStackTrace();
    }
    try {
      //log.debug(stori.getVirtualFileSystem());
      log.debug(" The glange of peeper.. STORI = " + stori.getVirtualFileSystem().getRootPath());
     // log.debug("   stori.vfs istance  "+stori.getVirtualFileSystem().hashCode());
     // log.debug("   VFS Creation time : "+ stori.getVirtualFileSystem().getCreationTime());
     // log.debug("   stori.vfs.root istance  "+stori.getVirtualFileSystem().getRootPath().hashCode());
    }
    catch (NamespaceException ex1) {
    }

  }

}


}
