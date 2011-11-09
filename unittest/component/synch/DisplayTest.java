package component.synch;

import it.grid.storm.authz.sa.model.EGEEFQANPattern;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.xmlrpc.converter.ParameterDisplayHelper;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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


    public void testParseDate() {
        String rfcDate = "Fri, 23 Sep 2011 13:01:03 +0200";
        String pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
        Date date;
        String dateStr2 = "Fri Sep 23 13:14:26 CEST 2011";
        String pattern2 = "EEE MMM dd HH:mm:ss z yyyy";
        SimpleDateFormat format2 = new SimpleDateFormat(pattern2, Locale.ENGLISH);
        try {
            date = (Date)format.parse(rfcDate);
            log.debug("date:"+date);
            date = (Date)format2.parse(dateStr2);
            log.debug("date:"+date);
            
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }


    public static void main(String[] args) {
    	DisplayTest test = new DisplayTest();
        test.init();
//        fqans.add(test.createFQAN("/atlas/ciccio/Role=sgm"));
//        fqans.add(test.createFQAN("/atlas/ciccio/Role=sgm"));
//        fqans.add(test.createFQAN("/atlas/ciccio"));
//        fqans.add(test.createFQAN("/atlas/Role=ciccio"));
//        HashMap<String,List<FQAN>> map = new HashMap<String, List<FQAN>>();
//        map.put("ciccio", fqans);
//        log.debug(ParameterDisplayHelper.display(map));
 
        test.testParseDate();

    }
	
}
