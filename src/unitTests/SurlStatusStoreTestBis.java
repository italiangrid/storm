package unitTests;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import it.grid.storm.config.Configuration;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class SurlStatusStoreTestBis
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    private static final String FE_HOST = "omii005-vm03.cnaf.infn.it";

    private static Random randomGenerator = new Random();

    private static final Logger logger;
    static
    {
        logger = ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME));
    }
    private static final String EXPIRED_TOKEN_PREFIX = "Expired-";
    
    @Before
    public void setUp() throws Exception
    {
        logger.setLevel(Level.TRACE);
        try
        {
            SurlStatusStore.getInstance().checkIntegrity();
        } catch(Exception e)
        {
            try
            {
                SurlStatusStore.getInstance().checkIntegrity();
            } catch(Exception e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
    
    @After
    public void tearDown() 
    {
        try
        {
            SurlStatusStore.getInstance().checkIntegrity();
        } catch(Exception e)
        {
            try
            {
                SurlStatusStore.getInstance().checkIntegrity();
            } catch(Exception e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    @Test
    public final void testStore() throws Exception
    {
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, code, explanation));
        
        Map<TSURL, TReturnStatus> surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl);
        
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl).getStatusCode());
        assertEquals("Status code for the stored token should be " + code, code, surlStatusMap.get(surl).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", surlStatusMap.get(surl).getExplanation());
        assertEquals("explanation for the stored token should be " + explanation, explanation, surlStatusMap.get(surl).getExplanation());
    }
    
    @Test
    public final void testStoreList() throws Exception
    {
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl1 = buildRandomSurl();
        TSURL surl2 = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{surl1,surl2}, code, explanation));
        
        Map<TSURL, TReturnStatus> surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl1);
        
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl1));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl1).getStatusCode());
        assertEquals("Status code for the stored token should be " + code, code, surlStatusMap.get(surl1).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", surlStatusMap.get(surl1).getExplanation());
        assertEquals("explanation for the stored token should be " + explanation, explanation, surlStatusMap.get(surl1).getExplanation());
        
        surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl2);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl2));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl2).getStatusCode());
        assertEquals("Status code for the stored token should be " + code, code, surlStatusMap.get(surl2).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", surlStatusMap.get(surl2).getExplanation());
        assertEquals("explanation for the stored token should be " + explanation, explanation, surlStatusMap.get(surl2).getExplanation());
    }
    
    @Test
    public final void testUpdate() throws Exception
    {
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, code, explanation));
        
        TStatusCode updatedStatus = TStatusCode.SRM_DUPLICATION_ERROR;
        SurlStatusStore.getInstance().update(token, surl, updatedStatus);
        
        Map<TSURL, TReturnStatus> surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl);
        
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(surl).getStatusCode());
        assertNotNull("Explanation for the stored token should not be null", surlStatusMap.get(surl).getExplanation());
        assertTrue("Explanation for the stored token should be empty" , surlStatusMap.get(surl).getExplanation().trim().isEmpty());
    }
    
    @Test
    public final void testUpdateWithExplanation() throws Exception
    {
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, code, explanation));
        
        TStatusCode updatedStatus = TStatusCode.SRM_DUPLICATION_ERROR;
        String updatedExpaination = "a good explaination";
        SurlStatusStore.getInstance().update(token, surl, updatedStatus, updatedExpaination);
        
        Map<TSURL, TReturnStatus> surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl);
        
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(surl).getStatusCode());
        assertNotNull("Explanation for the stored token should not be null", surlStatusMap.get(surl).getExplanation());
        assertEquals("Explanation for the stored token should be " + updatedExpaination , updatedExpaination, surlStatusMap.get(surl).getExplanation());
    }
    
    @Test
    public final void testUpdateList() throws Exception
    {
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl1 = buildRandomSurl();
        TSURL surl2 = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{surl1,surl2}, code, explanation));
        
        TStatusCode updatedStatus = TStatusCode.SRM_DUPLICATION_ERROR;
        SurlStatusStore.getInstance().update(token, Arrays.asList(new TSURL[]{surl1,surl2}), updatedStatus);

        Map<TSURL, TReturnStatus> surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl1);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl1));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl1).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(surl1).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", surlStatusMap.get(surl1).getExplanation());
        assertTrue("explanation for the stored token should be empty", surlStatusMap.get(surl1).getExplanation().trim().isEmpty());
        
        surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl2);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl2));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl2).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(surl2).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", surlStatusMap.get(surl2).getExplanation());
        assertTrue("explanation for the stored token should be empty", surlStatusMap.get(surl2).getExplanation().trim().isEmpty());
    }
    
    @Test
    public final void testUpdateListWithExplanation() throws Exception
    {
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl1 = buildRandomSurl();
        TSURL surl2 = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{surl1,surl2}, code, explanation));
        
        TStatusCode updatedStatus = TStatusCode.SRM_DUPLICATION_ERROR;
        String updatedExpaination = "a good explaination";
        SurlStatusStore.getInstance().update(token, Arrays.asList(new TSURL[]{surl1,surl2}), updatedStatus, updatedExpaination);

        Map<TSURL, TReturnStatus> surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl1);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl1));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl1).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(surl1).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", surlStatusMap.get(surl1).getExplanation());
        assertEquals("explanation for the stored token should be " + updatedExpaination, updatedExpaination, surlStatusMap.get(surl1).getExplanation());
        
        surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl2);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl2));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl2).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(surl2).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", surlStatusMap.get(surl2).getExplanation());
        assertEquals("explanation for the stored token should be " + updatedExpaination, updatedExpaination, surlStatusMap.get(surl2).getExplanation());
    }
    
    @Test
    public final void testCheckUpdate() throws Exception
    {
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, code, explanation));
        
        TStatusCode updatedStatus = TStatusCode.SRM_DUPLICATION_ERROR;
        SurlStatusStore.getInstance().checkAndUpdate(token, surl, code, updatedStatus);
        
        Map<TSURL, TReturnStatus> surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl);
        
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(surl).getStatusCode());
        assertNotNull("Explanation for the stored token should not be null", surlStatusMap.get(surl).getExplanation());
        assertTrue("Explanation for the stored token should be empty" , surlStatusMap.get(surl).getExplanation().trim().isEmpty());
    }
    
    @Test
    public final void testCheckUpdateFailed() throws Exception
    {
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, code, explanation));
        
        SurlStatusStore.getInstance().checkAndUpdate(token, surl, TStatusCode.SRM_DONE, TStatusCode.SRM_DUPLICATION_ERROR);
        
        Map<TSURL, TReturnStatus> surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl);
        
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl).getStatusCode());
        assertEquals("Status code for the stored token should be " + code, code, surlStatusMap.get(surl).getStatusCode());
        assertNotNull("Explanation for the stored token should not be null", surlStatusMap.get(surl).getExplanation());
        assertEquals("explanation for the stored token should be " + explanation, explanation, surlStatusMap.get(surl).getExplanation());
    }
    
    @Test
    public final void testCheckUpdateWithExplanation() throws Exception
    {
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, code, explanation));
        
        TStatusCode updatedStatus = TStatusCode.SRM_DUPLICATION_ERROR;
        String updatedExpaination = "a good explaination";
        SurlStatusStore.getInstance().checkAndUpdate(token, surl, code, updatedStatus, updatedExpaination);
        
        Map<TSURL, TReturnStatus> surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl);
        
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(surl).getStatusCode());
        assertNotNull("Explanation for the stored token should not be null", surlStatusMap.get(surl).getExplanation());
        assertEquals("explanation for the stored token should be " + updatedExpaination, updatedExpaination, surlStatusMap.get(surl).getExplanation());
    }
    
    @Test
    public final void testCheckUpdateWithExplanationFailed() throws Exception
    {
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, code, explanation));
        
        SurlStatusStore.getInstance().checkAndUpdate(token, surl, TStatusCode.SRM_DONE, TStatusCode.SRM_DUPLICATION_ERROR, "a good explaination");
        
        Map<TSURL, TReturnStatus> surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl);
        
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl).getStatusCode());
        assertEquals("Status code for the stored token should be " + code, code, surlStatusMap.get(surl).getStatusCode());
        assertNotNull("Explanation for the stored token should not be null", surlStatusMap.get(surl).getExplanation());
        assertEquals("explanation for the stored token should be " + explanation, explanation, surlStatusMap.get(surl).getExplanation());
    }
    
    @Test
    public final void testCheckUpdateList() throws Exception
    {
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl1 = buildRandomSurl();
        TSURL surl2 = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{surl1,surl2}, code, explanation));
        
        TStatusCode updatedStatus = TStatusCode.SRM_DUPLICATION_ERROR;
        SurlStatusStore.getInstance().checkAndUpdate(token, Arrays.asList(new TSURL[]{surl1,surl2}), code, updatedStatus);

        Map<TSURL, TReturnStatus> surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl1);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl1));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl1).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(surl1).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", surlStatusMap.get(surl1).getExplanation());
        assertTrue("explanation for the stored token should be empty", surlStatusMap.get(surl1).getExplanation().trim().isEmpty());
        
        surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl2);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl2));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl2).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(surl2).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", surlStatusMap.get(surl2).getExplanation());
        assertTrue("explanation for the stored token should be empty", surlStatusMap.get(surl2).getExplanation().trim().isEmpty());
    }
    
    @Test
    public final void testCheckUpdateListFailed() throws Exception
    {
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl1 = buildRandomSurl();
        TSURL surl2 = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{surl1,surl2}, code, explanation));
        
        SurlStatusStore.getInstance().checkAndUpdate(token, Arrays.asList(new TSURL[]{surl1,surl2}), TStatusCode.SRM_FILE_BUSY, TStatusCode.SRM_DUPLICATION_ERROR);

        Map<TSURL, TReturnStatus> surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl1);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl1));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl1).getStatusCode());
        assertEquals("Status code for the stored token should be " + code, code, surlStatusMap.get(surl1).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", surlStatusMap.get(surl1).getExplanation());
        assertEquals("explanation for the stored token should be " + explanation, explanation, surlStatusMap.get(surl1).getExplanation());
        
        surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl2);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl2));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl2).getStatusCode());
        assertEquals("Status code for the stored token should be " + code, code, surlStatusMap.get(surl2).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", surlStatusMap.get(surl2).getExplanation());
        assertEquals("explanation for the stored token should be " + explanation, explanation, surlStatusMap.get(surl2).getExplanation());
    }
    
    @Test
    public final void testCheckUpdateListWithExplanation() throws Exception
    {
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl1 = buildRandomSurl();
        TSURL surl2 = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{surl1,surl2}, code, explanation));
        
        TStatusCode updatedStatus = TStatusCode.SRM_DUPLICATION_ERROR;
        String updatedExpaination = "a good explaination";
        SurlStatusStore.getInstance().checkAndUpdate(token, Arrays.asList(new TSURL[]{surl1,surl2}), code, updatedStatus, updatedExpaination);

        Map<TSURL, TReturnStatus> surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl1);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl1));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl1).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(surl1).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", surlStatusMap.get(surl1).getExplanation());
        assertEquals("explanation for the stored token should be " + updatedExpaination, updatedExpaination, surlStatusMap.get(surl1).getExplanation());
        
        surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl2);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl2));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl2).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(surl2).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", surlStatusMap.get(surl2).getExplanation());
        assertEquals("explanation for the stored token should be " + updatedExpaination, updatedExpaination, surlStatusMap.get(surl2).getExplanation());
    }
    
    @Test
    public final void testCheckUpdateListWithExplanationFailed() throws Exception
    {
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl1 = buildRandomSurl();
        TSURL surl2 = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{surl1,surl2}, code, explanation));
        
        SurlStatusStore.getInstance().checkAndUpdate(token, Arrays.asList(new TSURL[]{surl1,surl2}), TStatusCode.SRM_FILE_BUSY, TStatusCode.SRM_DUPLICATION_ERROR, "not good");

        Map<TSURL, TReturnStatus> surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl1);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl1));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl1).getStatusCode());
        assertEquals("Status code for the stored token should be " + code, code, surlStatusMap.get(surl1).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", surlStatusMap.get(surl1).getExplanation());
        assertEquals("explanation for the stored token should be " + explanation, explanation, surlStatusMap.get(surl1).getExplanation());
        
        surlStatusMap = SurlStatusStore.getInstance().getSurlsStatus(token, surl2);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(surl2));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(surl2).getStatusCode());
        assertEquals("Status code for the stored token should be " + code, code, surlStatusMap.get(surl2).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", surlStatusMap.get(surl2).getExplanation());
        assertEquals("explanation for the stored token should be " + explanation, explanation, surlStatusMap.get(surl2).getExplanation());
    }
    
    
    @Test
    public final void testStoreSurlStored() throws IllegalArgumentException, TokenDuplicationException, UnknownTokenException, ExpiredTokenException, UnknownSurlException
    {
        TRequestToken token1 = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        TStatusCode code1 = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation1 = "Autentication failed";
        SurlStatusStore.getInstance().store(token1, buildSurlStatusMap(surl, code1, explanation1));
        
        TRequestToken token2 = TRequestToken.getRandom();
        TStatusCode code2 = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation2 = "Autentication failed";
        SurlStatusStore.getInstance().store(token2, buildSurlStatusMap(surl, code2, explanation2));
        
        Map<TRequestToken, TReturnStatus> tokenStatusMap = SurlStatusStore.getInstance().getSurlPerTokenStatuses(surl);
        
        assertNotNull("Token status map should not be null", tokenStatusMap);
        assertFalse("Token status map should not be empty", tokenStatusMap.isEmpty());
        assertNotNull("Token for the stored surl should be available", tokenStatusMap.get(token1));
        assertNotNull("Status code for the stored surl should not be null", tokenStatusMap.get(token1).getStatusCode());
        assertEquals("Status code for the stored token should be " + code1, code1, tokenStatusMap.get(token1).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", tokenStatusMap.get(token1).getExplanation());
        assertEquals("explanation for the stored token should be " + explanation1, explanation1, tokenStatusMap.get(token1).getExplanation());
        
        assertNotNull("Token for the stored surl should be available", tokenStatusMap.get(token2));
        assertNotNull("Status code for the stored surl should not be null", tokenStatusMap.get(token2).getStatusCode());
        assertEquals("Status code for the stored token should be " + code2, code2, tokenStatusMap.get(token2).getStatusCode());
        assertNotNull("explanation for the stored token should not be null", tokenStatusMap.get(token2).getExplanation());
        assertEquals("explanation for the stored token should be " + explanation2, explanation2, tokenStatusMap.get(token2).getExplanation());
    }
    
    @Test
    public final void testGetExpiredTokens() throws Exception
    {
        TRequestToken token = buildExpiredToken();
        assertTrue("expiredToken should have an ExpirationDate", token.hasExpirationDate());
        assertTrue("expiredToken should be expired", token.isExpired());
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, code, explanation));
        
        List<TRequestToken> expiredTokens = SurlStatusStore.getInstance().getExpiredTokens();

        assertNotNull("expiredTokens should not be null", expiredTokens);
        assertFalse("expiredTokens should not be empty", expiredTokens.isEmpty());
        assertTrue("expiredTokens should contain the stored token", expiredTokens.contains(token));
        
        thrown.expect(ExpiredTokenException.class);
        SurlStatusStore.getInstance().getSurlsStatus(token, surl);
    }
    
    @Test
    public final void testPurge() throws Exception
    {
        TRequestToken token = buildExpiredToken();
        assertTrue("expiredToken should have an ExpirationDate", token.hasExpirationDate());
        assertTrue("expiredToken should be expired", token.isExpired());
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, code, explanation));
        
        Object lock = new Object();
        synchronized (lock)
        {
            lock.wait((Configuration.getInstance().getRequestPurgerPeriod() * 1000) + (Configuration.getInstance().getRequestPurgerDelay() * 1000) + 1);
        }
        
        thrown.expect(UnknownTokenException.class);
        SurlStatusStore.getInstance().getSurlsStatus(token, surl);
    }
    
    private TRequestToken buildExpiredToken()
    {
        Calendar expiredTime = Calendar.getInstance();
        expiredTime.setTimeInMillis(expiredTime.getTimeInMillis() - ((Configuration.getInstance().getExpiredRequestTime() + 1) * 1000));
        try
        {
            return new TRequestToken(EXPIRED_TOKEN_PREFIX.concat(UUID.randomUUID()
                                                                           .toString()
                                                                           .substring(EXPIRED_TOKEN_PREFIX.length())) , expiredTime.getTime());
        } catch(InvalidTRequestTokenAttributesException e)
        {
            throw new IllegalStateException("Unexpected InvalidTRequestTokenAttributesException: " + e);
        }
    }
    
    
    private void addSurlStatus(HashMap<TSURL, TReturnStatus> map, TSURL surl, TStatusCode code, String explanation)
    {
        map.put(surl, buildStatus(code, explanation));    
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
        HashMap<TSURL, TReturnStatus> surlStatusMap = new HashMap<TSURL, TReturnStatus>(surls.length);
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
