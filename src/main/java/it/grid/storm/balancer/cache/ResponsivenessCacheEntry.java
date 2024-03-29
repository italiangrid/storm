package it.grid.storm.balancer.cache;

import java.text.SimpleDateFormat;
import java.util.Date;

import it.grid.storm.balancer.Node;

public class ResponsivenessCacheEntry {

  private final Node cachedNode;
  private final long lifetime;

  private long checkTime = -1;
  private Responsiveness status;
  private SimpleDateFormat dt;

  public ResponsivenessCacheEntry(Node node, long lifetime) {

    this.cachedNode = node;
    this.lifetime = lifetime;
    dt = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    refresh();
  }

  public final Responsiveness getStatus() {
    return status;
  }

  private void check() {
    this.checkTime = new Date().getTime();
  }

  public boolean isExpired() {
    return System.currentTimeMillis() - checkTime >= lifetime;
  }

  public final Responsiveness refresh() {

    if (cachedNode.checkServer()) {
      status = Responsiveness.RESPONSIVE;
    } else {
      status = Responsiveness.UNRESPONSIVE;
    }
    check();
    return status;
  }

  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();
    builder.append("ResponsivenessCacheEntry [cachedNode=");
    builder.append(cachedNode);
    builder.append(", checkTime=");
    builder.append(dt.format(new Date(checkTime)));
    builder.append(", status=");
    builder.append(status);
    builder.append("]");
    return builder.toString();
  }
}
