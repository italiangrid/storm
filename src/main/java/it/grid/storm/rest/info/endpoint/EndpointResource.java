package it.grid.storm.rest.info.endpoint;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.Namespace;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.rest.info.endpoint.model.EndpointInfo;
import it.grid.storm.rest.info.storageareas.model.SAInfo;

@Path("/info")
public class EndpointResource {

  private static final Logger log = LoggerFactory.getLogger(EndpointResource.class);

  private EndpointInfo endpoint;

  public EndpointResource() {

    this(Configuration.getInstance());
  }

  public EndpointResource(Configuration config) {

    endpoint = new EndpointInfo();
    endpoint.setSiteName(config.getSiteName());
    endpoint.setQualityLevel(config.getQualityLevel());
    endpoint.setVersion(getClass().getPackage().getImplementationVersion());

    List<VirtualFSInterface> vfsCollection = Namespace.getInstance().getAllDefinedVFS();
    Map<String, SAInfo> sas = Maps.newHashMap();

    for (VirtualFSInterface vfs : vfsCollection) {
      try {
        sas.put(vfs.getAliasName(), SAInfo.buildFromVFS(vfs));
      } catch (NamespaceException e) {
        log.error(e.getMessage(), e);
      }
    }
    endpoint.setStorageAreas(sas);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public EndpointInfo getEndpointInfo() {
    return endpoint;
  }
}
