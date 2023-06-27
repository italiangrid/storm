package it.grid.storm.namespace.model;

import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.remote.Constants.HttpPerms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SAInfoV13 {

	private String name;
	private String token;
	private String voname;
	private String root;
	private String storageclass;
	private List<String> stfnRoot;
	private String retentionPolicy;
	private String accessLatency;
	private List<String> protocols;
	private HttpPerms anonymous;
	private long availableNearlineSpace;
	private List<String> approachableRules;

	// Must have no-argument constructor
	public SAInfoV13() {

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

	public String getVoname() {

		return voname;
	}

	public void setVoname(String voname) {

		this.voname = voname;
	}

	public String getRoot() {

		return root;
	}

	public void setRoot(String root) {

		this.root = root;
	}

	public String getStorageclass() {

		return storageclass;
	}

	public void setStorageclass(String storageclass) {

		this.storageclass = storageclass;
	}

	public List<String> getStfnRoot() {

		return stfnRoot;
	}

	public void setStfnRoot(List<String> stfnRoot) {

		this.stfnRoot = stfnRoot;
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

	public void setProtocols(List<String> protocols) {

		this.protocols = protocols;
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

	public void setApproachableRules(List<String> approachableRules) {

		this.approachableRules = approachableRules;
	}

    public static SAInfoV13 buildFromVFS(VirtualFS vfs) throws NamespaceException {

		SAInfoV13 sa = new SAInfoV13();

		sa.setName(vfs.getAliasName());
		sa.setToken(vfs.getSpaceTokenDescription());
		sa.setVoname(vfs.getApproachableRules().get(0).getSubjectRules()
			.getVONameMatchingRule().getVOName());
		sa.setRoot(vfs.getRootPath());
		sa.setStfnRoot(new ArrayList<String>());
		for (MappingRule rule : vfs.getMappingRules()) {
			sa.getStfnRoot().add(rule.getStFNRoot());
		}
		sa.setProtocols(new ArrayList<String>());
		Iterator<Protocol> protocolsIterator = vfs.getCapabilities()
			.getAllManagedProtocols().iterator();
		while (protocolsIterator.hasNext()) {
			sa.getProtocols().add(protocolsIterator.next().getSchema());
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
		sa.setStorageclass(vfs.getStorageClassType().getStorageClassTypeString());
		sa.setRetentionPolicy(vfs.getProperties().getRetentionPolicy()
			.getRetentionPolicyName());
		sa.setAccessLatency(vfs.getProperties().getAccessLatency()
			.getAccessLatencyName());
		sa.setAvailableNearlineSpace(vfs.getAvailableNearlineSpace().value());
		sa.setApproachableRules(new ArrayList<String>());
		for (ApproachableRule rule : vfs.getApproachableRules()) {
			if (rule.getSubjectRules().getDNMatchingRule().isMatchAll()
				&& rule.getSubjectRules().getVONameMatchingRule().isMatchAll()) {
				continue;
			}
			if (!rule.getSubjectRules().getDNMatchingRule().isMatchAll()) {
				sa.getApproachableRules().add(
					rule.getSubjectRules().getDNMatchingRule()
						.toShortSlashSeparatedString());
			}
			if (!rule.getSubjectRules().getVONameMatchingRule().isMatchAll()) {
				sa.getApproachableRules().add(
					"vo:" + rule.getSubjectRules().getVONameMatchingRule().getVOName());
			}
		}
		if (sa.getApproachableRules().size() == 0) {
			sa.getApproachableRules().add("'ALL'");
		}

		return sa;
	}
}