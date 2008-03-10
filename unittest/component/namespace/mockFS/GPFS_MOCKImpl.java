package component.namespace.mockFS;


import it.grid.storm.filesystem.FilesystemError;



/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public class GPFS_MOCKImpl implements MOCK_GPFS {

  public GPFS_MOCKImpl(String mntpath) {

}


  /**
   * delete
   *
   * @todo Implement this component.namespace.mockFS.MOCK_GenericFS method
   */
  public void delete() {
  }


  /**
   * get_exact_last_modification_time
   *
   * @param pathname String
   * @return long
   * @throws FilesystemError
   * @todo Implement this component.namespace.mockFS.MOCK_GenericFS method
   */
  public long get_exact_last_modification_time(String pathname) throws FilesystemError {
    return 0L;
  }


  /**
   * get_exact_size
   *
   * @param filename String
   * @return int
   * @throws FilesystemError
   * @todo Implement this component.namespace.mockFS.MOCK_GenericFS method
   */
  public int get_exact_size(String filename) throws FilesystemError {
    return 0;
  }


  /**
   * get_free_space
   *
   * @return int
   * @throws FilesystemError
   * @todo Implement this component.namespace.mockFS.MOCK_GenericFS method
   */
  public int get_free_space() throws FilesystemError {
    return 0;
  }


  /**
   * get_last_modification_time
   *
   * @param pathname String
   * @return long
   * @throws FilesystemError
   * @todo Implement this component.namespace.mockFS.MOCK_GenericFS method
   */
  public long get_last_modification_time(String pathname) throws FilesystemError {
    return 0L;
  }


  /**
   * get_size
   *
   * @param filename String
   * @return int
   * @throws FilesystemError
   * @todo Implement this component.namespace.mockFS.MOCK_GenericFS method
   */
  public int get_size(String filename) throws FilesystemError {
    return 0;
  }


  /**
   * prealloc
   *
   * @param filename String
   * @param size int
   * @todo Implement this component.namespace.mockFS.MOCK_GPFS method
   */
  public void prealloc(String filename, int size) {
  }
}
