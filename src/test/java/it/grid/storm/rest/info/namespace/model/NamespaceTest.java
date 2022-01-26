package it.grid.storm.rest.info.namespace.model;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class NamespaceTest {

  @Test
  public void test() throws JsonParseException, JsonMappingException, IOException {

    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("namespace.xml").getFile());
    XmlMapper xmlMapper = new XmlMapper();
    Namespace info = xmlMapper.readValue(file, Namespace.class);
    Assert.assertEquals("TESTVO-FS", info.getFilesystems().get(0).getName());    
    ObjectMapper jsonMapper = new ObjectMapper();
    System.out.println(jsonMapper.writeValueAsString(info));
    
  }

}
