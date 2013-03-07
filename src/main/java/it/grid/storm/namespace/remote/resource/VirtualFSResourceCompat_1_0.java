package it.grid.storm.namespace.remote.resource;


/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.namespace.remote.Constants;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;

/**
 * @author Michele Dibenedetto
 */
@Path("/" + Constants.RESOURCE + "/" + Constants.VERSION_1_0)
public class VirtualFSResourceCompat_1_0
{


    private static final Logger log = LoggerFactory.getLogger(VirtualFSResourceCompat_1_0.class);

    /**
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.LIST_ALL_KEY)
    @Produces("text/plain")
    public String listVFS() throws WebApplicationException
    {
        log.info("Serving VFS resource listing");
        String vfsListString = "";
        Collection<VirtualFSInterface> vfsCollection = null;
        try
        {
            vfsCollection = NamespaceDirector.getNamespace().getAllDefinedVFS();
        }
        catch (NamespaceException e)
        {
            log.error("Unable to retrieve virtual file system list. NamespaceException : " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("Unable to retrieve virtual file systems");
            throw new WebApplicationException(responseBuilder.build());
        }
        for (VirtualFSInterface vfs : vfsCollection)
        {
            if (!vfsListString.equals(""))
            {
                vfsListString += Constants.VFS_LIST_SEPARATOR;
            }
            try
            {
                vfsListString += encodeVFS(vfs);
            }
            catch (NamespaceException e)
            {
                log.error("Unable to encode the virtual file system. NamespaceException : " + e.getMessage());
                ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
                responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
                responseBuilder.entity("Unable to encode the virtual file system");
                throw new WebApplicationException(responseBuilder.build());
            }
        }
        return vfsListString;
    }


    /**
     * @param vfs
     * @return
     * @throws NamespaceException
     */
    private String encodeVFS(VirtualFSInterface vfs) throws NamespaceException
    {
        String vfsEncoded = Constants.VFS_NAME_KEY + Constants.VFS_FIELD_MATCHER + vfs.getAliasName();
        vfsEncoded += Constants.VFS_FIELD_SEPARATOR;
        vfsEncoded += Constants.VFS_ROOT_KEY + Constants.VFS_FIELD_MATCHER + vfs.getRootPath();
        vfsEncoded += Constants.VFS_FIELD_SEPARATOR;
        List<MappingRule> mappingRules = vfs.getMappingRules();
        vfsEncoded += Constants.VFS_STFN_ROOT_KEY + Constants.VFS_FIELD_MATCHER;
        for (int i = 0; i < mappingRules.size(); i++)
        {
            MappingRule mappingRule = mappingRules.get(i);
            if (i > 0)
            {
                vfsEncoded += Constants.VFS_STFN_ROOT_SEPARATOR;
            }
            vfsEncoded += mappingRule.getStFNRoot();
        }
        return vfsEncoded;
    }
}
