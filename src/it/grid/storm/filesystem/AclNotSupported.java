/**
 * @file   AclNotSupported.java
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Source code for class AclNotSupported
 *
 */
/* 
 * Copyright (c) 2006, Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 * 
 * You may copy, distribute and modify this file under the terms of
 * the LICENSE.txt file at the root of the StoRM backend source tree.
 *
 * $Id: AclNotSupported.java,v 1.1 2006/03/31 13:35:01 rmurri Exp $
 */

package it.grid.storm.filesystem;



/**
 * Thrown when some ACL manipulation is requested on a filesystem that
 * does not support ACLs, or when ACL support is not enabled in the
 * kernel.
 *
 * Corresponds in usage to fs::acl_not_supported exception thrown
 * by C++ filesystem code.
 *
 * @see fs::acl_not_supported
 * 
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.1 $
 */
public class AclNotSupported 
    extends FilesystemError
{
    public AclNotSupported(final String msg) {
        super(msg);
    }
}

