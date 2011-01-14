/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
 * This module of StoRM have to manage effective space allocation / creation into disk space.
 * When space_allocation function is call by StoRM_SpaceManager module, space allocator have to check what kind
 * of file system is mounted on specified partition, then use correct wraapping to realize space area (file).
 * In first prototipe are developed only Ext2 Wrapping (ext3, ReiserFS,...all normal File System Not distributed) 
 * for testing purposal and GPFS Wrapping for effective employing in Grid Farm.
 * 
*/


#ifndef _STORM_SPCALLOC
#define _STORM_SPCALLOC

//For struct stat
#include <sys/stat.h>
#include <sys/statfs.h>

#ifdef GPFS

#include "../h/StoRM_GPFSWrap.h"

#endif 

#include "../h/StoRM_PosixFSWrap.h"

//SRM specification and request structure definition
#include "../h/StoRM_SRMspec.h"
#include "../h/StoRM_SRMRequest.h"

//Exception
#include "../h/StoRM_Ex.h"

class StoRM_SpaceAllocator {
	private:
		/**
		 * Path for Space Area location into StoRM Storage.
		 * Could be specified by user or generated by StoRM_SpaceManager Module.
		*/
		char* spaceAreaPath;
		
		/**
		 *Stat struct for testing underlying File System Type.
		 *Testing Underlyng File System Type SpaceAllocator con call right file System Wrapping Module
		 *allocate space into storage. 
		*/ 
		struct statfs fp;
		
	public: 
		/**
		 *Costructor for StoRM_SpaceAllocator. 
		*/
		StoRM_SpaceAllocator();
		
		
		/**
		 * Destructor for StoRM_SpaceAllocator.
		*/
		 ~StoRM_SpaceAllocator(){};
		
		/**
		 * Create Space to allocate.
		 * This function create space into storage system specified detecting underlying file system
		 * and contact wrapping module associated.
		 * @param spaceToAlloc  Size in MB of Space to Allocate.
		 * @param spacePath Path for Space Area.Definied by user or generate by StoRM_SpaceManager Module.
		 * @return 1 If Success.
		*/
		int spaceAlloc(TSizeInBytes spaceAllocSize, string spacePath,string fileName, gid_t owner, uid_t group);

		
	
};

#endif
