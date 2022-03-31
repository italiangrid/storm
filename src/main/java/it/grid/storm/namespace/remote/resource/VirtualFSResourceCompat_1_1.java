package it.grid.storm.namespace.remote.resource;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.namespace.remote.Constants;

/**
 * @author Michele Dibenedetto
 */
@Path("/" + Constants.RESOURCE + "/" + Constants.VERSION_1_1)
public class VirtualFSResourceCompat_1_1 {

  private static final Logger log = LoggerFactory.getLogger(VirtualFSResourceCompat_1_1.class);

  /**
   * @return
   */
  @GET
  @Path("/" + Constants.LIST_ALL_KEY)
  @Produces("text/plain")
  public String listVFS() {

    log.info("Serving VFS resource listing");
    String vfsListString = "";
    List<VirtualFS> vfsCollection = NamespaceDirector.getNamespace().getAllDefinedVFS();
    for (VirtualFS vfs : vfsCollection) {
      if (!vfsListString.equals("")) {
        vfsListString += Constants.VFS_LIST_SEPARATOR;
      }
      try {
        vfsListString += encodeVFS(vfs);
      } catch (NamespaceException e) {
        log.error("Unable to encode the virtual file system. NamespaceException : {}",
            e.getMessage());
        throw new WebApplicationException(Response.status(INTERNAL_SERVER_ERROR)
          .entity("Unable to encode the virtual file system")
          .build());
      }
    }
    return vfsListString;
  }

  /**
   * @param vfs
   * @return
   * @throws NamespaceException
   */
  private String encodeVFS(VirtualFS vfs) throws NamespaceException {

    String vfsEncoded = Constants.VFS_NAME_KEY + Constants.VFS_FIELD_MATCHER + vfs.getAliasName();
    vfsEncoded += Constants.VFS_FIELD_SEPARATOR;
    vfsEncoded += Constants.VFS_ROOT_KEY + Constants.VFS_FIELD_MATCHER + vfs.getRootPath();
    vfsEncoded += Constants.VFS_FIELD_SEPARATOR;
    List<MappingRule> mappingRules = vfs.getMappingRules();
    vfsEncoded += Constants.VFS_STFN_ROOT_KEY + Constants.VFS_FIELD_MATCHER;
    for (int i = 0; i < mappingRules.size(); i++) {
      MappingRule mappingRule = mappingRules.get(i);
      if (i > 0) {
        vfsEncoded += Constants.VFS_STFN_ROOT_SEPARATOR;
      }
      vfsEncoded += mappingRule.getStFNRoot();
    }
    Iterator<Protocol> protocolsIterator =
        vfs.getCapabilities().getAllManagedProtocols().iterator();
    if (protocolsIterator.hasNext()) {
      vfsEncoded += Constants.VFS_FIELD_SEPARATOR;
      vfsEncoded += Constants.VFS_ENABLED_PROTOCOLS_KEY;
      vfsEncoded += Constants.VFS_FIELD_MATCHER;
    }
    while (protocolsIterator.hasNext()) {
      vfsEncoded += protocolsIterator.next().getSchema();
      if (protocolsIterator.hasNext()) {
        vfsEncoded += Constants.VFS_ENABLED_PROTOCOLS_SEPARATOR;
      }
    }
    return vfsEncoded;
  }
}
