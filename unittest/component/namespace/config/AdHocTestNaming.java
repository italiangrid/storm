package component.namespace.config;

import org.apache.commons.logging.impl.Jdk14Logger;
import java.io.File;
import org.apache.log4j.PropertyConfigurator;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import it.grid.storm.namespace.naming.SURL;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.*;
import it.grid.storm.common.types.SiteProtocol;
import it.grid.storm.common.types.SFN;
import it.grid.storm.common.types.Machine;
import it.grid.storm.common.types.Port;
import it.grid.storm.common.types.StFN;
import java.util.Vector;
import java.util.*;
import it.grid.storm.namespace.naming.NamespaceUtil;
import it.grid.storm.namespace.naming.NameParser;


public class AdHocTestNaming {

  private static Log log = LogFactory.getLog(AdHocTestNaming.class);

  private void init() {
    String path = System.getProperty("user.dir") + File.separator + "etc";
    String filename = "namespace.xml";
    int refresh = 3;

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
  }

  private void testNewSURL(){
    SURL surl = new SURL("testbed006.cnaf.infn.it",1234,"/cnaf/test/pippo.txt", null);
    log.debug(surl);
    surl = new SURL("testbed006.cnaf.infn.it",-1,"/cnaf/test/pippo.txt", "test");
    log.debug(surl);
  }

  private void testOldTSURL(){
    SURL surl = new SURL("testbed006.cnaf.infn.it",1234,"cnaf/test/pippo.txt", null);
    TSURL tsurl = null;
    try {
      tsurl = TSURL.makeFromString(surl.toString());
    }
    catch (InvalidTSURLAttributesException ex) {
      ex.printStackTrace();
    }
    log.debug("TSURL = "+tsurl);
    SiteProtocol sp = tsurl.protocol();
    log.debug("SITE PROTOCOL : "+sp);
    SFN sfn = tsurl.sfn();
    log.debug("SFN = "+sfn);
    Machine m = sfn.machine();
    log.debug("MACHINE : "+m);
    Port port = sfn.port();
    log.debug("PORT : "+port);
    StFN stfn = sfn.stfn();
    String stfnStr = stfn.toString();
    log.debug("StFN : "+stfn);
    Vector v = new Vector(NamespaceUtil.getPathElement(stfn.toString()));
    log.debug(v);
    NameParser nameParser = new NameParser();
    String path = nameParser.getPath(stfnStr);
    log.debug("StFN path : "+path);
    String fileName = NamespaceUtil.getFileName(stfnStr);
    log.debug("FileName = "+fileName);
    String stfnPath = NamespaceUtil.getStFNPath(stfnStr);
    log.debug("StFN path = "+stfnPath);
    stfnPath = NamespaceUtil.getStFNPath("/cnaf/pippo.txt");
    log.debug("StFN path = "+stfnPath);
    stfnPath = NamespaceUtil.getStFNPath("/pippo.txt");
    log.debug("StFN path = "+stfnPath);
    stfnPath = NamespaceUtil.getStFNPath("pippo.txt");
    log.debug("StFN path = "+stfnPath);
    stfnPath = NamespaceUtil.consumeElement("/cnaf/test/pippo.txt");
    log.debug("StFN path = "+stfnPath);
    stfnPath = NamespaceUtil.consumeElement("/cnaf/test/");
    log.debug("StFN path = "+stfnPath);
    stfnPath = NamespaceUtil.consumeElement("/cnaf/");
    log.debug("StFN path = "+stfnPath);
  }

 private void testResolveRelative(){
   String root = "/cnaf";
   String abso = "/cnaf/test/A/";
   String rel =NamespaceUtil.extractRelativePath(root,abso);
   log.debug("ABS : "+abso+" root : "+root +" RELAT : "+rel);
 }


  private void testParentsFile(){
    String filePath = "prova/uno/due/tre/pippo.txt";
    String fileName = NamespaceUtil.getFileName(filePath);
    String path = NamespaceUtil.getStFNPath(filePath);
    log.debug("ORIG : "+filePath);
    log.debug("FILE NAME : "+fileName);
    log.debug("FILE PATH : "+path);
    ArrayList parentList = new ArrayList();

    File pathFile = new File(path);
    String pathName = pathFile.toString();
    parentList.add(pathName);
    log.debug("pathName INIT : "+pathName);
    while (pathFile.getParent()!=null) {
      pathFile = pathFile.getParentFile();
      pathName = pathFile.toString();
      log.debug("pathName : "+pathName);
      parentList.add(pathName);
    }
  }


  private void testNamespaceUtil(){
    String relPath = "/";
    log.debug(NamespaceUtil.getFileName(relPath));
  }

  public static void main(String[] args) {
    AdHocTestNaming test = new AdHocTestNaming();
    test.init();
    //test.testNewSURL();
    test.testOldTSURL();
    //test.testResolveRelative();

    test.testParentsFile();
    test.testNamespaceUtil();
  }




}
