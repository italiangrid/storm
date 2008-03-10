package component.namespace.mockFS;


import it.grid.storm.filesystem.FilesystemError;
//import it.grid.storm.filesystem.swig.posixfs_acl;


public interface MOCK_GenericFS {

  public void delete();


  public long get_exact_last_modification_time(String pathname) throws FilesystemError;


  public int get_exact_size(String filename) throws FilesystemError;


  public int get_free_space() throws FilesystemError;


  public long get_last_modification_time(String pathname) throws FilesystemError;


  public int get_size(String filename) throws FilesystemError;


  //public posixfs_acl new_acl() throws FilesystemError;
}
