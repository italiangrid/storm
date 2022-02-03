package it.grid.storm.rest.info.endpoint;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.Namespace;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.model.Authority;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.rest.info.endpoint.model.EndpointInfo;
import it.grid.storm.rest.info.storageareas.model.SAInfo;

@Path("/info")
public class EndpointResource {

  private static final Logger log = LoggerFactory.getLogger(EndpointResource.class);

  private EndpointInfo endpoint;

  public EndpointResource() {

    this(Configuration.getInstance(), Namespace.getInstance());
  }

  public EndpointResource(Configuration config, Namespace ns) {

    endpoint = new EndpointInfo();
    endpoint.setSiteName(config.getSiteName());
    endpoint.setQualityLevel(config.getQualityLevel());
    String version = getClass().getPackage().getImplementationVersion();
    endpoint.setVersion(version != null ? version : "unknown");
    endpoint.setVos(ns.getSupportedVOs());
    endpoint.setSrmEndpoints(config.getSrmEndpoints());
    endpoint.setGridftpEndpoints(ns.getManagedEndpoints(Protocol.GSIFTP));
    Set<Authority> davEndpoints = Sets.newHashSet();
    davEndpoints.addAll(ns.getManagedEndpoints(Protocol.HTTPS));
    davEndpoints.addAll(ns.getManagedEndpoints(Protocol.HTTP));
    endpoint.setDavEndpoints(davEndpoints);
    Set<Authority> xrootEndpoints = Sets.newHashSet();
    xrootEndpoints.addAll(ns.getManagedEndpoints(Protocol.XROOT));
    xrootEndpoints.addAll(ns.getManagedEndpoints(Protocol.ROOT));
    endpoint.setXrootEndpoints(xrootEndpoints);

    Map<String, SAInfo> sas = Maps.newHashMap();
    ns.getAllDefinedVFS().forEach(vfs -> {
      try {
        sas.put(vfs.getAliasName(), SAInfo.buildFromVFS(vfs));
      } catch (NamespaceException e) {
        log.error(e.getMessage(), e);
      }
    });
    endpoint.setStorageAreas(sas);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public EndpointInfo getEndpointInfo() {
    return endpoint;
  }
}
