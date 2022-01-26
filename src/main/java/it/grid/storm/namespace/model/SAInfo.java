package it.grid.storm.namespace.model;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.remote.Constants.HttpPerms;

public class SAInfo {

  private String name;
  private String token;
  private List<String> vos;
  private String root;
  private String storageClass;
  private List<String> accessPoints;
  private String retentionPolicy;
  private String accessLatency;
  private List<String> protocols;
  private HttpPerms anonymous;
  private long availableNearlineSpace;
  private List<String> approachableRules;

  // Must have no-argument constructor
  public SAInfo() {

    vos = Lists.newArrayList();
    accessPoints = Lists.newArrayList();
    protocols = Lists.newArrayList();
    approachableRules = Lists.newArrayList();
  }

  public String getName() {

    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public String getToken() {

    return token;
  }

  public void setToken(String token) {

    this.token = token;
  }

  public List<String> getVos() {

    return vos;
  }

  public void addVo(String voName) {

    this.vos.add(voName);
  }

  public String getRoot() {

    return root;
  }

  public void setRoot(String root) {

    this.root = root;
  }

  public String getStorageClass() {

    return storageClass;
  }

  public void setStorageClass(String storageClass) {

    this.storageClass = storageClass;
  }

  public List<String> getAccessPoints() {

    return accessPoints;
  }

  public void addAccessPoint(String accessPoint) {

    this.accessPoints.add(accessPoint);
  }

  public String getRetentionPolicy() {

    return retentionPolicy;
  }

  public void setRetentionPolicy(String retentionPolicy) {

    this.retentionPolicy = retentionPolicy;
  }

  public String getAccessLatency() {

    return accessLatency;
  }

  public void setAccessLatency(String accessLatency) {

    this.accessLatency = accessLatency;
  }

  public List<String> getProtocols() {

    return protocols;
  }

  public void addProtocol(String protocol) {

    this.protocols.add(protocol);
  }

  public HttpPerms getAnonymous() {

    return anonymous;
  }

  public void setAnonymous(HttpPerms anonymous) {

    this.anonymous = anonymous;
  }

  public long getAvailableNearlineSpace() {

    return availableNearlineSpace;
  }

  public void setAvailableNearlineSpace(long availableNearlineSpace) {

    this.availableNearlineSpace = availableNearlineSpace;
  }

  public List<String> getApproachableRules() {

    return approachableRules;
  }

  public void addApproachableRule(String approachableRule) {

    this.approachableRules.add(approachableRule);
  }

  public static SAInfo buildFromVFS(VirtualFSInterface vfs) throws NamespaceException {

    SAInfo sa = new SAInfo();

    sa.setName(vfs.getAliasName());
    sa.setToken(vfs.getSpaceTokenDescription());
    vfs.getApproachableRules().forEach(ar -> {
      sa.addVo(ar.getSubjectRules().getVONameMatchingRule().getVOName());
    });
    sa.setRoot(vfs.getRootPath());
    vfs.getMappingRules().forEach(mr -> {
      sa.addAccessPoint(mr.getStFNRoot());
    });
    Iterator<Protocol> protocolsIterator =
        vfs.getCapabilities().getAllManagedProtocols().iterator();
    while (protocolsIterator.hasNext()) {
      sa.addProtocol(protocolsIterator.next().getSchema());
    }
    if (vfs.isHttpWorldReadable()) {
      if (vfs.isApproachableByAnonymous()) {
        sa.setAnonymous(HttpPerms.READWRITE);
      } else {
        sa.setAnonymous(HttpPerms.READ);
      }
    } else {
      sa.setAnonymous(HttpPerms.NOREAD);
    }
    sa.setStorageClass(vfs.getStorageClassType().getStorageClassTypeString());
    sa.setRetentionPolicy(vfs.getProperties().getRetentionPolicy().getRetentionPolicyName());
    sa.setAccessLatency(vfs.getProperties().getAccessLatency().getAccessLatencyName());
    sa.setAvailableNearlineSpace(vfs.getAvailableNearlineSpace().value());

    for (ApproachableRule rule : vfs.getApproachableRules()) {
      if (rule.getSubjectRules().getDNMatchingRule().isMatchAll()
          && rule.getSubjectRules().getVONameMatchingRule().isMatchAll()) {
        continue;
      }
      if (!rule.getSubjectRules().getDNMatchingRule().isMatchAll()) {
        sa.addApproachableRule(
            rule.getSubjectRules().getDNMatchingRule().toShortSlashSeparatedString());
      }
      if (!rule.getSubjectRules().getVONameMatchingRule().isMatchAll()) {
        sa.addApproachableRule("vo:" + rule.getSubjectRules().getVONameMatchingRule().getVOName());
      }
    }
    if (sa.getApproachableRules().size() == 0) {
      sa.getApproachableRules().add("'ALL'");
    }

    return sa;
  }
}
