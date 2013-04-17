package it.grid.storm.info.model;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

	// *******************
	// Parameters referring this node
	private String path = null;
	private int nrChildren = -1;
	private long nrFiles = 0;
	private long sumSize = 0;
	private long minSize = 0;
	private long maxSize = 0;

	// ********************
	// Time properties
	//
	private boolean toCompute = true;
	// Time retrieved from FS
	private long modificationTime = -1L;
	// Time set when a check of values between FS-path and Node occurs
	private long checkTime = -1L;
	// Time set when the Service see a incoherence between FS-path and Node
	private long toComputeTime = -1L;
	// Time set when the Service performs a synch between FS and Node
	private long nodeUpdateTime = -1L;

	// *******************
	// Parameters referring the tree starting from this node
	//
	// Sum of sizes of all files under the tree with this node as root.
	private long totSize = 0;
	// Sum of sizes of all files in term of BLOCK sizes under the tree with this
	// node as root.
	private long totApparentSize = 0;

	// *******************
	// Parameters to navigate the Tree, and summarizing the underlying tree
	//
	// Position
	private int depthFromRoot = -1;
	private TreeStructureSummary tsSummary;
	private TreeNode father = null;
	private TreeNode firstChild = null;
	private TreeNode lastChild = null;
	private TreeNode brother = null;

	// ****************************
	// Constructors
	//

	/**
	 * Simplest constructor
	 */
	public TreeNode(String path) {

		super();
		this.path = path;
	}

	// ****************************
	// Setter and Getter methods
	//

	public final String getPath() {

		return path;
	}

	public final int getDepthFromRoot() {

		return depthFromRoot;
	}

	public final TreeStructureSummary getTsSummary() {

		return tsSummary;
	}

	public final TreeNode getFather() {

		return father;
	}

	public final void setFather(TreeNode father) {

		this.father = father;
	}

	/**
	 * @param brother
	 *          the brother to set
	 */
	public final void setBrother(TreeNode brother) {

		this.brother = brother;
	}

	/**
	 * @return the brother
	 */
	public final TreeNode getBrother() {

		return brother;
	}

	/**
	 * @param nrChildren
	 *          the nrChildren to set
	 */
	public final void setNrChildren(int nrChildren) {

		this.nrChildren = nrChildren;
	}

	/**
	 * @return the nrChildren
	 */
	public final int getNrChildren() {

		return nrChildren;
	}

	/**
	 * @param nrFiles
	 *          the nrFiles to set
	 */
	public final void setNrFiles(long nrFiles) {

		this.nrFiles = nrFiles;
	}

	/**
	 * @return the nrFiles
	 */
	public final long getNrFiles() {

		return nrFiles;
	}

	/**
	 * @param sumSize
	 *          the sumSize to set
	 */
	public final void setSumSize(long sumSize) {

		this.sumSize = sumSize;
	}

	/**
	 * @return the sumSize
	 */
	public final long getSumSize() {

		return sumSize;
	}

	/**
	 * @param minSize
	 *          the minSize to set
	 */
	public final void setMinSize(long minSize) {

		this.minSize = minSize;
	}

	/**
	 * @return the minSize
	 */
	public final long getMinSize() {

		return minSize;
	}

	/**
	 * @param maxSize
	 *          the maxSize to set
	 */
	public final void setMaxSize(long maxSize) {

		this.maxSize = maxSize;
	}

	/**
	 * @return the maxSize
	 */
	public final long getMaxSize() {

		return maxSize;
	}

	/**
	 * @param totSize
	 *          the totSize to set
	 */
	public final void setTotSize(long totSize) {

		this.totSize = totSize;
	}

	/**
	 * @return the totSize
	 */
	public final long getTotSize() {

		return totSize;
	}

	/**
	 * @param totApparentSize
	 *          the totApparentSize to set
	 */
	public final void setTotApparentSize(long totApparentSize) {

		this.totApparentSize = totApparentSize;
	}

	/**
	 * @return the totApparentSize
	 */
	public final long getTotApparentSize() {

		return totApparentSize;
	}

	/**
	 * @param toCompute
	 *          the toCompute to set
	 */
	public final void setToCompute(boolean toCompute) {

		this.toCompute = toCompute;
	}

	/**
	 * @return the toCompute
	 */
	public final boolean isToCompute() {

		return toCompute;
	}

	/**
	 * @param dateModification
	 *          the dateModification to set
	 */
	public final void setModificationTime(long dateModification) {

		this.modificationTime = dateModification;
	}

	/**
	 * @return the dateModification
	 */
	public final long getModificationTime() {

		return modificationTime;
	}

	/**
	 * @param checkTime
	 *          the checkTime to set
	 */
	public void setCheckTime(long checkTime) {

		this.checkTime = checkTime;
	}

	/**
	 * @return the checkTime
	 */
	public long getCheckTime() {

		return checkTime;
	}

	/**
	 * @param toComputeTime
	 *          the toComputeTime to set
	 */
	public void setToComputeTime(long toComputeTime) {

		this.toComputeTime = toComputeTime;
	}

	/**
	 * @return the toComputeTime
	 */
	public long getToComputeTime() {

		return toComputeTime;
	}

	/**
	 * @param nodeUpdateTime
	 *          the nodeUpdateTime to set
	 */
	public void setNodeUpdateTime(long nodeUpdateTime) {

		this.nodeUpdateTime = nodeUpdateTime;
	}

	/**
	 * @return the nodeUpdateTime
	 */
	public long getNodeUpdateTime() {

		return nodeUpdateTime;
	}

	public final TreeNode getFirstChild() {

		return firstChild;
	}

	public final void setFirstChild(TreeNode firstChild) {

		this.firstChild = firstChild;
	}

	public final TreeNode getLastChild() {

		return lastChild;
	}

	public final void setLastChild(TreeNode lastChild) {

		this.lastChild = lastChild;
	}

	public final void setDepthFromRoot(int depthFromRoot) {

		this.depthFromRoot = depthFromRoot;
	}

	// ****************************
	// Business Methods
	//

	private void setToComputeUntilRoot() {

		TreeNode cursor = this.father;
		while (cursor != null) {
			if (!(cursor.toCompute)) {
				cursor.toCompute = true;
				cursor.setToComputeTime(System.currentTimeMillis());
			}
		}
	}

	/**
	 * Modification of path tree: ADDING a child node
	 * 
	 * @param child
	 */
	public final void addChildNode(TreeNode child) {

		// Set the modification Time

		setModificationTime(System.currentTimeMillis());

		if (firstChild == null) {
			firstChild = child;
		}
		// If children exist, the brother of the last is the last node added
		if (lastChild != null) {
			lastChild.setBrother(child);
		}

		// The new last child is the last node added
		this.lastChild = child;

		// Set the father relationship
		child.father = this;

		// Set the depth of the child
		child.depthFromRoot = this.depthFromRoot + 1;

		// Increase the number of children
		this.nrChildren++;

		boolean toSetComp = false;

		// Summary: Max branches check
		if (this.nrChildren > this.tsSummary.getMaxBranches()) {
			// new max
			this.tsSummary.setMaxBranches(this.nrChildren);
			toSetComp = true;
		}

		// Summary: Max distance from leaves
		if (this.tsSummary.getMaxDepthToLeaves() == 0) {
			this.tsSummary.setMaxDepthToLeaves(1);
			toSetComp = true;
		}

		// Summary: Max files per node
		if (this.tsSummary.getMaxFilePerNode() < child.getNrFiles()) {
			this.tsSummary.setMaxFilePerNode(child.getNrFiles());
			toSetComp = true;
		}

		if (toSetComp) {
			setToComputeUntilRoot();
		}
	}

	/**
	 * Building a list containing all the children
	 * 
	 * @return
	 */
	public final List<TreeNode> getChildren() {

		ArrayList<TreeNode> children = new ArrayList<TreeNode>();
		if (firstChild != null) {
			children.add(firstChild);
			TreeNode cursor = firstChild;
			while (cursor.brother != null) {
				cursor = cursor.brother;
				children.add(cursor);
			}
		}
		return children;
	}

	/**
	 * Update node parameters from children info
	 * 
	 */
	public final void compute() {

		List<TreeNode> children = getChildren();
		if (!(children.isEmpty())) {
			int maxDistanceLeaves = -1;
			int maxBranches = -1;
			long maxNodes = -1;

			// Search for maxes
			for (TreeNode treeNode : children) {
				TreeStructureSummary ts = treeNode.getTsSummary();
				if (ts.getMaxBranches() > maxBranches) {
					maxBranches = ts.getMaxBranches();
				}
				if (ts.getMaxDepthToLeaves() > maxDistanceLeaves) {
					maxDistanceLeaves = ts.getMaxDepthToLeaves();
				}
				if (ts.getMaxFilePerNode() > maxNodes) {
					maxNodes = ts.getMaxFilePerNode();
				}
			}

			boolean toSetComp = false;

			// Check and update
			if (maxDistanceLeaves > this.tsSummary.getMaxDepthToLeaves()) {
				this.tsSummary.setMaxDepthToLeaves(maxDistanceLeaves);
				toSetComp = true;
			}
			if (maxBranches > this.tsSummary.getMaxBranches()) {
				this.tsSummary.setMaxBranches(maxBranches);
				toSetComp = true;
			}
			if (maxNodes > this.tsSummary.getMaxFilePerNode()) {
				this.tsSummary.setMaxFilePerNode(maxNodes);
				toSetComp = true;
			}

			if (toSetComp) {
				setToComputeUntilRoot();
			}
		}

		this.toCompute = false;
		setNodeUpdateTime(System.currentTimeMillis());
	}

}
