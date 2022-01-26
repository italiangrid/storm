package it.grid.storm.rest.info.namespace;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import it.grid.storm.config.Configuration;
import it.grid.storm.rest.info.namespace.model.Namespace;

@Path("/info/namespace")
public class NamespaceInfoEndpoint {

  private Namespace info;
  
  public NamespaceInfoEndpoint() throws JsonParseException, JsonMappingException, IOException {

    buildNamespace();
  }

  private void buildNamespace() throws JsonParseException, JsonMappingException, IOException {

    // read XML from namespace.xml and load it into Namespace.class
    File namespaceFile = new File(Configuration.getInstance().getNamespaceConfigFilePath());
    XmlMapper xmlMapper = new XmlMapper();
    info = xmlMapper.readValue(namespaceFile, Namespace.class);
  }

  @GET
  @Produces(APPLICATION_JSON)
  public Namespace getNamespace() {

    return info;
  }

}
