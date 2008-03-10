/**
 * This class represents the Ping Output Data
 * @author  Alberto Forti
 * @author  CNAF-INFN Bologna
 * @date    Feb 2007
 * @version 1.0
 */

package it.grid.storm.synchcall.discovery;

import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.TExtraInfo;

public class PingOutputData
{
    private String versionInfo = null;
    private ArrayOfTExtraInfo extraInfoArray = null;
    
    public PingOutputData() {}
    
    public PingOutputData(String versionInfo, ArrayOfTExtraInfo otherInfo) {
        this.versionInfo = versionInfo;
        this.extraInfoArray = otherInfo;
    }
    
    /**
     * Set versionInfo.
     * @param versionInfo String
     */
    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }
    
    /**
     * Get versionInfo.
     * @return String
     */
    public String getVersionInfo() {
        return this.versionInfo;
    }
    
    /**
     * Set extraInfoArray.
     * @param extraInfoArray TExtraInfo
     */
    public void setExtraInfoArray(ArrayOfTExtraInfo otherInfo) {
        this.extraInfoArray = otherInfo;
    }
    
    /**
     * Get extraInfoArray.
     * @return TExtraInfo
     */
    public ArrayOfTExtraInfo getExtraInfoArray() {
        return this.extraInfoArray;
    }
}
