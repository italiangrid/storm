//
//	<StoRM: Disk Resource Management Middleware.>
//	Copyright (C) <2004> <Magnoni Luca>
//
//	This file is a part of StoRM.
//	StoRM is free software; you can redistribute it and/or
//	modify it under the terms of the GNU Lesser General Public License
//	as published by the Free Software Foundation; either version 2.1
//	of the License, or (at your option) any later version.
//
//	StoRM is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
//	See the GNU Lesser General Public License for more details.
//
//	You should have received a copy of the GNU Lesser General
//	Public License along with this library; if not,
//	write to the Free Software Foundation, Inc., 59 Temple Place, 
//	Suite 330, Boston, MA 02111-1307 USA 
//
//
//



/**
 *Define Magic number used to identify file system type.
 *
 */

#include <linux/ext2_fs.h> //Define EXT_SUPER_MAGIC File System Type
#include <linux/ext3_fs.h>

#ifndef _MAGIC
#define _MAGIC

#define GPFS_1_MAGIC 0xbeef
#define GPFS_2_MAGIC 48879

#define LUSTRE_MAGIC 0xbd00bd0

#define EXT3_MAGIC  61267 
#endif
