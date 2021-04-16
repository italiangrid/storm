package it.grid.storm.tape.recalltable.resources;

import static org.junit.Assert.assertEquals;

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
  public void testConstructorWithMapper()
      throws JsonParseException, JsonMappingException, IOException, JSONException {
    ObjectMapper mapper = new ObjectMapper();
    JSONObject j = getTaskInsertRequestAsJsonObject();
    TaskInsertRequest request = mapper.readValue(j.toString().getBytes(), TaskInsertRequest.class);
    assertEquals(request.getStfn(), j.getString("stfn"));
    assertEquals(Integer.valueOf(request.getRetryAttempts()),
        Integer.valueOf(j.getInt("retryAttempts")));
    assertEquals(request.getVoName(), j.getString("voName"));
    assertEquals(request.getPinLifetime(), Integer.valueOf(j.getInt("pinLifetime")));
    assertEquals(request.getUserId(), j.getString("userId"));

  }

}
