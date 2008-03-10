/**
 * @file   InvalidPermissionOnFileException.java
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Source code for class InvalidPermissionOnFileException
 *
 */
/* 
 * Copyright (c) 2006, Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 * 
 * You may copy, distribute and modify this file under the terms of
 * the LICENSE.txt file at the root of the StoRM backend source tree.
 *
 * $Id: InvalidPermissionOnFileException.java,v 1.1 2006/03/31 13:35:01 rmurri Exp $
 */

package it.grid.storm.filesystem;


/**
 * Thrown when an operation is attempted on the filesystem, for which
 * the StoRM process has insufficient privileges (see ENOPERM system
 * error code).  Corresponds in usage fs::permission_denied exception
 * thrown by the C++ filesystem code, but the name has been retained
 * from old it.grid.storm.wrapper code.
 *
 * @see fs::permission_denied
 *
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.1 $
 */
public class InvalidPermissionOnFileException 
    extends FilesystemError
{
    public InvalidPermissionOnFileException(final String msg) {
        super(msg);
    }
}

