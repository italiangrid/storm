package it.grid.storm.tape.recalltable.model;

import java.io.IOException;
import java.io.InputStream;

public interface RequestDataParser<T> {

  T parse(InputStream input) throws IOException;
}
