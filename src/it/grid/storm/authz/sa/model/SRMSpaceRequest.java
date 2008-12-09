package it.grid.storm.authz.sa.model;

import java.util.List;
import java.util.ArrayList;
import java.util.*;


public class SRMSpaceRequest {

/**
  RELEASE_SPACE (D)
  UPDATE_SPACE (U)
  READ_FROM_SPACE (R)
  WRITE_TO_SPACE (W)
  STAGE_TO_SPACE (S)
  REPLICATE_FROM_SPACE(C)
  PURGE_FROM_SPACE (P)
  QUERY_SPACE (Q)
  MODIFY_SPACE_ACL (M)
**/

    public final static SRMSpaceRequest PTP = new SRMSpaceRequest("srmPrepareToPut", "PTP", new String[]{"W"});
    public final static SRMSpaceRequest PTG = new SRMSpaceRequest("srmPrepareToGet", "PTG", new String[]{"R","C"});
    public final static SRMSpaceRequest BOL = new SRMSpaceRequest("srmBringOnLine", "BOL", new String[]{"S","C"});
    public final static SRMSpaceRequest CPto = new SRMSpaceRequest("srmCopy to", "CPto", new String[]{"W"});
    public final static SRMSpaceRequest CPfrom = new SRMSpaceRequest("srmCopy from", "CPfrom", new String[]{"R","C"});
    public final static SRMSpaceRequest PFS = new SRMSpaceRequest("srmPurgeFromSpace", "PFS", new String[]{"P"});
    public final static SRMSpaceRequest RS = new SRMSpaceRequest("srmReleaseSpace", "RS", new String[]{"D"});
    public final static SRMSpaceRequest QS = new SRMSpaceRequest("srmGetSpaceMetadata", "QS", new String[]{"Q","U"});

    //OVERLOAD with OP
    public final static SRMSpaceRequest RM = new SRMSpaceRequest("srmRemove", "RM", new String[]{"W"});
    public final static SRMSpaceRequest RMD = new SRMSpaceRequest("srmRemoveDir", "RMD", new String[]{"W"});
    public final static SRMSpaceRequest MD = new SRMSpaceRequest("srmMakeDir", "MD", new String[]{"W"});
    public final static SRMSpaceRequest LS = new SRMSpaceRequest("srmLS", "LS", new String[]{"R"});
    public final static SRMSpaceRequest MV = new SRMSpaceRequest("srmMove", "MV", new String[]{"W"});


    private String description;
    private String srmOp;
    private List<SpacePermission> spaceOps;

    /**
     * SRMOperation
     */
    private SRMSpaceRequest(String description, String srmOp, String[] spaceOps) {
        this.description = description;
        this.srmOp = srmOp;
        this.spaceOps = new ArrayList();
        for (int i = 0; i < spaceOps.length; i++) {
           this.spaceOps.add(SpacePermission.getSpaceOperation(spaceOps[i]));
        }
    }

    public String toString(){
        String result;
        String spaceOpsStr ="";
        for (Iterator iter = spaceOps.iterator(); iter.hasNext(); ) {
            SpacePermission item = (SpacePermission) iter.next();
            spaceOpsStr = spaceOpsStr + item.toString();
        }
        result = this.srmOp + " : " + this.description + " = " + spaceOpsStr;
        return result;
    }
}
