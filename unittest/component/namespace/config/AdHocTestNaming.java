package component.namespace.config;

import it.grid.storm.common.types.Machine;
import it.grid.storm.common.types.Port;
import it.grid.storm.common.types.SFN;
import it.grid.storm.common.types.SiteProtocol;
import it.grid.storm.common.types.StFN;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.Namespace;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.config.NamespaceLoader;
import it.grid.storm.namespace.config.xml.XMLNamespaceLoader;
import it.grid.storm.namespace.config.xml.XMLNamespaceParser;
import it.grid.storm.namespace.config.xml.XMLParserUtil;
import it.grid.storm.namespace.naming.NameParser;
import it.grid.storm.namespace.naming.NamespaceUtil;
import it.grid.storm.namespace.naming.SURL;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TSURL;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.utiltest.SetUpTest;




public class AdHocTestNaming {

    private static Logger log = LoggerFactory.getLogger(AdHocTestNaming.class);
    private static XMLNamespaceParser parser;
    private static XMLParserUtil parserUtil;

    private void init()
    {
        String path = System.getProperty("user.dir") + File.separator + "etc";
        String filename = "namespace_1.4.0_test.xml";
        int refresh = 3;

        System.out.println("Using SLF4J and LogBack");
        String unitTestPath = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        String logConfigFile = unitTestPath + "logging-test.xml";
        SetUpTest.init(logConfigFile);

        NamespaceLoader loader = new XMLNamespaceLoader(path, filename, refresh, false);
        parser = new XMLNamespaceParser(loader,false,true);
        parserUtil = new XMLParserUtil(loader.getConfiguration());
        //log.debug("###"+loader.getConfiguration().getString("filesystems.filesystem(0).capabilities.trans-prot.prot.schema"));

    }


/*    private void testNewSURL(){
        SURL surl = new SURL("testbed006.cnaf.infn.it",1234,"/cnaf/test/pippo.txt", null);
        log.debug(surl.toString());
        surl = new SURL("testbed006.cnaf.infn.it",-1,"/cnaf/test/pippo.txt", "test");
        log.debug(surl.toString());
    }*/

 /*   private void testOldTSURL(){
        SURL surl = new SURL("testbed006.cnaf.infn.it",1234,"cnaf/test/pippo.txt", null);
        TSURL tsurl = null;
        try {
            tsurl = TSURL.makeFromStringValidate(surl.toString());
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
        log.debug(v.toString());
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
*/
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
        //test.testOldTSURL();
        //test.testResolveRelative();
        //test.testParentsFile();
        //test.testNamespaceUtil();
/*        SURL surl = new SURL("testbed006.cnaf.infn.it",1234,"cnaf/cappio/pippo.txt", null);
        log.debug("SURL = "+surl);
        String stfnStr = surl.getStFN();
        log.debug("StFN = "+stfnStr);
        String path = surl.getPath();
        log.debug("Path = "+path);*/

        NamespaceInterface ns = new Namespace(parser);

        GridUserInterface user = MockGridUser.buildMockGridUser(); //"C=IT,O=INFN,OU=Personal Certificate,L=CNAF,CN=Luca Magnoni,Email=luca.magnoni@cnaf.infn.it", "ciccio");
        log.debug("user = "+user);
        boolean result = false;
/*        try {
            result = ns.isStfnFittingSomewhere(stfnStr, user);
            log.debug("is fitting somewhere? : "+result);
        } catch (NamespaceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }




}
