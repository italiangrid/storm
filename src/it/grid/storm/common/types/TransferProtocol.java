package it.grid.storm.common.types;

/**
 * This class represents the possible transfer protocols of StoRM.
 *
 * @author  EGRID ICTP - CNAF Bologna
 * @date    March 23rd, 2005
 * @version 2.0
 */
public class TransferProtocol {

    private String protocol;

    /**
     * Static attribute that indicates EMPTY TransferProtocol
     */
    public static TransferProtocol EMPTY = new TransferProtocol("empty") {
         public int hashCode() {
            return 0;
        }
    };


    /**
     * Static attribute that indicates FILE TransferProtocol.
     */
    public static TransferProtocol FILE = new TransferProtocol("file") {
        public int hashCode() {
            return 1;
        }
   };

    /**
     * Static attribute that indicates GSIFTP TransferProtocol.
     */
    public static TransferProtocol GSIFTP = new TransferProtocol("gsiftp") {
          public int hashCode() {
            return 2;
        }
   };

    /**
     * Static attribute that indicates RFIO TransferProtocol.
     */
    public static TransferProtocol RFIO = new TransferProtocol("rfio") {
          public int hashCode() {
            return 3;
        }
   };

   /**
    * Static attribute that indicates RFIO TransferProtocol.
    */
   public static TransferProtocol ROOT = new TransferProtocol("root") {
         public int hashCode() {
           return 4;
       }
  };


    private TransferProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getValue() {
        return  protocol;
    }

    public String toString() {
        return protocol;
    }

    /**
     * Facility method to obtain a TransferProtocol object given its String
     * representation. Any white spaces are removed. In case no match is found,
     * an EMPTY TransferProtocol is returned.
     */
    public static TransferProtocol getTransferProtocol(String protocol) {
        if(protocol.toLowerCase().replaceAll(" ","").equals(FILE.toString()))
            return FILE;
        if(protocol.toLowerCase().replaceAll(" ","").equals(GSIFTP.toString()))
            return GSIFTP;
        if(protocol.toLowerCase().replaceAll(" ","").equals(RFIO.toString()))
            return RFIO;
        if(protocol.toLowerCase().replaceAll(" ","").equals(ROOT.toString()))
            return ROOT;
        return EMPTY;
    }


  /*
   public static TransferProtocol makeEmpty() {
            return EMPTY;
   }

    public boolean equals(Object o) {
        if (o==this) return true;
        if (!(o instanceof TransferProtocol)) return false;
    return false;
     //   TransferProtocol tp = (TransferProtocol) o;
     //   return e.equals(po.name);
    }
*/


/*
    public static void main(String[] args) {
        //Testing types...
        System.out.println("Testing types...");
        System.out.println("The empty protocol:"+TransferProtocol.EMPTY+"; hashCode:"+TransferProtocol.EMPTY.hashCode());
        System.out.println("The gsiftp protocol:"+TransferProtocol.GSIFTP+"; hashCode:"+TransferProtocol.GSIFTP.hashCode());
        System.out.println("The file protocol:"+TransferProtocol.FILE+"; hashCode:"+TransferProtocol.FILE.hashCode());
        //
        //Testing facility method
        System.out.println("\nTesting facility method...");
        System.out.println("Should see the empty protocol: "+TransferProtocol.getTransferProtocol(TransferProtocol.EMPTY.toString()));
        System.out.println("Should see the file protocol: "+TransferProtocol.getTransferProtocol(TransferProtocol.FILE.toString()));
        System.out.println("Should see the gsiftp protocol: "+TransferProtocol.getTransferProtocol(TransferProtocol.GSIFTP.toString()));
        System.out.println("TransferProtocol.GSIFTP=gsiftp,GSIFTP,gSiFtP: ");
        System.out.println(TransferProtocol.GSIFTP.equals(TransferProtocol.getTransferProtocol("gsiftp")) + " " +TransferProtocol.GSIFTP.equals(TransferProtocol.getTransferProtocol("GSIFTP")) + " " +TransferProtocol.GSIFTP.equals(TransferProtocol.getTransferProtocol("gSiFtP")));
        System.out.println("Should see the empty protocol: "+TransferProtocol.getTransferProtocol(" "+TransferProtocol.EMPTY.toString()+" "));
    }*/
}
