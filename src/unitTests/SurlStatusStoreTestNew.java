package unitTests;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.SurlStatusStore;
import it.grid.storm.synchcall.surl.TokenDuplicationException;
import it.grid.storm.synchcall.surl.UnknownSurlException;
import it.grid.storm.synchcall.surl.UnknownTokenException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class SurlStatusStoreTestNew
{

    private static final String FE_HOST = "omii005-vm03.cnaf.infn.it";
    
//    private static final TStatusCode status = TStatusCode.SRM_DONE;
//    private static final TStatusCode statusNotStored = TStatusCode.SRM_ABORTED;
//    private static final String surlString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/myFile";
//    private static TSURL surl = null;
//    private static TRequestToken requestToken = TRequestToken.getRandom();
//    
//    private static final String surlStringNotStored = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/myFileNotStored";
//    private static TSURL surlNotStored = null;
//    private TRequestToken requestTokenNotStored = null;
//    private static SurlStatusStore instance = SurlStatusStore.getInstance();
    
    private static Random randomGenerator = new Random();
    
    static{
        ((Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.INFO);
    }
    
    
    @Before
    public void setUp() throws Exception
    {
    }

    @Test
    public final void testGetInstance()
    {
        assertNotNull("Null instance!" , SurlStatusStore.getInstance());
    }

    @Test
    public final void testStore() throws IllegalArgumentException, TokenDuplicationException
    {
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(buildRandomSurl(), TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
    }

    @Test
    public final void testUpdateTRequestTokenTSURLTStatusCode() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().update(token, surl, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testUpdateTRequestTokenTSURLTStatusCodeString() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().update(token, surl, TStatusCode.SRM_INTERNAL_ERROR, "nasty stuff");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCode() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        TSURL surl1 = buildRandomSurl();
        TSURL surl2 = buildRandomSurl();
        surls.add(surl1);
        surls.add(surl2);
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{surl1, surl2}, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().update(token, surls, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCodeString() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        TSURL surl1 = buildRandomSurl();
        TSURL surl2 = buildRandomSurl();
        surls.add(surl1);
        surls.add(surl2);
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{surl1, surl2}, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().update(token, surls, TStatusCode.SRM_INTERNAL_ERROR, "got problems");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCode() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, code, "Autentication failed"));
        SurlStatusStore.getInstance().checkAndUpdate(token, surl, code, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeString() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, code, "Autentication failed"));
        SurlStatusStore.getInstance().checkAndUpdate(token, surl, code, TStatusCode.SRM_INTERNAL_ERROR, "we are dead!");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testUpdateTSURLTStatusCodeString() throws IllegalArgumentException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTSURLTStatusCodeString()");
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().update(surl, TStatusCode.SRM_INTERNAL_ERROR, "zombies are coming!");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTSURLTStatusCodeTStatusCodeString() throws IllegalArgumentException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTSURLTStatusCodeTStatusCodeString()");
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, code, "Autentication failed"));
        SurlStatusStore.getInstance().checkAndUpdate(surl, code, TStatusCode.SRM_INTERNAL_ERROR, "I've seen the white Wheel!");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCode() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        TSURL surl1 = buildRandomSurl();
        TSURL surl2 = buildRandomSurl();
        surls.add(surl1);
        surls.add(surl2);
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{surl1, surl2}, code, "Autentication failed"));
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, code, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeString() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        TSURL surl1 = buildRandomSurl();
        TSURL surl2 = buildRandomSurl();
        surls.add(surl1);
        surls.add(surl2);
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{surl1, surl2}, code, "Autentication failed"));
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, code, TStatusCode.SRM_INTERNAL_ERROR, "the hell is coming");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenTStatusCodeTStatusCodeString() throws UnknownTokenException, ExpiredTokenException, IllegalArgumentException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenTStatusCodeTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{buildRandomSurl(), buildRandomSurl()}, code, "Autentication failed"));
        SurlStatusStore.getInstance().checkAndUpdate(token, code, TStatusCode.SRM_INTERNAL_ERROR, "berlusconi is immortal");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testGetSurlsStatusTRequestToken() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testGetSurlsStatusTRequestToken()");
        TRequestToken token = TRequestToken.getRandom();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(buildRandomSurl(), TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().getSurlsStatus(token);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testGetSurlsStatusTRequestTokenTSURL() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testGetSurlsStatusTRequestTokenTSURL()");
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().getSurlsStatus(token, surl);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testGetSurlsStatusTRequestTokenCollectionOfTSURL() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testGetSurlsStatusTRequestTokenCollectionOfTSURL()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        TSURL surl1 = buildRandomSurl();
        TSURL surl2 = buildRandomSurl();
        surls.add(surl1);
        surls.add(surl2);
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().getSurlsStatus(token, surls);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testGetSurlStatuses() throws IllegalArgumentException, UnknownSurlException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testGetSurlStatuses()");
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().getSurlStatuses(surl);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testGetSurlPerTokenStatuses() throws IllegalArgumentException, UnknownSurlException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testGetSurlPerTokenStatuses()");
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().getSurlPerTokenStatuses(surl);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testGetExpiredTokens()
    {
        System.out.println("\nSurlStatusStoreTestNew.testGetExpiredTokens()");
        SurlStatusStore.getInstance().getExpiredTokens();
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckIntegrity() throws Exception
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckIntegrity()");
        SurlStatusStore.getInstance().checkIntegrity();
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    private static HashMap<TSURL, TReturnStatus> buildSurlStatusMap(TSURL surl, TStatusCode code, String explanation)
    {
        return buildSurlStatusMap(new TSURL[]{surl}, code, explanation);
    }
    
    private HashMap<TSURL, TReturnStatus> buildSurlStatusMap(ArrayList<TSURL> surls,
            TStatusCode code, String explanation)
    {
        return buildSurlStatusMap(surls.toArray(new TSURL[surls.size()]), code, explanation);
    }

    private static HashMap<TSURL, TReturnStatus> buildSurlStatusMap(TSURL[] surls, TStatusCode code, String explanation)
    {
        HashMap<TSURL, TReturnStatus> surlStatusMap = new HashMap<TSURL, TReturnStatus>(1);
        for(TSURL surl : surls)
        {
            surlStatusMap.put(surl, buildStatus(code, explanation));    
        }
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
    
    private TSURL buildRandomSurl()
    {
        return buildSurl("srm://" + FE_HOST + ":8444/" + randomGenerator.nextInt());
    }

    private TSURL buildSurl(String string)
    {
        TSURL surl = null;
        try
        {
            surl = TSURL.makeFromStringWellFormed(string);
        } catch(InvalidTSURLAttributesException e)
        {
           fail("Unable to build the TSURL: " + e);
        }
        return surl;
    }

}
