package it.grid.storm.balancer.cache;

import static it.grid.storm.balancer.cache.Responsiveness.RESPONSIVE;
import static it.grid.storm.balancer.cache.Responsiveness.UNRESPONSIVE;
import static it.grid.storm.config.Configuration.CONFIG_FILE_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import it.grid.storm.balancer.BalancerUtils;
import it.grid.storm.balancer.Node;

public class ResponsivenessCacheTest extends BalancerUtils {

  static {
    System.setProperty(CONFIG_FILE_PATH, "storm.properties");
  }

  private final ResponsivenessCache CACHE = ResponsivenessCache.INSTANCE;

  @Before
  public void initCache() {
    CACHE.invalidate();
  }

  @Test
  public void testCaching() {

    Node https1 = getResponsiveHttpsNode(1, "dav01.example.org", 8443);
    Node https2 = getUnresponsiveHttpsNode(2, "dav02.example.org", 8443);

    Node http1 = getResponsiveHttpNode(3, "dav01.example.org", 8085);
    Node http2 = getUnresponsiveHttpNode(4, "dav02.example.org", 8085);

    Node ftp1 = getResponsiveFtpNode(5, "ftp01.example.org", 2811);
    Node ftp2 = getUnresponsiveFtpNode(6, "ftp02.example.org", 2811);

    assertFalse(CACHE.isCached(https1));
    assertFalse(CACHE.isCached(https2));
    assertFalse(CACHE.isCached(http1));
    assertFalse(CACHE.isCached(http2));
    assertFalse(CACHE.isCached(ftp1));
    assertFalse(CACHE.isCached(ftp2));

    assertEquals(RESPONSIVE, CACHE.getResponsiveness(https1));
    assertTrue(CACHE.isCached(https1));
    assertEquals(UNRESPONSIVE, CACHE.getResponsiveness(https2));
    assertTrue(CACHE.isCached(https2));

    assertEquals(RESPONSIVE, CACHE.getResponsiveness(http1));
    assertTrue(CACHE.isCached(http1));
    assertEquals(UNRESPONSIVE, CACHE.getResponsiveness(http2));
    assertTrue(CACHE.isCached(http2));

    assertEquals(RESPONSIVE, CACHE.getResponsiveness(ftp1));
    assertTrue(CACHE.isCached(ftp1));
    assertEquals(UNRESPONSIVE, CACHE.getResponsiveness(ftp2));
    assertTrue(CACHE.isCached(ftp2));
  }

}
