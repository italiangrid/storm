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
 *SRM Specification v. 2.1
 *Standard structures definied in SRM v. 2.1
*/
///////////////////////////////////
// Standar structures defined in //
// SRM Specification v. 2.1      //
///////////////////////////////////

#ifndef _STORM_SRM
#define _STORM_SRM

//#include <cstring.h>
#include <iostream>
#include <string>
#include <time.h>

using namespace std;

enum TSpaceType	{Volatile,Durable,Permanent};
//Confilct with previous identical type
enum TFileStorageType {FVolatile,FDurable,FPermanent};

enum TFileType	{File,Directory,Link};

enum TPermissionMode 	{NONE, X, W, WX, R, RX, RW, RWX};
enum TPermissionType 	{ADD, REMOVE, CHANGE};
enum TRequestType    	{PrepareToGet, PrepareToPut, Copy};
enum TOverwriteMode  	{Never, Always, WhenFilesAreDifferent};

typedef string TRequestToken;
typedef string TSpaceToken;
typedef string TUserID;
typedef string TGroupID;
typedef TPermissionMode  TOwnerPermission;



typedef struct { TUserID          UserID;
		 TPermissionMode mode;
		} TUserPermission;

typedef struct { TGroupID         GroupID;
	         TPermissionMode  mode;
		} TGroupPermission;

typedef TPermissionMode TOtherPermission;
typedef string          TCheckSumType;
typedef string          TCheckSumValue;

typedef unsigned long TSizeInBytes;
typedef time_t TGMTTime;//dateTime TGMTTime;

typedef unsigned long TLifeTimeInSeconds;

typedef string TSURL;//anyURI        TSURL; // site URL
typedef string TTURL;//anyURI        TTURL; // transfer URL

enum TStatusCode	{ SRM_SUCCESS,
			  SRM_FAILURE,
			  SRM_AUTHENTICATION_FAILURE,
			  SRM_UNAUTHORIZED_ACCESS,
			  SRM_INVALID_REQUEST,
			  SRM_INVALID_PATH,
			  SRM_FILE_LIFETIME_EXPIRED,
			  SRM_SPACE_LIFETIME_EXPIRED,
			  SRM_EXCEED_ALLOCATION,
			  SRM_NO_USER_SPACE,
			  SRM_NO_FREE_SPACE,
			  SRM_DUPLICATION_ERROR,
			  SRM_NON_EMPTY_DIRECTORY,
			  SRM_TOO_MANY_RESULTS,
			  SRM_INTERNAL_ERROR,
			  SRM_FATAL_INTERNAL_ERROR,
			  SRM_NOT_SUPPORTED,
			  SRM_REQUEST_QUEUED,
			  SRM_REQUEST_INPROGRESS,
			  SRM_REQUEST_SUSPENDED,
			  SRM_ABORTED,
			  SRM_RELEASED,
			  SRM_FILE_PINNED,
			  SRM_FILE_IN_CACHE,
			  SRM_SPACE_AVAILABLE,
			  SRM_LOWER_SPACE_GRANTED,
			  SRM_DONE,
			  SRM_CUSTOM_STATUS
			};


typedef struct {TStatusCode statusCode;
	        string        explanation;
	        } TReturnStatus;

typedef struct {TSURL surl;
		TReturnStatus status;
		} TSURLReturnStatus;


typedef  struct {string                path; // both dir and file
	         TReturnStatus         status;
	         TSizeInBytes          size; // 0 if dir
	         TOwnerPermission      ownerPermission;
	         TUserPermission userPermission;//TUserPermission[]     userPermission;
	         TGroupPermission groupPermission;//TGroupPermission[]    groupPermission;
	         TOtherPermission      otherPermission;
	         TGMTTime              createdAtTime;
	         TGMTTime              lastModificationTime;
	         TUserID               owner;
	         TFileStorageType      fileStorageType;
	         TFileType             type; // Directory or File
	         TLifeTimeInSeconds    lifetimeAssigned;
	         TLifeTimeInSeconds    lifetimeLeft;
	         TCheckSumType         checkSumType;
	         TCheckSumValue        checkSumValue;
	         TSURL                 originalSURL; // if path is a file
	         //TMetaDataPathDetail subPath;//TMetaDataPathDetail[] subPath;       // optional recursive
	         } TMetaDataPathDetail;


typedef struct {TSpaceType         type;
                TSpaceToken        spaceToken;
                bool            isValid;
                TUserID            owner;
                TSizeInBytes       totalSize;    // best effort
                TSizeInBytes       GuaranteedSize;
                TSizeInBytes       unusedSize;
                TLifeTimeInSeconds lifetimeAssigned;
                TLifeTimeInSeconds lifetimeLeft ;
               } TMetaDataSpace;

typedef string TStorageSystemInfo;

typedef struct {bool      isSourceADirectory;
                bool      allLevelRecursive;  // default = false
                int          numOfLevels;         // default = 1
               } TDirOption;

typedef struct {TSURL               SURLOrStFN;
                TStorageSystemInfo storageSystemInfo;
               } TSURLInfo;


typedef struct {TSURLInfo          fromSURLInfo;
                TLifeTimeInSeconds lifetime; // pin time
                TFileStorageType   fileStorageType;
                TSpaceToken        spaceToken;
                TDirOption         dirOption;
		} TGetFileRequest;

typedef struct {TSURLInfo          toSURLInfo; // local to SRM
                TLifeTimeInSeconds lifetime;      // pin time
                TFileStorageType   fileStorageType;
                TSpaceToken        spaceToken;
                TSizeInBytes       knownSizeOfThisFile;
		} TPutFileRequest;

typedef struct {TSURLInfo          fromSURLInfo;
                TSURLInfo          toSURLInfo;
                TLifeTimeInSeconds lifetime; // pin time
                TFileStorageType   fileStorageType;
                TSpaceToken        spaceToken;
                TOverwriteMode     overwriteMode;
                TDirOption         dirOption;
		} TCopyFileRequest;





typedef struct {TSURL              fromSURLInfo;
                TSizeInBytes       fileSize;
                TReturnStatus      status;
                TLifeTimeInSeconds estimatedWaitTimeOnQueue;
	        TLifeTimeInSeconds estimatedProcessingTime;
	        TTURL              transferURL;
	        TLifeTimeInSeconds remainingPinTime;
	        } TGetRequestFileStatus;


typedef struct { TSizeInBytes      fileSize;
                 TReturnStatus      status;
                 TLifeTimeInSeconds estimatedWaitTimeOnQueue;
                 TLifeTimeInSeconds estimatedProcessingTime;
                 TTURL              transferURL;
                 TSURL              siteURL; // for future reference
                 TLifeTimeInSeconds remainingPinTime;
               } TPutRequestFileStatus;

typedef struct { TSURL fromSURL;
	         TSURL toSURL;
                 TSizeInBytes       fileSize;
		 TReturnStatus      status;
		 TLifeTimeInSeconds estimatedWaitTimeOnQueue;
		 TLifeTimeInSeconds estimatedProcessingTime;
		 TLifeTimeInSeconds remainingPinTime;
		} TCopyRequestFileStatus;

typedef struct { TRequestToken   requestToken;
                 TRequestType    requestType;
	         int             totalFilesInThisRequest;
	         int             numOfQueuedRequests;
	         int             numOfFinishedRequests;
	         int             numOfProgressingRequests;
	         bool         isSuspended;
	       } TRequestSummary;

typedef struct { TSURL           surl;
	         TReturnStatus   status;
		 TPermissionType userPermission;
	       } TSURLPermissionReturn;

typedef struct { TRequestToken   requestToken;
                 TGMTTime        createdAtTime;
 	       } TRequestTokenReturn;


#endif
