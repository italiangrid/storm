package it.grid.storm.namespace;

import static it.grid.storm.config.Configuration.CONFIG_FILE_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import it.grid.storm.common.types.InvalidStFNAttributeException;
import it.grid.storm.common.types.StFN;
import it.grid.storm.namespace.model.Authority;
import it.grid.storm.srm.types.InvalidTTURLAttributesException;
import it.grid.storm.srm.types.TTURL;

public class TURLBuilderTest {

  private static final String HOSTNAME = "storm-test.example.org";
  private static final int PORT_HTTP = 8085;
  private static final int PORT_HTTPS = 8443;
  private static final String PATH = "/path/to/file.dat";

  private static final String FILE_TURL = "file:///full/path/to/file.dat";
  private static final String FILE_RELATIVE_TURL = "file://relative/path/to/file.dat";
  private static final String DAV_TURL = "dav://storm-test.example.org:8085/path/to/file.dat";
  private static final String DAVS_TURL = "davs://storm-test.example.org:8443/path/to/file.dat";
  private static final String HTTP_TURL = "http://storm-test.example.org:8085/path/to/file.dat";
  private static final String HTTPS_TURL = "https://storm-test.example.org:8443/path/to/file.dat";
  private static final String GSIFTP_TURL = "gsiftp://storm-test.example.org:2811/path/to/file.dat";

  private static final List<String> VALID_TURLS = Lists.newArrayList(FILE_TURL, FILE_RELATIVE_TURL,
      DAV_TURL, DAVS_TURL, HTTP_TURL, HTTPS_TURL, GSIFTP_TURL);
  private static final List<String> INVALID_TURLS =
      Lists.newArrayList("://host.org:1234/path", "fake://host.org:1234/path");

  private Authority authorityHttp;
  private Authority authorityHttps;
  private StFN stfn;

  static {
    System.setProperty(CONFIG_FILE_PATH, "storm.properties");
  }

  @Before
  public void init() throws InvalidStFNAttributeException {
    authorityHttp = new Authority(HOSTNAME, PORT_HTTP);
    authorityHttps = new Authority(HOSTNAME, PORT_HTTPS);
    stfn = StFN.make(PATH);
  }

  

  @Test
  public void testValidTURL() {

    VALID_TURLS.forEach(s -> {
      TTURL turl = TTURL.makeFromString(s);
      System.out.println("Created TURL: " + turl);
    });
  }

//  @Test
//  public void testValidTURLLegacy() {
//
//    VALID_TURLS.forEach(s -> {
//      TTURL turl = TTURL.makeFromStringLegacy(s);
//      System.out.println("Created TURL: " + turl);
//    });
//  }

  @Test
  public void testInvalidTURL() {

    INVALID_TURLS.forEach(s -> {
      try {
        TTURL.makeFromString(s);
        fail("This turl '" + s + "' should be invalid!");
      } catch (InvalidTTURLAttributesException e) {
      }
    });
  }

//  @Test
//  public void testInvalidTURLLegacy() {
//
//    INVALID_TURLS.forEach(s -> {
//      try {
//        TTURL.makeFromStringLegacy(s);
//        fail("This turl '" + s + "' should be invalid!");
//      } catch (InvalidTTURLAttributesException e) {
//      }
//    });
//  }

  @Test
  public void testDavURL() {

    TTURL turl = TURLBuilder.buildDavURL(authorityHttp, stfn);
    assertEquals(turl.toString(), DAV_TURL);
  }

  @Test
  public void testDavsURL() {

    TTURL turl = TURLBuilder.buildDavsURL(authorityHttps, stfn);
    assertEquals(turl.toString(), DAVS_TURL);
  }

  @Test
  public void testHttpURL() {

    TTURL turl = TURLBuilder.buildHttpURL(authorityHttp, stfn);
    assertEquals(turl.toString(), HTTP_TURL);
  }

  @Test
  public void testHttpsURL() {

    TTURL turl = TURLBuilder.buildHttpsURL(authorityHttps, stfn);
    assertEquals(turl.toString(), HTTPS_TURL);
  }
}
