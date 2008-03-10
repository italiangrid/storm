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
public interface MOCK_GPFS extends MOCK_GenericFS {
  /**
   * delete
   *
   * @todo Implement this component.namespace.mockFS.MOCK_GenericFS method
   */
  public void delete() ;


  /**
   * get_exact_last_modification_time
   *
   * @param pathname String
   * @return long
   * @throws FilesystemError
   * @todo Implement this component.namespace.mockFS.MOCK_GenericFS method
   */
  public long get_exact_last_modification_time(String pathname) throws FilesystemError ;


  /**
   * get_exact_size
   *
   * @param filename String
   * @return int
   * @throws FilesystemError
   * @todo Implement this component.namespace.mockFS.MOCK_GenericFS method
   */
  public int get_exact_size(String filename) throws FilesystemError ;

  /**
   * get_free_space
   *
   * @return int
   * @throws FilesystemError
   * @todo Implement this component.namespace.mockFS.MOCK_GenericFS method
   */
  public int get_free_space() throws FilesystemError ;


  /**
   * get_last_modification_time
   *
   * @param pathname String
   * @return long
   * @throws FilesystemError
   * @todo Implement this component.namespace.mockFS.MOCK_GenericFS method
   */
  public long get_last_modification_time(String pathname) throws FilesystemError ;


  /**
   * get_size
   *
   * @param filename String
   * @return int
   * @throws FilesystemError
   * @todo Implement this component.namespace.mockFS.MOCK_GenericFS method
   */
  public int get_size(String filename) throws FilesystemError ;


  public void prealloc(String filename, int size) ;

}
