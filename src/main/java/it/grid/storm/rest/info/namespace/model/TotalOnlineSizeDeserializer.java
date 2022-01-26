package it.grid.storm.rest.info.namespace.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class TotalOnlineSizeDeserializer extends StdDeserializer<TotalOnlineSize> {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  protected TotalOnlineSizeDeserializer() {
    this(null);
  }

  protected TotalOnlineSizeDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public TotalOnlineSize deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    
    TotalOnlineSize result = new TotalOnlineSize();
    JsonNode node = jp.getCodec().readTree(jp);
    // unit is optional
    JsonNode unitNode = node.get("unit");
    if (unitNode != null) {
      result.setUnit(UnitType.valueOf(unitNode.asText()));
    } else {
      result.setUnit(UnitType.TB);
    }
    // limited size is required
    result.setLimitedSize(node.get("limited-size").asBoolean());
    // value is required
    result.setValue(node.get("").asLong());
    return result;
  }

}
