/**
 * 
 */
package it.grid.storm.authz.path.model;


/**
 * @author zappi
 *
 */
public class SRMFileRequest {
  
  /**
     * WRITE_FILE 'W' READ_FILE 'R' RENAME 'F' DELETE 'D' TRAVERSE_DIRECTORY 'T' LIST_DIRECTORY 'L' MAKE_DIRECTORY 'M'
     * CREATE_FILE 'N' UNDEFINED '?'
     **/

      //Operations to SURL
    public final static SRMFileRequest PTP_Overwrite = new SRMFileRequest("srmPrepareToPut-overwrite",
                                                                          "PTP-Over",
                                                                          new PathOperation[] { PathOperation.WRITE_FILE });

    public final static SRMFileRequest PTP = new SRMFileRequest("srmPrepareToPut",
                                                                "PTP",
                                                                new PathOperation[] {
                                                                        PathOperation.CREATE_FILE,
                                                                        PathOperation.CREATE_FILE });

    public final static SRMFileRequest PTG = new SRMFileRequest("srmPrepareToGet",
                                                                "PTG",
                                                                new PathOperation[] { PathOperation.READ_FILE });

    public final static SRMFileRequest CPto = new SRMFileRequest("srmCopy to", "CPto", new PathOperation[] {
            PathOperation.WRITE_FILE, PathOperation.CREATE_FILE });

    public final static SRMFileRequest CPfrom = new SRMFileRequest("srmCopy from",
                                                                   "CPfrom",
                                                                   new PathOperation[] { PathOperation.READ_FILE });

    // OVERLOAD with OP
    public final static SRMFileRequest RM = new SRMFileRequest("srmRemove",
                                                               "RM",
                                                               new PathOperation[] { PathOperation.DELETE });

    public final static SRMFileRequest RMD = new SRMFileRequest("srmRemoveDir",
                                                                "RMD",
                                                                new PathOperation[] { PathOperation.DELETE });

    public final static SRMFileRequest MD = new SRMFileRequest("srmMakeDir",
                                                               "MD",
                                                               new PathOperation[] { PathOperation.MAKE_DIRECTORY });

    public final static SRMFileRequest LS = new SRMFileRequest("srmLS",
                                                               "LS",
                                                               new PathOperation[] { PathOperation.LIST_DIRECTORY });

    public final static SRMFileRequest MV_source = new SRMFileRequest("srmMove-source",
                                                                      "MV-source",
                                                                      new PathOperation[] {
                                                                              PathOperation.READ_FILE,
                                                                              PathOperation.DELETE });

    public final static SRMFileRequest MV_dest = new SRMFileRequest("srmMove-dest",
                                                                    "MV-dest",
                                                                    new PathOperation[] {
                                                                            PathOperation.CREATE_FILE,
                                                                            PathOperation.WRITE_FILE });
                                                                


      private String description;
      private String srmOp;
      private PathAccessMask requestedPathOps;

      /**
       * SRMOperation
       */
      private SRMFileRequest(String description, String srmOp, PathOperation[] pathOps) {
          this.description = description;
          this.srmOp = srmOp;
          requestedPathOps = new PathAccessMask();
          for (PathOperation pathOp : pathOps) {
             requestedPathOps.addPathOperation(pathOp);
          }
      }

      public String toString(){
          String result;
          result = srmOp + " : " + description + " = " + requestedPathOps;
          return result;
      }
}
