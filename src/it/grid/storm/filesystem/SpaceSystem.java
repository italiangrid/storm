package it.grid.storm.filesystem;

/**
 * Interface that represents the Space functionality that some filesystems
 * may have natively present, or that must be added as an external feature
 * to those that do not.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    May 2006
 */
public interface SpaceSystem {

    /**
     * Method that pre-allocates size bytes on pathToFile; it returns a long
     * representing the actual size in bytes of reserved space. Notice that
     * pathToFile is the complete path to the desired file, including the name
     * of the file itself.
     *
     * The method throws a ReservationException if the operation cannot be completed.
     */
    public long reserveSpace(String pathToFile, long size) throws ReservationException;

    /**
     * Method that gives back to the filesystem any space previously pre-allocated
     * to pathToFile, but presently unused. Notice that pathToFile is the complete
     * path to the desired file, including the name of the file itself. The method
     * returns a long representing the file size after compacting.
     *
     * The method throws a ReservationException if the operation cannot be completed.
     */
    public long compactSpace(String pathToFile) throws ReservationException;

    /**
     * Method that de-allocates space previously assigned to pathToFile. Notice that
     * pathToFile is the complete path to the file, including the name of the file
     * itself.
     *
     * The method throws a ReservationException if the operation cannot be completed.
     */
    public void removeSpace(String pathToFile) throws ReservationException;

    /**
     * Method used to modify the size already reserved for a file: it requires the
     * String pathToFile and the long newSize. Notice that pathToFile is the complete
     * path to the file, including the file itself. The method returns the actual changed
     * size after the operation completes.
     *
     * The method throws a ReservationException if the operation cannot be completed.
     */
    public long changeSize(String pathToFile, long newSize) throws ReservationException;
}
