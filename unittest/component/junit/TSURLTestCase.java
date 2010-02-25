package component.junit;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TSURL;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TSURLTestCase extends TestCase {

    private Hashtable<String,Boolean> testsSurl = new Hashtable<String, Boolean>() ;  
    
    public static Test suite() {
        return new TestSuite(TSURLTestCase.class);
    }    
    
    protected void setUp() throws Exception {
        super.setUp();
        //Expected valid SURL
        testsSurl.put("srm://storm.cnaf.infn.it:8444/srmv2/manager?SFN=/pippo/pluto.txt", true);
        testsSurl.put("srm://storm.cnaf.infn.it:8444/srmv2/manager?SFN=/pippo/pl:uto.txt", true);
        testsSurl.put("srm://storm.cnaf.infn.it/srmv2/manager?SFN=/pippo/pluto.txt", true);
        testsSurl.put("srm://storm.cnaf.infn.it:8444/pippo/pluto.txt", true);
        testsSurl.put("srm://storm.cnaf.infn.it:8444/pippo/plu:to.txt", true);
        testsSurl.put("srm://storm.cnaf.infn.it/pippo/pluto.txt", true);
        testsSurl.put("srm://storm.cnaf.infn.it/pippo/plu:to.txt", true);
        //Expected Invalid SURL
        testsSurl.put("srm://stormXX.cnaf.infn.it:8444/srmv2/manager?SFN=/pippo/pluto.txt", false);
        testsSurl.put("srm://storm.cnaf.infn.it:8422/srmv2/manager?SFN=/pippo/pluto.txt", false);
        testsSurl.put("srm://storm.cnaf.infn.it:8444/srmv2/manager?SFN=/pippo/pl#uto.txt", false);

        
    }

    private boolean testMakeFromStringValidate(String surlString, boolean expectedValid) {   
        try {
            TSURL.makeFromStringValidate(surlString);
        } catch (InvalidTSURLAttributesException e) {
            //Fail to build the SURL. 
            if (expectedValid) {
                return false;
                //fail("The String '"+surlString+"' is expected to be a valid SURL, but id didn't. FAIL!!");
            } else {
                return true;
                //assertFalse("TSURL is not valid, as expected.",expectedValid);
            }        
        }
        if (expectedValid) {
            return true;
        }
        return false;
    }
    
    public void testSurls() {
        Set<String> surls = testsSurl.keySet();
        Iterator<String> surlIter = surls.iterator();
        while (surlIter.hasNext()) {
            String surl = surlIter.next();
            Boolean expectedResult = testsSurl.get(surl);
            assertTrue(testMakeFromStringValidate(surl, expectedResult.booleanValue()));
        }
    }
    

}
