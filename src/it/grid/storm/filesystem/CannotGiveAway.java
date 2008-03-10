/**
 * @file   CannotGiveAway.java
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Source file for class CannotGiveAway
 *
 */
/* 
 * Copyright (c) 2006, Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 * 
 * You may copy, distribute and modify this file under the terms of
 * the LICENSE.txt file at the root of the StoRM backend source tree.
 *
 * $Id: CannotGiveAway.java,v 1.1 2006/03/31 13:35:01 rmurri Exp $
 */

package it.grid.storm.filesystem;


/**
 * Thrown when the StoRM process has insufficient privileges to change
 * ownership of a file.
 *
 * Ownership change is a privileged operation on most POSIX systems,
 * which usually requires "root" privileges.
 *
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.1 $
 */
public class CannotGiveAway 
    extends FilesystemError
{
    public CannotGiveAway(final String msg) {
        super(msg);
    }
}

