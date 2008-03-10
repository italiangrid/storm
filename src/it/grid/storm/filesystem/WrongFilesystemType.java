/**
 * @file   WrongFilesystemType.java
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Source code for class WrongFilesystemType
 *
 */
/* 
 * Copyright (c) 2006, Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 * 
 * You may copy, distribute and modify this file under the terms of
 * the LICENSE.txt file at the root of the StoRM backend source tree.
 *
 * $Id: WrongFilesystemType.java,v 1.1 2006/03/31 13:35:01 rmurri Exp $
 */

package it.grid.storm.filesystem;



/**
 * Thrown by genericfs subclasses ctors when the filesystem the passed
 * pathname resides on, is not of a type supported by the class.
 *
 * Corresponds in usage to fs::wrong_filesystem_type exception thrown
 * by C++ filesystem code.
 *
 * @see fs::wrong_filesystem_type
 * 
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.1 $
 */
public class WrongFilesystemType 
    extends FilesystemError
{
    public WrongFilesystemType(final String msg) {
        super(msg);
    }
}

