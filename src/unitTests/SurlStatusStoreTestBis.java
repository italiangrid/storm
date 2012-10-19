package unitTests;

import static org.junit.Assert.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import it.grid.storm.config.Configuration;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.surl.SurlStatusStore;
import it.grid.storm.synchcall.surl.UnknownSurlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import edu.emory.mathcs.backport.java.util.Arrays;

public class SurlStatusStoreTestBis
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    private static final TStatusCode status = TStatusCode.SRM_REQUEST_QUEUED;
    
    private static final String storedSurlString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/mystoredSurlS";
    private static final TSURL storedSurl = buildSurl(storedSurlString);
    
    private static final TRequestToken requestToken = TRequestToken.getRandom();
    
    private static final SurlStatusStore instance = SurlStatusStore.getInstance();

    private static final String EXPIRED_TOKEN_PREFIX = "Expired-";
    private static boolean firtSetup = true;
    
    @Before
    public void setUp() throws Exception
    {
        if(firtSetup)
        {
            try
            {
                instance.checkIntegrity();
            } catch(Exception e)
            {
                fail("SurlStatusStore integrity compromised. Exception: " + e.getMessage());
            }
            instance.store(requestToken, buildSurlStatusMap(storedSurl, status));
            firtSetup = false;
        }
        try
        {
            instance.checkIntegrity();
        } catch(Exception e)
        {
            fail("SurlStatusStore integrity compromised. Exception: " + e.getMessage());
        }
    }

    @After
    public void tearDown() 
    {
        try
        {
            instance.checkIntegrity();
        } catch(Exception e)
        {
            fail("SurlStatusStore integrity compromised. Exception: " + e.getMessage());
        }
    }
    
    @Test
    public final void testStore() throws Exception
    {
        TRequestToken storeRequestToken = TRequestToken.getRandom();
        String storeSurlString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/mystoreSurl";
        TSURL storeSurl = buildSurl(storeSurlString);
        instance.store(storeRequestToken, buildSurlStatusMap(storeSurl, status));
        instance.checkIntegrity();
    }
    
    @Test
    public final void testMultipleStore() throws Exception
    {
        String storeSurlString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/mystoreSurl";
        TSURL storeSurl = buildSurl(storeSurlString);
        HashMap<TSURL, TReturnStatus> surlStatus = buildSurlStatusMap(storeSurl, status);
        instance.store(TRequestToken.getRandom(), surlStatus);
        instance.store(TRequestToken.getRandom(), surlStatus);
        instance.store(TRequestToken.getRandom(), surlStatus);
        instance.store(TRequestToken.getRandom(), surlStatus);
        instance.store(TRequestToken.getRandom(), surlStatus);
        instance.store(TRequestToken.getRandom(), surlStatus);
        instance.checkIntegrity();
    }
    
    
    
    @Test
    public final void testUpdate() throws Exception
    {
        TRequestToken updateRequestToken = TRequestToken.getRandom();
        String updateSurlString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/myupdate";
        TSURL updateSurl = buildSurl(updateSurlString);
        instance.store(updateRequestToken, buildSurlStatusMap(updateSurl, status));
        instance.checkIntegrity();
        TStatusCode updatedStatus = TStatusCode.SRM_DUPLICATION_ERROR;
        instance.update(updateRequestToken, updateSurl, updatedStatus);
        Map<TSURL, TReturnStatus> surlStatusMap = instance.getSurlsStatus(updateRequestToken, updateSurl);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(updateSurl));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(updateSurl).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(updateSurl).getStatusCode());
    }
    
    @Test
    public final void testUpdateWithExplanation() throws Exception
    {
        TRequestToken updateExplanationRequestToken = TRequestToken.getRandom();
        String updateExplanationSurlString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/myupdateExplanation";
        TSURL updateExplanationSurl = buildSurl(updateExplanationSurlString);
        instance.store(updateExplanationRequestToken, buildSurlStatusMap(updateExplanationSurl, status));
        instance.checkIntegrity();
        TStatusCode updatedStatus = TStatusCode.SRM_DUPLICATION_ERROR;
        String updatedExpaination = "a good explaination";
        instance.update(updateExplanationRequestToken, updateExplanationSurl, updatedStatus, updatedExpaination);
        Map<TSURL, TReturnStatus> surlStatusMap = instance.getSurlsStatus(updateExplanationRequestToken, updateExplanationSurl);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(updateExplanationSurl));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(updateExplanationSurl).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(updateExplanationSurl).getStatusCode());
        assertEquals("explanation for the stored token should be \'" + updatedExpaination + "\'", updatedExpaination, surlStatusMap.get(updateExplanationSurl).getExplanation());
    }
    
    @Test
    public final void testUpdateList() throws Exception
    {
        TRequestToken updateListRequestToken = TRequestToken.getRandom();
        String updateListSurlOneString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/myupdateListOne";
        TSURL updateListSurlOne = buildSurl(updateListSurlOneString);
        String updateListSurlTwoString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/myupdateListTwo";
        TSURL updateListSurlTwo = buildSurl(updateListSurlTwoString);
        instance.store(updateListRequestToken,
                       buildSurlStatusMap(new TSURL[] { updateListSurlOne, updateListSurlTwo },
                                          new TStatusCode[] { status, status }));
        instance.checkIntegrity();
        TStatusCode updatedStatusOne = TStatusCode.SRM_REQUEST_INPROGRESS;
        instance.update(updateListRequestToken, updateListSurlOne, updatedStatusOne);
        instance.checkIntegrity();
        Map<TSURL, TReturnStatus> surlStatusMap = instance.getSurlsStatus(updateListRequestToken, updateListSurlOne);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(updateListSurlOne));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(updateListSurlOne).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatusOne, updatedStatusOne, surlStatusMap.get(updateListSurlOne).getStatusCode());
        TStatusCode updatedStatusTwo = TStatusCode.SRM_ABORTED;
        instance.update(updateListRequestToken, Arrays.asList(new TSURL[]{updateListSurlOne,updateListSurlTwo}), updatedStatusTwo);
        surlStatusMap = instance.getSurlsStatus(updateListRequestToken, Arrays.asList(new TSURL[]{updateListSurlOne, updateListSurlTwo}));
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(updateListSurlOne));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(updateListSurlOne).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatusTwo, updatedStatusTwo, surlStatusMap.get(updateListSurlOne).getStatusCode());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(updateListSurlTwo));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(updateListSurlTwo).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatusTwo, updatedStatusTwo, surlStatusMap.get(updateListSurlTwo).getStatusCode());
    }
    
    @Test
    public final void testUpdateListWithExplanation() throws Exception
    {
        TRequestToken updateListExplanationRequestToken = TRequestToken.getRandom();
        String updateListExplanationSurlOneString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/myupdateListExplanationOne";
        TSURL updateListExplanationSurlOne = buildSurl(updateListExplanationSurlOneString);
        String updateListExplanationSurlTwoString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/myupdateListExplanationTwo";
        TSURL updateListExplanationSurlTwo = buildSurl(updateListExplanationSurlTwoString);
        instance.store(updateListExplanationRequestToken,
                       buildSurlStatusMap(new TSURL[] { updateListExplanationSurlOne, updateListExplanationSurlTwo },
                                          new TStatusCode[] { status, status }));
        instance.checkIntegrity();
        TStatusCode updatedStatus = TStatusCode.SRM_ABORTED;
        String updatedExpaination = "life is so";
        instance.update(updateListExplanationRequestToken, Arrays.asList(new TSURL[]{updateListExplanationSurlOne,updateListExplanationSurlTwo}), updatedStatus,updatedExpaination);
        Map<TSURL, TReturnStatus> surlStatusMap = instance.getSurlsStatus(updateListExplanationRequestToken, Arrays.asList(new TSURL[]{updateListExplanationSurlOne, updateListExplanationSurlTwo}));
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(updateListExplanationSurlOne));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(updateListExplanationSurlOne).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(updateListExplanationSurlOne).getStatusCode());
        assertEquals("explanation for the stored token should be \'" + updatedExpaination + "\'", updatedExpaination, surlStatusMap.get(updateListExplanationSurlOne).getExplanation());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(updateListExplanationSurlTwo));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(updateListExplanationSurlTwo).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(updateListExplanationSurlTwo).getStatusCode());
        assertEquals("explanation for the stored token should be \'" + updatedExpaination + "\'", updatedExpaination, surlStatusMap.get(updateListExplanationSurlTwo).getExplanation());
    }
    
    @Test
    public final void testCheckUpdate() throws Exception
    {
        TRequestToken checkUpdateRequestToken = TRequestToken.getRandom();
        String checkUpdateSurlString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/mycheckUpdate";
        TSURL checkUpdateSurl = buildSurl(checkUpdateSurlString);
        instance.store(checkUpdateRequestToken, buildSurlStatusMap(checkUpdateSurl, status));
        instance.checkIntegrity();
        TStatusCode checkUpdatedStatus = TStatusCode.SRM_DUPLICATION_ERROR;
        instance.checkAndUpdate(checkUpdateRequestToken, checkUpdateSurl, status, checkUpdatedStatus);
        Map<TSURL, TReturnStatus> surlStatusMap = instance.getSurlsStatus(checkUpdateRequestToken, checkUpdateSurl);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(checkUpdateSurl));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(checkUpdateSurl).getStatusCode());
        assertEquals("Status code for the stored token should be " + checkUpdatedStatus, checkUpdatedStatus, surlStatusMap.get(checkUpdateSurl).getStatusCode());
    }
    
    @Test
    public final void testCheckUpdateWrongStatus() throws Exception
    {
        TRequestToken checkUpdateWrongRequestToken = TRequestToken.getRandom();
        String checkUpdateWrongSurlString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/mycheckUpdateWrong";
        TSURL checkUpdateWrongSurl = buildSurl(checkUpdateWrongSurlString);
        instance.store(checkUpdateWrongRequestToken, buildSurlStatusMap(checkUpdateWrongSurl, status));
        instance.checkIntegrity();
        instance.checkAndUpdate(checkUpdateWrongRequestToken, checkUpdateWrongSurl, TStatusCode.SRM_AUTHORIZATION_FAILURE, TStatusCode.SRM_DUPLICATION_ERROR);
        Map<TSURL, TReturnStatus> surlStatusMap = instance.getSurlsStatus(checkUpdateWrongRequestToken, checkUpdateWrongSurl);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(checkUpdateWrongSurl));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(checkUpdateWrongSurl).getStatusCode());
        assertEquals("Status code for the stored token should be " + status, status, surlStatusMap.get(checkUpdateWrongSurl).getStatusCode());
    }
    
    @Test
    public final void testCheckUpdateWithExplanation() throws Exception
    {
        TRequestToken checkUpdateExplanationRequestToken = TRequestToken.getRandom();
        String checkUpdateExplanationSurlString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/mycheckUpdateExplanation";
        TSURL checkUpdateExplanationSurl = buildSurl(checkUpdateExplanationSurlString);
        instance.store(checkUpdateExplanationRequestToken, buildSurlStatusMap(checkUpdateExplanationSurl, status));
        instance.checkIntegrity();
        TStatusCode checkUpdatedStatus = TStatusCode.SRM_DUPLICATION_ERROR;
        String checkUpdatedExpaination = "a good explaination";
        instance.checkAndUpdate(checkUpdateExplanationRequestToken, checkUpdateExplanationSurl, status, checkUpdatedStatus, checkUpdatedExpaination);
        Map<TSURL, TReturnStatus> surlStatusMap = instance.getSurlsStatus(checkUpdateExplanationRequestToken, checkUpdateExplanationSurl);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(checkUpdateExplanationSurl));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(checkUpdateExplanationSurl).getStatusCode());
        assertEquals("Status code for the stored token should be " + checkUpdatedStatus, checkUpdatedStatus, surlStatusMap.get(checkUpdateExplanationSurl).getStatusCode());
        assertEquals("explanation for the stored token should be \'" + checkUpdatedExpaination + "\'", checkUpdatedExpaination, surlStatusMap.get(checkUpdateExplanationSurl).getExplanation());
    }
    
    @Test
    public final void testCheckUpdateWithExplanationWrong() throws Exception
    {
        TRequestToken checkUpdateExplanationWrongRequestToken = TRequestToken.getRandom();
        String checkUpdateExplanationWrongSurlString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/mycheckUpdateExplanationWrong";
        TSURL checkUpdateExplanationWrongSurl = buildSurl(checkUpdateExplanationWrongSurlString);
        instance.store(checkUpdateExplanationWrongRequestToken, buildSurlStatusMap(checkUpdateExplanationWrongSurl, status));
        instance.checkIntegrity();
        String checkUpdatedExpaination = "a good explaination";
        instance.checkAndUpdate(checkUpdateExplanationWrongRequestToken, checkUpdateExplanationWrongSurl, TStatusCode.SRM_DUPLICATION_ERROR, TStatusCode.SRM_FATAL_INTERNAL_ERROR, checkUpdatedExpaination);
        Map<TSURL, TReturnStatus> surlStatusMap = instance.getSurlsStatus(checkUpdateExplanationWrongRequestToken, checkUpdateExplanationWrongSurl);
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(checkUpdateExplanationWrongSurl));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(checkUpdateExplanationWrongSurl).getStatusCode());
        assertEquals("Status code for the stored token should be " + status, status, surlStatusMap.get(checkUpdateExplanationWrongSurl).getStatusCode());
        assertTrue("explanation for the stored token should be empty. got " + surlStatusMap.get(checkUpdateExplanationWrongSurl).getExplanation(), surlStatusMap.get(checkUpdateExplanationWrongSurl).getExplanation().trim().isEmpty());
    }
    
    @Test
    public final void testCheckUpdateList() throws Exception
    {
        TRequestToken checkUpdateListRequestToken = TRequestToken.getRandom();
        String checkUpdateListSurlOneString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/mycheckUpdateListOne";
        TSURL checkUpdateListSurlOne = buildSurl(checkUpdateListSurlOneString);
        String checkUpdateListSurlTwoString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/mycheckUpdateListTwo";
        TSURL checkUpdateListSurlTwo = buildSurl(checkUpdateListSurlTwoString);
        instance.store(checkUpdateListRequestToken,
                       buildSurlStatusMap(new TSURL[] { checkUpdateListSurlOne, checkUpdateListSurlTwo },
                                          new TStatusCode[] { status, status }));
        instance.checkIntegrity();
        TStatusCode updatedStatus = TStatusCode.SRM_REQUEST_INPROGRESS;
        instance.checkAndUpdate(checkUpdateListRequestToken, Arrays.asList(new TSURL[]{checkUpdateListSurlOne,checkUpdateListSurlTwo}), status, updatedStatus);
        Map<TSURL, TReturnStatus> surlStatusMap = instance.getSurlsStatus(checkUpdateListRequestToken, Arrays.asList(new TSURL[]{checkUpdateListSurlOne, checkUpdateListSurlTwo}));
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(checkUpdateListSurlOne));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(checkUpdateListSurlOne).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(checkUpdateListSurlOne).getStatusCode());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(checkUpdateListSurlTwo));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(checkUpdateListSurlTwo).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(checkUpdateListSurlTwo).getStatusCode());
    }
    
    @Test
    public final void testCheckUpdateListWrong() throws Exception
    {
        TRequestToken checkUpdateListWrongRequestToken = TRequestToken.getRandom();
        String checkUpdateListWrongSurlOneString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/mycheckUpdateListWrongOne";
        TSURL checkUpdateListWrongSurlOne = buildSurl(checkUpdateListWrongSurlOneString);
        String checkUpdateListWrongSurlTwoString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/mycheckUpdateListWrongTwo";
        TSURL checkUpdateListWrongSurlTwo = buildSurl(checkUpdateListWrongSurlTwoString);
        instance.store(checkUpdateListWrongRequestToken,
                       buildSurlStatusMap(new TSURL[] { checkUpdateListWrongSurlOne, checkUpdateListWrongSurlTwo },
                                          new TStatusCode[] { status, status }));
        instance.checkIntegrity();
        instance.checkAndUpdate(checkUpdateListWrongRequestToken, Arrays.asList(new TSURL[]{checkUpdateListWrongSurlOne,checkUpdateListWrongSurlTwo}), TStatusCode.SRM_AUTHORIZATION_FAILURE, TStatusCode.SRM_REQUEST_INPROGRESS);
        Map<TSURL, TReturnStatus> surlStatusMap = instance.getSurlsStatus(checkUpdateListWrongRequestToken, Arrays.asList(new TSURL[]{checkUpdateListWrongSurlOne, checkUpdateListWrongSurlTwo}));
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(checkUpdateListWrongSurlOne));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(checkUpdateListWrongSurlOne).getStatusCode());
        assertEquals("Status code for the stored token should be " + status, status, surlStatusMap.get(checkUpdateListWrongSurlOne).getStatusCode());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(checkUpdateListWrongSurlTwo));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(checkUpdateListWrongSurlTwo).getStatusCode());
        assertEquals("Status code for the stored token should be " + status, status, surlStatusMap.get(checkUpdateListWrongSurlTwo).getStatusCode());
    }
    
    @Test
    public final void testCheckUpdateListWithExplanation() throws Exception
    {
        TRequestToken checkUpdateListExplanationRequestToken = TRequestToken.getRandom();
        String checkUpdateListExplanationSurlOneString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/mycheckUpdateListExplanationOne";
        TSURL checkUpdateListExplanationSurlOne = buildSurl(checkUpdateListExplanationSurlOneString);
        String checkUpdateListExplanationSurlTwoString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/mycheckUpdateListExplanationTwo";
        TSURL checkUpdateListExplanationSurlTwo = buildSurl(checkUpdateListExplanationSurlTwoString);
        instance.store(checkUpdateListExplanationRequestToken,
                       buildSurlStatusMap(new TSURL[] { checkUpdateListExplanationSurlOne, checkUpdateListExplanationSurlTwo },
                                          new TStatusCode[] { status, status }));
        instance.checkIntegrity();
        TStatusCode updatedStatus = TStatusCode.SRM_ABORTED;
        String updatedExpaination = "life is so";
        instance.checkAndUpdate(checkUpdateListExplanationRequestToken, Arrays.asList(new TSURL[]{checkUpdateListExplanationSurlOne,checkUpdateListExplanationSurlTwo}), status, updatedStatus,updatedExpaination);
        Map<TSURL, TReturnStatus> surlStatusMap = instance.getSurlsStatus(checkUpdateListExplanationRequestToken, Arrays.asList(new TSURL[]{checkUpdateListExplanationSurlOne, checkUpdateListExplanationSurlTwo}));
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(checkUpdateListExplanationSurlOne));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(checkUpdateListExplanationSurlOne).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(checkUpdateListExplanationSurlOne).getStatusCode());
        assertEquals("explanation for the stored token should be \'" + updatedExpaination + "\'", updatedExpaination, surlStatusMap.get(checkUpdateListExplanationSurlOne).getExplanation());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(checkUpdateListExplanationSurlTwo));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(checkUpdateListExplanationSurlTwo).getStatusCode());
        assertEquals("Status code for the stored token should be " + updatedStatus, updatedStatus, surlStatusMap.get(checkUpdateListExplanationSurlTwo).getStatusCode());
        assertEquals("explanation for the stored token should be \'" + updatedExpaination + "\'", updatedExpaination, surlStatusMap.get(checkUpdateListExplanationSurlTwo).getExplanation());
    }
    
    @Test
    public final void testCheckUpdateListWithExplanationWrong() throws Exception
    {
        TRequestToken checkUpdateListExplanationWrongRequestToken = TRequestToken.getRandom();
        String checkUpdateListExplanationWrongSurlOneString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/mycheckUpdateListExplanationWrongOne";
        TSURL checkUpdateListExplanationWrongSurlOne = buildSurl(checkUpdateListExplanationWrongSurlOneString);
        String checkUpdateListExplanationWrongSurlTwoString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/mycheckUpdateListExplanationWrongTwo";
        TSURL checkUpdateListExplanationWrongSurlTwo = buildSurl(checkUpdateListExplanationWrongSurlTwoString);
        instance.store(checkUpdateListExplanationWrongRequestToken,
                       buildSurlStatusMap(new TSURL[] { checkUpdateListExplanationWrongSurlOne, checkUpdateListExplanationWrongSurlTwo },
                                          new TStatusCode[] { status, status }));
        instance.checkIntegrity();
        instance.checkAndUpdate(checkUpdateListExplanationWrongRequestToken, Arrays.asList(new TSURL[]{checkUpdateListExplanationWrongSurlOne,checkUpdateListExplanationWrongSurlTwo}), TStatusCode.SRM_DONE, TStatusCode.SRM_FATAL_INTERNAL_ERROR,"useless");
        Map<TSURL, TReturnStatus> surlStatusMap = instance.getSurlsStatus(checkUpdateListExplanationWrongRequestToken, Arrays.asList(new TSURL[]{checkUpdateListExplanationWrongSurlOne, checkUpdateListExplanationWrongSurlTwo}));
        assertNotNull("Surl status map should not be null", surlStatusMap);
        assertFalse("Surl status map should not be empty", surlStatusMap.isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(checkUpdateListExplanationWrongSurlOne));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(checkUpdateListExplanationWrongSurlOne).getStatusCode());
        assertEquals("Status code for the stored token should be " + status, status, surlStatusMap.get(checkUpdateListExplanationWrongSurlOne).getStatusCode());
        assertTrue("explanation for the stored token should be empty. got " + surlStatusMap.get(checkUpdateListExplanationWrongSurlOne).getExplanation(), surlStatusMap.get(checkUpdateListExplanationWrongSurlOne).getExplanation().trim().isEmpty());
        assertNotNull("Status for the stored surl should be available", surlStatusMap.get(checkUpdateListExplanationWrongSurlTwo));
        assertNotNull("Status code for the stored token should not be null", surlStatusMap.get(checkUpdateListExplanationWrongSurlTwo).getStatusCode());
        assertEquals("Status code for the stored token should be " + status, status, surlStatusMap.get(checkUpdateListExplanationWrongSurlTwo).getStatusCode());
        assertTrue("explanation for the stored token should be empty. got " + surlStatusMap.get(checkUpdateListExplanationWrongSurlTwo).getExplanation(), surlStatusMap.get(checkUpdateListExplanationWrongSurlTwo).getExplanation().trim().isEmpty());
    }
    
    @Test
    public final void testStoreTokenStored() throws Exception
    {
        thrown.expect(IllegalArgumentException.class);
        instance.store(requestToken, buildSurlStatusMap(storedSurl, status));
    }
    
    @Test
    public final void testStoreSurlStored()
    {
        instance.store(TRequestToken.getRandom(), buildSurlStatusMap(storedSurl, TStatusCode.SRM_REQUEST_QUEUED));
    }
    
    @Test
    public final void testGetSurlStatusesSurlStoredIncompatibleStatus() throws IllegalArgumentException, UnknownSurlException
    {
        String incompatibleStatusSurlString = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/myIncompatibleStatus";
        TSURL incompatibleStatusSurl = buildSurl(incompatibleStatusSurlString);
        instance.store(TRequestToken.getRandom(), buildSurlStatusMap(incompatibleStatusSurl, TStatusCode.SRM_REQUEST_INPROGRESS));
        instance.store(TRequestToken.getRandom(), buildSurlStatusMap(incompatibleStatusSurl, TStatusCode.SRM_SPACE_AVAILABLE));
        Collection<TReturnStatus> statuses = instance.getSurlStatuses(incompatibleStatusSurl);
        assertNotNull("Statuses should not be null", statuses);
        for(TReturnStatus status : statuses)
        {
            assertTrue("Returned statuses should matche the provided ones : "
                    + TStatusCode.SRM_REQUEST_INPROGRESS + " or " + TStatusCode.SRM_SPACE_AVAILABLE
                    + " , got " + status, TStatusCode.SRM_REQUEST_INPROGRESS.equals(status.getStatusCode())
                    || TStatusCode.SRM_SPACE_AVAILABLE.equals(status.getStatusCode()));
        }
    }
    
    @Test
    public final void testGetSurlPerTokenStatuses() throws Exception
    {
        Map<TRequestToken, TReturnStatus> surlStatuses = instance.getSurlPerTokenStatuses(storedSurl);
        instance.checkIntegrity();
        assertNotNull("Surl status should not be null", surlStatuses);
        assertFalse("Surl status should not be empty", surlStatuses.isEmpty());
        assertNotNull("Status for the stored token should be available", surlStatuses.get(requestToken));
        assertNotNull("Status code for the stored token should not be null", surlStatuses.get(requestToken).getStatusCode());
        assertEquals("Status code for the stored token should be " + status, status, surlStatuses.get(requestToken).getStatusCode());
    }

    @Test
    public final void testGetExpiredTokens() throws Exception
    {
        TRequestToken expiredToken = buildExpiredToken();
        assertTrue("expiredToken should have an ExpirationDate", expiredToken.hasExpirationDate());
        assertTrue("expiredToken should be expired", expiredToken.isExpired());
        instance.store(expiredToken, buildSurlStatusMap(storedSurl, status));
        instance.checkIntegrity();
        TRequestToken notExpiredToken = TRequestToken.getRandom();
        instance.store(notExpiredToken, buildSurlStatusMap(storedSurl, status));
        instance.checkIntegrity();
        List<TRequestToken> expiredTokens = instance.getExpiredTokens();
        instance.checkIntegrity();
        assertNotNull("expiredTokens should not be null", expiredTokens);
        assertFalse("expiredTokens should not be empty", expiredTokens.isEmpty());
        assertTrue("expiredTokens should contain the stored token", expiredTokens.contains(expiredToken));
        assertFalse("expiredTokens should contain the notExpiredToken", expiredTokens.contains(notExpiredToken));
    }
    
    @Test
    public final void testGetSurlPerTokenStatusesExpiring() throws Exception
    {
        TRequestToken expiredToken = buildExpiredToken();
        assertTrue("expiredToken should have an ExpirationDate", expiredToken.hasExpirationDate());
        String expiredSurlStringOne = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/myexpiredSurlOne";
        String expiredSurlStringTwo = "srm://omii005-vm03.cnaf.infn.it:8444/testers.eu-emi.eu/myexpiredSurlTwo";
        TSURL expiredSurlOne = buildSurl(expiredSurlStringOne);
        TSURL expiredSurlTwo = buildSurl(expiredSurlStringTwo);
        instance.store(expiredToken, buildSurlStatusMap(new TSURL[] {expiredSurlOne, expiredSurlTwo}, new TStatusCode[] {status,status}));
        instance.checkIntegrity();
        assertTrue("expiredToken should be expired", expiredToken.isExpired());
        thrown.expect(UnknownSurlException.class);
        instance.getSurlPerTokenStatuses(expiredSurlOne);
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
    
    
    private static HashMap<TSURL, TReturnStatus> buildSurlStatusMap(TSURL[] surls, TStatusCode[] codes)
            throws IllegalArgumentException
    {
        if (surls == null || codes == null || surls.length != codes.length)
        {
            throw new IllegalArgumentException("Wronte parameters: surl=" + surls + " code=" + codes);
        }
        HashMap<TSURL, TReturnStatus> surlStatusMap = new HashMap<TSURL, TReturnStatus>(1);
        for (int i = 0; i < surls.length; i++)
        {
            surlStatusMap.put(surls[i], buildStatus(codes[i], ""));
        }
        return surlStatusMap;
    }
    
    private static HashMap<TSURL, TReturnStatus> buildSurlStatusMap(TSURL surl, TStatusCode code)
    {
        return buildSurlStatusMap(new TSURL[] { surl }, new TStatusCode[] { code });
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
        } catch(InvalidTReturnStatusAttributeException e)
        {
            fail("Unable to build the TReturnStatus: " + e);
            throw new IllegalStateException("Unexpected InvalidTReturnStatusAttributeException "
                    + "in building TReturnStatus: " + e.getMessage());
        }
    }
    
    private static TSURL buildSurl(String surlstring)
    {
        try
        {
            return TSURL.makeFromStringWellFormed(surlstring);
        } catch(InvalidTSURLAttributesException e)
        {
           fail("Unable to build the TSURL: " + e);
           throw new IllegalStateException("Unexpected InvalidTSURLAttributesException: " + e);
        }
    }
}
