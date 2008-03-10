package component.namespace.config;

import junit.framework.TestCase;
import it.grid.storm.namespace.*;
import org.apache.commons.configuration.*;
import it.grid.storm.namespace.config.xml.*;
import it.grid.storm.namespace.config.NamespaceLoader;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

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
public class TestXMLParserUtil extends TestCase {

  private XMLParserUtil parserUtil = null; ;


  protected void setUp() throws Exception {
    super.setUp();
    String path = System.getProperty("user.dir") + File.separator + "etc";
    String filename = "namespace_test.xml";
    int refresh = 3;
    NamespaceLoader loader = new XMLNamespaceLoader(path, filename, refresh, true);
    parserUtil = new XMLParserUtil(loader.getConfiguration());
  }

  protected void tearDown() throws Exception {
    parserUtil = null;
    super.tearDown();
  }

  public void testAreThereSustitutionCharInside() {
    String element = "";
    boolean expectedReturn = false;
    boolean actualReturn = parserUtil.areThereSustitutionCharInside(element);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetACLMode() throws NamespaceException {
    String nameOfFS = "";
    String expectedReturn = null;
    String actualReturn = parserUtil.getACLMode(nameOfFS);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetAppRule_AppFS() throws NamespaceException {
    String nameOfAppRule = "";
    ArrayList expectedReturn = new ArrayList();
    List actualReturn = parserUtil.getAppRule_AppFS(nameOfAppRule);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetAppRule_RelativePath() throws NamespaceException {
    String nameOfAppRule = "";
    String expectedReturn = null;
    String actualReturn = parserUtil.getAppRule_RelativePath(nameOfAppRule);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetAppRule_SubjectDN() throws NamespaceException {
    String nameOfAppRule = "";
    String expectedReturn = null;
    String actualReturn = parserUtil.getAppRule_SubjectDN(nameOfAppRule);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetAppRule_SubjectVO() throws NamespaceException {
    String nameOfAppRule = "";
    String expectedReturn = null;
    String actualReturn = parserUtil.getAppRule_SubjectVO(nameOfAppRule);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetApproachRuleName() throws NamespaceException {
    int numOfAppRule = 0;
    String expectedReturn = null;
    String actualReturn = parserUtil.getApproachRuleName(numOfAppRule);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetDefaultFileLifeTime() throws NamespaceException {
    String nameOfFS = "";
    long expectedReturn = 0L;
    long actualReturn = parserUtil.getDefaultFileLifeTime(nameOfFS);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetDefaultFileType() throws NamespaceException {
    String nameOfFS = "";
    String expectedReturn = null;
    String actualReturn = parserUtil.getDefaultFileType(nameOfFS);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetDefaultSpaceGuarSize() throws NamespaceException {
    String nameOfFS = "";
    long expectedReturn = 0L;
    long actualReturn = parserUtil.getDefaultSpaceGuarSize(nameOfFS);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetDefaultSpaceLifeTime() throws NamespaceException {
    String nameOfFS = "";
    long expectedReturn = 0L;
    long actualReturn = parserUtil.getDefaultSpaceLifeTime(nameOfFS);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetDefaultSpaceTotSize() throws NamespaceException {
    String nameOfFS = "";
    long expectedReturn = 0L;
    long actualReturn = parserUtil.getDefaultSpaceTotSize(nameOfFS);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetDefaultSpaceType() throws NamespaceException {
    String nameOfFS = "";
    String expectedReturn = null;
    String actualReturn = parserUtil.getDefaultSpaceType(nameOfFS);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetFSDriver() throws NamespaceException {
    String nameOfFS = "";
    String expectedReturn = null;
    String actualReturn = parserUtil.getFSDriver(nameOfFS);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetFSName() throws NamespaceException {
    int numOfFS = 0;
    String expectedReturn = null;
    String actualReturn = parserUtil.getFSName(numOfFS);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetFSNumber() throws NamespaceException {
    String nameOfFS = "";
    int expectedReturn = 0;
    int actualReturn = parserUtil.getFSNumber(nameOfFS);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetFSRoot() throws NamespaceException {
    String nameOfFS = "";
    String expectedReturn = null;
    String actualReturn = parserUtil.getFSRoot(nameOfFS);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetFSType() throws NamespaceException {
    String nameOfFS = "";
    String expectedReturn = null;
    String actualReturn = parserUtil.getFSType(nameOfFS);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }


  public void testGetMapRuleName() throws NamespaceException {
    int numOfMapRule = 0;
    String expectedReturn = null;
    String actualReturn = parserUtil.getMapRuleName(numOfMapRule);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetMapRule_StFNRoot() throws NamespaceException {
    String nameOfMapRule = "";
    String expectedReturn = null;
    String actualReturn = parserUtil.getMapRule_StFNRoot(nameOfMapRule);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetMapRule_mappedFS() throws NamespaceException {
    String nameOfMapRule = "";
    String expectedReturn = null;
    String actualReturn = parserUtil.getMapRule_mappedFS(nameOfMapRule);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetNumberOfApproachRule() throws NamespaceException {
    int expectedReturn = 0;
    int actualReturn = parserUtil.getNumberOfApproachRule();
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetNumberOfFS() throws NamespaceException {
    int expectedReturn = 0;
    int actualReturn = parserUtil.getNumberOfFS();
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetNumberOfMappingRule() throws NamespaceException {
    int expectedReturn = 0;
    int actualReturn = parserUtil.getNumberOfMappingRule();
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetNumberOfProt() throws NamespaceException {
    String nameOfFS = "";
    int expectedReturn = 0;
    int actualReturn = parserUtil.getNumberOfProt(nameOfFS);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetProtHost() throws NamespaceException {
    String nameOfFS = "";
    String protName = "";
    String expectedReturn = null;
    String actualReturn = parserUtil.getProtHost(nameOfFS, protName);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetProtName() throws NamespaceException {
    String nameOfFS = "";
    int numOfProt = 0;
    String expectedReturn = null;
    String actualReturn = parserUtil.getProtName(nameOfFS, numOfProt);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetProtNumber() throws NamespaceException {
    String nameOfFS = "";
    String nameOfProt = "";
    int expectedReturn = 0;
    int actualReturn = parserUtil.getProtNumberByName(nameOfFS, nameOfProt);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetProtPort() throws NamespaceException {
    String nameOfFS = "";
    String protName = "";
    String expectedReturn = "8444";
    String actualReturn = parserUtil.getProtPort(nameOfFS, protName);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetProtSchema() throws NamespaceException {
    String nameOfFS = "";
    String protName = "";
    String expectedReturn = null;
    String actualReturn = parserUtil.getProtSchema(nameOfFS, protName);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testGetSpaceDriver() throws NamespaceException {
    String nameOfFS = "";
    String expectedReturn = null;
    String actualReturn = parserUtil.getSpaceDriver(nameOfFS);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }







  public void testValidateXML() {
    boolean expectedReturn = false;
    boolean actualReturn = parserUtil.validateXML();
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testWhicSubstitutionChar() {
    String element = "";
    char expectedReturn = '0';
    char actualReturn = parserUtil.whicSubstitutionChar(element);
    assertEquals("return value", expectedReturn, actualReturn);
    /**@todo fill in the test code*/
  }

  public void testXMLParserUtil() {
    Configuration config = null;
    parserUtil = new XMLParserUtil(config);
    /**@todo fill in the test code*/
  }

}
