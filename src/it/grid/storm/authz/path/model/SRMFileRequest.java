/**
 * 
 */
package it.grid.storm.authz.path.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zappi
 */
public enum SRMFileRequest {

    /**
     * WRITE_FILE 'W' READ_FILE 'R' RENAME 'F' DELETE 'D' TRAVERSE_DIRECTORY 'T' LIST_DIRECTORY 'L' MAKE_DIRECTORY 'M'
     * CREATE_FILE 'N' UNDEFINED '?'
     **/

    // Operations to SURL
    PTP_Overwrite("srmPrepareToPut-overwrite", "PTP-Over", new ArrayList<PathOperation>() {
        {
            add(PathOperation.WRITE_FILE);
        }
    }),

    PTP("srmPrepareToPut", "PTP", new ArrayList<PathOperation>() {
        {
            add(PathOperation.CREATE_FILE);
            add(PathOperation.WRITE_FILE);
        }
    }),

    PTG("srmPrepareToGet", "PTG", new ArrayList<PathOperation>() {
        {
            add(PathOperation.READ_FILE);
        }
    }),

    CPto("srmCopy to", "CPto", new ArrayList<PathOperation>() {
        {
            add(PathOperation.WRITE_FILE);
            add(PathOperation.CREATE_FILE);
        }
    }),

    CPfrom("srmCopy from", "CPfrom", new ArrayList<PathOperation>() {
        {
            add(PathOperation.READ_FILE);
        }
    }),

    // OVERLOAD with OP
    RM("srmRemove", "RM", new ArrayList<PathOperation>() {
        {
            add(PathOperation.DELETE);
        }
    }),

    RMD("srmRemoveDir", "RMD", new ArrayList<PathOperation>() {
        {
            add(PathOperation.DELETE);
        }
    }),

    MD("srmMakeDir", "MD", new ArrayList<PathOperation>() {
        {
            add(PathOperation.MAKE_DIRECTORY);
        }
    }),

    LS("srmLS", "LS", new ArrayList<PathOperation>() {
        {
            add(PathOperation.LIST_DIRECTORY);
        }
    }),

    MV_source("srmMove-source", "MV-source", new ArrayList<PathOperation>() {
        {
            add(PathOperation.READ_FILE);
            add(PathOperation.DELETE);
        }
    }),

    MV_dest("srmMove-dest", "MV-dest", new ArrayList<PathOperation>() {
        {
            add(PathOperation.CREATE_FILE);
            add(PathOperation.WRITE_FILE);
        }
    });

    private final String description;
    private final String srmOp;
    private final PathAccessMask requestedPathOps;

    private static HashMap<String, SRMFileRequest> ops = new HashMap<String, SRMFileRequest>() {
        {
            put("PTP-Over", PTP_Overwrite);
            put("srmPrepareToPut-overwrite", PTP_Overwrite);
            put("PTP", PTP);
            put("srmPrepareToPut", PTP);
            put("PTG", PTG);
            put("srmPrepareToGet", PTG);
            put("CPto", CPto);
            put("srmCopy to", CPto);
            put("CPFrom", CPfrom);
            put("srmCopy from", CPfrom);
            put("RM", RM);
            put("srmRm", RM);
            put("RMD", RMD);
            put("srmRemoveDir", RM);
            put("MD", MD);
            put("srmMakeDir", MD);
            put("LS", LS);
            put("srmLs", LS);
            put("MV-source", MV_source);
            put("srmMove-source", MV_source);
            put("MV-dest", MV_dest);
            put("srmMove-dest", MV_dest);
        }
    };

    /*
     * Used only for testing
     */
    public static SRMFileRequest buildFromString(String srmOp) {
        if (ops.containsKey(srmOp)) {
            return ops.get(srmOp);
        } else {
            return null;
        }
    }

    /**
     * SRMOperation
     */
    private SRMFileRequest(String description, String srmOp, List<PathOperation> pathOps) {
        this.description = description;
        this.srmOp = srmOp;
        requestedPathOps = new PathAccessMask();
        for (PathOperation pathOp : pathOps) {
            requestedPathOps.addPathOperation(pathOp);
        }
    }

    public PathAccessMask getSRMPathAccessMask() {
        return requestedPathOps;
    }

    @Override
    public String toString() {
        String result;
        result = srmOp + " : " + description + " = " + requestedPathOps;
        return result;
    }

}
