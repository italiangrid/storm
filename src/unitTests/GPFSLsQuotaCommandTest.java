/**
 * 
 */
package unitTests;


import static org.junit.Assert.fail;
import it.grid.storm.namespace.model.Quota;
import it.grid.storm.namespace.model.QuotaType;
import it.grid.storm.space.quota.GPFSLsQuotaCommand;
import it.grid.storm.space.quota.GPFSQuotaInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Michele Dibenedetto
 */
public class GPFSLsQuotaCommandTest
{
    private static final String eol = System.getProperty("line.separator");
    
    private static final String mmlsquotaOriginalOutputLine1 = "                         Block Limits                                    |     File Limits";

    private static final String mmlsquotaOriginalOutputLine2 = "Filesystem type             KB      quota      limit   in_doubt    grace |    files   quota    limit in_doubt    grace  Remarks";
    
    private static final String mmlsquotaOriginalOutputLine3 = "storage_2  FILESET  203872043440 204010946560 204010946560   39468360     none |   279103       0        0     2490     none glite-condor.mi.infn.it";
    
    private static final String mmlsquotaOriginalOutput = mmlsquotaOriginalOutputLine1 + eol + mmlsquotaOriginalOutputLine2 + eol + mmlsquotaOriginalOutputLine3;

    private static final String mmlsquotaOutputLine1 = "Block       Limits   |             File          Limits";

    private static final String mmlsquotaOutputLine2 = "Filesystem  type     KB            quota         limit         in_doubt  grace  |  files   quota  limit  in_doubt  grace  Remarks";
    
    private static final String mmlsquotaOutputLine3 = "storage_2   FILESET  203872043440  204010946560  204010946560  39463248  none   |  279103  0      0      2470      none";
    
    private static final String mmlsquotaOutput = mmlsquotaOutputLine1 + eol + mmlsquotaOutputLine2 + eol + mmlsquotaOutputLine3;

    private GPFSLsQuotaCommand quotaCommand;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        quotaCommand = new GPFSLsQuotaCommand();
    }

    @Test
    public final void testManageSuccess()
    {
        Method method = null;
        try
        {
            method = GPFSLsQuotaCommand.class.getDeclaredMethod("manageSuccess", new Class[] { String.class,
                    Quota.class });
        } catch(SecurityException e)
        {
            fail("getDeclaredMethod failed. SecurityException: " + e.getMessage());
        } catch(NoSuchMethodException e)
        {
            fail("getDeclaredMethod failed. NoSuchMethodException: " + e.getMessage());
        }
        method.setAccessible(true);
        GPFSQuotaInfo qInfo = null;
        try
        {
            qInfo = (GPFSQuotaInfo) method.invoke(quotaCommand, mmlsquotaOriginalOutput, new Quota(true, "banane", QuotaType.buildQuotaType(QuotaType.FILESET)));
        } catch(IllegalArgumentException e)
        {
            fail("invoke failed. IllegalArgumentException: " + e.getMessage());
        } catch(IllegalAccessException e)
        {
            fail("invoke failed. IllegalAccessException: " + e.getMessage());
        } catch(InvocationTargetException e)
        {
            fail("invoke failed. InvocationTargetException: " + e.getMessage());
        }
        assertFalse("mmlsquotaOriginalOutput should not fail!" , qInfo.isFailure());
        try
        {
            qInfo = (GPFSQuotaInfo) method.invoke(quotaCommand, mmlsquotaOutput, new Quota(true, "banane", QuotaType.buildQuotaType(QuotaType.FILESET)));
        } catch(IllegalArgumentException e)
        {
            fail("invoke failed. IllegalArgumentException: " + e.getMessage());
        } catch(IllegalAccessException e)
        {
            fail("invoke failed. IllegalAccessException: " + e.getMessage());
        } catch(InvocationTargetException e)
        {
            fail("invoke failed. InvocationTargetException: " + e.getMessage());
        }
        assertFalse("mmlsquotaOutput should not fail!" , qInfo.isFailure());
    }
}
