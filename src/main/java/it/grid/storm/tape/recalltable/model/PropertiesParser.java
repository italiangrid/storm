package it.grid.storm.tape.recalltable.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesParser implements RequestDataParser<Properties> {

  @Override
  public Properties parse(InputStream input) throws IOException {

    Properties props = new Properties();
    props.load(input);
    return props;
  }
}
