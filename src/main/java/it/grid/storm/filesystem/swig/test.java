package it.grid.storm.filesystem.swig;

import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.AclNotSupported;
import it.grid.storm.filesystem.FilesystemError;

public class test extends posixfs {

  public test(String mntpath) throws AclNotSupported, FilesystemError {
    super(mntpath);
  }

  protected test(long cPtr, boolean cMemoryOwn) {
    super(cPtr, cMemoryOwn);
  }

  @Override
  public boolean is_file_on_disk(String filename) throws it.grid.storm.filesystem.FilesystemError {

    return StormEA.getOnline(filename);
  }
}
