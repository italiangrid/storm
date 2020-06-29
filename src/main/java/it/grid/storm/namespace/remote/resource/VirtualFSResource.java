package it.grid.storm.namespace.remote.resource;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.SAInfo;
import it.grid.storm.namespace.remote.Constants;

/**
 * @author Michele Dibenedetto
 */
@Path("/" + Constants.RESOURCE + "/" + Constants.VERSION)
public class VirtualFSResource {

  private static final Logger log = LoggerFactory.getLogger(VirtualFSResource.class);

  /**
   * @return
   * @throws WebApplicationException
   */
  @GET
  @Path("/" + Constants.LIST_ALL_VFS)
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, SAInfo> listVFS() throws WebApplicationException {

    log.debug("Serving VFS resource listing");
    Collection<VirtualFSInterface> vfsCollection = null;
    try {
      vfsCollection = NamespaceDirector.getNamespace().getAllDefinedVFS();
    } catch (NamespaceException e) {
      log.error(
          "Unable to retrieve virtual file system list. NamespaceException : " + e.getMessage());
      throw new WebApplicationException(Response.status(INTERNAL_SERVER_ERROR)
        .entity("Unable to retrieve virtual file systems")
        .build());
    }
    Map<String, SAInfo> output = new HashMap<String, SAInfo>();
    for (VirtualFSInterface vfs : vfsCollection) {
      try {
        output.put(vfs.getAliasName(), SAInfo.buildFromVFS(vfs));
      } catch (NamespaceException e) {
        log.error(e.getMessage());
      }
    }

    return output;
  }

}
