package it.grid.storm.info.model;

public class TreeStructureSummary {

	private String path;
	private long nodes;
	private long files;
	private int maxDepthToLeaves;
	private int maxBranches;
	private long maxFilePerNode;

	public TreeStructureSummary(String path) {

		super();
		this.path = path;
	}

	public final String getPath() {

		return path;
	}

	public final long getNodes() {

		return nodes;
	}

	public final void setNodes(long nodes) {

		this.nodes = nodes;
	}

	public final long getFiles() {

		return files;
	}

	public final void setFiles(long files) {

		this.files = files;
	}

	public final int getMaxDepthToLeaves() {

		return maxDepthToLeaves;
	}

	public final void setMaxDepthToLeaves(int maxDepthToLeaves) {

		this.maxDepthToLeaves = maxDepthToLeaves;
	}

	public final int getMaxBranches() {

		return maxBranches;
	}

	public final void setMaxBranches(int maxBranches) {

		this.maxBranches = maxBranches;
	}

	public final long getMaxFilePerNode() {

		return maxFilePerNode;
	}

	public final void setMaxFilePerNode(long maxFilePerNode) {

		this.maxFilePerNode = maxFilePerNode;
	}

	@Override
	public String toString() {

		return "TreeStructureSummary [nodes=" + nodes + ", files=" + files
			+ ", maxDepthToLeaves=" + maxDepthToLeaves + ", maxBranches="
			+ maxBranches + ", maxFilePerNode=" + maxFilePerNode + ", path=" + path
			+ "]";
	}

}
