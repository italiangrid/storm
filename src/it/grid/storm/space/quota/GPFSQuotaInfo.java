/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.space.quota;


import it.grid.storm.namespace.model.Quota;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 *
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 *
 *
**/

public class GPFSQuotaInfo {

    public GPFSQuotaInfo() {
        initializated = false;
        failure = false;
    }


    /**
     * @return the failure
     */
    public final boolean isFailure() {
        return failure;
    }


    /**
     * @param failure the failure to set
     */
    public final void setFailure(boolean failure) {
        this.failure = failure;
    }


    private static final Logger log = LoggerFactory.getLogger(GPFSQuotaInfo.class);
    
    private boolean initializated = false;
    private boolean failure = false;
    
    /**
     * 
     * @author ritz
     *
     */
    public enum QuotaType {
        USR(0, "USR"), GRP(1, "GRP"), FILESET(2, "FILESET"), UNDEFINED(-1, "UNDEFINED");

        private int code;
        private String printOut;
        
        QuotaType(int c, String printOut) {
            this.code = c;
            this.printOut = printOut; 
        }

        public int getCode() {
            return code;
        }
        
        public String getPrintOut() {
            return printOut;
        }

        static QuotaType getQuotaType(int code) {
        	QuotaType result = QuotaType.UNDEFINED;
            for (QuotaType qt : QuotaType.values()) {
                if (qt.getCode() == code) {
                    result = qt;
                    break;
                }
            }
            return result;
        }
        
        static QuotaType getQuotaType(String printOut) {
            QuotaType result = QuotaType.UNDEFINED;
            for (QuotaType qt : QuotaType.values()) {
                if (qt.getPrintOut().equals(printOut) ) {
                    result = qt;
                    break;
                }
            }
            return result;
        }
        
    }
    
    public enum EntryType {
        DEFAULT_ON(0, "default on"), 
        DEFAULT_OFF(1, "default off"), 
        EXPLICIT_QUOTA(2, "e"), 
        DEFAULT_QUOTA(3, "d"), 
        INITIAL_QUOTA(4, "i"),  
        UNDEFINED(-1, "?");

        private int code;
        private String printOut;

        EntryType(int c, String printOut) {
            this.code = c;
            this.printOut = printOut;
        }

        public int getCode() {
            return code;
        }
        
        public String getPrintOut() {
            return printOut;
        }
                

        static EntryType getEntryType(String printOut) {
        	EntryType result = EntryType.UNDEFINED;
            for (EntryType et : EntryType.values()) {
                if (et.getPrintOut().equals(printOut) ) {
                    result = et;
                    break;
                }
            }
            return result;
        }
    }
    
    /**
     * 
0 <DeviceName> devicename 
1 <Name> root
2 <QuotaType> USR
3 <CurrentUsage> 15617856
4 <SoftLimit> 0
5 <HardLimit> 0
6 <SpaceInDoubt> 42976
7 <GracePeriod> none
8 <sep> |
9 <CurrentNumbFiles> 56
0 <SoftLimit> 0
1 <HardLimit> 0
2 <FilesInDoubt> 34
3 <GracePeriod> none
4 <EntryType>  i
     */
    
    private String deviceName;
    
    private String quotaEntryName;
    
    private QuotaType quotaType;
   
    private long currentBlocksUsage = -1L;
    
    private long softBlocksLimit = -1L;
    
    private long hardBlocksLimit = -1L;
    
    private long spaceInDoubt = -1L;
    
    private String blockGracePeriod;
    
    private long currentNumberOfFiles = -1L;
    
    private long softFilesLimit = -1L;
    
    private long hardFilesLimit = -1L;
    
    private long filesInDoubt = -1L;
    
    private String fileGracePeriod;
    
    private EntryType entryType;

    /**
	 * @return the deviceName
	 */
	public final String getDeviceName() {
		return deviceName;
	}


	/**
	 * @return the quotaEntryName
	 */
	public final String getQuotaEntryName() {
		return quotaEntryName;
	}



	/**
	 * @return the quotaType
	 */
	public final QuotaType getQuotaType() {
		return quotaType;
	}


	/**
	 * @return the currentBlocksUsage
	 */
	public final long getCurrentBlocksUsage() {
		return currentBlocksUsage;
	}



	/**
	 * @return the softBlocksLimit
	 */
	public final long getSoftBlocksLimit() {
		return softBlocksLimit;
	}



	/**
	 * @return the hardBlocksLimit
	 */
	public final long getHardBlocksLimit() {
		return hardBlocksLimit;
	}


	/**
	 * @return the spaceInDoubt
	 */
	public final long getSpaceInDoubt() {
		return spaceInDoubt;
	}


	/**
	 * @return the blockGracePeriod
	 */
	public final String getBlockGracePeriod() {
		return blockGracePeriod;
	}


	/**
	 * @return the currentNumebrOfFiles
	 */
	public final long getCurrentNumebrOfFiles() {
		return currentNumberOfFiles;
	}


	/**
	 * @return the softFilesLimit
	 */
	public final long getSoftFilesLimit() {
		return softFilesLimit;
	}


	/**
	 * @return the hardFilesLimit
	 */
	public final long getHardFilesLimit() {
		return hardFilesLimit;
	}


	/**
	 * @return the filesInDoubt
	 */
	public final long getFilesInDoubt() {
		return filesInDoubt;
	}



	/**
	 * @return the fileGracePeriod
	 */
	public final String getFileGracePeriod() {
		return fileGracePeriod;
	}



	/**
	 * @return the entryType
	 */
	public final EntryType getEntryType() {
		return entryType;
	}


    public static List<String> splitTokens(String outputLine) {
        ArrayList<String> outputList = new ArrayList<String>();
        if (outputLine!=null) {
            StringTokenizer st = new StringTokenizer(outputLine);
            while (st.hasMoreTokens()) {
                String element = st.nextToken();
                outputList.add(element);
                log.trace(element);
            }    
        }
        return outputList;
    }
	
	
	public static final boolean meaningfullLine(String outputLine) {
	    boolean result = true;
	    List<String> tokens = splitTokens(outputLine);
	    if (tokens.size()!=15) {
	        result = false;
	    } else {
	        if (tokens.get(0) != null) {
	            if (tokens.get(0).equals("Name")) {
	                result = false;
	            }
	        }
	    }
	    return result;
	}
	
	/**
     * Used to build QuotaInfo from a String corresponding to
     * 
     * 
        <DeviceName> devicename 
        <Name> root
        <QuotaType> USR
        <CurrentUsage> 15617856
        <SoftLimit> 0
        <HardLimit> 0
        <SpaceInDoubt> 42976
        <GracePeriod> none
        <sep> |
        <CurrentNumbFiles> 56
        <SoftLimit> 0
        <HardLimit> 0
        <FilesInDoubt> 34
        <GracePeriod> none
        <EntryType>  i
     * 
     * @param output String
     * @return QuotaInfoInterface
     */
    

    public boolean isInitializated() {
        return initializated;
    }
    
    
    
    public void parseMMLSQuotaCommandOutput(String line, Quota quotaElement) {
        initializated = true;
        
        List<String> outputList = splitTokens(line);

       // Filesystem type             KB      quota      limit   in_doubt    grace |    files   quota    limit in_doubt    grace  Remarks
        
        // ### DeviceName
        deviceName = outputList.get(0);
       
        quotaEntryName = quotaElement.getQuotaElementName();
        log.debug("QuotaEntryName : "+quotaEntryName);
        
        // ### QuotaType
        quotaType = QuotaType.getQuotaType(outputList.get(1));

        // ### EntryType
        try {
            currentBlocksUsage = Long.parseLong(outputList.get(2));
        }
        catch (NumberFormatException nfe) {
            log.warn("Unable to parse Long '" + outputList.get(2) + "'");
            currentBlocksUsage = 0;
        }

        // ### EntryType
        try {
            softBlocksLimit = Long.parseLong(outputList.get(3));
        }
        catch (NumberFormatException nfe) {
            log.warn("Unable to parse Long '" + outputList.get(3) + "'");
            softBlocksLimit = 0;
        }
        // ### EntryType
        try {
            hardBlocksLimit = Long.parseLong(outputList.get(4));
        }
        catch (NumberFormatException nfe) {
            log.warn("Unable to parse Long '" + outputList.get(4) + "'");
            hardBlocksLimit = 0;
        }

        // ### EntryType
        try {
            spaceInDoubt = Long.parseLong(outputList.get(5));
        }
        catch (NumberFormatException nfe) {
            log.warn("Unable to parse Long '" + outputList.get(5) + "'");
            spaceInDoubt = 0;
        }
        // ### EntryType
        blockGracePeriod = outputList.get(6);

        // ### EntryType
        try {
            currentNumberOfFiles = Long.parseLong(outputList.get(8));
        }
        catch (NumberFormatException nfe) {
            log.warn("Unable to parse Long '" + outputList.get(8) + "'");
            currentNumberOfFiles = 0;
        }

        // ### EntryType
        try {
            softFilesLimit = Long.parseLong(outputList.get(9));
        }
        catch (NumberFormatException nfe) {
            log.warn("Unable to parse Long '" + outputList.get(9) + "'");
            softFilesLimit = 0;
        }
        // ### EntryType
        try {
            hardFilesLimit = Long.parseLong(outputList.get(10));
        }
        catch (NumberFormatException nfe) {
            log.warn("Unable to parse Long '" + outputList.get(10) + "'");
            hardFilesLimit = 0;
        }
        // ### EntryType
        try {
            filesInDoubt = Long.parseLong(outputList.get(11));
        }
        catch (NumberFormatException nfe) {
            log.warn("Unable to parse Long '" + outputList.get(11) + "'");
            filesInDoubt = 0;
        }

        // ### EntryType
        fileGracePeriod = outputList.get(12);

        // ### EntryType
        entryType = EntryType.UNDEFINED;        
    }
    
    /**

-- OK --
[]# /usr/lpp/mmfs/bin/mmlsquota -j data1 gemss_test
                         Block Limits                                    |     File Limits
Filesystem type             KB      quota      limit   in_doubt    grace |    files   quota    limit in_doubt    grace  Remarks
gemss_test FILESET         512 2147483648 2147483648          0     none |     3128       0        0        0     none 
[]# 

-- ERR1 (wrong device) -- 
[root@vm-storage-03 backend-server]# /usr/lpp/mmfs/bin/mmlsquota -j data1 gemss_test3
mmlsquota: File system gemss_test3 is not known to the GPFS cluster.
mmlsquota: Command failed.  Examine previous error messages to determine cause.
[root@vm-storage-03 backend-server]#

-- ERR2 (wrong fileset) --
[root@vm-storage-03 backend-server]# /usr/lpp/mmfs/bin/mmlsquota -j dat991 gemss_test
dat991: no such fileset in quota enabled file systems
mmlsquota: Command failed.  Examine previous error messages to determine cause.
[root@vm-storage-03 backend-server]#

*/


    public static boolean meaningfullLineForLS(String line) {
        boolean result = true;
        List<String> tokens = splitTokens(line);
        int size = tokens.size();
        if (size!=13 && size!=14) {
            result = false;
        } else {
            if (tokens.get(0) != null) {
                if (tokens.get(0).equals("Filesystem")) {
                    result = false;
                }
            }
        }
        return result;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GPFSQuotaInfo [deviceName=");
        builder.append(deviceName);
        builder.append(", quotaEntryName=");
        builder.append(quotaEntryName);
        builder.append(", quotaType=");
        builder.append(quotaType);
        builder.append(", currentBlocksUsage=");
        builder.append(currentBlocksUsage);
        builder.append(", softBlocksLimit=");
        builder.append(softBlocksLimit);
        builder.append(", hardBlocksLimit=");
        builder.append(hardBlocksLimit);
        builder.append(", spaceInDoubt=");
        builder.append(spaceInDoubt);
        builder.append(", blockGracePeriod=");
        builder.append(blockGracePeriod);
        builder.append(", currentNumberOfFiles=");
        builder.append(currentNumberOfFiles);
        builder.append(", softFilesLimit=");
        builder.append(softFilesLimit);
        builder.append(", hardFilesLimit=");
        builder.append(hardFilesLimit);
        builder.append(", filesInDoubt=");
        builder.append(filesInDoubt);
        builder.append(", fileGracePeriod=");
        builder.append(fileGracePeriod);
        builder.append(", entryType=");
        builder.append(entryType);
        builder.append("]");
        return builder.toString();
    }




}
