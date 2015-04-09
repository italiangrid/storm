package it.grid.storm.info;

import it.grid.storm.space.DUResult;
import it.grid.storm.srm.types.TSpaceToken;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Preconditions;

public class BackgroundDUTasks {

	private Set<BgDUTask> tasks = new HashSet<BgDUTask>();

	private static final AtomicInteger taskId = new AtomicInteger();

	public void addTask(TSpaceToken token, String path) throws SAInfoException {

		addTask(new BgDUTask(token, true, path));
	}

	public void addTask(BgDUTask task) {

		tasks.add(task);
	}
	
	public void updateTask(BgDUTask task) throws SAInfoException {

		if (tasks.contains(task)) {
			tasks.remove(task);
			tasks.add(task);
		} else {
			throw new SAInfoException(String.format(
				"DU Task on %s doesn't exist in Task queue", task.absPath));
		}
	}

	public BgDUTask getTask(String absRootPath) {

		for (BgDUTask task : tasks) {
			if (task.absPathMatches(absRootPath)) {
				return task;
			}
		}
		return null;
	}

	public void removeTask(String absRootPath) {

		for (Iterator<BgDUTask> i = tasks.iterator(); i.hasNext();) {
			BgDUTask task = i.next();
			if (task.absPathMatches(absRootPath)) {
				tasks.remove(task);
				return;
			}
		}
	}

	public void removeSuccessTask() {

		for (Iterator<BgDUTask> i = tasks.iterator(); i.hasNext();) {
			BgDUTask task = i.next();
			if (task.getDuResult().isSuccess()) {
				tasks.remove(task);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("BackgroundDUTasks [tasks=");
		builder.append(tasks);
		builder.append("]");
		return builder.toString();
	}

	public Collection<BgDUTask> getTasks() {

		return tasks;
	}

	public int howManyTask() {

		return tasks.size();
	}

	public void clearTasks() {

		tasks.clear();
	}

	/**
     * 
     *
     */
	class BgDUTask implements Comparable<BgDUTask> {

		private boolean isSARoot = false;
		private String absPath;
		private TSpaceToken spaceToken;
		private DUResult duResult;
		private int attempt = 0;

		public BgDUTask(TSpaceToken sToken, boolean root, String absPath)
			throws SAInfoException {

			Preconditions.checkNotNull(sToken, "Invalid null token");
			Preconditions.checkNotNull(absPath, "Invalid null absPath");
			
			String pathNorm = FilenameUtils.normalize(FilenameUtils
				.getFullPath(absPath + File.separator));
			File cf = new File(pathNorm);

			if (!(cf.exists())) {
				throw new SAInfoException(String.format("The path %s doesn't exists.",
					absPath));
			}
			if (!(cf.isDirectory())) {
				throw new SAInfoException(String.format(
					"The path %s is not a directory.", absPath));
			}
			this.absPath = absPath;
			this.isSARoot = root;
			this.spaceToken = sToken;
			taskId.incrementAndGet();
			// This is the first attempt
			this.attempt = 1;
		}

		public boolean isSARoot() {

			return isSARoot;
		}

		public String getAbsPath() {

			return absPath;
		}

		public TSpaceToken getSpaceToken() {

			return spaceToken;
		}

		public int getTaskId() {

			return taskId.get();
		}

		public void setDuResult(DUResult duResult) {

			this.duResult = duResult;
		}

		public DUResult getDuResult() {

			return duResult;
		}

		public int getAttempt() {

			return attempt;
		}

		public void increaseAttempt() {

			attempt++;
		}
		
		public boolean absPathMatches(String absPath) {
			
			return FilenameUtils.equalsNormalized(getAbsPath(), absPath);
		}
		
		public int compareTo(BgDUTask other) {

			int result = -1;
			if (this.getTaskId() < other.getTaskId()) {
				result = -1;
			} else {
				if (this.getTaskId() > other.getTaskId()) {
					result = +1;
				} else {
					result = (this.equals(other) ? 0 : -1);
				}
			}
			return result;
		}

		@Override
		public boolean equals(Object obj) {

			if (obj instanceof BgDUTask) {
				BgDUTask other = (BgDUTask) obj;
				if (this.getTaskId() == other.getTaskId()) {
					return true;
				}
			}
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {

			StringBuilder builder = new StringBuilder();
			builder.append("BgDUTask [isSARoot=");
			builder.append(isSARoot);
			builder.append(", absPath=");
			builder.append(absPath);
			builder.append(", spaceToken=");
			builder.append(spaceToken);
			builder.append(", duResult=");
			builder.append(duResult);
			builder.append(", attempt=");
			builder.append(attempt);
			builder.append("]");
			return builder.toString();
		}

	} // End inner class BgDUTask

}
