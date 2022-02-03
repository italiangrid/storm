package it.grid.storm.namespace.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.Namespace;
import it.grid.storm.namespace.NamespaceException;

public class NamespaceTest {

  @Before
  public void init() throws DOMException, ConfigurationException,
  ParserConfigurationException, SAXException, IOException, NamespaceException {
    ClassLoader classLoader = getClass().getClassLoader();
    File configFile = new File(classLoader.getResource("storm.properties").getFile());
    Configuration.init(configFile.getAbsolutePath());
    File namespaceFile = new File(classLoader.getResource("namespace.xml").getFile());
    Namespace.init(namespaceFile.getAbsolutePath(), false);
  }
  
  @Test
  public void checkGridftpEndpoints()  {
    Namespace ns = Namespace.getInstance();
    Set<Authority> gridftpEndpoints = ns.getManagedEndpoints(Protocol.GSIFTP);
    System.out.println(gridftpEndpoints);
    assertEquals(2, gridftpEndpoints.size());
    assertTrue(gridftpEndpoints.contains(new Authority("gridftp01.cnaf.infn.it", 2811)));
    assertTrue(gridftpEndpoints.contains(new Authority("gridftp02.cnaf.infn.it", 2811)));
  }

  @Test
  public void checkDavEndpoints()  {
    Namespace ns = Namespace.getInstance();
    Set<Authority> davEndpoints = ns.getManagedEndpoints(Protocol.HTTPS);
    davEndpoints.addAll(ns.getManagedEndpoints(Protocol.HTTP));
    System.out.println(davEndpoints);
    assertEquals(4, davEndpoints.size());
    assertTrue(davEndpoints.contains(new Authority("dav01.cnaf.infn.it", 8443)));
    assertTrue(davEndpoints.contains(new Authority("dav02.cnaf.infn.it", 8443)));
    assertTrue(davEndpoints.contains(new Authority("dav01.cnaf.infn.it", 8085)));
    assertTrue(davEndpoints.contains(new Authority("dav02.cnaf.infn.it", 8085)));
  }
}
