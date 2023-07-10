/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.remote.resource;

import com.google.common.collect.Maps;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.model.SAInfo;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.namespace.remote.Constants;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Michele Dibenedetto */
@Path("/" + Constants.RESOURCE + "/" + Constants.VERSION)
public class VirtualFSResource {

  private static final Logger log = LoggerFactory.getLogger(VirtualFSResource.class);

  /** @return */
  @GET
  @Path("/" + Constants.LIST_ALL_VFS)
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, SAInfo> listVFS() {

    log.debug("Serving VFS resource listing");
    List<VirtualFS> vfsCollection = NamespaceDirector.getNamespace().getAllDefinedVFS();
    Map<String, SAInfo> output = Maps.newHashMap();

    for (VirtualFS vfs : vfsCollection) {
      try {
        output.put(vfs.getAliasName(), SAInfo.buildFromVFS(vfs));
      } catch (NamespaceException e) {
        log.error(e.getMessage());
      }
    }

    return output;
  }
}
