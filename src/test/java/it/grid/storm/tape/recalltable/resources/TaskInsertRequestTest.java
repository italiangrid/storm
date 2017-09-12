package it.grid.storm.tape.recalltable.resources;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TaskInsertRequestTest {

	private JSONObject getTaskInsertRequestAsJsonObject() throws JSONException {

		JSONObject j = new JSONObject();
		j.put("stfn", "/fake.vo/fake/path/to/file.dat");
		j.put("retryAttempts", 0);
		j.put("voName", "fake.vo");
		j.put("pinLifetime", 1223123);
		j.put("userId", "test-user");
		return j;
	}

	@Test
	public void testConstructorWithMapper() throws JsonParseException, JsonMappingException, IOException, JSONException {
		ObjectMapper mapper = new ObjectMapper();
		JSONObject j = getTaskInsertRequestAsJsonObject();
		TaskInsertRequest request = mapper.readValue(j.toString().getBytes(), TaskInsertRequest.class);
		assertThat(request.getStfn(), equalTo(j.getString("stfn")));
		assertThat(request.getRetryAttempts(), equalTo(j.getInt("retryAttempts")));
		assertThat(request.getVoName(), equalTo(j.getString("voName")));
		assertThat(request.getPinLifetime(), equalTo(j.getInt("pinLifetime")));
		assertThat(request.getUserId(), equalTo(j.getString("userId")));

	}

}
