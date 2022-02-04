package it.grid.storm.config.model.v2;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum StorageType {

  V,
  D,
  P;
}
