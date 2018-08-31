package it.grid.storm.filesystem;

import static java.lang.Integer.parseInt;
import static java.lang.System.getProperty;
import static java.util.Objects.isNull;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.griduser.LocalUser;

public class RandomWaitFilesystemAdapter implements FilesystemIF {

  public static final Logger LOG = LoggerFactory.getLogger(RandomWaitFilesystemAdapter.class);

  public static final String RANDOM_SLEEP_ENABLED = "storm.fs.sleep.enabled";
  public static final String MAX_WAIT_TIME_MSEC = "storm.fs.sleep.maxTimeMs";
  public static final String MIN_WAIT_TIME_MSEC = "storm.fs.sleep.minTimeMs";

  final FilesystemIF delegate;
  final Random random = new Random();
  int maxWaitTime = 1000;
  int minWaitTime = 10;

  private long randomTimeinMsec() {

    int time = random.nextInt(maxWaitTime);
    if (time < minWaitTime) {
      time = minWaitTime;
    }

    return time;
  }

  private RandomWaitFilesystemAdapter(FilesystemIF delegate) {

    LOG.warn("RandomWaitFilesystemAdapter is ENABLED ({} property is defined)",
        RANDOM_SLEEP_ENABLED);

    LOG.warn("This adapter introduces synthentic sleep time on each fs call!! Not advisable "
        + "in production");

    this.delegate = delegate;
    maxWaitTime = parseInt(getProperty(MAX_WAIT_TIME_MSEC, "1000"));
    minWaitTime = parseInt(getProperty(MIN_WAIT_TIME_MSEC, "10"));
  }


  public static FilesystemIF maybeWrapFilesystem(FilesystemIF delegate) {
    if (!isNull(getProperty(RANDOM_SLEEP_ENABLED))) {
      return new RandomWaitFilesystemAdapter(delegate);
    }

    return delegate;
  }

  protected void sleepSomeTime() {
    try {
      long sleepTime = randomTimeinMsec();
      Thread.sleep(sleepTime);
      LOG.debug("Slept {} msec", sleepTime);
    } catch (InterruptedException e) {
      LOG.warn("Interrupted", e);
      Thread.currentThread().interrupt();
    }
  }


  @Override
  public long getSize(String file) {
    sleepSomeTime();
    return delegate.getSize(file);
  }

  public long getLastModifiedTime(String fileOrDirectory) {
    sleepSomeTime();
    return delegate.getLastModifiedTime(fileOrDirectory);
  }

  public long getExactSize(String file) {
    sleepSomeTime();
    return delegate.getExactSize(file);
  }

  public long getExactLastModifiedTime(String fileOrDirectory) {
    sleepSomeTime();
    return delegate.getExactLastModifiedTime(fileOrDirectory);
  }

  public int truncateFile(String filename, long desired_size) {
    sleepSomeTime();
    return delegate.truncateFile(filename, desired_size);
  }

  public boolean isFileOnDisk(String filename) {
    sleepSomeTime();
    return delegate.isFileOnDisk(filename);
  }

  public long getFileBlockSize(String filename) {
    sleepSomeTime();
    return delegate.getFileBlockSize(filename);
  }

  public void changeFileGroupOwnership(String filename, String groupName) {
    sleepSomeTime();
    delegate.changeFileGroupOwnership(filename, groupName);
  }

  public long getFreeSpace() {
    sleepSomeTime();
    return delegate.getFreeSpace();
  }

  public boolean canAccess(LocalUser u, String fileOrDirectory, FilesystemPermission accessMode) {
    sleepSomeTime();
    return delegate.canAccess(u, fileOrDirectory, accessMode);
  }

  public FilesystemPermission getEffectiveGroupPermission(LocalUser u, String fileOrDirectory) {
    sleepSomeTime();
    return delegate.getEffectiveGroupPermission(u, fileOrDirectory);
  }

  public FilesystemPermission getEffectiveUserPermission(LocalUser u, String fileOrDirectory) {
    sleepSomeTime();
    return delegate.getEffectiveUserPermission(u, fileOrDirectory);
  }

  public FilesystemPermission getGroupPermission(LocalUser u, String fileOrDirectory) {
    sleepSomeTime();
    return delegate.getGroupPermission(u, fileOrDirectory);
  }

  public FilesystemPermission getUserPermission(LocalUser u, String fileOrDirectory) {
    sleepSomeTime();
    return delegate.getUserPermission(u, fileOrDirectory);
  }

  public FilesystemPermission grantGroupPermission(LocalUser u, String fileOrDirectory,
      FilesystemPermission permission) {
    sleepSomeTime();
    return delegate.grantGroupPermission(u, fileOrDirectory, permission);
  }

  public FilesystemPermission grantUserPermission(LocalUser u, String fileOrDirectory,
      FilesystemPermission permission) {
    sleepSomeTime();
    return delegate.grantUserPermission(u, fileOrDirectory, permission);
  }

  public FilesystemPermission removeGroupPermission(LocalUser u, String fileOrDirectory) {
    sleepSomeTime();
    return delegate.removeGroupPermission(u, fileOrDirectory);
  }

  public FilesystemPermission removeUserPermission(LocalUser u, String fileOrDirectory) {
    sleepSomeTime();
    return delegate.removeUserPermission(u, fileOrDirectory);
  }

  public FilesystemPermission revokeGroupPermission(LocalUser u, String fileOrDirectory,
      FilesystemPermission permission) {
    sleepSomeTime();
    return delegate.revokeGroupPermission(u, fileOrDirectory, permission);
  }

  public FilesystemPermission revokeUserPermission(LocalUser u, String fileOrDirectory,
      FilesystemPermission permission) {
    sleepSomeTime();
    return delegate.revokeUserPermission(u, fileOrDirectory, permission);
  }

  public FilesystemPermission setGroupPermission(LocalUser u, String fileOrDirectory,
      FilesystemPermission permission) {
    sleepSomeTime();
    return delegate.setGroupPermission(u, fileOrDirectory, permission);
  }

  public FilesystemPermission setUserPermission(LocalUser u, String fileOrDirectory,
      FilesystemPermission permission) {
    sleepSomeTime();
    return delegate.setUserPermission(u, fileOrDirectory, permission);
  }

}
