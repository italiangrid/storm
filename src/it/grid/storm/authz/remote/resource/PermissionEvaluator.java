package it.grid.storm.authz.remote.resource;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.path.model.PathOperation;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.authz.remote.Constants;
import it.grid.storm.catalogs.OverwriteModeConverter;
import it.grid.storm.common.types.InvalidStFNAttributeException;
import it.grid.storm.common.types.StFN;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.srm.types.TOverwriteMode;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.jersey.server.impl.ResponseBuilderImpl;

class PermissionEvaluator
{
    private static final Logger log = LoggerFactory.getLogger(PermissionEvaluator.class);
    
    public static Boolean isOverwriteAllowed()
    {
        return OverwriteModeConverter.getInstance().toSTORM(Configuration.getInstance().getDefaultOverwriteMode()).equals(TOverwriteMode.ALWAYS);
    }
    
    static Boolean evaluateVomsGridUserPermission(String DNDecoded, String FQANSDecoded,
            String filePathDecoded, PathOperation operation)
    {
        String[] FQANSArray = parseFQANS(FQANSDecoded);
        GridUserInterface gu = buildGridUser(DNDecoded, FQANSArray); 
        
        VirtualFSInterface fileVFS;
        try
        {
            fileVFS = NamespaceDirector.getNamespace().resolveVFSbyAbsolutePath(filePathDecoded);
        } catch(NamespaceException e)
        {
            log.error("Unable to determine a VFS that maps the requested file path \'" + filePathDecoded + "\'. NamespaceException: " + e.getMessage());
          ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
          responseBuilder.status(Response.Status.NOT_FOUND);
          responseBuilder.entity("Unable to determine file path\'s associated virtual file system");
          throw new WebApplicationException(responseBuilder.build());
        }
        if(!fileVFS.isApproachableByUser(gu))
        {
            log.debug("User\'" + gu + "\' not authorize to approach the requeste Storage Area \'" + fileVFS.getAliasName() + "\'");
            return new Boolean(false);    
        }
        StFN fileStFN = buildStFN(filePathDecoded, fileVFS);
        AuthzDecision decision = AuthzDirector.getPathAuthz().authorize(gu, operation, fileStFN);
        return evaluateDecision(decision);
    }
    
    /**
     * @param DNDecoded
     * @param FQANSDecoded
     * @param filePathDecoded
     * @param request
     * @return never null
     * @throws WebApplicationException
     */
    static Boolean evaluateVomsGridUserPermission(String DNDecoded, String FQANSDecoded,
            String filePathDecoded, SRMFileRequest request) throws WebApplicationException
    {
        String[] FQANSArray = parseFQANS(FQANSDecoded);
        GridUserInterface gu = buildGridUser(DNDecoded, FQANSArray); 
        
        VirtualFSInterface fileVFS;
        try
        {
            fileVFS = NamespaceDirector.getNamespace().resolveVFSbyAbsolutePath(filePathDecoded);
        } catch(NamespaceException e)
        {
            log.error("Unable to determine a VFS that maps the requested file path \'" + filePathDecoded + "\'. NamespaceException: " + e.getMessage());
          ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
          responseBuilder.status(Response.Status.NOT_FOUND);
          responseBuilder.entity("Unable to determine file path\'s associated virtual file system");
          throw new WebApplicationException(responseBuilder.build());
        }
        if(!fileVFS.isApproachableByUser(gu))
        {
            log.debug("User\'" + gu + "\' not authorize to approach the requeste Storage Area \'" + fileVFS.getAliasName() + "\'");
            return new Boolean(false);    
        }
        StFN fileStFN = buildStFN(filePathDecoded, fileVFS);
        AuthzDecision decision = AuthzDirector.getPathAuthz().authorize(gu, request, fileStFN);
        
        return evaluateDecision(decision);
    }
    
    static Boolean evaluateAnonymousPermission(String filePathDecoded, PathOperation request)
    {
        VirtualFSInterface fileVFS;
        try
        {
            fileVFS = NamespaceDirector.getNamespace().resolveVFSbyAbsolutePath(filePathDecoded);
        } catch(NamespaceException e)
        {
            log.error("Unable to determine a VFS that maps the requested file path \'" + filePathDecoded + "\'. NamespaceException: " + e.getMessage());
          ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
          responseBuilder.status(Response.Status.NOT_FOUND);
          responseBuilder.entity("Unable to determine file path\'s associated virtual file system");
          throw new WebApplicationException(responseBuilder.build());
        }
        if(!fileVFS.isApproachableByAnonymous())
        {
            log.debug("The requeste Storage Area \'" + fileVFS.getAliasName() + "\' is not appoachable by anonymous users");
            return new Boolean(false);    
        }
        StFN fileStFN = buildStFN(filePathDecoded, fileVFS);
        AuthzDecision decision = AuthzDirector.getPathAuthz().authorizeAnonymous(request, fileStFN);
        
        return evaluateDecision(decision);
    }
    
    static Boolean evaluateAnonymousPermission(String filePathDecoded, SRMFileRequest request)
    {
        VirtualFSInterface fileVFS;
        try
        {
            fileVFS = NamespaceDirector.getNamespace().resolveVFSbyAbsolutePath(filePathDecoded);
        } catch(NamespaceException e)
        {
            log.error("Unable to determine a VFS that maps the requested file path \'" + filePathDecoded + "\'. NamespaceException: " + e.getMessage());
          ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
          responseBuilder.status(Response.Status.NOT_FOUND);
          responseBuilder.entity("Unable to determine file path\'s associated virtual file system");
          throw new WebApplicationException(responseBuilder.build());
        }
        if(!fileVFS.isApproachableByAnonymous())
        {
            log.debug("The requeste Storage Area \'" + fileVFS.getAliasName() + "\' is not appoachable by anonymous users");
            return new Boolean(false);    
        }
        StFN fileStFN = buildStFN(filePathDecoded, fileVFS);
        AuthzDecision decision = AuthzDirector.getPathAuthz().authorizeAnonymous(request, fileStFN);
        
        return evaluateDecision(decision);
    }
    
    private static Boolean evaluateDecision(AuthzDecision decision)
    {
        if (decision.equals(AuthzDecision.PERMIT))
        {
            return new Boolean(true);
        }
        else
        {
            if (decision.equals(AuthzDecision.DENY))
            {
                return new Boolean(false);
            }
            else
            {
                if (decision.equals(AuthzDecision.INDETERMINATE))
                {
                    log.warn("Authorization decision is INDETERMINATE! Unable to determine authorization of the user to perform requested operation on the resource");
                    return new Boolean(false);
                }
                else
                {
                    log.warn("Authorization decision has an unknown value \'"
                            + decision
                            + "\' ! Unable to determine authorization of the user to perform requested operation on the resource");
                    return new Boolean(false);
                }
            }
        }
    }

    static StFN buildStFN(String filePathDecoded, VirtualFSInterface fileVFS) throws WebApplicationException
    {
        String VFSRootPath;
        String VFSStFNRoot;
        try
        {
            if(fileVFS != null)
            {
                
                VFSRootPath = fileVFS.getRootPath();
                if(VFSRootPath == null)
                {
                    log.error("Unable to build StFN for path \'" + filePathDecoded + "\'. VFS: " + fileVFS.getAliasName() + " has null RootPath");
                    ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
                    responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
                    responseBuilder.entity("Unable to build StFN for path the provided path");
                    throw new WebApplicationException(responseBuilder.build());
                }
                if(!VFSRootPath.startsWith("/"))
                {
                    VFSRootPath = "/" + VFSRootPath;
                }
                if(VFSRootPath.endsWith("/"))
                {
                    VFSRootPath = VFSRootPath.substring(0, VFSRootPath.length() - 1);
                }
                log.debug("Chosen VFSRootPath " + VFSRootPath);
                List<MappingRule> VFSMappingRules = fileVFS.getMappingRules();
                if(VFSMappingRules != null && VFSMappingRules.size() > 0)
                {
                    VFSStFNRoot = VFSMappingRules.get(0).getStFNRoot();
                    if(VFSStFNRoot == null)
                    {
                        log.error("Unable to build StFN for path \'" + filePathDecoded + "\'. VFS: " + fileVFS.getAliasName() + " has null StFNRoot");
                        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
                        responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
                        responseBuilder.entity("Unable to build StFN for path the provided path");
                        throw new WebApplicationException(responseBuilder.build());
                    }
                    if(!VFSStFNRoot.startsWith("/"))
                    {
                        VFSStFNRoot = "/" + VFSStFNRoot;
                    }
                    if(VFSStFNRoot.endsWith("/"))
                    {
                        VFSStFNRoot = VFSStFNRoot.substring(0, VFSStFNRoot.length() - 1);
                    }
                    log.debug("Chosen StFNRoot " + VFSStFNRoot);
                }
                else
                {
                    log.error("Unable to determine the StFNRoot for file path's VFS. VFSMappingRules is empty!");
                    ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
                    responseBuilder.status(Response.Status.NOT_FOUND);
                    responseBuilder.entity("Unable to determine the StFNRoot for file path's VFS");
                    throw new WebApplicationException(responseBuilder.build());
                }
            }
            else
            {
                log.error("None of the VFS maps the requested file path \'" + filePathDecoded + "\'. fileVFS is null!");
                ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
                responseBuilder.status(Response.Status.NOT_FOUND);
                responseBuilder.entity("Unable to determine file path\'s associated virtual file system");
                throw new WebApplicationException(responseBuilder.build());
            }
        }
        catch (NamespaceException e)
        {
            log.error("Unable to determine a VFS that maps the requested file path \'" + filePathDecoded + "\'. NamespaceException: " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.NOT_FOUND);
            responseBuilder.entity("Unable to determine file path\'s associated virtual file system");
            throw new WebApplicationException(responseBuilder.build());
        }
        if(!filePathDecoded.startsWith(VFSRootPath))
        {
            log.error("The provided file path does not starts with the VFSRoot of its VFS");
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("The provided file path does not starts with the VFSRoot of its VFS");
            throw new WebApplicationException(responseBuilder.build());
        }
        String fileStFNpath = VFSStFNRoot + filePathDecoded.substring(VFSRootPath.length(), filePathDecoded.length());
        try
        {
            return StFN.make(fileStFNpath);
        }
        catch (InvalidStFNAttributeException e)
        {
            log.error("Unable to build StFN for path \'" + fileStFNpath + "\'. InvalidStFNAttributeException: " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("Unable to determine file path\'s associated virtual file system");
            throw new WebApplicationException(responseBuilder.build());
        }
    }


    static GridUserInterface buildGridUser(String DNDecoded, String[] FQANSArray)
    {
        try
        {
            if(FQANSArray == null || FQANSArray.length == 0)
            {
                return loadGridUser(DNDecoded);
            }
            else
            {
                return loadVomsGridUser(DNDecoded, FQANSArray);
            }
            
        } catch(IllegalArgumentException e)
        {
            // never thrown
            log.error("Unable to build the GridUserInterface object for DN \'" + DNDecoded
                    + "\' and FQANS \'" + Arrays.toString(FQANSArray) + "\'. IllegalArgumentException: "
                    + e.getMessage());
            ResponseBuilderImpl builder = new ResponseBuilderImpl();
            builder.status(Response.Status.BAD_REQUEST);
            builder.entity("Unable to build a GridUser for DN \'" + DNDecoded + "\' and FQANS \'"
                    + Arrays.toString(FQANSArray) + "\'. Missing argument(s)");
            throw new WebApplicationException(builder.build());
        }
    }

    /**
     * @param fQANS
     * @return
     */
    static String[] parseFQANS(String fQANS)
    {
        if(fQANS == null)
        {
            return new String[0];
        }
        return fQANS.trim().split(Constants.FQANS_SEPARATOR);
    }

    /**
     * Creates a GridUserInterface from the provided DN and FQANS
     * 
     * @param dn
     * @param fqansStringVector
     * @return the VOMS grid user corresponding to the provided parameters. never null
     * @throws IllegalArgumentException
     */
    static GridUserInterface loadVomsGridUser(String dn, String[] fqansStringVector) throws IllegalArgumentException
    {
        if(dn == null || fqansStringVector == null || fqansStringVector.length == 0)
        {
            log.error("Received invalid arguments DN parameter in loadVomsGridUser!");
            throw new IllegalArgumentException("Received null DN parameter");
        }
        
        FQAN[] fqansVector = new FQAN[fqansStringVector.length];
        for (int i = 0; i < fqansStringVector.length; i++)
        {
            fqansVector[i] = new FQAN(fqansStringVector[i]);
        }
        GridUserInterface gridUser = null;
        try
        {
            gridUser = GridUserManager.makeVOMSGridUser(dn, fqansVector);
        }
        catch (IllegalArgumentException e)
        {
            log.error("Unexpected error on voms grid user creation. Contact StoRM Support : IllegalArgumentException "
                      + e.getMessage());
        }
        return gridUser;
    }
    
    /**
     * Creates a GridUserInterface from the provided DN
     * 
     * @param dn
     * @return the grid user corresponding to the provided parameter. never null
     * @throws IllegalArgumentException
     */
    static GridUserInterface loadGridUser(String dn) throws IllegalArgumentException
    {
        if(dn == null)
        {
            log.error("Received null DN parameter in loadVomsGridUser!");
            throw new IllegalArgumentException("Received null DN parameter");
        }
        return GridUserManager.makeGridUser(dn);
    }

}
