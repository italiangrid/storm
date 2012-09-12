package unitTests;

import it.grid.storm.namespace.SurlStatusStore;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SurlStatusStoreTest
{

    private static final TStatusCode status = TStatusCode.SRM_DONE;
    private static final TStatusCode statusNotStored = TStatusCode.SRM_ABORTED;
    private static final String surlstring = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/myFile";
    private static TSURL surl = null;
    private static TSURL surlNotStored = null;
    private SurlStatusStore instance = SurlStatusStore.getInstance();
    
    
    @Before
    public void setUp() throws Exception
    {
        try
        {
            surl = TSURL.makeFromStringWellFormed(surlstring);
        } catch(InvalidTSURLAttributesException e)
        {
           fail("Unable to build the TSURL: " + e);
        }
        instance.storeSurlStatus(surl, status);
        try
        {
            surlNotStored = TSURL.makeFromStringWellFormed(surlstring);
        } catch(InvalidTSURLAttributesException e)
        {
           fail("Unable to build the TSURL: " + e);
        }
    }
    
    @Test
    public void testStoreSurlStatus()
    {
        instance.storeSurlStatus(surl, statusNotStored);
        TStatusCode storedStatus = instance.getSurlStatus(surl);
        assertEquals(statusNotStored, storedStatus);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreSurlStatusNullArgs()
    {
        instance.storeSurlStatus(null, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testStoreSurlStatusNullArg1()
    {
        instance.storeSurlStatus(null, status);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testStoreSurlStatusNullArg2()
    {
        instance.storeSurlStatus(surl, null);
    }
    
    @Test
    public void testForgetSurl()
    {
        instance.forgetSurl(surl);
        TStatusCode storedStatus = instance.getSurlStatus(surl);
        assertEquals(null, storedStatus);
    }
    
    @Test
    public void testForgetSurlIdempotence()
    {
        instance.forgetSurl(surl);
        instance.forgetSurl(surl);
        TStatusCode storedStatus = instance.getSurlStatus(surl);
        assertEquals(null, storedStatus);
    }
    
    @Test
    public void testForgetSurlUnknownSurl()
    {
        instance.forgetSurl(surlNotStored);
        TStatusCode storedStatus = instance.getSurlStatus(surlNotStored);
        assertEquals(null, storedStatus);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForgetSurlNullArgs()
    {
        instance.forgetSurl(null);
    }
    
    @Test
    public void testGetSurlStatus()
    {
        TStatusCode storedStatus = instance.getSurlStatus(surl);
        assertEquals(status, storedStatus);
    }
    
}
