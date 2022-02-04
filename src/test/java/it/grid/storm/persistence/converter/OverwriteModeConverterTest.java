package it.grid.storm.persistence.converter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import it.grid.storm.config.model.v2.OverwriteMode;
import it.grid.storm.srm.types.TOverwriteMode;

public class OverwriteModeConverterTest {

  @Test
  public void ConvertFromDb() {

    assertEquals(TOverwriteMode.ALWAYS, OverwriteModeConverter.toSTORM(OverwriteMode.A));
    assertEquals(TOverwriteMode.NEVER, OverwriteModeConverter.toSTORM(OverwriteMode.N));
    assertEquals(TOverwriteMode.WHENFILESAREDIFFERENT, OverwriteModeConverter.toSTORM(OverwriteMode.D));
  }

  @Test
  public void ConvertToDb() {

    assertEquals(OverwriteMode.A, OverwriteModeConverter.toDB(TOverwriteMode.ALWAYS));
    assertEquals(OverwriteMode.N, OverwriteModeConverter.toDB(TOverwriteMode.NEVER));
    assertEquals(OverwriteMode.D, OverwriteModeConverter.toDB(TOverwriteMode.WHENFILESAREDIFFERENT));
    assertEquals(OverwriteMode.A.name(), "A");
    assertEquals(OverwriteMode.N.name(), "N");
    assertEquals(OverwriteMode.D.name(), "D");
  }
}
