package it.grid.storm.rest.info;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.grid.storm.config.Configuration;
import it.grid.storm.config.model.v2.QualityLevel;
import it.grid.storm.config.model.v2.SrmEndpoint;
import it.grid.storm.namespace.Namespace;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.model.AccessLatency;
import it.grid.storm.namespace.model.Authority;
import it.grid.storm.namespace.model.RetentionPolicy;
import it.grid.storm.namespace.model.StorageClassType;
import it.grid.storm.rest.info.endpoint.EndpointResource;
import it.grid.storm.rest.info.endpoint.model.EndpointInfo;
import it.grid.storm.rest.info.storageareas.model.HttpPerms;
import it.grid.storm.rest.info.storageareas.model.SAInfo;
import jersey.repackaged.com.google.common.collect.Lists;

public class EndpointResourceTest {

  @Before
  public void init() throws DOMException, ConfigurationException, ParserConfigurationException,
      SAXException, IOException, NamespaceException {
    ClassLoader classLoader = getClass().getClassLoader();
    File configFile = new File(classLoader.getResource("storm.properties").getFile());
    Configuration.init(configFile.getAbsolutePath());
    File namespaceFile = new File(classLoader.getResource("namespace.xml").getFile());
    Namespace.init(namespaceFile.getAbsolutePath(), false);
  }

  @Test
  public void getInfoTest() throws JsonProcessingException {
    EndpointResource er =
        new EndpointResource(Configuration.getInstance(), Namespace.getInstance());
    EndpointInfo info = er.getEndpointInfo();
    assertEquals("StoRM test", info.getSiteName());
    assertEquals(QualityLevel.PRODUCTION, info.getQualityLevel());
    assertEquals(2, info.getSrmEndpoints().size());
    assertTrue(info.getSrmEndpoints().contains(new SrmEndpoint("storm-fe01.example", 8444)));
    assertTrue(info.getSrmEndpoints().contains(new SrmEndpoint("storm-fe02.example", 8445)));
    assertEquals(2, info.getGridftpEndpoints().size());
    assertTrue(info.getGridftpEndpoints().contains(new Authority("gridftp01.cnaf.infn.it", 2811)));
    assertTrue(info.getGridftpEndpoints().contains(new Authority("gridftp02.cnaf.infn.it", 2811)));
    assertEquals(4, info.getDavEndpoints().size());
    assertTrue(info.getDavEndpoints().contains(new Authority("dav01.cnaf.infn.it", 8443)));
    assertTrue(info.getDavEndpoints().contains(new Authority("dav02.cnaf.infn.it", 8443)));
    assertTrue(info.getDavEndpoints().contains(new Authority("dav01.cnaf.infn.it", 8085)));
    assertTrue(info.getDavEndpoints().contains(new Authority("dav02.cnaf.infn.it", 8085)));
    assertEquals(2, info.getVos().size());
    assertTrue(info.getVos().contains("test.vo"));
    assertTrue(info.getVos().contains("test.vo.2"));
    assertEquals(7, info.getStorageAreas().size());
    assertTrue(info.getStorageAreas().keySet().contains("TESTVO-FS"));
    assertTrue(info.getStorageAreas().keySet().contains("TESTVO2-FS"));
    assertTrue(info.getStorageAreas().keySet().contains("TESTVOBIS-FS"));
    assertTrue(info.getStorageAreas().keySet().contains("NOAUTH-FS"));
    assertTrue(info.getStorageAreas().keySet().contains("TAPE-FS"));
    assertTrue(info.getStorageAreas().keySet().contains("NESTED-FS"));
    SAInfo sa = info.getStorageAreas().get("TESTVO-FS");
    assertEquals("TESTVO-FS", sa.getName());
    assertEquals("TESTVO_TOKEN", sa.getToken());
    assertEquals(1, sa.getVos().size());
    assertEquals("test.vo", sa.getVos().get(0));
    assertEquals("/storage/test.vo", sa.getRootPath());
    assertEquals(StorageClassType.T0D1, sa.getStorageClass());
    assertEquals(1, sa.getAccessPoints().size());
    assertEquals("/test.vo", sa.getAccessPoints().get(0));
    assertEquals(RetentionPolicy.replica, sa.getRetentionPolicy());
    assertEquals(AccessLatency.online, sa.getAccessLatency());
    assertEquals(6, sa.getProtocols().size());
    assertTrue(sa.getProtocols()
      .containsAll(Lists.newArrayList("xroot", "https", "http", "root", "gsiftp", "file")));
    assertEquals(HttpPerms.NOREAD, sa.getAnonymous());
    assertEquals(0, sa.getAvailableNearlineSpace());
    assertEquals(1, sa.getApproachableRules().size());
    assertEquals("vo:test.vo", sa.getApproachableRules().get(0));

    ObjectMapper mapper = new ObjectMapper();
    System.out.println(mapper.writeValueAsString(info));
  }
}
