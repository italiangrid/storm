package it.grid.storm.filesystem;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import it.grid.storm.griduser.LocalUser;

public class MetricsFilesystemAdapter implements FilesystemIF {

  public static enum FilesystemMetric {
    FILE_ATTRIBUTE_OP("fs.fileAttributeOp"),
    FILE_ONDISK_OP("fs.fileOnDiskOp"),
    FILE_ACL_OP("fs.aclOp"),
    FILE_TRUNCATE_OP("fs.fileTruncateOp"),
    FILE_CHOWN_OP("fs.fileChownOp"),
    GET_FREE_SPACE_OP("fs.getFreeSpaceOp");

    private String opName;

    private FilesystemMetric(String name) {
      opName = name;
    }

    public String getOpName() {

      return opName;
    }

  }

  final FilesystemIF delegate;
  final MetricRegistry registry;

  final Timer fileAttributeAccessTimer;
  final Timer fileOnDiskTimer;
  final Timer aclOperationTimer;
  final Timer fileTruncateTimer;
  final Timer fileOwnershipTimer;
  final Timer getFreeSpaceTimer;

  public MetricsFilesystemAdapter(FilesystemIF fs, MetricRegistry r) {
    delegate = fs;
    registry = r;

    fileAttributeAccessTimer = registry
      .timer(FilesystemMetric.FILE_ATTRIBUTE_OP.getOpName());
    fileOnDiskTimer = registry
      .timer(FilesystemMetric.FILE_ONDISK_OP.getOpName());
    aclOperationTimer = registry
      .timer(FilesystemMetric.FILE_ACL_OP.getOpName());
    fileTruncateTimer = registry
      .timer(FilesystemMetric.FILE_TRUNCATE_OP.getOpName());
    fileOwnershipTimer = registry
      .timer(FilesystemMetric.FILE_CHOWN_OP.getOpName());
    getFreeSpaceTimer = registry
      .timer(FilesystemMetric.GET_FREE_SPACE_OP.getOpName());
  }

  public long getSize(String file) {

    final Timer.Context context = fileAttributeAccessTimer.time();

    try {
      return delegate.getSize(file);
    } finally {
      context.stop();
    }

  }

  public long getLastModifiedTime(String fileOrDirectory) {

    final Timer.Context context = fileAttributeAccessTimer.time();

    try {
      return delegate.getLastModifiedTime(fileOrDirectory);
    } finally {
      context.stop();
    }
  }

  public long getExactSize(String file) {

    final Timer.Context context = fileAttributeAccessTimer.time();

    try {
      return delegate.getExactSize(file);
    } finally {
      context.stop();
    }

  }

  public long getExactLastModifiedTime(String fileOrDirectory) {

    final Timer.Context context = fileAttributeAccessTimer.time();
    try {
      return delegate.getExactLastModifiedTime(fileOrDirectory);
    } finally {
      context.stop();
    }
  }

  public int truncateFile(String filename, long desired_size) {

    final Timer.Context context = fileTruncateTimer.time();
    try {
      return delegate.truncateFile(filename, desired_size);
    } finally {
      context.stop();
    }
  }

  public boolean isFileOnDisk(String filename) {

    final Timer.Context context = fileOnDiskTimer.time();
    try {
      return delegate.isFileOnDisk(filename);
    } finally {
      context.stop();
    }

  }

  public long getFileBlockSize(String filename) {

    final Timer.Context context = fileAttributeAccessTimer.time();

    try {
      return delegate.getFileBlockSize(filename);
    } finally {
      context.stop();
    }

  }

  public void changeFileGroupOwnership(String filename, String groupName) {

    final Timer.Context context = fileOwnershipTimer.time();
    try {
      delegate.changeFileGroupOwnership(filename, groupName);
    } finally {
      context.stop();
    }
  }

  public long getFreeSpace() {

    final Timer.Context context = getFreeSpaceTimer.time();
    try {
      return delegate.getFreeSpace();
    } finally {
      context.stop();
    }

  }

  public boolean canAccess(LocalUser u, String fileOrDirectory,
    FilesystemPermission accessMode) {

    final Timer.Context context = aclOperationTimer.time();

    try {
      return delegate.canAccess(u, fileOrDirectory, accessMode);
    } finally {
      context.stop();
    }

  }

  public FilesystemPermission getEffectiveGroupPermission(LocalUser u,
    String fileOrDirectory) {

    final Timer.Context context = aclOperationTimer.time();

    try {
      return delegate.getEffectiveGroupPermission(u, fileOrDirectory);
    } finally {
      context.stop();
    }

  }

  public FilesystemPermission getEffectiveUserPermission(LocalUser u,
    String fileOrDirectory) {

    final Timer.Context context = aclOperationTimer.time();

    try {
      return delegate.getEffectiveUserPermission(u, fileOrDirectory);
    } finally {
      context.stop();
    }

  }

  public FilesystemPermission getGroupPermission(LocalUser u,
    String fileOrDirectory) {

    final Timer.Context context = aclOperationTimer.time();

    try {
      return delegate.getGroupPermission(u, fileOrDirectory);
    } finally {
      context.stop();
    }

  }

  public FilesystemPermission getUserPermission(LocalUser u,
    String fileOrDirectory) {

    final Timer.Context context = aclOperationTimer.time();

    try {
      return delegate.getUserPermission(u, fileOrDirectory);
    } finally {
      context.stop();
    }

  }

  public FilesystemPermission grantGroupPermission(LocalUser u,
    String fileOrDirectory, FilesystemPermission permission) {

    final Timer.Context context = aclOperationTimer.time();

    try {
      return delegate.grantGroupPermission(u, fileOrDirectory, permission);
    } finally {
      context.stop();
    }

  }

  public FilesystemPermission grantUserPermission(LocalUser u,
    String fileOrDirectory, FilesystemPermission permission) {

    final Timer.Context context = aclOperationTimer.time();
    try {
      return delegate.grantUserPermission(u, fileOrDirectory, permission);
    } finally {
      context.stop();
    }
  }

  public FilesystemPermission removeGroupPermission(LocalUser u,
    String fileOrDirectory) {

    final Timer.Context context = aclOperationTimer.time();
    try {
      return delegate.removeGroupPermission(u, fileOrDirectory);
    } finally {
      context.stop();
    }

  }

  public FilesystemPermission removeUserPermission(LocalUser u,
    String fileOrDirectory) {

    final Timer.Context context = aclOperationTimer.time();
    try {
      return delegate.removeUserPermission(u, fileOrDirectory);
    } finally {
      context.stop();
    }

  }

  public FilesystemPermission revokeGroupPermission(LocalUser u,
    String fileOrDirectory, FilesystemPermission permission) {

    final Timer.Context context = aclOperationTimer.time();
    try {
      return delegate.revokeGroupPermission(u, fileOrDirectory, permission);
    } finally {
      context.stop();
    }
  }

  public FilesystemPermission revokeUserPermission(LocalUser u,
    String fileOrDirectory, FilesystemPermission permission) {

    final Timer.Context context = aclOperationTimer.time();
    try {
      return delegate.revokeUserPermission(u, fileOrDirectory, permission);
    } finally {
      context.stop();
    }
  }

  public FilesystemPermission setGroupPermission(LocalUser u,
    String fileOrDirectory, FilesystemPermission permission) {

    final Timer.Context context = aclOperationTimer.time();
    try {
      return delegate.setGroupPermission(u, fileOrDirectory, permission);
    } finally {
      context.stop();
    }

  }

  public FilesystemPermission setUserPermission(LocalUser u,
    String fileOrDirectory, FilesystemPermission permission) {

    final Timer.Context context = aclOperationTimer.time();
    try {
      return delegate.setUserPermission(u, fileOrDirectory, permission);
    } finally {
      context.stop();
    }
  }

}
