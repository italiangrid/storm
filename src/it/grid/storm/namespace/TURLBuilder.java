package it.grid.storm.namespace;

import it.grid.storm.common.types.PFN;
import it.grid.storm.srm.types.InvalidTTURLAttributesException;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.model.Authority;
import org.apache.commons.logging.Log;
import it.grid.storm.namespace.model.Protocol;


public class TURLBuilder {

  private static Log log = NamespaceDirector.getLogger();

  public TURLBuilder() {
    super();
  }

  private static TTURL buildTURL(Protocol protocol, Authority authority, String extraSlashes, PFN physicalFN) {
    TTURL turl = null;
    String turlString = null;
    try {
      turlString = protocol.getProtocolPrefix() + authority.toString() + extraSlashes + physicalFN.getValue();
      log.debug("turlString used to build the TURL : " + turlString);
      turl = TTURL.makeFromString( turlString );
    }
    catch ( InvalidTTURLAttributesException ex ) {
      log.error("Error while constructing TURL with Authority :'" + authority + "'; EXCEP: "+ex);
    }
    return turl;
  }


  /**
   * buildFileTURL
   *
   * @return TTURL
   */
  public static TTURL buildFileTURL(Authority authority, PFN physicalFN) {
    //Authority for Protocol File is empty
    String extraSlashesForFile = Configuration.getInstance().getExtraSlashesForFileTURL();
    return buildTURL(Protocol.FILE, authority, extraSlashesForFile, physicalFN) ;
  }


  /**
   * buildGsiftpTURL
   *
   * @return TTURL
   */
  public static TTURL buildGsiftpTURL(Authority authority, PFN physicalFN) {
    String extraSlashesForGSIFTP = Configuration.getInstance().getExtraSlashesForGsiFTPTURL();
    return buildTURL(Protocol.GSIFTP, authority, extraSlashesForGSIFTP, physicalFN) ;
  }


  /**
   * buildRFIOTURL
   *
   * @return TTURL
   */
  public static TTURL buildRFIOTURL(Authority authority, PFN physicalFN) {
    String extraSlashesForRFIO = Configuration.getInstance().getExtraSlashesForRFIOTURL();
    return buildTURL(Protocol.RFIO, authority, extraSlashesForRFIO, physicalFN) ;
  }


  /**
   * buildROOTTURL
   *
   * @return TTURL
   */
  public static TTURL buildROOTTURL(Authority authority, PFN physicalFN) {
    String extraSlashesForROOT = Configuration.getInstance().getExtraSlashesForROOTTURL();
    return buildTURL(Protocol.ROOT, authority, extraSlashesForROOT, physicalFN) ;
  }

}
