package it.grid.storm.namespace.model;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import it.grid.storm.namespace.remote.Constants.HttpPerms;

public class SAInfoTest {

	private static final Logger log = LoggerFactory.getLogger(SAInfoTest.class);

	private static final String JSON_STRING = "{\"name\":\"test.vo\",\"token\":\"TESTVO_TOKEN\",\"voname\":\"test.vo\",\"root\":\"/storage/test.vo\",\"storageclass\":\"T1D0\",\"stfnRoot\":[\"/test.vo\"],\"retentionPolicy\":\"CUSTODIAL\",\"accessLatency\":\"ONLINE\",\"protocols\":[\"xroot\",\"webdav\"],\"anonymous\":\"NOREAD\",\"availableNearlineSpace\":20000000,\"approachableRules\":[\"Fake-DN-Matching-Rule\"]}";

	private static final SAInfo saInfo;

	static {
		saInfo = new SAInfo();
		saInfo.setName("test.vo");
		saInfo.setToken("TESTVO_TOKEN");
		saInfo.setVoname("test.vo");
		saInfo.setRoot("/storage/test.vo");
		saInfo.setStorageclass("T1D0");
		saInfo.setStfnRoot(Lists.newArrayList("/test.vo"));
		saInfo.setRetentionPolicy(RetentionPolicy.CUSTODIAL.getRetentionPolicyName());
		saInfo.setAccessLatency(AccessLatency.ONLINE.getAccessLatencyName());
		saInfo.setProtocols(Lists.newArrayList("xroot", "webdav"));
		saInfo.setAnonymous(HttpPerms.NOREAD);
		saInfo.setAvailableNearlineSpace(20000000);
		saInfo.setApproachableRules(Lists.newArrayList("Fake-DN-Matching-Rule"));
	}

	@Test
	public void testWrite() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(saInfo);
		log.info("Object as JSON String: {}", jsonString);
		assertThat(jsonString.replaceAll(" ", ""), equalTo(JSON_STRING.replaceAll(" ", "")));
	}

	@Test
	public void testRead() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		SAInfo saInfoRead = mapper.readValue(JSON_STRING.getBytes(), SAInfo.class);
		log.info("sa info read {}", saInfoRead);
		assertThat(saInfoRead.getName(), equalTo(saInfo.getName()));
		assertThat(saInfoRead.getToken(), equalTo(saInfo.getToken()));
	}

	

}
