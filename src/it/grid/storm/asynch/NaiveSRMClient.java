package it.grid.storm.asynch;

import org.apache.log4j.Logger;

import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.common.types.TransferProtocol;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.InvalidTUserIDAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TStatusCode;

import srmClientStubs.*;

import org.apache.axis.types.URI;
import org.apache.axis.types.UnsignedLong;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.transport.http.HTTPSender;

import org.globus.axis.transport.GSIHTTPSender;
import org.globus.axis.transport.GSIHTTPTransport;
import org.globus.axis.util.Util;

import org.globus.gsi.gssapi.auth.NoAuthorization;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.gsi.gssapi.GlobusGSSException;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;

import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Class that represents a first implementation of an SRMClient.
 *
 * @author  EGRID - ICTP Trieste
 * @version 2.0
 * @date    October, 2005
 */
public class NaiveSRMClient implements SRMClient {

    private static Logger log = Logger.getLogger("srmclient");

    /**
     * Method used to execute an srmPrepareToPut.
     *
     * It will connect to the webservice derived from the toSURL using the credentials
     * available in GridUser. It requires the lifetime, the fileStorageType, the
     * spaceToken, the fileSize, the protocol, a description, the overwriteOption,
     * the retryTime. The method returns an SRMPrepareToPutReply containing the result
     * of the operation, and throws an SRMClient exception in case the operation cannot
     * be completed.
     *
     * This client is said to be naive, because given the reply from an SRMPrepareToPut,
     * the client checks the srm field of the whole request to obtain the request token
     * which the remote srm server associated to the request; it _ignores_ the return
     * status of the whole request; it _ignores_ the status for the specific file
     * requested.
     *
     * This is important to keep in mind, because there can be remote servers that have
     * specific ways of behaving such as leaving the specific file status blank until a
     * status for the whole request is completed, etc.
     */
    public SRMPrepareToPutReply prepareToPut(VomsGridUser gu, TSURL toSURL, TLifeTimeInSeconds lifetime, TFileStorageType fileStorageType, TSpaceToken spaceToken, TSizeInBytes filesize, TransferProtocol protocol, String description, TOverwriteMode overwriteOption, TLifeTimeInSeconds retryTime) throws SRMClientException {
        try {
            srmClientStubs.ISRM _srm = setUpGSI(gu,toSURL);

            //prepare srm request and execute it
            srmClientStubs.SrmPrepareToPutRequest req = new srmClientStubs.SrmPrepareToPutRequest(); //srm request
            //set authorization ID
            String stubuid = gu.getDn();
            req.setAuthorizationID(stubuid);
            //set description
            req.setUserRequestDescription(description);
            //set array of transfer protocols
            String[] protocolArray = new String[1]; protocolArray[0] = protocol.toString();
            srmClientStubs.ArrayOfString protocolArrayString = new srmClientStubs.ArrayOfString();
            protocolArrayString.setStringArray(protocolArray);
            srmClientStubs.TTransferParameters stubtp = new srmClientStubs.TTransferParameters();
            stubtp.setArrayOfTransferProtocols(protocolArrayString);
            req.setTransferParameters(stubtp);
            //set Overwrite Option
            req.setOverwriteOption(new WSOverwriteModeConverter().fromStoRM(overwriteOption));
            //set retry time
            req.setDesiredTotalRequestTime(new Integer(new Long(retryTime.value()).intValue()));

            //set storage type
            srmClientStubs.TFileStorageType stubfst = new WSFileStorageTypeConverter().fromStoRM(fileStorageType); //ws file storage type!
            req.setDesiredFileStorageType(stubfst);
            //set space token
            req.setTargetSpaceToken(spaceToken.toString());
            //set lifetime
            req.setDesiredFileLifeTime(new Integer(new Long(lifetime.value()).intValue()));

            //set request specific info
            srmClientStubs.TPutFileRequest stubpfr = new srmClientStubs.TPutFileRequest(); //ws put file request!
            //set file size
            org.apache.axis.types.UnsignedLong ulFileSize = new org.apache.axis.types.UnsignedLong(filesize.value());
            stubpfr.setExpectedFileSize(ulFileSize);
            //set target SURL
            stubpfr.setTargetSURL(new org.apache.axis.types.URI(toSURL.toString()));

            //set array of requests
            srmClientStubs.TPutFileRequest[] stubpfrArray = new srmClientStubs.TPutFileRequest[1]; //ws array of put file request!
            stubpfrArray[0] = stubpfr;
            srmClientStubs.ArrayOfTPutFileRequest arrayOfPut = new srmClientStubs.ArrayOfTPutFileRequest(stubpfrArray);
            req.setArrayOfFileRequests(arrayOfPut);

            //execute request!
            log.debug("NAIVE SRM CLIENT: sending request "+arrayOfPut);
            srmClientStubs.SrmPrepareToPutResponse response = _srm.srmPrepareToPut(req);
            log.debug("NAIVE SRM CLIENT: received reply "+response);


            //get TRequestToken
            it.grid.storm.srm.types.TRequestToken requestToken = new WSRequestTokenConverter().fromWS(response.getRequestToken());
            //get ArrayOfTPutRequestFileStatus
            srmClientStubs.ArrayOfTPutRequestFileStatus arrayOfPutFileStatuses = response.getArrayOfFileStatuses();
            srmClientStubs.TPutRequestFileStatus[] stubFileStatusArray = arrayOfPutFileStatuses.getStatusArray();
            //get specific request
            srmClientStubs.TPutRequestFileStatus stubFileStatus = stubFileStatusArray[0];
            srmClientStubs.TReturnStatus stubStatus = stubFileStatus.getStatus();
            it.grid.storm.srm.types.TReturnStatus retStat = WSReturnStatusConverter.getInstance().fromWS(stubStatus);
            return new SRMPrepareToPutReply(requestToken);

        } catch (it.grid.storm.srm.types.InvalidTUserIDAttributeException e) {
            //GridUser threw an exception! Cannot get credentials!
            throw new SRMClientException("Unexpected error in NaiveSRMClient! Cannot get gredentials from GridUser! "+e);
        } catch (GlobusCredentialException e) {
            //Globus credentials cannot be created!
            throw new SRMClientException("Unexpected error in NaiveSRMClient! Cannot create GlobusCredentials from GridUser! "+e);
        } catch (GSSException e) {
            //GSS credentials cannot be created!
            throw new SRMClientException("Unexpected error in NaiveSRMClient! Cannot create GSSCredentials from GlobusCredential! "+e);
        } catch (org.apache.axis.types.URI.MalformedURIException ex) {
            //This is a programming bug and should not occur! The URI is malformed!
            throw new SRMClientException("Unexpected error in NaiveSRMClient! Cannot create URI! "+ex);
        } catch (javax.xml.rpc.ServiceException ex) {
            //Something went wrong with the web service!
            throw new SRMClientException("Web Service problem! "+ex);
        } catch (java.rmi.RemoteException ex) {
            //something went wrong at low level communication with web service!
            throw new SRMClientException("Web Service Communication problem! "+ex);
        } catch (SOAPFaultException e) {
            //The remote service launched an exception! This is a programmatic exception remotely thrown, _not_ an error!
            throw new SRMClientException("The remote web service launched an exception! "+e);
        } catch (WSConversionException e) {
            //The data received from the web service could not be translated into StoRM object model equivalents!
            throw new SRMClientException("There were problems translating between WebService and StoRM object model! "+e);
        } catch (InvalidPutReplyAttributeException e) {
            //This is a programming bug and should not occur! For some reason, correctly translated WebService attributes
            //did not allow the correct creation of an SRMPrepareToPutReply!
            throw new SRMClientException("Unexpected error in NaiveSRMClient! Cannot create a reply to return! "+e);
        } catch (Exception e) {
            //Unexpected runtime exception! For example, a time out when connectin to the web service!
            throw new SRMClientException("Unexpected runtime error in NaiveSRMClient! "+e);
        } //end synchronized!
    }

    /**
     * Method used to execute an srmStatusOfPutRequest: it requires the request token
     * returned durign an srmPrepareToPut operation, the GridUser that issued the command,
     * and the toSURL of the request. An SRMStatusOfPutRequestReply is returned, but in
     * case the operation fails an SRMClientException is thrown containing an explanation
     * String.
     * 
     * This client is said to be naive, because given the reply from an SRMStatusOfPutRequest,
     * the client checks the srm field of the specific file to obtain both the TURL and the
     * status of the request, _ignoring_ the overall request status.
     *
     * This is important to keep in mind, because there can be remote servers that have
     * specific ways of behaving such as leaving the specific file status blank until a
     * status for the whole request is completed, etc.
     */
     public SRMStatusOfPutRequestReply statusOfPutRequest(it.grid.storm.srm.types.TRequestToken rt, VomsGridUser gu, it.grid.storm.srm.types.TSURL toSURL) throws SRMClientException{
        try {
            srmClientStubs.ISRM _srm = setUpGSI(gu,toSURL);

            //prepare srm request and execute it
            srmClientStubs.SrmStatusOfPutRequestRequest req = new srmClientStubs.SrmStatusOfPutRequestRequest();
            //set TRequestToken
            req.setRequestToken(rt.toString());
            //set Authorization ID
            req.setAuthorizationID(gu.getDn());
            //set SURL array
            org.apache.axis.types.URI[] stubSurlArray = new org.apache.axis.types.URI[1];
            org.apache.axis.types.URI stubSurl = new org.apache.axis.types.URI(toSURL.toString());
            stubSurlArray[0] = stubSurl;
            srmClientStubs.ArrayOfAnyURI arrayOfTSURL = new srmClientStubs.ArrayOfAnyURI(stubSurlArray);
            req.setArrayOfTargetSURLs(arrayOfTSURL);

            log.debug("NAIVE SRM CLIENT: invoking status of put with "+req);
            srmClientStubs.SrmStatusOfPutRequestResponse response = _srm.srmStatusOfPutRequest(req);
            log.debug("NAIVE SRM CLIENT: received response "+response);

            //process response
            srmClientStubs.ArrayOfTPutRequestFileStatus arrayOfPutStatuses = response.getArrayOfFileStatuses();
            srmClientStubs.TPutRequestFileStatus[] stubFileStatusArray = arrayOfPutStatuses.getStatusArray();
            srmClientStubs.TPutRequestFileStatus stubFileStatus = stubFileStatusArray[0];
            org.apache.axis.types.URI stubTurl = stubFileStatus.getTransferURL();
            srmClientStubs.TReturnStatus stubStatus = stubFileStatus.getStatus();

            it.grid.storm.srm.types.TTURL turl = new WSTurlConverter().fromWS(stubTurl);
            it.grid.storm.srm.types.TReturnStatus retStat = WSReturnStatusConverter.getInstance().fromWS(stubStatus);
            return new SRMStatusOfPutRequestReply(turl,retStat);
        } catch (it.grid.storm.srm.types.InvalidTUserIDAttributeException e) {
            //GridUser threw an exception! Cannot get credentials!
            throw new SRMClientException("Unexpected error in NaiveSRMClient! Cannot get gredentials from GridUser! "+e);
        } catch (GlobusCredentialException e) {
            //Globus credentials cannot be created!
            throw new SRMClientException("Unexpected error in NaiveSRMClient! Cannot create GlobusCredentials from GridUser! "+e);
        } catch (GSSException e) {
            //GSS credentials cannot be created!
            throw new SRMClientException("Unexpected error in NaiveSRMClient! Cannot create GSSCredentials from GlobusCredential! "+e);
        } catch (org.apache.axis.types.URI.MalformedURIException ex) {
            //This is a programming bug and should not occur! The URI is malformed!
            throw new SRMClientException("Unexpected error in NaiveSRMClient! Cannot create URI! "+ex);
        } catch (javax.xml.rpc.ServiceException ex) {
            //Something went wrong with the web service!
            throw new SRMClientException("Web Service problem! "+ex);
        } catch (java.rmi.RemoteException ex) {
            //something went wrong at low level communication with web service!
            throw new SRMClientException("Communication problem! "+ex);
        } catch (SOAPFaultException e) {
            //the remote service launched an exception! This is a programmatic exception remotely thrown, _not_ an error!
            throw new SRMClientException("The remote web service launched an exception! "+e);
        } catch (WSConversionException e) {
            //the data received fro mthe web service could not be translated into StoRM object model equivalents!
            throw new SRMClientException("The answer received from the WebService cannot be converted to StoRM object model! "+e);
        } catch (InvalidPutStatusAttributesException e) {
            //This is a programming bug and should not occur! For some reason, correctly translated WebService attributes
            //did not allow the correct creation of an SRMStatusOfPutRequestReply!
            throw new SRMClientException("Unexpected error in NaiveSRMClient! Cannot create a reply to return! "+e);
        } catch (Exception e) {
            //Catch any runtime exceptions, such as a time out!
            throw new SRMClientException("Unexpected runtime error in NaiveSRMClient! "+e);
        } //end synchronized!
    }

    /**
     * Method that returns an SRMPutDoneReply containing TReturnStatus with TStatusCode.SRM_SUCCESS,
     * and explanation string "DUMMY SUCCESS".
     */
    public SRMPutDoneReply srmPutDone(TRequestToken rt, VomsGridUser gu, TSURL toSURL) throws SRMClientException {
        return null;
/*        try {
            return new SRMPutDoneReply(new it.grid.storm.srm.types.TReturnStatus(it.grid.storm.srm.types.TStatusCode.SRM_SUCCESS,"DUMMY SUCCESS"));
        } catch (Exception e) {
            throw new SRMClientException();
        }*/
    }

    /**
     * Private auxiliary method that sets up a secure GSI connection for the given
     * GridUser to the specfied toSURL. The method returns the service interface.
     */
    private srmClientStubs.ISRM setUpGSI(VomsGridUser gu, it.grid.storm.srm.types.TSURL toSURL) throws javax.xml.rpc.ServiceException,
    GlobusCredentialException, GSSException, it.grid.storm.srm.types.InvalidTUserIDAttributeException {
        //get web service, setting HTTP and HTTPG as transport!
        Util.registerTransport();
        SimpleProvider provider  = new SimpleProvider();
        SimpleTargetedChain c = null;
        c = new SimpleTargetedChain(new GSIHTTPSender());
        provider.deployTransport("httpg",c);
        c = new SimpleTargetedChain(new HTTPSender());
        provider.deployTransport("http",c);
        srmClientStubs.SRMService sRMService = new srmClientStubs.SRMServiceLocator(provider);
        srmClientStubs.ISRM _srm = sRMService.getsrm();

        //set proxy in stub
        if (gu.getUserCredentials()==null) log.error("ERROR in NaiveSRMClient! No proxy present for "+gu.getDn());
        InputStream proxy = new ByteArrayInputStream(gu.getUserCredentials().getBytes()); //String containing the proxy seen as an input stream!
        GSSCredential globusProxy = new GlobusGSSCredentialImpl(new GlobusCredential(proxy) , GSSCredential.INITIATE_AND_ACCEPT); //GSSCredential containing the proxy!
        ((Stub) _srm)._setProperty(GSIHTTPTransport.GSI_CREDENTIALS,globusProxy); //set the proxy to be used during GSI connection!
        ((Stub) _srm)._setProperty(GSIHTTPTransport.GSI_AUTHORIZATION,NoAuthorization.getInstance()); //set the authorization that will be performed by the web service for the supplied credentails!
        ((Stub) _srm)._setProperty(GSIHTTPTransport.GSI_MODE,GSIHTTPTransport.GSI_MODE_NO_DELEG); //set the supply of both private and public key of credentials

        //set service endpoint address
        String sea = "httpg://" + toSURL.sfn().machine() + ":" + toSURL.sfn().port() + "/";
        ((Stub) _srm)._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY,sea);

        //return the interface
        return _srm;
    }

    /**
     * Private method that returns a String representation of srmClientStubs.SrmPrepareToPutResponse
     */
/*    private String srmPtPResponseToString(srmClientStubs.SrmPrepareToPutResponse response) {
        StringBuffer sb = new StringBuffer();
        if (response==null) {
            sb.append("srmClientStubs.SrmPrepareToPutResponse is null!");
        } else {
            sb.append("srmClientStubs.SrmPrepareToPutResponse:\n");
            srmClientStubs.TRequestToken stubrt = response.getRequestToken();
            if (stubrt==null) {
                sb.append("srmClientStubs.TRequestToken is null!\n");
            } else {
                sb.append("srmClientStubs.TRequestToken is ");
                sb.append(stubrt.getValue());
                sb.append("\n");
            }
            srmClientStubs.TReturnStatus stubretstat = response.getReturnStatus();
            if (stubretstat==null) {
                sb.append("srmClientStubs.TReturnStatus is null!\n");
            } else {
                sb.append("srmClientStubs.TReturnStatus has ");
                srmClientStubs.TStatusCode stubstatcode = stubretstat.getStatusCode();
                if (stubstatcode==null) {
                    sb.append("TStatusCode=null and ");
                } else {
                    sb.append("TStatusCode=");
                    sb.append(stubstatcode.getValue());
                    sb.append(" and ");
                }
                String aux = stubretstat.getExplanation();
                if (aux==null) {
                    sb.append("explanationString=null");
                } else {
                    sb.append("explanationString=");
                    sb.append(aux);
                }
                sb.append("\n");
            }
            srmClientStubs.ArrayOfTPutRequestFileStatus stubaprfs = response.getArrayOfFileStatuses();
            if (stubaprfs==null) {
                sb.append("srmClientStubs.ArrayOfTPutRequestFileStatus is null!\n");
            } else {
                srmClientStubs.TPutRequestFileStatus[] stubprfs = stubaprfs.getPutStatusArray();
                int arraysize = stubprfs.length;
                sb.append("srmClientStubs.ArrayOfTPutRequestFileStatus has ");
                sb.append(arraysize);
                sb.append(" elements\n");
                srmClientStubs.TPutRequestFileStatus stubaux;
                srmClientStubs.TReturnStatus stubauxretstat;
                srmClientStubs.TStatusCode stubauxstatcode;
                String auxString;
                for (int i=0; i<arraysize; i++) {
                    sb.append("Element ");
                    sb.append(i);
                    stubaux = stubprfs[i];
                    if (stubaux==null) {
                        sb.append(": null srmClientStubs.TPutRequestFileStatus!");
                    } else {
                        stubauxretstat = stubaux.getStatus();
                        if (stubauxretstat==null) {
                            sb.append(": srmClientStubs.TReturnStatus is null!");
                        } else {
                            sb.append(": srmClientStubs.TReturnStatus has ");
                            stubauxstatcode = stubauxretstat.getStatusCode();
                            if (stubauxstatcode==null) {
                                sb.append("TStatusCode=null and ");
                            } else {
                                sb.append("TStatusCode=");
                                sb.append(stubauxstatcode.getValue());
                                sb.append(" and ");
                            }
                            auxString = stubauxretstat.getExplanation();
                            if (auxString==null) {
                                sb.append("explanationString=null");
                            } else {
                                sb.append("explanationString=");
                                sb.append(auxString);
                            }
                        }
                    }
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }
*/
}
