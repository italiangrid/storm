package it.grid.storm.rest.info.namespace.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonPropertyOrder({"id", "schema", "hostname", "port"})
public class Protocol {

  @JacksonXmlProperty(isAttribute = true, localName = "name")
  String name;

  @JacksonXmlProperty(localName = "id")
  Integer id;
  @JacksonXmlProperty(localName = "schema")
  String schema;
  @JacksonXmlProperty(localName = "hostname")
  String hostname;
  @JacksonXmlProperty(localName = "port")
  Integer port;

}
