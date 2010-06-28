package component.naming.surl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.utiltest.SetUpTest;

import it.grid.storm.config.ConfigReader;
import it.grid.storm.config.Configuration;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import  it.grid.storm.srm.types.TSURL;

public class SURLNamingTest {

    static String surlTest;    
    static TSURL tsurl;
    private static Logger log = LoggerFactory.getLogger(SURLNamingTest.class);
   
    
    private static void init() {

        //Logging Set Up
          
        System.out.println("Using SLF4J and LogBack");
        String unitTestPath = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        String logConfigFile = unitTestPath + "logging-test.xml";
        SetUpTest.init(logConfigFile);
 

        int refreshInSeconds = 5;
        Configuration config = Configuration.getInstance();
        String configurationPATH = System.getProperty("user.dir") + File.separator + "etc";
        String configurationFileName = configurationPATH + File.separator + "storm-test.properties";
        config.setConfigReader(new ConfigReader(configurationFileName, refreshInSeconds));

        log.debug(" CONFIGURATION = "+configurationFileName);

    }

    
    /**
     * @param args
     */
    public static void main(String[] args) {

        init();
        
        try {
            
            System.out.println(" ----- ");
            surlTest = "srm://storm.cnaf.infn.it:8444/atlas/test/ciccio.text";
            tsurl = TSURL.makeFromStringValidate(surlTest);
            System.out.println("TSURL : "+tsurl);
            
            System.out.println(" ----- ");
            surlTest = "srm://storm.cnaf.infn.it:8444/atlas/test/ciccio:pluto.text";
            tsurl = TSURL.makeFromStringValidate(surlTest);
            System.out.println("TSURL : "+tsurl);
            
            System.out.println(" ----- ");
            surlTest = "srm://storm.cnaf.infn.it:8444/srmv2/manager?SFN=atlas/test/ciccio:pluto.text";
            tsurl = TSURL.makeFromStringValidate(surlTest);
            System.out.println("TSURL : "+tsurl);
            
            System.out.println(" ----- ");
            surlTest = "srm://storm-fe.cr.cnaf.infn.it//atlas/atlasdatatape/data10_cos/NTUP_MUONCALIB/f212_m370/data10_cos.00149310.physics_RNDM.merge.NTUP_MUONCALIB.f212_m370";
            tsurl = TSURL.makeFromStringValidate(surlTest);
            System.out.println("TSURL : "+tsurl);
            
            System.out.println(" ----- ");
            surlTest = "srm://stormx.cnaf.infn.it:8444/atlas/test/ciccio:pluto.text";
            tsurl = TSURL.makeFromStringValidate(surlTest);
            System.out.println("TSURL : "+tsurl);
            
            
        } catch (InvalidTSURLAttributesException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
          
        
    }

}
