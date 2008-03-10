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
 *StoRM Exception Implementation 
 */

#include"../h/StoRM_Ex.h"

StoRM_ExInterface::StoRM_ExInterface() {
	Type = "";
	}

StoRM_ExInterface::~StoRM_ExInterface() {
	if(Type.size() != 0)
	;//	delete[] Type;
	}

string StoRM_ExInterface::Report() {
	return Type;
	}

Ex_FileNotFound::Ex_FileNotFound(string path ) {
	Type = "SRM_INVALID_PATH :"+path;
	}
