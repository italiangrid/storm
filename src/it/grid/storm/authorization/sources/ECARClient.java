package it.grid.storm.authorization.sources;

import ecarClientStubs.Ecar;
import ecarClientStubs.EcarLocator;
import ecarClientStubs.EcarPortType;
import javax.xml.rpc.Stub;
import org.apache.axis.AxisFault;

/**
 * Class that represents a consumer of the ECAR web service.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    June 2006
 */
public class ECARClient {

    private String sea = ""; //String representing the service endpoint access
    private EcarPortType service = null; //The ECAR web service!

    /**
     * Public constructor that requires a String representing the service endpoint access of
     * the ECAR WebService.
     */
    public ECARClient(String sea) {
        if (sea!=null) this.sea=sea;
    }

    /**
     * Method used to set up a connection to the ECAR web service identified by the end point
     * specified during constructor invocation. The connection is kept open all the time!
     */
    public void open() throws ECARClientException {
        try {
            Ecar ecarLocator = new EcarLocator();
            service = ecarLocator.getecar();
            ((Stub) service)._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY,this.sea);
            ((Stub) service)._setProperty(Stub.SESSION_MAINTAIN_PROPERTY,new Boolean(true));
        } catch (javax.xml.rpc.ServiceException e) {
            throw new ECARClientException("Unexpected exception with remote webservice: "+e);
        } catch (Exception e) {
            //Generic exception!
            throw new ECARClientException("Unexpected exception in ECAR client: "+e);
        }
    }

    /**
     * Method used to close an open connection.
     */
    public void close() {
        service = null;
    }

    /**
     * Method that asks ECAR whether the Distinguished Name dn, has permission,
     * on the given logicalFile. This method does _not_ make use of FQAN!
     */
    public boolean canAccess(String logicalFile, int permission, String dn) throws ECARServiceException, ECARClientException, ECARMissingPolicyException {
        try {
            if (service!=null) return service.canAccess(logicalFile,permission,dn,null);
            throw new ECARClientException("Invoking canAccess, but connection is not open!");
        } catch (ECARClientException e) {
            throw e;
        } catch (AxisFault e) {
            //ECAR threw a SoapFault; it could be: "Database Error", "Invalid path", "Cannot read ACL"
            //"LFC server access error", or "Internal server error"
            String fault = e.getFaultString();
            if (e.equals("Invalid Path")) throw new ECARMissingPolicyException("ECAR said it does not have the requested path! "+fault);
            throw new ECARServiceException("Remote web service launched a SoapFault: "+fault);
        } catch (Exception e) {
            //Generic exception!
            throw new ECARClientException("Unexpected exception in ECAR client: "+e);
        }
    }

}
