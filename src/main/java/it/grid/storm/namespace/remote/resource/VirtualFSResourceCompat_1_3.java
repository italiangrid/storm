/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.remote.resource;

import com.google.common.collect.Maps;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.model.SAInfoV13;
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
@Path("/" + Constants.RESOURCE + "/" + Constants.VERSION_1_3)
public class VirtualFSResourceCompat_1_3 {

  private static final Logger log = LoggerFactory.getLogger(VirtualFSResourceCompat_1_3.class);

  /** @return */
  @GET
  @Path("/" + Constants.LIST_ALL_VFS)
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, SAInfoV13> listVFS() {

    log.debug("Serving VFS resource listing");
    List<VirtualFS> vfsCollection = NamespaceDirector.getNamespace().getAllDefinedVFS();
    Map<String, SAInfoV13> output = Maps.newHashMap();

    for (VirtualFS vfs : vfsCollection) {
      try {
        output.put(vfs.getAliasName(), SAInfoV13.buildFromVFS(vfs));
      } catch (NamespaceException e) {
        log.error(e.getMessage());
      }
    }

    return output;
  }
}
