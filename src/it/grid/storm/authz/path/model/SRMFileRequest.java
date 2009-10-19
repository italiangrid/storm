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
public class SRMFileRequest {

    /**
     * WRITE_FILE 'W' READ_FILE 'R' RENAME 'F' DELETE 'D' TRAVERSE_DIRECTORY 'T' LIST_DIRECTORY 'L' MAKE_DIRECTORY 'M'
     * CREATE_FILE 'N' UNDEFINED '?'
     **/

    // Operations to SURL
    public final static SRMFileRequest PTP_Overwrite = new SRMFileRequest("srmPrepareToPut-overwrite",
                                                                          "PTP-Over",
                                                                          new ArrayList<PathOperation>() {
                                                                              {
                                                                                  add(PathOperation.WRITE_FILE);
                                                                              }
                                                                          });

    public final static SRMFileRequest PTP = new SRMFileRequest("srmPrepareToPut",
                                                                "PTP",
                                                                new ArrayList<PathOperation>() {
                                                                    {
                                                                        add(PathOperation.CREATE_FILE);
                                                                        add(PathOperation.WRITE_FILE);
                                                                    }
                                                                });

    public final static SRMFileRequest PTG = new SRMFileRequest("srmPrepareToGet",
                                                                "PTG",
                                                                new ArrayList<PathOperation>() {
                                                                    {
                                                                        add(PathOperation.READ_FILE);
                                                                    }
                                                                });

    public final static SRMFileRequest CPto = new SRMFileRequest("srmCopy to",
                                                                 "CPto",
                                                                 new ArrayList<PathOperation>() {
                                                                     {
                                                                         add(PathOperation.WRITE_FILE);
                                                                         add(PathOperation.CREATE_FILE);
                                                                     }
                                                                 });

    public final static SRMFileRequest CPfrom = new SRMFileRequest("srmCopy from",
                                                                   "CPfrom",
                                                                   new ArrayList<PathOperation>() {
                                                                       {
                                                                           add(PathOperation.READ_FILE);
                                                                       }
                                                                   });

    // OVERLOAD with OP
    public final static SRMFileRequest RM = new SRMFileRequest("srmRemove",
                                                               "RM",
                                                               new ArrayList<PathOperation>() {
                                                                   {
                                                                       add(PathOperation.DELETE);
                                                                   }
                                                               });

    public final static SRMFileRequest RMD = new SRMFileRequest("srmRemoveDir",
                                                                "RMD",
                                                                new ArrayList<PathOperation>() {
                                                                    {
                                                                        add(PathOperation.DELETE);
                                                                    }
                                                                });

    public final static SRMFileRequest MD = new SRMFileRequest("srmMakeDir",
                                                               "MD",
                                                               new ArrayList<PathOperation>() {
                                                                   {
                                                                       add(PathOperation.MAKE_DIRECTORY);
                                                                   }
                                                               });

    public final static SRMFileRequest LS = new SRMFileRequest("srmLS", "LS", new ArrayList<PathOperation>() {
        {
            add(PathOperation.LIST_DIRECTORY);
        }
    });

    public final static SRMFileRequest MV_source = new SRMFileRequest("srmMove-source",
                                                                      "MV-source",
                                                                      new ArrayList<PathOperation>() {
                                                                          {
                                                                              add(PathOperation.READ_FILE);
                                                                              add(PathOperation.DELETE);
                                                                          }
                                                                      });

    public final static SRMFileRequest MV_dest = new SRMFileRequest("srmMove-dest",
                                                                    "MV-dest",
                                                                    new ArrayList<PathOperation>() {
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
