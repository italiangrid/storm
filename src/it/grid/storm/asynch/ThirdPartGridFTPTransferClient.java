package it.grid.storm.asynch;

import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.common.types.TransferProtocol;
import it.grid.storm.srm.types.InvalidTUserIDAttributeException;

import org.globus.ftp.GridFTPClient;
import org.globus.ftp.Session;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.gsi.gssapi.GlobusGSSException;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import java.io.*;
import org.globus.ftp.MarkerListener;
import org.globus.ftp.ByteRangeList;
import org.globus.ftp.Marker;
import org.globus.ftp.GridFTPRestartMarker;
import org.globus.ftp.exception.PerfMarkerException;
import org.globus.ftp.PerfMarker;
import org.globus.ftp.GridFTPSession;
import org.globus.ftp.RetrieveOptions;
import org.apache.log4j.Logger;

/**
 * <p>Title: ThirdPartGridFTPTransferClient </p>
 *
 * <p>Description: to be defined</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN - CNAF </p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 * @date  18 March, 2006
 */

public class ThirdPartGridFTPTransferClient implements GridFTPTransferClient {

    private static Logger log = Logger.getLogger("asynch");

    /**
     *
     * @param gu VomsGridUser
     * @param source TTURL
     * @param destination TTURL
     * @throws GridFTPTransferClientException
     */
    public void putFile(VomsGridUser gu, TTURL source, TTURL destination) throws GridFTPTransferClientException
    {

        log.debug(" --- Third part copy --- ");

        boolean firstPart = (source.protocol()==TransferProtocol.GSIFTP);
        boolean secondPart = (destination.protocol()==TransferProtocol.GSIFTP);
        if (!firstPart||!secondPart) {
            throw new GridFTPTransferClientException(
                    "The third part gridFTP trasfer requires both TURL in GSIFTP protocol! (first ="+firstPart+
                    ") second ="+secondPart+")");
        }

        String fullSourceFile = "/"+source.tfn().pfn().getValue();
        String fullDestinationFile = "/"+destination.tfn().pfn().getValue();

       log.debug("source file: "+fullSourceFile);
       log.debug("destin file: "+fullDestinationFile);

       //Smart implementation of Marker Listener
       MarkerListenerImpl listener = new MarkerListenerImpl();

        try {
            InputStream proxy = new ByteArrayInputStream(gu.getUserCredentials().getBytes());
	    GSSCredential cred = new GlobusGSSCredentialImpl(new GlobusCredential(proxy), GSSCredential.INITIATE_AND_ACCEPT);

	    log.debug("Created GSS Credential from proxy");

	    //Set up of remote Source GridFTP
	    String remoteSourceHost = source.tfn().machine().getValue();
	    int remoteSourcePort = source.tfn().port().toInt();
	    log.debug("remote source GridFTP : "+remoteSourceHost+":"+remoteSourcePort);
            GridFTPClient sourceClient = new GridFTPClient(remoteSourceHost, remoteSourcePort);
            setParams(sourceClient,cred);

            //Set up of remote Destination GridFTP
	    String remoteDestinationHost = destination.tfn().machine().getValue();
	    int remoteDestinationPort = destination.tfn().port().toInt();
	    log.debug("remote destination GridFTP : "+remoteDestinationHost+":"+remoteDestinationPort);
	    GridFTPClient destClient = new GridFTPClient(remoteDestinationHost, remoteDestinationPort);
            setParams(destClient,cred);

            //Retrieve Parallelism of transfer from Configuration file
	    //org.globus.ftp.test.gridftp.parallelism=6
            int parallelism = 6;
	    sourceClient.setOptions(new RetrieveOptions(parallelism));
            log.debug(" Transfer parallelism : "+parallelism);

             //Execute the transfer
	    log.debug(" Starting transfer ... ");
            sourceClient.extendedTransfer(fullSourceFile, destClient, fullDestinationFile, listener);
            log.debug(" ... transfer ended.");

	    //Close the session of transfer
	    sourceClient.close();
            destClient.close();
	}
        catch (IOException e) {
	    log.warn("IO Except",e);
            throw new GridFTPTransferClientException(e.toString());
        }
        catch (ServerException e) {
	    log.warn("Server Except",e);
            throw new GridFTPTransferClientException(e.toString());
        }
        catch (ClientException e) {
	    log.warn("Client Except",e);
            throw new GridFTPTransferClientException(e.toString());
        }
        catch (GlobusCredentialException e) {
	    log.warn("Credential Except",e);
            throw new GridFTPTransferClientException(e.toString());
        }
        catch (GSSException e) {
	    log.warn("GSS Except",e);
            throw new GridFTPTransferClientException(e.toString());
        }
        catch (Exception e) {
	    log.warn("Generic Except",e);
            //Catch any runtime exception!
            throw new GridFTPTransferClientException("Unexpected runtime error in ThirdPartGridFTPTransferClient! "+e);
        }
    }


    void setParams(GridFTPClient client, GSSCredential cred)
	  throws Exception{
	  client.authenticate(cred);
	  client.setProtectionBufferSize(16384);
	  client.setType(GridFTPSession.TYPE_IMAGE);
	  client.setMode(GridFTPSession.MODE_EBLOCK);
      }


      /**
       *
       * <p>Title: </p>
       *
       * <p>Description: </p>
       *
       * <p>Copyright: Copyright (c) 2005</p>
       *
       * <p>Company: </p>
       *
       * @author not attributable
       * @version 1.0
       */
    class MarkerListenerImpl implements MarkerListener {

        public ByteRangeList list;

        public MarkerListenerImpl()
        {
            list = new ByteRangeList();
        }


        public void markerArrived(Marker m)
        {
            if (m instanceof GridFTPRestartMarker) {
                restartMarkerArrived( (GridFTPRestartMarker)m);
            }
            else if (m instanceof PerfMarker) {
                perfMarkerArrived( (PerfMarker)m);
            }
            else {
                log.warn("Received unsupported marker type");
            }
        };

        private void restartMarkerArrived(GridFTPRestartMarker marker)
        {
            log.info("--> restart marker arrived:");
            list.merge(marker.toVector());
            log.info("Current transfer state: "+list.toFtpCmdArgument());
        }


        private void perfMarkerArrived(PerfMarker marker)
        {
            log.info("--> perf marker arrived");
            // time stamp
            log.info("Timestamp = "+marker.getTimeStamp());

            // stripe index
            if (marker.hasStripeIndex()) {
                try {
                    log.info("Stripe index ="+marker.getStripeIndex());
                }
                catch (PerfMarkerException e) {
                    log.warn(e.toString());
                }
            }
            else {
                log.info("Stripe index: not present");
            }

            // stripe bytes transferred
            if (marker.hasStripeBytesTransferred()) {
                try {
                    log.info("Stripe bytes transferred = "+marker.getStripeBytesTransferred());
                }
                catch (PerfMarkerException e) {
                    log.warn(e.toString());
                }
            }
            else {
                log.info("Stripe Bytes Transferred: not present");
            }

            // total stripe count
            if (marker.hasTotalStripeCount()) {
                try {
                    log.info("Total stripe count = "+marker.getTotalStripeCount());
                }
                catch (PerfMarkerException e) {
                    log.warn(e.toString());
                }
            }
            else {
                log.info("Total stripe count: not present");
            }
        } //PerfMarkerArrived
    } //class MarkerListenerImpl

}
