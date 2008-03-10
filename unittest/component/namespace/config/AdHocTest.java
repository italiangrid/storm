package component.namespace.config;

import java.io.*;

import org.apache.commons.logging.*;
import org.apache.commons.logging.impl.*;
import org.apache.log4j.*;
import it.grid.storm.namespace.config.*;
import it.grid.storm.namespace.config.xml.*;
import java.util.*;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.ApproachableRule;
import it.grid.storm.namespace.model.TransportPrefix;
import it.grid.storm.namespace.model.Authority;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.namespace.naming.SURL;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.naming.NamespaceUtil;
import it.grid.storm.common.types.StFN;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.namespace.NamespaceInterface;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public class AdHocTest {

  private  XMLNamespaceParser parser;
  private  XMLParserUtil parserUtil;
  private static Log log = LogFactory.getLog(AdHocTest.class);

  public AdHocTest() {
    super();
  }

  private void init()
  {
    String path = System.getProperty("user.dir") + File.separator + "etc";
    String filename = "namespace_test.xml";
    int refresh = 3;

    boolean jdk14Logger = (log instanceof Jdk14Logger);
    boolean log4jlog = (log instanceof Log4JLogger);

    if (jdk14Logger)
    {
      System.out.println("Using Jdk14Logger = "+jdk14Logger);
    }
    if (log4jlog)
    {
      System.out.println("Using Log14Logger = "+log4jlog);
      String logConfigFile = System.getProperty("user.dir")+File.separator+
          "unittest"+File.separator+"log4j_for_testing.properties";
      System.out.println("config file = "+logConfigFile);
      PropertyConfigurator.configure(logConfigFile);
    }



    NamespaceLoader loader = new XMLNamespaceLoader(path, filename, refresh, false);
    parser = new XMLNamespaceParser(loader,false,true);
    parserUtil = new XMLParserUtil(loader.getConfiguration());
    //log.debug("###"+loader.getConfiguration().getString("filesystems.filesystem(0).capabilities.trans-prot.prot.schema"));

  }


  private void numberOfVFS()
  {
    int numVFS = 0;
    try {
      numVFS = parserUtil.getNumberOfFS();
    }
    catch (NamespaceException ex) {
      log.error("Number of File System error",ex);
    }
    log.debug("Number of Virtual FS = " + numVFS);

  }

  private void numberOfMappingRule()
  {
    int numMapRules = 0;
    try {
      numMapRules = parserUtil.getNumberOfMappingRule();
    }
    catch (NamespaceException ex) {
      log.error("Number of Mapping Rules error",ex);
    }
    log.debug("Number of Mapping Rule = " + numMapRules);
  }

  /**
   * numberOfApproachRule
   */
  private void numberOfApproachRule() {
    int numAppRules = 0;
    try {
      numAppRules = parserUtil.getNumberOfApproachRule();
    }
    catch (NamespaceException ex) {
      log.error("Number of Approach Rules error", ex);
    }
    log.debug("Number of Approach Rule = " + numAppRules);

  }



  private void validate()
  {
    boolean val = parserUtil.validateXML();
    log.debug("Validate = " + val);
  }


  private void scanVFSName()
  {
    int numOfFS = -1;
    String name = null;
    try {
      numOfFS = parserUtil.getNumberOfFS();
    }
    catch (NamespaceException ex) {
      log.error("Number of File System error", ex);
    }
    if (numOfFS > 0)
    {
      for (int i=0; i<numOfFS; i++)
      {
        try {
          name = parserUtil.getFSName(i);
          log.debug("VFS Name ("+i+") = "+name);
        }
        catch (NamespaceException ne)
        {
          log.error("Error while retrieving FS name", ne);
        }

      }
    }
  }

  private void retrieveVFSByName(String name)
  {
    int result = -1;
    try {
      result = parserUtil.getFSNumber(name);
      log.debug("VFS Name ("+name+") is the number "+result);
    }
    catch (NamespaceException ex) {
      log.error("Error while retrieving FS name", ex);
    }
  }

  private void retrieveMappingRuleByName(String name)
  {
    int result = -1;
    try {
      result = parserUtil.getFSNumber(name);
      log.debug("VFS Name ("+name+") is the number "+result);
    }
    catch (NamespaceException ex) {
      log.error("Error while retrieving FS name", ex);
    }
  }



  private void protocolCounting(String fs_name)
  {
    int result = -1;
   try {
     result = parserUtil.getNumberOfProt(fs_name);
     log.debug("Within the VFS Name ("+fs_name+") there are protocols = "+result);
   }
   catch (NamespaceException ex) {
     log.error("Error while retrieving FS name", ex);
   }
  }


  private void getProtName(String fs_name, int nrProt)
  {
    String result = null;
    try {
      result = parserUtil.getProtName(fs_name,nrProt);
      log.debug("Within the VFS Name (" + fs_name + ") the protocol nr '"+nrProt+"' = " + result);
    }
    catch (NamespaceException ex) {
      log.error("Error while retrieving FS name", ex);
    }

  }


  private void getProtNumber(String fs_name, String prot_name)
{
  int result = -2;
  try {
    result = parserUtil.getProtNumberByName(fs_name,prot_name);
    log.debug("Within the VFS Name (" + fs_name + ") the protocol named '"+prot_name+"' = " + result);
  }
  catch (NamespaceException ex) {
    log.error("Error while retrieving FS name", ex);
  }

}


  private void getIndexOfPattern(String xpath, char patternChar, int number)
  {

  int startIndex = 0;
  int pos = 0;
  StringBuffer result = new StringBuffer();
  pos = xpath.indexOf(patternChar, startIndex);
  log.debug("          1         2         3         4         5         6         7         8");
  log.debug("012345678901234567890123456789012345678901234567890123456789012345678901234567890");
  log.debug(xpath);
  log.debug("Position of char ="+patternChar+" is "+pos);
  String numStr = Integer.toString(number);
  log.debug("The number is = "+numStr);
  result.append(xpath.substring(startIndex, pos));
  result.append(numStr);
  result.append(xpath.substring(pos+1));
  String ris = result.toString();
  log.debug("Risultato finale è :");
  log.debug(ris);

}

/**
 * searchSubChar
 */
private void searchSubChar() {
  boolean res =parserUtil.areThereSustitutionCharInside(XMLConst.APP_RULE);
  log.debug("Found a sub char in element = '"+XMLConst.APP_RULE+"'");
  }


private void testParser()
  {
    Hashtable vfss = new Hashtable(parser.getApproachableRules());
    log.debug("number of VFS = "+vfss.size());

  }

/**
private void retrieveProtocol(String fsName, int nrProt) {

  TransferProtocol transfProtocol =  null;
  Protocol protocol;
  Authority service;
  String name = null;
  try {
    name = parserUtil.getProtName(fsName, nrProt);
    String schema = parserUtil.getProtSchema(fsName, name);
    protocol = Protocol.getProtocol(schema);
    protocol.setProtocolServiceName(name);
    service =
    transfProtocol.setHost(parserUtil.getProtHost(fsName, name));
    String portValue = parserUtil.getProtPort(fsName, name);
    int portIntValue = -1;
    try {
      portIntValue = Integer.parseInt(portValue);
    }
    catch (NumberFormatException nfe) {

      log.warn("to evaluate the environmental variable " + portValue);
    }
    protocol.setPort(portIntValue);
  }
  catch (NamespaceException ex) {
    log.error("ERROR",ex);
  }
  log.debug(protocol.toString());
}
**/

private void testHashtable(){
  Hashtable numbers = new Hashtable();
  numbers.put("one", new Integer(1));
  numbers.put("two", new Integer(2));
  numbers.put("three", new Integer(3));
  log.debug("HT size = "+ numbers.size());
  numbers.put("two", new Integer(2));
  log.debug("HT size = "+ numbers.size());

}

private void allProperties(){
    // Get all system properties
  Properties props = System.getProperties();

  // Enumerate all system properties
  Enumeration enumer = props.propertyNames();
  for (; enumer.hasMoreElements(); ) {
      // Get property name
      String propName = (String)enumer.nextElement();

      // Get property value
      String propValue = (String)props.get(propName);
      log.debug("Name : "+propName+" = "+propValue);
  }
}

private VirtualFSInterface getFirstVFS(){
  VirtualFSInterface vfs = null;
  Hashtable vfss = new Hashtable(parser.getVFSs());
  Enumeration enumer = vfss.keys();
  Vector fsName = new Vector();
  for (; enumer.hasMoreElements(); ) {
    // Get fs name
    fsName.add ((String) enumer.nextElement());
  }
  log.debug("VFS names = " + fsName);
  String firstElement = (String) fsName.firstElement();
  log.debug("VFS first element = " + firstElement);

  vfs = (VirtualFSInterface) vfss.get(fsName.firstElement());
  return vfs;
}


private void testPrintVFSSConfiguration() {
  Hashtable vfss = new Hashtable(parser.getVFSs());
  log.debug("VFSS size ="+vfss.size());
  Enumeration enumer = vfss.keys();
  VirtualFSInterface vfs;
  String vfsName;
  for (; enumer.hasMoreElements(); ) {
    vfsName = (String) enumer.nextElement();
    log.debug("VFS_NAME : "+vfsName);
  }
  log.debug("--------------------------");
  enumer = vfss.elements();
  for (; enumer.hasMoreElements(); ) {
    vfs = (VirtualFSInterface) enumer.nextElement();
    log.debug(vfs);
  }
  log.debug("--------------------------");

}

private void testPrintMAPPINGRULE_Configuration() {
  Hashtable maprules = new Hashtable(parser.getMappingRules());
  log.debug("maprules size ="+maprules.size());
  Enumeration enumer = maprules.keys();
  MappingRule mapRule;
  String ruleName;
  for (; enumer.hasMoreElements(); ) {
    ruleName = (String) enumer.nextElement();
    log.debug("MAPRULE_NAME : "+ruleName);
  }
  log.debug("XXXXXXXXXXXXXXXXXXXXXXXXXXX");
  enumer = maprules.elements();
  for (; enumer.hasMoreElements(); ) {
    mapRule = (MappingRule) enumer.nextElement();
    log.debug(mapRule);
  }
  log.debug("XXXXXXXXXXXXXXXXXXXXXXXXXXX");

}

private void testPrintAPPROACHABLERULE_Configuration() {
  Hashtable apprules = new Hashtable(parser.getApproachableRules());
  log.debug("apprules size ="+apprules.size());
  Enumeration enumer = apprules.keys();
  ApproachableRule appRule;
  String ruleName;
  for (; enumer.hasMoreElements(); ) {
    ruleName = (String) enumer.nextElement();
    log.debug("APP_RULE_NAME : "+ruleName);
  }
  log.debug("WWWWWWWWWWWWWWWWWWWWWWWWWWWW");
  enumer = apprules.elements();
  for (; enumer.hasMoreElements(); ) {
    appRule = (ApproachableRule) enumer.nextElement();
    log.debug(appRule);
  }
  log.debug("WWWWWWWWWWWWWWWWWWWWWWWWWWWW");

}


 private void testTrasferProtocol(){
   TransportPrefix tp;
   tp = new TransportPrefix(Protocol.GSIFTP,new Authority("testbed006.cnaf.infn.it"));
   log.debug(tp);
   tp = new TransportPrefix(Protocol.GSIFTP,new Authority("testbed006.cnaf.infn.it",12345));
   log.debug(tp);
 }

  private void testAllVFSRoots(){
    Vector v = new  Vector(parser.getAllVFS_Roots());
    log.debug("VFS-ROOTs = "+v);
  }

  private void testAllMappingRuleStFNRoot(){
    Vector v = new  Vector(parser.getAllMappingRule_StFNRoots());
    log.debug("MAP Rule StFN-ROOTs = "+v);
  }

  private void testResolveStoRIBySURL(){
    SURL surl = new SURL("testbed006.cnaf.infn.it", 1234, "cnaf/test/pippo.txt", null);
    TSURL tsurl = null;
    try {
      tsurl = TSURL.makeFromString(surl.toString());
    }
    catch (InvalidTSURLAttributesException ex) {
      ex.printStackTrace();
    }
    StFN stfn = tsurl.sfn().stfn();
    log.debug(stfn.toString());
    String other = "/afcqdaf/dfaf/rfd";
    log.debug(other);
    int d = NamespaceUtil.computeDistanceFromPath(stfn.toString(),other);
    log.debug("Distance = "+d);

    try {
      NamespaceDirector.getNamespace().resolveStoRIbySURL(tsurl);
    }
    catch (NamespaceException ex1) {
      ex1.printStackTrace();
    }
  }


  private void testRetrieveVFSbyAbsolutePath(){
    String absolutePath = "/mnt/gpfs/cnaf/testRitZ/dir1/file.txt";
    VirtualFSInterface vfs = null;
    try {
      vfs = NamespaceDirector.getNamespace().resolveVFSbyAbsolutePath(absolutePath);
    }
    catch (NamespaceException ex) {
       ex.printStackTrace();
    }
    try {
      log.debug("VFS found is '" + vfs.getAliasName() + "' with root : '" + vfs.getRootPath());
    }
    catch (NamespaceException ex1) {
      ex1.printStackTrace();
    }

  }


  private void testGetTrasferProtocol(){
    VirtualFSInterface vfs = parser.getVFS("cnaf-FS");
    try {
      Vector prot = new Vector(vfs.getCapabilities().getManagedProtocols());
      log.debug(prot);
    }
    catch (NamespaceException ex) {
    }

  }
  private void testGetDefaultProtocol(){
    VirtualFSInterface vfs = parser.getVFS("cnaf-FS");
    Vector prot = null;
    try {
      prot = new Vector(vfs.getCapabilities().getManagedProtocols());
    }
    catch (NamespaceException ex) {
    }
    if (prot!=null) {
      log.debug((TransportPrefix)prot.firstElement());
    }
  }


  private void testSpaceFileName(){
    GridUserInterface user = new MockGridUser();
    NamespaceInterface namespace = NamespaceDirector.getNamespace();
    String spaceFN = null ;
    try {
      spaceFN = namespace.makeSpaceFileURI(user);
    }
    catch (NamespaceException ex) {
      log.error("AHH!",ex);
    }
    log.debug(" SPACE FN : "+spaceFN);
  }

  public static void main(String[] args) {
    AdHocTest test = new AdHocTest();
    test.init();

    //test.numberOfVFS();
    //test.numberOfMappingRule();
    test.numberOfApproachRule();
    //test.scanVFSName();
    //test.retrieveVFSByName("cnaf-FS");
    //test.retrieveVFSByName("scrath-FS");
    //test.retrieveVFSByName("XXXX");
    //test.protocolCounting("cnaf-FS");
    //test.protocolCounting("scrath-FS");
    //test.protocolCounting("XXXX");
    //test.getProtName("cnaf-FS",0);
    //test.getProtNumber("cnaf-FS","gsiftp");
    //test.validate();
    //test.searchSubChar();
    //test.getIndexOfPattern(XMLConst.PROT_SCHEMA, XMLConst.PROT_SUB_PATTERN,13);
    //test.searchSubChar();
    //test.testParser();
    //test.retrieveVFSByName("egrid-FS");
    //test.protocolCounting("scrath-FS");
    //test.getProtNumber("cnaf-FS","gsiftp");
    //test.retrieveProtocol("scrath-FS",0);
    //test.testHashtable();
    //test.allProperties();
    //test.testPrintVFSSConfiguration();
    //test.testPrintMAPPINGRULE_Configuration();
    test.testPrintAPPROACHABLERULE_Configuration();
    //test.testTrasferProtocol();
    //test.testAllVFSRoots();
    //test.testAllMappingRuleStFNRoot();
    //test.testResolveStoRIBySURL();
    //test.testRetrieveVFSbyAbsolutePath();
    //test.testGetTrasferProtocol();
    //test.testGetDefaultProtocol();
    //test.testSpaceFileName();

  }



}
