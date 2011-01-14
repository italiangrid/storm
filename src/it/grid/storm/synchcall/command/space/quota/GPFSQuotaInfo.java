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

package it.grid.storm.synchcall.command.space.quota;

import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.namespace.model.QuotaType;

import java.util.ArrayList;
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
 * typedef struct gpfs_quotaInfo
 * {
 *   gpfs_off64_t blockUsage;      /* current block count *
 *   gpfs_off64_t blockHardLimit;  * absolute limit on disk blks alloc *
 *   gpfs_off64_t blockSoftLimit;  /* preferred limit on disk blks *
 *   gpfs_off64_t blockInDoubt;    /* distributed shares + "lost" usage for blks *
 *   int          inodeUsage;      /* current # allocated inodes *
 *   int          inodeHardLimit;  /* absolute limit on allocated inodes *
 *   int          inodeSoftLimit;  /* preferred inode limit *
 *   int          inodeInDoubt;    /* distributed shares + "lost" usage for inodes *
 *   gpfs_uid_t   quoId;           /* uid, gid or fileset id
 *   int          entryType;       /* entry type, not used *
 *   unsigned int blockGraceTime;  /* time limit for excessive disk use *
 *   unsigned int inodeGraceTime;  /* time limit for excessive inode use *
 * } gpfs_quotaInfo_t;
 *
 *                          Block Limits                                    |     File Limits
Filesystem type             KB      quota      limit   in_doubt    grace |    files   quota    limit in_doubt    grace  Remarks
gpfs_storm FILESET  110010268672 126953000960 126953125888     492384     none |  1796915       0        0      197     none

 blockUsage
     Current block count in 1 KB units.
 blockHardLimit
     Absolute limit on disk block allocation.
 blockSoftLimit
     Preferred limit on disk block allocation.
 blockInDoubt
     Distributed shares and block usage that have not been not accounted for.
 inodeUsage
     Current number of allocated inodes.
 inodeHardLimit
     Absolute limit on allocated inodes.
 inodeSoftLimit
     Preferred inode limit.
 inodeInDoubt
     Distributed inode share and inode usage that have not been accounted for.
 quoId
     user ID, group ID, or fileset ID.
 entryType
     Not used
 blockGraceTime
     Time limit (in seconds since the Epoch) for excessive disk use.
 inodeGraceTime
    Time limit (in seconds since the Epoch) for excessive inode use.
 *
 **/

public class GPFSQuotaInfo extends QuotaInfoAbstract {

    private static final Logger log = LoggerFactory.getLogger(GPFSQuotaInfo.class);

    private long blockInDoubt = -1L;
    private long iNodeInDoubt = -1L;
    private String remarks = null;

    private int filesystemTokenIndex = 0;
    private int quotaTypeTokenIndex = 1;
    private int sizeUsedTokenIndex = 2;
    private int sizeHardLimitTokenIndex = 4;
    private int sizeSoftLimitTokenIndex = 3;
    private int sizeInDoubtTokenIndex = 5;
    private int sizeGracePeriodTokenIndex = 6;
    private int separator = 7;
    private int iNodeUsedTokenIndex = 8;
    private int iNodeHardLimitTokenIndex = 10;
    private int iNodeSoftLimitTokenIndex = 9;
    private int iNodeInDoubtTokenIndex = 11;
    private int iNodeGracePeriodTokenIndex = 12;
    private int remarkTokenIndex = 13;



    public GPFSQuotaInfo() {
        super();
    }

    /**
     * Used to build QuotaInfo from a String corresponding to
     * Filesystem, type, KB, quota, limit, in_doubt, grace | files, quota, limit, in_doubt, grace, Remarks
     *
     * @param output String
     * @return QuotaInfoInterface
     */
    public void build(String output) {
        StringTokenizer st = new StringTokenizer(output);
        ArrayList<String> outputList = new ArrayList<String>();
        int cont = 0;
        while (st.hasMoreTokens()) {
            String element = st.nextToken();
            outputList.add(element);
            log.debug(element);
            //System.out.println("ELEMENT ("+cont+"): "+element);
            cont++;
        }
        //Retrieve and Set Filesystem
        String fileSystemName = outputList.get(filesystemTokenIndex);
        this.setFilesystemName(fileSystemName);
        //Retrieve and Set QuotaType
        this.setQuotaType(QuotaType.getQuotaType(outputList.get(quotaTypeTokenIndex)));
        //Retrieve and Set Size Used
        this.setBlockUsage(Long.parseLong(outputList.get(sizeUsedTokenIndex)));
        //Retrieve and Set Size Hard Limit
        this.setBlockHardLimit(Long.parseLong(outputList.get(sizeHardLimitTokenIndex)));
        //Retrieve and Set Size Soft Limit
        this.setBlockSoftLimit(Long.parseLong(outputList.get(sizeSoftLimitTokenIndex)));
        //Retrieve and Set Size In Doubt
        this.setBlockInDoubt(Long.parseLong(outputList.get(sizeInDoubtTokenIndex)));
        //Retrieve and Set Size Grace Time
        this.setBlockGraceTime(outputList.get(sizeGracePeriodTokenIndex));
        //Retrieve and Set INode used
        this.setINodeUsage(Long.parseLong(outputList.get(iNodeUsedTokenIndex)));
        //Retrieve and Set INode Hard Limit
        this.setINodeHardLimit(Long.parseLong(outputList.get(iNodeHardLimitTokenIndex)));
        //Retrieve and Set INode Soft Limit
        this.setINodeSoftLimit(Long.parseLong(outputList.get(iNodeSoftLimitTokenIndex)));
        //Retrieve and Set INode In Doubt
        this.setINodeInDoubt(Long.parseLong(outputList.get(iNodeInDoubtTokenIndex)));
        //Retrieve and Set INode Grace Time
        this.setINodeGraceTime(outputList.get(iNodeGracePeriodTokenIndex));
        //Retrieve and Set Remarks
        this.setRemarks("N/A");

    }


    /**
     * getRemarks
     *
     * @return String
     */
    public String getRemarks() {
        return this.remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    /**
     * getINodeInDoubt
     *
     * @return String
     */
    public long getINodeInDoubt() {
        return this.iNodeInDoubt;
    }

    public void setINodeInDoubt(long iNodeInDoubt) {
        this.iNodeInDoubt = iNodeInDoubt;
    }

    /**
     * getBlockInDoubt
     *
     * @return String
     */
    public long getBlockInDoubt() {
        return this.blockInDoubt;
    }

    public void setBlockInDoubt(long blockInDoubt) {
        this.blockInDoubt = blockInDoubt;
    }


    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("QUOTA [ Filesystem: '"+getFilesystemName()+"' \n");
        result.append("  Size used: '"+getBlockUsage()+" "+getSizeUnit()+"' \n");
        result.append("  Size Hard Limit: '"+getBlockHardLimit()+" "+getSizeUnit()+"' \n");
        result.append("  Size Soft Limit: '"+getBlockSoftLimit()+" "+getSizeUnit()+"' \n");
        result.append("  Size In Doubt: '"+getBlockInDoubt()+" "+getSizeUnit()+"' \n");
        if (getSizeTimeUnit().equals(TimeUnit.EMPTY)) {
            result.append("  Size Grace Time: '"+getSizeTimeUnit()+"' \n");
        } else {
            result.append("  Size Grace Time: '"+getBlockGraceTime()+" "+getSizeTimeUnit()+"' \n");
        }
        result.append("  iNode used: '"+getINodeUsage()+"' \n");
        result.append("  iNode Hard Limit: '"+getINodeHardLimit()+"' \n");
        result.append("  iNode Soft Limit: '"+getINodeSoftLimit()+"' \n");
        result.append("  iNode In Doubt: '"+getINodeInDoubt()+"' \n");
        if (getINodeTimeUnit().equals(TimeUnit.EMPTY)) {
            result.append("  iNode Grace Time: '"+getINodeTimeUnit()+"' \n");
        } else {
            result.append("  iNode Grace Time: '"+getINodeGraceTime()+" "+getINodeTimeUnit()+"' \n");
        }

        result.append("  Remarks : '"+getRemarks()+"' \n");
        result.append("]");
        return result.toString();
    }



}
