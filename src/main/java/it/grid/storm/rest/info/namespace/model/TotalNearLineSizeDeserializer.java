package it.grid.storm.rest.info.namespace.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class TotalNearLineSizeDeserializer extends StdDeserializer<TotalNearLineSize> {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  protected TotalNearLineSizeDeserializer() {
    this(null);
  }

  protected TotalNearLineSizeDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public TotalNearLineSize deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    
    TotalNearLineSize result = new TotalNearLineSize();
    JsonNode node = jp.getCodec().readTree(jp);
    // unit is optional
    JsonNode unitNode = node.get("unit");
    if (unitNode != null) {
      result.setUnit(UnitType.valueOf(unitNode.asText()));
    } else {
      result.setUnit(UnitType.TB);
    }
    // value is required
    result.setValue(node.get("").asLong());
    return result;
  }

}
