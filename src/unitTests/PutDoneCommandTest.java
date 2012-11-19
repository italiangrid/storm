package unitTests;

import static org.junit.Assert.*;
import java.util.HashMap;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.datatransfer.PutDoneCommand;
import it.grid.storm.synchcall.data.datatransfer.IdentityPutDoneInputData;
import it.grid.storm.synchcall.data.datatransfer.PutDoneOutputData;
import it.grid.storm.synchcall.surl.SurlStatusStore;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import component.namespace.config.FakeGridUser;

public class PutDoneCommandTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    private PutDoneCommand command = null;
    private IdentityPutDoneInputData data = null;
    
    private GridUserInterface auth = new FakeGridUser("mimmo", "atlas");
    
    private TRequestToken unknownReqToken = TRequestToken.getRandom();
    private TRequestToken reqToken = TRequestToken.getRandom();
    
    private ArrayOfSURLs unknownSurlArray = new ArrayOfSURLs(new TSURL[]{TSURL.makeEmpty()});
    
    private TSURL surlSpaceAvailable = null;
    private String surlString = "srm://banane.it:8443/mele";
    
    private String surlSuccessfullString = "srm://banane.it:8443/pere";
    private TSURL surlSuccessfull = null;
    
    private String surlAbortedString = "srm://banane.it:8443/fichi";
    private TSURL surlAborted = null;
    
    private TStatusCode successCode = TStatusCode.SRM_SUCCESS;
    private TStatusCode spaceAvailableCode = TStatusCode.SRM_SPACE_AVAILABLE;
    private TStatusCode abortedCode = TStatusCode.SRM_ABORTED;
    
    private TReturnStatus successStatus = null;
    private TReturnStatus spaceAvailableStatus = null;
    private TReturnStatus abortedStatus = null;
    
    private HashMap<TSURL, TReturnStatus> surlStatus = new HashMap<TSURL, TReturnStatus>();

    private ArrayOfSURLs mixedSurlArray = null;
    private ArrayOfSURLs surlSpaceAvalableArray = null;
    private ArrayOfSURLs surlSuccessfulArray = null;
    private ArrayOfSURLs abortedSurlArray = null;



    

    @Before
    public void setUp() throws Exception
    {
        command  = new PutDoneCommand();
//        data = new PutDoneInputData();
        
        surlSuccessfull = TSURL.makeFromStringWellFormed(surlSuccessfullString);
        successStatus = new TReturnStatus(successCode);
        surlStatus.put(surlSuccessfull,successStatus);
        
        surlSpaceAvailable = TSURL.makeFromStringWellFormed(surlString);
        spaceAvailableStatus = new TReturnStatus(spaceAvailableCode);
        surlStatus.put(surlSpaceAvailable,spaceAvailableStatus);
        
        surlAborted = TSURL.makeFromStringWellFormed(surlAbortedString);
        abortedStatus = new TReturnStatus(abortedCode);
        surlStatus.put(surlAborted,abortedStatus);
        
        SurlStatusStore.getInstance().store(reqToken, surlStatus);
        
        mixedSurlArray = new ArrayOfSURLs(new TSURL[]{surlSpaceAvailable, surlSuccessfull});
        surlSpaceAvalableArray = new ArrayOfSURLs(new TSURL[]{surlSpaceAvailable});
        surlSuccessfulArray = new ArrayOfSURLs(new TSURL[]{surlSuccessfull});
        abortedSurlArray = new ArrayOfSURLs(new TSURL[]{surlAborted});
    }

    @Test
    public final void testExecuteNullArg()
    {
        PutDoneOutputData output = (PutDoneOutputData) command.execute(data);
        assertNotNull("output should never be null" , output);
        assertFalse("resoult should be failure" , output.getReturnStatus().isSRM_SUCCESS());
        assertEquals("resoult should be failure" , TStatusCode.SRM_INVALID_REQUEST, output.getReturnStatus().getStatusCode());
    }
    
    @Test
    public final void testExecuteEmptySurls()
    {
        data = new IdentityPutDoneInputData(auth,reqToken,new ArrayOfSURLs());
        PutDoneOutputData output = (PutDoneOutputData) command.execute(data);
        assertNotNull("output should never be null" , output);
        assertFalse("resoult should be failure" , output.getReturnStatus().isSRM_SUCCESS());
        assertEquals("resoult should be failure" , TStatusCode.SRM_INVALID_REQUEST, output.getReturnStatus().getStatusCode());
    }
    
    @Test
    public final void testExecuteUnknownTokenAndSurls()
    {
        data = new IdentityPutDoneInputData(auth,unknownReqToken,unknownSurlArray);
        PutDoneOutputData output = (PutDoneOutputData) command.execute(data);
        assertNotNull("output should never be null" , output);
        assertFalse("resoult should be failure" , output.getReturnStatus().isSRM_SUCCESS());
        assertEquals("resoult should be failure" , TStatusCode.SRM_INVALID_REQUEST, output.getReturnStatus().getStatusCode());
    }
    
    @Test
    public final void testExecuteUnknownSurls()
    {
        data = new IdentityPutDoneInputData(auth,reqToken,unknownSurlArray);
        PutDoneOutputData output = (PutDoneOutputData) command.execute(data);
        assertNotNull("output should never be null" , output);
        assertFalse("resoult should be failure" , output.getReturnStatus().isSRM_SUCCESS());
        assertEquals("resoult should be failure" , TStatusCode.SRM_INVALID_REQUEST, output.getReturnStatus().getStatusCode());
    }
    
    @Test
    public final void testExecuteUnknownToken()
    {
        data = new IdentityPutDoneInputData(auth,unknownReqToken,mixedSurlArray);
        PutDoneOutputData output = (PutDoneOutputData) command.execute(data);
        assertNotNull("output should never be null" , output);
        assertFalse("resoult should be failure" , output.getReturnStatus().isSRM_SUCCESS());
        assertEquals("resoult should be failure" , TStatusCode.SRM_INVALID_REQUEST, output.getReturnStatus().getStatusCode());
    }

    @Test
    public final void testExecuteAlreadySuccessSurl()
    {
        data = new IdentityPutDoneInputData(auth,reqToken,surlSuccessfulArray);
        PutDoneOutputData output = (PutDoneOutputData) command.execute(data);
        assertNotNull("output should never be null" , output);
        assertFalse("resoult should be failure" , output.getReturnStatus().isSRM_SUCCESS());
        assertEquals("resoult should be failure" , TStatusCode.SRM_FAILURE, output.getReturnStatus().getStatusCode());
    }
    
    @Test
    public final void testExecute()
    {
        data = new IdentityPutDoneInputData(auth,reqToken,surlSpaceAvalableArray);
        PutDoneOutputData output = (PutDoneOutputData) command.execute(data);
        assertNotNull("output should never be null" , output);
        assertTrue("resoult should be failure" , output.getReturnStatus().isSRM_SUCCESS());
        assertEquals("resoult should be failure" , TStatusCode.SRM_SUCCESS, output.getReturnStatus().getStatusCode());
    }
    
    @Test
    public final void testExecutePartialSuccess()
    {
        data = new IdentityPutDoneInputData(auth,reqToken,mixedSurlArray);
        PutDoneOutputData output = (PutDoneOutputData) command.execute(data);
        assertNotNull("output should never be null" , output);
        assertFalse("resoult should be failure" , output.getReturnStatus().isSRM_SUCCESS());
        assertEquals("resoult should be failure" , TStatusCode.SRM_PARTIAL_SUCCESS, output.getReturnStatus().getStatusCode());
    }
    
    @Test
    public final void testExecuteAborted()
    {
        data = new IdentityPutDoneInputData(auth,reqToken,abortedSurlArray);
        PutDoneOutputData output = (PutDoneOutputData) command.execute(data);
        assertNotNull("output should never be null" , output);
        assertFalse("resoult should be failure" , output.getReturnStatus().isSRM_SUCCESS());
        assertEquals("resoult should be failure" , TStatusCode.SRM_ABORTED, output.getReturnStatus().getStatusCode());
    }

}
