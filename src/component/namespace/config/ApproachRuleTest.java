package component.namespace.config;

import it.grid.storm.namespace.config.xml.XMLNamespaceLoader;
import org.apache.commons.logging.LogFactory;
import it.grid.storm.namespace.config.xml.XMLNamespaceParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Jdk14Logger;
import java.io.File;
import it.grid.storm.namespace.config.xml.XMLParserUtil;
import it.grid.storm.namespace.config.NamespaceLoader;
import org.apache.log4j.PropertyConfigurator;
import org.apache.commons.logging.impl.Log4JLogger;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.naming.SURL;
import it.grid.storm.common.types.StFN;
import it.grid.storm.namespace.naming.NamespaceUtil;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.namespace.model.ApproachableRule;
import java.util.Hashtable;
import java.util.Enumeration;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.DistinguishedName;
import javax.security.auth.x500.X500Principal;
import it.grid.storm.griduser.DNMatchingRule;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.griduser.GridUserManager;
import java.util.Arrays;
//import it.grid.storm.griduser.DistinguishedName;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF</p>
 *
 * @author R.Zappi
 * @version 1.0
 */
public class ApproachRuleTest {
    private XMLNamespaceParser parser;
    private XMLParserUtil parserUtil;
    private static Log log = LogFactory.getLog(AdHocTest.class);
    private NamespaceInterface namespace;

    public ApproachRuleTest() {
        super();
    }

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

        //NamespaceLoader loader = new XMLNamespaceLoader(path, filename, refresh, false);
        //parser = new XMLNamespaceParser(loader, false, true);
        //parserUtil = new XMLParserUtil(loader.getConfiguration());
        //log.debug("###"+loader.getConfiguration().getString("filesystems.filesystem(0).capabilities.trans-prot.prot.schema"));
        //NamespaceDirector.initializeDirector(false,true);
        namespace = NamespaceDirector.getNamespace(true,true);
    }


    /***+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *
     *               TEST methods
     *
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */

    /*******************************************************************
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

    /*******************************************************************
     * numberOfApproachRule
     */
    private void testPrintAPPROACHABLERULE_Configuration() {
        Hashtable apprules = new Hashtable(parser.getApproachableRules());
        log.debug("apprules size =" + apprules.size());
        Enumeration enumer = apprules.keys();
        ApproachableRule appRule;
        String ruleName;
        log.debug("========= list of APP-Rule NAME ========");
        for (; enumer.hasMoreElements(); ) {
            ruleName = (String) enumer.nextElement();
            log.debug("APP_RULE_NAME : " + ruleName);
        }
        enumer = apprules.elements();

        log.debug("========= list of APP-Rule Full Details ========");
        for (; enumer.hasMoreElements(); ) {
            appRule = (ApproachableRule) enumer.nextElement();
            log.debug(appRule);
        }
        log.debug("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }



    /*******************************************************************
     * testResolveStoRIBySURL
     */
    private void testResolveStoRIBySURL(){
      SURL surl = new SURL("testbed006.cnaf.infn.it", 1234, "cnaf/test/pippo.txt", null);

      FakeGridUser fakeGU = null;
      fakeGU = new FakeGridUser("/C=UK/O=eScience/OU=Bristol/L=IS/CN=jon wakelin", "ciccio");

      TSURL tsurl = null;
      try {
        tsurl = TSURL.makeFromString(surl.toString());
      }
      catch (InvalidTSURLAttributesException ex) {
        ex.printStackTrace();
      }
      StFN stfn = tsurl.sfn().stfn();
      log.debug(stfn.toString());
      /**
      String other = "/afcqdaf/dfaf/rfd";
      log.debug(other);
      int d = NamespaceUtil.computeDistanceFromPath(stfn.toString(),other);
      log.debug("Distance = "+d);
      **/
      try {
        NamespaceDirector.getNamespace().resolveStoRIbySURL(tsurl, fakeGU);
      }
      catch (NamespaceException ex1) {
        ex1.printStackTrace();
      }
    }


    /*******************************************************************
     * create Fake Grid User
     */
     private GridUserInterface makeGUser(String dn, String voName) {
         return new FakeGridUser(dn, voName);
     }

     private DistinguishedName makeDistinguishedName(String proxyDN) {
         return new DistinguishedName(proxyDN);
     }

     private void testDN() {

         //String proxyDN = "/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Luca Magnoni/Email=luca.magnoni@cnaf.infn.it";
         String proxyDN = "C=IT,O=INFN,OU=Personal Certificate,L=CNAF,CN=Luca Magnoni,Email=luca.magnoni@cnaf.infn.it";
         DistinguishedName dn = makeDistinguishedName(proxyDN);
         log.debug("input = "+proxyDN);
         log.debug("output = \n"+dn);
         log.debug("X500DN Canonical = "+dn.getX500DNString(X500Principal.CANONICAL));
         log.debug("X500DN RFC1779 = "+dn.getX500DNString(X500Principal.RFC1779));
         log.debug("X500DN RFC2253 = "+dn.getX500DNString(X500Principal.RFC2253));
         String proxyDN_jon1 = "C=UK,O=eScience,OU=Bristol,L=IS,CN=jon wakelin";
         DistinguishedName dn_jon1 = makeDistinguishedName(proxyDN_jon1);
         log.debug("input = " + proxyDN);
         log.debug("output = \n" + dn_jon1);
         log.debug("X500DN Canonical = "+dn_jon1.getX500DNString(X500Principal.CANONICAL));
         log.debug("X500DN RFC1779 = "+dn_jon1.getX500DNString(X500Principal.RFC1779));
         log.debug("X500DN RFC2253 = "+dn_jon1.getX500DNString(X500Principal.RFC2253));
         String proxyDN_jon2 = "/C=UK/O=eScience/OU=Bristol/L=IS/CN=jon wakelin";
         DistinguishedName dn_jon2 = makeDistinguishedName(proxyDN_jon2);
         log.debug("input = " + proxyDN);
         log.debug("output = \n" + dn_jon2);
         log.debug("X500DN Canonical = "+dn_jon2.getX500DNString(X500Principal.CANONICAL));
         log.debug("X500DN RFC1779 = "+dn_jon2.getX500DNString(X500Principal.RFC1779));
         log.debug("X500DN RFC2253 = "+dn_jon2.getX500DNString(X500Principal.RFC2253));

         boolean dnEquals = dn_jon1.equals(dn_jon2);
         log.debug("DN equals : "+dnEquals);
     }


     private void matchDNs() {
       FakeGridUser fakeGU = new FakeGridUser("/C=UK/O=eScience/OU=Bristol/L=IS/CN=jon wakelin", "ciccio");
      String dnString = fakeGU.getDn();
      DistinguishedName dn = makeDistinguishedName(dnString);
      DNMatchingRule dnMatcher = new DNMatchingRule("*",".*",".*",".*",".*",".*");
      boolean match = dnMatcher.match(dn);
      log.debug("DN Matching Rule : "+dnMatcher);
      log.debug("DN (input)       : "+dnString);
      log.debug("DN (RFC1779)     : "+dn.getX500DN_rfc1779());
      log.debug("  --- is matching ? : "+match);

      dnMatcher = new DNMatchingRule("UK",".*",".*",".*",".*",".*");
      match = dnMatcher.match(dn);
      log.debug("DN Matching Rule : "+dnMatcher);
      log.debug("DN (input)       : "+dnString);
      log.debug("DN (RFC1779)     : "+dn.getX500DN_rfc1779());
      log.debug("  --- is matching ? : "+match);

      fakeGU = new FakeGridUser("C=IT,O=INFN,OU=Personal Certificate,L=CNAF,CN=Luca Magnoni,Email=luca.magnoni@cnaf.infn.it", "ciccio");
      dnString = fakeGU.getDn();
      dn = makeDistinguishedName(dnString);
      match = dnMatcher.match(dn);
      log.debug("DN Matching Rule : "+dnMatcher);
      log.debug("DN (input)       : "+dnString);
      log.debug("DN (RFC1779)     : "+dn.getX500DN_rfc1779());
      log.debug("  --- is matching ? : "+match);


      fakeGU = new FakeGridUser("/DC=ch/DC=cern/OU=Organic Units/OU=Users/CN=elanciot/CN=576215/CN=Elisa Lanciotti", "secca");
      dnMatcher = new DNMatchingRule(".*",".*",".*",".*","cippalippa","cern");
      dnString = fakeGU.getDn();
      dn = makeDistinguishedName(dnString);
      match = dnMatcher.match(dn);
      log.debug("DN Matching Rule : "+dnMatcher);
      log.debug("DN (input)       : "+dnString);
      log.debug("DN (RFC1779)     : "+dn.getX500DN_rfc1779());
      log.debug("  --- is matching ? : "+match);


     }

     private void retrieveVFSsFromAppRules(VomsGridUser fakeGU) {
         //FakeGridUser fakeGU = null;
         //fakeGU = new FakeGridUser("/C=UK/O=eScience/OU=Bristol/L=IS/CN=jon wakelin", "ciccio");
         //fakeGU = new FakeGridUser("C=IT,O=INFN,OU=Personal Certificate,L=CNAF,CN=Luca Magnoni,Email=aaa@ssrq","ciccio");

         Hashtable apprules = new Hashtable(parser.getApproachableRules());
         log.debug("apprules size =" + apprules.size());
         Enumeration enumer = apprules.elements();
         ApproachableRule appRule;
         //String ruleName;
         HashSet approachVFSs = new HashSet();

         log.debug("========= list of APP-Rule Full Details ========");
         for (; enumer.hasMoreElements(); ) {
             appRule = (ApproachableRule) enumer.nextElement();
             log.debug("Considering APP-RULE-NAME '"+appRule.getRuleName()+"'");
             log.debug("Considering APP-RULE : "+appRule.getSubjectRules().getDNMatchingRule());
             log.debug("Considering APP-RULE : "+appRule.getSubjectRules().getVONameMatchingRule());
             log.debug("Considering APP-RULE : only VOMS = "+appRule.getSubjectRules().isVomsCertRequired());
             if (appRule.match(fakeGU)) {

                 log.debug("G.User ["+fakeGU.getDn()+"] CAN APPROACH this VFS :"+appRule.getApproachableVFS());
                 approachVFSs.addAll(appRule.getApproachableVFS());

             } else {
                log.debug("G.User ["+fakeGU.getDn()+"] don't match above rule! ");
             }
        }
        log.debug("APP VFSs : "+approachVFSs);

        try {
          VirtualFSInterface vfs = namespace.getDefaultVFS( fakeGU );
          log.debug("Default VFS : "+vfs);
        }
        catch ( NamespaceException ex ) {
        }
     }


     private void appRulesNamespace() {
         FakeGridUser fakeGU = null;
         fakeGU = new FakeGridUser("/C=UK/O=eScience/OU=Bristol/L=IS/CN=jon wakelin", "ciccio");
         //fakeGU = new FakeGridUser("C=IT,O=INFN,OU=Personal Certificate,L=CNAF,CN=Luca Magnoni,Email=aaa@ssrq","ciccio");
         HashSet approachVFSs = (HashSet)namespace.getListOfVFSName(fakeGU);
         log.debug("APP VFSs : "+approachVFSs);
     }

 private void testContains() {
   HashSet test = new HashSet();
   test.add("cnaf-FS");
   test.add("test-1");
   test.add("Prova");
   log.debug("PROVA di Contatins : "+test.contains("cnaf-FS"));

 }


 private VomsGridUser makeFakeVomsGridUser() {
   VomsGridUser fakeVOMSUser = null;
   String dnString = "/C=UK/O=eScience/OU=Bristol/L=IS/CN=jon wakelin";
   String fqanString = "/infngrid/Role=NULL/Capability=NULL";
   //Fqan[] fqan = { new Fqan(fqanString) };
   FQAN[] fqans = { new FQAN(fqanString) };
   fakeVOMSUser = (VomsGridUser)GridUserManager.makeVOMSGridUser(dnString,fqans);
   //fakeVOMSUser = VomsGridUser.make(dnString, fqan);

   log.debug("Fake VOMS User : "+fakeVOMSUser);

   return fakeVOMSUser;
 }

  /**
   * MAIN
   *
   *
   * @param args String[]
   */
  public static void main(String[] args) {
        ApproachRuleTest test = new ApproachRuleTest();
        test.init();
        //test.numberOfApproachRule();
        //test.testPrintAPPROACHABLERULE_Configuration();
        //test.testResolveStoRIBySURL();
        //test.testDN();
        //test.matchDNs();
        //test.retrieveVFSsFromAppRules();
        //test.appRulesNamespace();
        //test.testResolveStoRIBySURL();
        //test.testContains();
        try {
          Thread.sleep( 1000 );
        }
        catch ( InterruptedException ex ) {
        }

        log.debug("================");
        //VomsGridUser fakeUser = test.makeFakeVomsGridUser();
        //log.debug("Is voms user? "+fakeUser.hasVoms());

        //test.retrieveVFSsFromAppRules(fakeUser);
        //HashSet approachVFSs = (HashSet)test.namespace.getListOfVFSName(fakeUser);
        //log.debug("VFSs approachable: "+approachVFSs);

    }

}
