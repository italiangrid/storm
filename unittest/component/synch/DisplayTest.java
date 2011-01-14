package component.synch;

import it.grid.storm.authz.sa.model.EGEEFQANPattern;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.xmlrpc.converter.ParameterDisplayHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.utiltest.SetUpTest;


public class DisplayTest {

    private static Logger log = LoggerFactory.getLogger(DisplayTest.class);
    private static List<FQAN> fqans = new ArrayList<FQAN>();
    
    
    private void init() {
        System.out.println("Using SLF4J and LogBack");
        String unitTestPath = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        String logConfigFile = unitTestPath + "logging-test.xml";
        SetUpTest.init(logConfigFile);
    }

    private FQAN createFQAN(String fqanStr) {
    	FQAN fqan = null;
        try {
            fqan = new FQAN(fqanStr);
            log.debug("FQAN = " + fqan);
            log.debug("FQAN VO         = " + fqan.getVo());
            log.debug("FQAN Group      = " + fqan.getGroup());
            log.debug("FQAN SubGroup   = " + fqan.getSubGroup());
            log.debug("FQAN Role       = " + fqan.getRole());
            log.debug("FQAN Capability = " + fqan.getCapability());
        }
        catch (Exception ex) {
            log.error("AHH! " + ex);
        }
        return fqan;
    }

       


    public static void main(String[] args) {
    	DisplayTest test = new DisplayTest();
        test.init();
        fqans.add(test.createFQAN("/atlas/ciccio/Role=sgm"));
        fqans.add(test.createFQAN("/atlas/ciccio/Role=sgm"));
        fqans.add(test.createFQAN("/atlas/ciccio"));
        fqans.add(test.createFQAN("/atlas/Role=ciccio"));
        HashMap<String,List<FQAN>> map = new HashMap<String, List<FQAN>>();
        map.put("ciccio", fqans);
        log.debug(ParameterDisplayHelper.display(map));
 

    }
	
}
