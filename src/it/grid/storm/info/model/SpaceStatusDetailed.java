package it.grid.storm.info.model;

public class SpaceStatusDetailed extends SpaceStatusSummary {

	private long nrDir;
	private long nrFiles;
	private int levelMax;
	private int levelMedian;
	private long nrMaxFilesPerDir;
	private long nrMedFilesPerDir;
	private long oldestAccessedFile;
	
	public SpaceStatusDetailed(String saAlias) {
		super(saAlias);
	}
	
	public SpaceStatusDetailed(String saAlias, long spaceTotal) {
		super(saAlias, spaceTotal);
	}

	/**
	 * @return the nrDir
	 */
	public long getNrDir() {
		return nrDir;
	}

	/**
	 * @return the nrFiles
	 */
	public long getNrFiles() {
		return nrFiles;
	}

	/**
	 * @return the levelMax
	 */
	public int getLevelMax() {
		return levelMax;
	}

	/**
	 * @return the levelMedian
	 */
	public int getLevelMedian() {
		return levelMedian;
	}

	/**
	 * @return the nrMaxFilesPerDir
	 */
	public long getNrMaxFilesPerDir() {
		return nrMaxFilesPerDir;
	}

	/**
	 * @return the nrMedFilesPerDir
	 */
	public long getNrMedFilesPerDir() {
		return nrMedFilesPerDir;
	}

	/**
	 * @return the oldestAccessedFile
	 */
	public long getOldestAccessedFile() {
		return oldestAccessedFile;
	}

	/**
	 * @param nrDir the nrDir to set
	 */
	public void setNrDir(long nrDir) {
		this.nrDir = nrDir;
	}

	/**
	 * @param nrFiles the nrFiles to set
	 */
	public void setNrFiles(long nrFiles) {
		this.nrFiles = nrFiles;
	}

	/**
	 * @param levelMax the levelMax to set
	 */
	public void setLevelMax(int levelMax) {
		this.levelMax = levelMax;
	}

	/**
	 * @param levelMedian the levelMedian to set
	 */
	public void setLevelMedian(int levelMedian) {
		this.levelMedian = levelMedian;
	}

	/**
	 * @param nrMaxFilesPerDir the nrMaxFilesPerDir to set
	 */
	public void setNrMaxFilesPerDir(long nrMaxFilesPerDir) {
		this.nrMaxFilesPerDir = nrMaxFilesPerDir;
	}

	/**
	 * @param nrMedFilesPerDir the nrMedFilesPerDir to set
	 */
	public void setNrMedFilesPerDir(long nrMedFilesPerDir) {
		this.nrMedFilesPerDir = nrMedFilesPerDir;
	}

	/**
	 * @param oldestAccessedFile the oldestAccessedFile to set
	 */
	public void setOldestAccessedFile(long oldestAccessedFile) {
		this.oldestAccessedFile = oldestAccessedFile;
	}

	
}
