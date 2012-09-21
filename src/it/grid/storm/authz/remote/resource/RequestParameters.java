package it.grid.storm.authz.remote.resource;

import it.grid.storm.authz.remote.Constants;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.jersey.server.impl.ResponseBuilderImpl;

class RequestParameters
{
    private static final Logger log = LoggerFactory.getLogger(RequestParameters.class);
    
    private final String DNDecoded, FQANSDecoded, filePathDecoded;
    private final boolean hasVomsExtension;

    RequestParameters(String filePath, String DN) throws WebApplicationException
    {
        log.info("Serving prepareToPut operation authorization on file '" + filePath + "\'");

        try
        {
            filePathDecoded = URLDecoder.decode(filePath, Constants.ENCODING_SCHEME);
            DNDecoded = URLDecoder.decode(DN, Constants.ENCODING_SCHEME);
        } catch(UnsupportedEncodingException e)
        {
            log.error("Unable to decode parameters. UnsupportedEncodingException : " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("Unable to decode paramethesr, unsupported encoding \'"
                    + Constants.ENCODING_SCHEME + "\'");
            throw new WebApplicationException(responseBuilder.build());
        }
        log.debug("Decoded filePath = " + filePathDecoded);
        log.debug("Decoded DN = " + DNDecoded);

        if (filePathDecoded == null || filePathDecoded.trim().equals("") || DNDecoded == null
                || DNDecoded.trim().equals(""))
        {
            log.error("Unable to evaluate permissions. Some parameters are missing : DN " + DNDecoded
                    + " filePath " + filePathDecoded);
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("Unable to evaluate permissions. Some parameters are missing");
            throw new WebApplicationException(responseBuilder.build());
        }
        hasVomsExtension = false;
        FQANSDecoded = null;
    }
    
    RequestParameters(String filePath, String DN, String FQANS) throws WebApplicationException
    {
        log.info("Serving prepareToPut operation authorization on file '" + filePath + "\'");

        try
        {
            filePathDecoded = URLDecoder.decode(filePath, Constants.ENCODING_SCHEME);
            DNDecoded = URLDecoder.decode(DN, Constants.ENCODING_SCHEME);
            FQANSDecoded = URLDecoder.decode(FQANS, Constants.ENCODING_SCHEME);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("Unable to decode parameters. UnsupportedEncodingException : " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("Unable to decode paramethesr, unsupported encoding \'" + Constants.ENCODING_SCHEME + "\'");
            throw new WebApplicationException(responseBuilder.build());
        }
        log.debug("Decoded filePath = " + filePathDecoded);
        log.debug("Decoded DN = " + DNDecoded);
        log.debug("Decoded FQANS = " + FQANSDecoded);
        
        if (filePathDecoded == null || filePathDecoded.trim().equals("") || DNDecoded == null || DNDecoded.trim().equals("")
                || FQANSDecoded == null || FQANSDecoded.trim().equals(""))
        {
            log.error("Unable to evaluate permissions. Some parameters are missing : DN " + DNDecoded
                    + " FQANS " + FQANSDecoded + " filePath " + filePathDecoded);
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("Unable to evaluate permissions. Some parameters are missing");
            throw new WebApplicationException(responseBuilder.build());
        }
        hasVomsExtension = true;
    }

    /**
     * @return the dNDecoded
     */
    public String getDNDecoded()
    {
        return DNDecoded;
    }

    /**
     * @return the fQANSDecoded
     */
    public String getFQANSDecoded()
    {
        return FQANSDecoded;
    }

    /**
     * @return the filePathDecoded
     */
    public String getFilePathDecoded()
    {
        return filePathDecoded;
    }

    /**
     * @return the hasVomsExtension
     */
    public boolean hasVomsExtension()
    {
        return hasVomsExtension;
    }
}
