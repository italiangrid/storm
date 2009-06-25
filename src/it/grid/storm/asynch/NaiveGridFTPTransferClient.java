package it.grid.storm.asynch;

import it.grid.storm.common.types.TransferProtocol;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.AbstractGridUser;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TTURL;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.globus.ftp.FeatureList;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.Session;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a GridFTP client; it is naive in that it makes no use
 * of extended features of GridFTP such as parallel streams and transfers between
 * third parties. Moreover, it does not share a data channel for multiple file
 * transfers. The performance, therefore, can be greatly improved upon!
 *
 * Moreover, to maintain compatibility with old GSIFTP servers, it sets
 * DataChannelAuthentication to none.
 *
 * @author  EGRID - ICTP Trieste
 * @version 2.0
 * @date    September, 2005
 */
public class NaiveGridFTPTransferClient implements GridFTPTransferClient {

    private static final Logger log = LoggerFactory.getLogger(NaiveGridFTPTransferClient.class);

    /**
     * Implementation of inherited put method: it executes a put of a file as per contract
     * specified in the interface. In particular GridFTPTransferClientException is thrown
     * if: the local TURL does not have file as protocol, or the remote one does not have
     * gsiftp as protocol; no credentials (proxy) were found, there were problems with the
     * proxy itself, or there were problems with the security framework that handles the
     * proxy; a generic input/output problem occured; the GridFTP server reported some
     * problems; there were client side problems.
     */
    public void putFile(GridUserInterface gu, TTURL local, TTURL remote) throws GridFTPTransferClientException {
        boolean localIsFile = (local.protocol()==TransferProtocol.FILE);
        boolean remoteIsGSIFTP = (remote.protocol()==TransferProtocol.GSIFTP);
        if (!localIsFile || !remoteIsGSIFTP) {
            throw new GridFTPTransferClientException("Unsupported local/remote protocol: local-is-file="+localIsFile+", remote-is-GSIFTP="+remoteIsGSIFTP);
        }
        String fullLocalFile = "/"+local.tfn().pfn().getValue();
        String fullRemoteFile = "/"+remote.tfn().pfn().getValue();
        try {
            InputStream proxy = new ByteArrayInputStream(((AbstractGridUser)gu).getUserCredentials().getBytes());
            int remotePort = 2811;
            if (!remote.tfn().port().isEmpty()) {
                remotePort = remote.tfn().port().toInt();
            }
            GridFTPClient client = new GridFTPClient(remote.tfn().machine().getValue(),remotePort);
            client.authenticate(new GlobusGSSCredentialImpl(new GlobusCredential(proxy) , GSSCredential.INITIATE_AND_ACCEPT));
            client.setType(Session.TYPE_IMAGE);
            FeatureList fl = client.getFeatureList();
            log.debug("NAIVE GSIFTP CLIENT: Remote server supports "+fl.toString());
            if (!fl.contains(FeatureList.DCAU)) {
                log.debug("NAIVE GSIFTP CLIENT: disabling DCAU");
                client.setLocalNoDataChannelAuthentication(); //compatibility with old GSIFTP server!
            }
            client.setClientWaitParams(Configuration.getInstance().getGridFTPTimeOut(),Session.DEFAULT_WAIT_DELAY);
            client.put(new File(fullLocalFile), fullRemoteFile, false);
            client.close();
            //} catch (InvalidTUserIDAttributeException e) {
            //    throw new GridFTPTransferClientException("No proxy found!");
        } catch (IOException e) {
            throw new GridFTPTransferClientException(e.toString());
        } catch (ServerException e) {
            throw new GridFTPTransferClientException(e.toString());
        } catch (ClientException e) {
            throw new GridFTPTransferClientException(e.toString());
        } catch (GlobusCredentialException e) {
            throw new GridFTPTransferClientException(e.toString());
        } catch (GSSException e) {
            throw new GridFTPTransferClientException(e.toString());
        } catch (Exception e) {
            //Catch any runtime exception!
            throw new GridFTPTransferClientException("Unexpected runtime error in NaiveGridFTPTransferClient! "+e);
        }
    }
}
