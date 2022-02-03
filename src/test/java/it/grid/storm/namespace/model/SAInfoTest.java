package it.grid.storm.namespace.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.grid.storm.rest.info.storageareas.model.HttpPerms;
import it.grid.storm.rest.info.storageareas.model.SAInfo;

public class SAInfoTest {

  private static final Logger log = LoggerFactory.getLogger(SAInfoTest.class);

  private static final String JSON_STRING =
      "{\"name\":\"test.vo\",\"token\":\"TESTVO_TOKEN\",\"vos\":[\"test.vo\"],\"rootPath\":\"/storage/test.vo\",\"storageClass\":\"T1D0\",\"accessPoints\":[\"/test.vo\"],\"retentionPolicy\":\"custodial\",\"accessLatency\":\"online\",\"protocols\":[\"xroot\",\"webdav\"],\"anonymous\":\"NOREAD\",\"availableNearlineSpace\":20000000,\"approachableRules\":[\"Fake-DN-Matching-Rule\"]}";

  private static final SAInfo saInfo;

  static {
    saInfo = new SAInfo();
    saInfo.setName("test.vo");
    saInfo.setToken("TESTVO_TOKEN");
    saInfo.addVo("test.vo");
    saInfo.setRoot("/storage/test.vo");
    saInfo.setStorageClass(StorageClassType.T1D0);
    saInfo.addAccessPoint("/test.vo");
    saInfo.setRetentionPolicy(RetentionPolicy.custodial);
    saInfo.setAccessLatency(AccessLatency.online);
    saInfo.addProtocol("xroot");
    saInfo.addProtocol("webdav");
    saInfo.setAnonymous(HttpPerms.NOREAD);
    saInfo.setAvailableNearlineSpace(20000000);
    saInfo.addApproachableRule("Fake-DN-Matching-Rule");
  }

  @Test
  public void testWrite() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = mapper.writeValueAsString(saInfo);
    log.info("Object as JSON String: {}", jsonString);
    assertEquals(jsonString.replaceAll(" ", ""), JSON_STRING.replaceAll(" ", ""));
  }

  @Test
  public void testRead() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    SAInfo saInfoRead = mapper.readValue(JSON_STRING.getBytes(), SAInfo.class);
    log.info("sa info read {}", saInfoRead);
    assertEquals(saInfoRead.getName(), saInfo.getName());
    assertEquals(saInfoRead.getToken(), saInfo.getToken());
  }

}
