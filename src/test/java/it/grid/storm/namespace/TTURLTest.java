package it.grid.storm.namespace;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

import it.grid.storm.srm.types.InvalidTTURLAttributesException;
import it.grid.storm.srm.types.TTURL;

public class TTURLTest {

  @Test
  public void testValidTURLsFromFile() {
    InputStream is = getClass().getClassLoader().getResourceAsStream("valid-turls.txt");
    try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader)) {

      String line;
      while ((line = reader.readLine()) != null) {
        TTURL turl = TTURL.makeFromString(line);
        System.out.println("Created TURL: " + turl);
        Assert.assertEquals(turl.toString(), line);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

//  @Test
//  public void testValidTURLsFromFileLegacy() {
//    InputStream is = getClass().getClassLoader().getResourceAsStream("valid-turls.txt");
//    try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
//        BufferedReader reader = new BufferedReader(streamReader)) {
//
//      String line;
//      while ((line = reader.readLine()) != null) {
//        TTURL turl = TTURL.makeFromStringLegacy(line);
//        System.out.println("Created TURL: " + turl);
//      }
//
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }

  @Test
  public void testInvalidTURLsFromFile() {
    InputStream is = getClass().getClassLoader().getResourceAsStream("invalid-turls.txt");
    try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader)) {

      String line;
      while ((line = reader.readLine()) != null) {
        try {
          TTURL.makeFromString(line);
          fail("TTURL creation should fail instead");
        } catch (InvalidTTURLAttributesException e) {
          System.out.println("Expected error: " + e.getMessage());
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

//  @Test
//  public void testInvalidTURLsFromFileLegacy() {
//    InputStream is = getClass().getClassLoader().getResourceAsStream("invalid-turls.txt");
//    try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
//        BufferedReader reader = new BufferedReader(streamReader)) {
//
//      String line;
//      while ((line = reader.readLine()) != null) {
//        try {
//          TTURL.makeFromStringLegacy(line);
//          fail("TTURL creation should fail instead");
//        } catch (InvalidTTURLAttributesException e) {
//          System.out.println("Expected error: " + e.getMessage());
//        }
//      }
//
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }
}
