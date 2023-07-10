/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.common.types.TimeUnit;

/**
 * Title:
 *
 * <p>Description:
 *
 * <p>Copyright: Copyright (c) 2008
 *
 * <p>Company:
 *
 * @author not attributable
 * @version 1.0
 *     <p>typedef struct gpfs_quotaInfo { gpfs_off64_t blockUsage; /* current block count *
 *     gpfs_off64_t blockHardLimit; * absolute limit on disk blks alloc * gpfs_off64_t
 *     blockSoftLimit; /* preferred limit on disk blks * gpfs_off64_t blockInDoubt; /* distributed
 *     shares + "lost" usage for blks * int inodeUsage; /* current # allocated inodes * int
 *     inodeHardLimit; /* absolute limit on allocated inodes * int inodeSoftLimit; /* preferred
 *     inode limit * int inodeInDoubt; /* distributed shares + "lost" usage for inodes * gpfs_uid_t
 *     quoId; /* uid, gid or fileset id int entryType; /* entry type, not used * unsigned int
 *     blockGraceTime; /* time limit for excessive disk use * unsigned int inodeGraceTime; /* time
 *     limit for excessive inode use * } gpfs_quotaInfo_t;
 *     <p>Block Limits | File Limits Filesystem type KB quota limit in_doubt grace | files quota
 *     limit in_doubt grace Remarks gpfs_storm FILESET 110010268672 126953000960 126953125888 492384
 *     none | 1796915 0 0 197 none
 *     <p>blockUsage Current block count in 1 KB units. blockHardLimit Absolute limit on disk block
 *     allocation. blockSoftLimit Preferred limit on disk block allocation. blockInDoubt Distributed
 *     shares and block usage that have not been not accounted for. inodeUsage Current number of
 *     allocated inodes. inodeHardLimit Absolute limit on allocated inodes. inodeSoftLimit Preferred
 *     inode limit. inodeInDoubt Distributed inode share and inode usage that have not been
 *     accounted for. quoId user ID, group ID, or fileset ID. entryType Not used blockGraceTime Time
 *     limit (in seconds since the Epoch) for excessive disk use. inodeGraceTime Time limit (in
 *     seconds since the Epoch) for excessive inode use.
 */
public class QuotaInfo {

  private String filesystemName = null;
  private QuotaType quotaType = null;
  private long blockUsage = -1L;
  private long blockHardLimit = -1L;
  private long blockSoftLimit = -1L;
  private long blockInDoubt = -1L;
  private long blockGraceTime = -1L;
  private long inodeUsage = -1L;
  private long inodeHardLimit = -1L;
  private long inodeSoftLimit = -1L;
  private long inodeInDoubt = -1L;
  private long inodeGraceTime = -1L;
  private String remarks = null;
  private SizeUnit sizeUnit = SizeUnit.KILOBYTES; // Default values for Blocks
  private TimeUnit timeUnit = TimeUnit.HOURS; // Default values is one week = 7
  // days = 168 hours

  public QuotaInfo() {

    super();
  }
}
