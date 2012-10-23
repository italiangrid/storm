package unitTests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.SurlStatusStore;
import it.grid.storm.synchcall.surl.TokenDuplicationException;
import it.grid.storm.synchcall.surl.UnknownTokenException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SurlStatusStoreTest
{

    private static final TStatusCode status = TStatusCode.SRM_DONE;
    private static final TStatusCode statusNotStored = TStatusCode.SRM_ABORTED;
    private static final String surlString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/myFile";
    private static TSURL surl = null;
    private static TRequestToken requestToken = TRequestToken.getRandom();
    
    private static final String surlStringNotStored = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/myFileNotStored";
    private static TSURL surlNotStored = null;
    private TRequestToken requestTokenNotStored = null;
    private static SurlStatusStore instance = SurlStatusStore.getInstance();
    
    
    @Before
    public void setUp() throws Exception
    {
        try
        {
            surl = TSURL.makeFromStringWellFormed(surlString);
        } catch(InvalidTSURLAttributesException e)
        {
           fail("Unable to build the TSURL: " + e);
        }
        instance.store(requestToken, buildSurlStatusMap(surl, status));
        try
        {
            surlNotStored = TSURL.makeFromStringWellFormed(surlStringNotStored);
        } catch(InvalidTSURLAttributesException e)
        {
           fail("Unable to build the TSURL: " + e);
        }
        requestTokenNotStored = TRequestToken.getRandom(); 
    }
    
    @Test
    public void testStore() throws IllegalArgumentException, TokenDuplicationException
    {
        instance.store(requestTokenNotStored, buildSurlStatusMap(surlNotStored, status));
//        TStatusCode storedStatus = instance.getSurlStatus(surl);
//        assertEquals(statusNotStored, storedStatus);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreNullArgs() throws TokenDuplicationException
    {
        instance.store(null, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testStoreNullArg1()throws TokenDuplicationException
    {
        instance.store(null, buildSurlStatusMap(surlNotStored, status));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testStoreNullArg2()throws TokenDuplicationException
    {
        instance.store(requestTokenNotStored, null);
    }
    
//    @Test
//    public void testForgetSurl()
//    {
//        instance.forgetSurl(surl);
//        TStatusCode storedStatus = instance.getSurlStatus(surl);
//        assertEquals(null, storedStatus);
//    }
    
//    @Test
//    public void testForgetSurlIdempotence()
//    {
//        instance.forgetSurl(surl);
//        instance.forgetSurl(surl);
//        TStatusCode storedStatus = instance.getSurlStatus(surl);
//        assertEquals(null, storedStatus);
//    }
    
//    @Test
//    public void testForgetSurlUnknownSurl()
//    {
//        instance.forgetSurl(surlNotStored);
//        TStatusCode storedStatus = instance.getSurlStatus(surlNotStored);
//        assertEquals(null, storedStatus);
//    }

//    @Test(expected = IllegalArgumentException.class)
//    public void testForgetSurlNullArgs()
//    {
//        instance.forgetSurl(null);
//    }
//    
    @Test
    public void testGetSurlStatus() throws UnknownTokenException, IllegalArgumentException, ExpiredTokenException
    {
        Map<TSURL, TReturnStatus> storedStatus = instance.getSurlsStatus(requestToken,
                                                                         Arrays.asList(new TSURL[] { surl }));
        assertNotNull(storedStatus.get(requestToken));
        assertEquals("Status should match", status, storedStatus.get(requestToken).getStatusCode());
    }
    
    private static HashMap<TSURL, TReturnStatus> buildSurlStatusMap(TSURL surl, TStatusCode code)
    {
        HashMap<TSURL, TReturnStatus> surlStatusMap = new HashMap<TSURL, TReturnStatus>(1);
        surlStatusMap.put(surl, buildStatus(code, ""));
        return surlStatusMap;
    }
    
    private static HashMap<TSURL, TReturnStatus> buildSurlStatusMap(TSURL surl, TStatusCode code, String explanation)
    {
        HashMap<TSURL, TReturnStatus> surlStatusMap = new HashMap<TSURL, TReturnStatus>(1);
        surlStatusMap.put(surl, buildStatus(code, explanation));
        return surlStatusMap;
    }

    private static TReturnStatus buildStatus(TStatusCode statusCode, String explaination)
            throws IllegalStateException
    {
        try
        {
            return new TReturnStatus(statusCode, explaination);
        } catch(InvalidTReturnStatusAttributeException e1)
        {
            // Never thrown
            throw new IllegalStateException("Unexpected InvalidTReturnStatusAttributeException "
                    + "in building TReturnStatus: " + e1.getMessage());
        }
    }
    
}
