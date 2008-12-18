package it.grid.storm.namespace.config.xml;

import java.util.*;

import it.grid.storm.namespace.*;
import it.grid.storm.namespace.model.SAAuthzType;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public interface XMLConst {

    public final char FS_SUB_PATTERN = '°';
    public final char MAP_SUB_PATTERN = 'ç';
    public final char PROT_SUB_PATTERN = '§';
    public final char APPRULE_SUB_PATTERN = '^';
    public final char ACL_ENTRY_SUB_PATTERN = '~';
    public final char MEMBER_SUB_PATTERN = '*';

    /**
     * ELEMENTS
     */

    //#########     Level-0     #########
    public final String MAIN_ELEMENT = "namespace";
    public final String NAMESPACE_VERSION = "[@version]";

    //#########     Level-1     #########
    public final String FILESYSTEMS = "filesystems";
    public final String MAPPINGRULES = "mapping-rules";
    public final String APPROACHABLERULES = "approachable-rules";

    //#########     Level-2     #########
    //    FILESYSTEMS-TYPE
    public String FILESYSTEM_NUDE = FILESYSTEMS + ".filesystem";
    public String FILESYSTEM = FILESYSTEM_NUDE + "(" + FS_SUB_PATTERN + ")";
    public String FILESYSTEM_NAME = FILESYSTEM + "[@name]";
    public String FILESYSTEM_TYPE = FILESYSTEM + "[@fs_type]";
    //    MAPPING-RULES-TYPE
    public String MAP_RULE_NUDE = MAPPINGRULES + ".map-rule";
    public String MAP_RULE = MAP_RULE_NUDE + "(" + MAP_SUB_PATTERN + ")";
    public String MAP_RULE_NAME = MAP_RULE + "[@name]";
    //    APPROACHABLE-RULES-TYPE
    public String APP_RULE_NUDE = APPROACHABLERULES + ".app-rule";
    public String APP_RULE = APP_RULE_NUDE + "(" + APPRULE_SUB_PATTERN + ")";
    public String APP_RULE_NAME = APP_RULE + "[@name]";

    //#########     Level-3     #########
    //    FILESYSTEM-TYPE
    public String FS_COUNTING = FILESYSTEM_NUDE + ".root";
    public String FS_BY_NAME = FILESYSTEM_NUDE + "[@name]";
    public String FS_ROOT = FILESYSTEM + ".root";
    public String FS_SPACE_TOKEN_DESCRIPTION = FILESYSTEM + ".space-token-description";
    public String FS_STORAGE_CLASS = FILESYSTEM + ".storage-class";
    public String FS_DRIVER = FILESYSTEM + ".filesystem-driver";
    public String FS_SPACE_DRIVER = FILESYSTEM + ".spacesystem-driver";
    public String FS_AUTHZ = FILESYSTEM + ".storage-area-authz"; //1.4.0
    public String FS_DEFAULTVALUES = FILESYSTEM + ".defaults-values";
    public String FS_CAPABILITIES = FILESYSTEM + ".capabilities";
    public String FS_PROPERTIES = FILESYSTEM + ".properties";
    //    MAP-RULE-TYPE
    public String MAP_RULE_COUNTING = MAP_RULE_NUDE + ".stfn-root";
    public String MAP_RULE_BY_NAME = MAP_RULE_NUDE + "[@name]";
    public String MAP_RULE_STFNROOT = MAP_RULE + ".stfn-root";
    public String MAP_RULE_MAPPED_FS = MAP_RULE + ".mapped-fs";
    //    APP-RULE-TYPE
    public String APP_RULE_COUNTING = APP_RULE_NUDE + "[@name]";
    public String APP_RULE_BY_NAME = APP_RULE_NUDE + "[@name]";
    public String APP_SUBJECTS = APP_RULE + ".subjects";
    public String APPROACHABLE_FS = APP_RULE + ".approachable-fs";
    public String APP_SPACE_REL_PATH = APP_RULE + ".space-rel-path";

    //#########     Level-4     #########
    //    STORAGE-AREA-AUTHZ
    public String SA_AUTHZ_FIXED = FS_AUTHZ + ".fixed"; //1.4.0
    public String SA_AUTHZ_DB = FS_AUTHZ + ".authz-db"; //1.4.0
    //    DEFAULTS-VALUES-TYPE
    public String DEF_SPACE = FS_DEFAULTVALUES + ".space";
    public String DEF_SPACE_LT = DEF_SPACE + "[@lifetime]";
    public String DEF_SPACE_TYPE = DEF_SPACE + "[@type]";
    public String DEF_SPACE_GUARSIZE = DEF_SPACE + "[@guarsize]";
    public String DEF_SPACE_TOTSIZE = DEF_SPACE + "[@totalsize]";
    public String DEF_FILE = FS_DEFAULTVALUES + ".file";
    public String DEF_FILE_LT = DEF_FILE + "[@lifetime]";
    public String DEF_FILE_TYPE = DEF_FILE + "[@type]";
    //    PROPERTIES-TYPE
    public String RETENTION_POLICY= FS_PROPERTIES + ".RetentionPolicy";
    public String ACCESS_LATENCY = FS_PROPERTIES + ".AccessLatency";
    public String EXPIRATION_MODE = FS_PROPERTIES + ".ExpirationMode";
    public String ONLINE_SIZE = FS_PROPERTIES + ".TotalOnlineSize";
    public String ONLINE_SIZE_UNIT = ONLINE_SIZE + "[@unit]";
    public String LIMITED_SIZE = ONLINE_SIZE + "[@limited-size]"; //1.4.0
    public String NEARLINE_SIZE = FS_PROPERTIES + ".TotalNearlineSize";
    public String NEARLINE_SIZE_UNIT = NEARLINE_SIZE + "[@unit]";
    //    CAPABILITIES-TYPE
    public String ACL_MODE = FS_CAPABILITIES + ".aclMode";
    public String DEFAULT_ACL = FS_CAPABILITIES + ".default-acl";
    public String TRANS_PROT = FS_CAPABILITIES + ".trans-prot";
    public String QUOTA = FS_CAPABILITIES + ".quota";
    public String QUOTA_ENABLED = QUOTA + "[@enabled]";
    //    SUBJECTS-TYPE
    public String APP_DN = APP_SUBJECTS + ".dn";
    public String APP_VO_NAME = APP_SUBJECTS + ".vo-name";
    //    PROTOCOL POOL DEFINITION
    public String POOL = FS_CAPABILITIES + ".pool"; //1.4.0

    //#########     Level-5     #########
    //    DEFAULT_ACL
    public String ACL_ENTRY = DEFAULT_ACL + ".acl-entry";
    public String ACL_ENTRY_COUNTING = DEFAULT_ACL + ".acl-entry.groupName";

    //    QUOTA-PROPERTIES
    public String QUOTA_PROPERTIES = QUOTA + ".properties";
    public String QUOTA_PROPERTIES_FILE = QUOTA + ".properties-file";
    //    TRANS-PROT-TYPE
    public String PROTOCOL_BY_NAME = TRANS_PROT + ".prot[@name]";
    public String PROTOCOL_COUNTING = TRANS_PROT + ".prot.schema";
    public String PROTOCOL = TRANS_PROT + ".prot(" + PROT_SUB_PATTERN + ")";
    public String PROTOCOL_NAME = PROTOCOL + "[@name]";

    //#########     Level-6     #########
    //    ACL-ENTRY DETAILS
    public String GROUP_NAME = ACL_ENTRY + ".groupName";
    public String PERMISSIONS = ACL_ENTRY + ".permissions";

    //    QUOTA-TYPE
    public String QUOTA_TYPE = QUOTA_PROPERTIES + ".quotaType";
    public String QUOTA_DEVICE = QUOTA_PROPERTIES + ".device";

    //    PROT-TYPE
    public String PROT_ID = PROTOCOL + ".id";    //1.4.0
    public String PROT_SCHEMA = PROTOCOL + ".schema";
    public String PROT_HOST = PROTOCOL + ".host";
    public String PROT_PORT = PROTOCOL + ".port";

    //    POOL DETAILS
    public String BALANCE_STRATEGY = POOL + ".balance-strategy";    //1.4.0
    public String POOL_MEMBERS = POOL + ".members";    //1.4.0


    //#########     Level-7     #########
    //    POOL MEMBER
    public String POOL_MEMBER_COUNTING = POOL_MEMBERS + ".member"; //1.4.0
    public String POOL_MEMBER_NUDE = POOL_MEMBERS + ".member"; //1.4.0
    public String POOL_MEMBER = POOL_MEMBER_NUDE + "(" + MEMBER_SUB_PATTERN + ")"; //1.4.0
    public String POOL_MEMBER_ID = POOL_MEMBER + "[@member-id]"; //1.4.0
    public String POOL_MEMBER_WEIGHT = POOL_MEMBER + ".weight"; //1.4.0

    //    QUOTA-TYPE-ID
    public String FILE_SET_ID = QUOTA_TYPE + ".filesetID";
    public String GROUP_ID = QUOTA_TYPE + ".groupID";
    public String USER_ID = QUOTA_TYPE + ".userID";

    //#####################################
    // OPTIONAL ELEMENT and DEFAULT VALUES
    //#####################################
    public final String DEFAULT_UNIT_TYPE = "TB";
    public final String DEFAULT_AUTHZ_SOURCE = "PermitAll";
    public final String DEFAULT_STORAGE_CLASS = "T0D1";

    /**
     * METHOD Interface
     */

    public String getNamespaceVersion() throws NamespaceException;

    public int getNumberOfFS() throws NamespaceException;

    public String getFSName(int numOfFS) throws NamespaceException;

    public int getFSNumber(String nameOfFS) throws NamespaceException;

    public String getFSType(String nameOfFS) throws NamespaceException;

    public String getFSSpaceTokenDescription(String nameOfFS) throws NamespaceException;

    public String getFSRoot(String nameOfFS) throws NamespaceException;

    public String getFSDriver(String nameOfFS) throws NamespaceException;

    public String getSpaceDriver(String nameOfFS) throws NamespaceException;

    public String getStorageAreaAuthz(String nameOfFS, SAAuthzType type) throws NamespaceException; //Modified in 1.4.0

    public boolean getStorageAreaAuthzFixedDefined(String nameOfFS) throws NamespaceException; //1.4.0

    public boolean getStorageAreaAuthzDBDefined(String nameOfFS) throws NamespaceException; //1.4.0

    public SAAuthzType getStorageAreaAuthzType(String nameOfFS) throws NamespaceException; //1.4.0

    public String getDefaultSpaceType(String nameOfFS) throws NamespaceException;

    public long getDefaultSpaceLifeTime(String nameOfFS) throws NamespaceException;

    public long getDefaultSpaceGuarSize(String nameOfFS) throws NamespaceException;

    public long getDefaultSpaceTotSize(String nameOfFS) throws NamespaceException;

    public String getDefaultFileType(String nameOfFS) throws NamespaceException;

    public long getDefaultFileLifeTime(String nameOfFS) throws NamespaceException;

    public String getACLMode(String nameOfFS) throws NamespaceException;

    public boolean getDefaultACLDefined(String nameOfFS) throws NamespaceException;

    public int getNumberOfACL(String nameOfFS) throws NamespaceException;

    public String getGroupName(String nameOfFS, int aclEntryNumber) throws NamespaceException;

    public String getPermissionString(String nameOfFS, int aclEntryNumber) throws NamespaceException;

    public boolean getQuotaDefined(String nameOfFS) throws NamespaceException;

    public boolean getQuotaEnabled(String nameOfFS) throws NamespaceException;

    public boolean getQuotaPropertiesFileDefined(String nameOfFS) throws NamespaceException;

    public boolean getQuotaPropertiesDefined(String nameOfFS) throws NamespaceException;

    public String getQuotaPropertiesFile(String nameOfFS) throws NamespaceException;

    public boolean getQuotaDeviceDefined(String nameOfFS) throws NamespaceException;

    public String getQuotaDevice(String nameOfFS) throws NamespaceException;

    public boolean getQuotaFilesetDefined(String nameOfFS) throws NamespaceException;

    public String getQuotaFileset(String nameOfFS) throws NamespaceException;

    public boolean getQuotaGroupIDDefined(String nameOfFS) throws NamespaceException;

    public String getQuotaGroupID(String nameOfFS) throws NamespaceException;

    public boolean getQuotaUserIDDefined(String nameOfFS) throws NamespaceException;

    public String getQuotaUserID(String nameOfFS) throws NamespaceException;

    public int getNumberOfProt(String nameOfFS) throws NamespaceException;

    public String getProtName(String nameOfFS, int numOfProt) throws NamespaceException;

    public int getProtId(String nameOfFS, int numOfProt) throws NamespaceException;  //1.4.0

    public String getProtSchema(String nameOfFS, int numOfProt) throws NamespaceException; //Modified in 1.4.0

    public String getProtHost(String nameOfFS,  int numOfProt) throws NamespaceException; //Modified in 1.4.0

    public String getProtPort(String nameOfFS,  int numOfProt) throws NamespaceException; //Modified in 1.4.0

    public String getRetentionPolicyType(String nameOfFS) throws NamespaceException;

    public String getAccessLatencyType(String nameOfFS) throws NamespaceException;

    public String getExpirationModeType(String nameOfFS) throws NamespaceException;

    public String getOnlineSpaceUnitType(String nameOfFS) throws NamespaceException;

    public boolean getOnlineSpaceLimitedSize(String nameOfFS) throws NamespaceException;  //1.4.0

    public long getOnlineSpaceSize(String nameOfFS) throws NamespaceException;

    public String getNearlineSpaceUnitType(String nameOfFS) throws NamespaceException;

    public long getNearlineSpaceSize(String nameOfFS) throws NamespaceException;

    public boolean getPoolDefined(String nameOfFS) throws NamespaceException;  //1.4.0

    public String getBalancerStrategy(String nameOfFS) throws NamespaceException;  //1.4.0

    public int getNumberOfPoolMembers(String nameOfFS) throws NamespaceException;  //1.4.0

    public int getMemberID(String nameOfFS, int memberNr) throws NamespaceException;  //1.4.0

    public int getMemberWeight(String nameOfFS, int memberNr) throws NamespaceException;  //1.4.0

    public int getNumberOfMappingRule() throws NamespaceException;

    public String getMapRuleName(int numOfMapRule) throws NamespaceException;

    public String getMapRule_StFNRoot(String nameOfMapRule) throws NamespaceException;

    public String getMapRule_mappedFS(String nameOfMapRule) throws NamespaceException;

    public int getNumberOfApproachRule() throws NamespaceException;

    public String getApproachRuleName(int numOfAppRule) throws NamespaceException;

    public String getAppRule_SubjectDN(String nameOfAppRule) throws NamespaceException;

    public String getAppRule_SubjectVO(String nameOfAppRule) throws NamespaceException;

    public List getAppRule_AppFS(String nameOfAppRule) throws NamespaceException;

    public String getAppRule_RelativePath(String nameOfAppRule) throws NamespaceException;
}
