/**
 * @file   FilesystemError.java
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Source for the it.grid.storm.filesystem.FilesystemError class.
 */
/*
 * Copyright (c) 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the same terms
 * as StoRM itself.
 */

package it.grid.storm.filesystem;

/** Root class for all exceptions thrown by classes in the {@link
 * it.grid.storm.filesystem} package.
 * 
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.1 $
 */
public class FilesystemError 
    extends RuntimeException
{
    /** Constructor, taking error message. Chains the parameter to the
     * superclass. 
     */
    public FilesystemError(final String msg)
    {
        super(msg);
    }

    /** Constructor with no parameters; for subclasses that implement
     * their own getMessage() method only.
     */
    protected FilesystemError()
    {
        // subclasses need to implement getMessage()
    }
}
