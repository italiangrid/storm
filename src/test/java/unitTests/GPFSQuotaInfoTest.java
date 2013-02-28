/**
 * 
 */
package unitTests;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import it.grid.storm.space.quota.GPFSQuotaInfo;
import org.junit.Test;

/**
 * @author Michele Dibenedetto
 *
 */
public class GPFSQuotaInfoTest
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
    
    
    @Test
    public final void testParameters()
    {
        assertEquals("mmlsquotaOutput should have 3 lines: lines =" + mmlsquotaOutput.split(eol).length, 3, mmlsquotaOutput.split(eol).length);
        assertEquals("mmlsquotaOriginalOutput should have 3 lines: lines =" + mmlsquotaOriginalOutput.split(eol).length , 3, mmlsquotaOriginalOutput.split(eol).length);
    }
    /**
     * Test method for {@link it.grid.storm.space.quota.GPFSQuotaInfo#meaningfullLineForLS(java.lang.String)}.
     */
    @Test
    public final void testMeaningfullLineForLS()
    {
        assertTrue("mmlsquotaOutputLines should be meaningfull. Line : " + mmlsquotaOutputLine3, GPFSQuotaInfo.meaningfullLineForLS(mmlsquotaOutputLine3));
        assertTrue("mmlsquotaOriginalOutputLines should be meaningfull. Line : " + mmlsquotaOriginalOutputLine3, GPFSQuotaInfo.meaningfullLineForLS(mmlsquotaOriginalOutputLine3));
    }

    @Test
    public final void testMeaningfullLineForLSFromArray()
    {

        String[] array = mmlsquotaOutput.split(eol);
        List<String> outputList = parseOutputLines(array);
        int count = 0;
        for (String line : outputList)
        {
            if (GPFSQuotaInfo.meaningfullLineForLS(line))
            {
                count++;
            }
        }
        assertTrue("At least one line must be meaningfull", count == 1);
        array = mmlsquotaOriginalOutput.split(eol);
        outputList = parseOutputLines(array);
        count = 0;
        for (String line : outputList)
        {
            if (GPFSQuotaInfo.meaningfullLineForLS(line))
            {
                count++;
            }
        }
        assertTrue("At least one line must be meaningfull", count == 1);
    }

    private List<String> parseOutputLines(String[] outputArray)
    {
        List<String> result = new ArrayList<String>();
        if (outputArray != null)
        {
            for (int i = 0; i < outputArray.length; i++)
            {
                String line = outputArray[i];
                result.add(line);
            }
        }
        return result;
    }

}
