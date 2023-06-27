package it.grid.storm.balancer;

import org.mockito.Mockito;

import it.grid.storm.balancer.node.FTPNode;
import it.grid.storm.balancer.node.HttpNode;
import it.grid.storm.balancer.node.HttpsNode;

public class BalancerUtils {

  protected Node getNode(Class<? extends Node> c, int id, String hostname, int port,
      boolean isResponsive) {

    Node n = Mockito.mock(c);
    Mockito.when(n.getHostname()).thenReturn(hostname);
    Mockito.when(n.getPort()).thenReturn(port);
    Mockito.when(n.getId()).thenReturn(id);
    Mockito.when(n.checkServer()).thenReturn(isResponsive);
    return n;
  }

  protected Node getNode(Class<? extends Node> c, int id, String hostname, int port,
      boolean isResponsive, int weight) {

    Node n = Mockito.mock(c);
    Mockito.when(n.getHostname()).thenReturn(hostname);
    Mockito.when(n.getPort()).thenReturn(port);
    Mockito.when(n.getId()).thenReturn(id);
    Mockito.when(n.checkServer()).thenReturn(isResponsive);
    Mockito.when(n.getWeight()).thenReturn(weight);
    return n;
  }

  protected Node getResponsiveFtpNode(int id, String hostname, int port) {
    return getNode(FTPNode.class, id, hostname, port, true);
  }

  protected Node getUnresponsiveFtpNode(int id, String hostname, int port) {
    return getNode(FTPNode.class, id, hostname, port, false);
  }

  protected Node getResponsiveFtpNode(int id, String hostname, int port, int weight) {
    return getNode(FTPNode.class, id, hostname, port, true, weight);
  }

  protected Node getUnresponsiveFtpNode(int id, String hostname, int port, int weight) {
    return getNode(FTPNode.class, id, hostname, port, false, weight);
  }

  protected Node getResponsiveHttpNode(int id, String hostname, int port) {
    return getNode(HttpNode.class, id, hostname, port, true);
  }

  protected Node getUnresponsiveHttpNode(int id, String hostname, int port) {
    return getNode(HttpNode.class, id, hostname, port, false);
  }

  protected Node getResponsiveHttpNode(int id, String hostname, int port, int weight) {
    return getNode(HttpNode.class, id, hostname, port, true, weight);
  }

  protected Node getUnresponsiveHttpNode(int id, String hostname, int port, int weight) {
    return getNode(HttpNode.class, id, hostname, port, false, weight);
  }

  protected Node getResponsiveHttpsNode(int id, String hostname, int port) {
    return getNode(HttpsNode.class, id, hostname, port, true);
  }

  protected Node getUnresponsiveHttpsNode(int id, String hostname, int port) {
    return getNode(HttpsNode.class, id, hostname, port, false);
  }

  protected Node getResponsiveHttpsNode(int id, String hostname, int port, int weight) {
    return getNode(HttpsNode.class, id, hostname, port, true, weight);
  }

  protected Node getUnresponsiveHttpsNode(int id, String hostname, int port, int weight) {
    return getNode(HttpsNode.class, id, hostname, port, false, weight);
  }

}
