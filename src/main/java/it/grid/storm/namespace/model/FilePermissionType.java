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

package it.grid.storm.namespace.model;

public class FilePermissionType {

  private int ordinalNumber;
  private String permissionType;
  private String permissionString;

  public final static FilePermissionType READ = new FilePermissionType("READ", "R", 0);
  public final static FilePermissionType READWRITE = new FilePermissionType("READWRITE", "RW", 1);
  public final static FilePermissionType WRITE = new FilePermissionType("WRITE", "W", 2);
  public final static FilePermissionType UNKNOWN = new FilePermissionType("UNKNOWN", "Permission Type UNKNOWN!", -1);

  private FilePermissionType(String permissionType, String permissionString, int ord) {
    this.permissionType = permissionType;
    this.permissionString = permissionString;
    this.ordinalNumber = ord;
  }

  //Only get method for Ordinal Number
  public int getOrdinalNumber() {
    return this.ordinalNumber;
  }

  public String getPermissionString() {
    return this.permissionString;
  }

  /**
   *
   * @param quotaType String
   * @return QuotaType
   */
  public static FilePermissionType getFilePermissionType(String permission) {
    if (permission.equals(READ.getPermissionString())) {
      return FilePermissionType.READ;
    }
    if (permission.equals(READWRITE.getPermissionString())) {
      return FilePermissionType.READWRITE;
    }
    if (permission.equals(WRITE.getPermissionString())) {
      return FilePermissionType.WRITE;
    }
    return FilePermissionType.UNKNOWN;
  }


  /**
   *
   * @param quotaType String
   * @return QuotaType
   */
  public static FilePermissionType getFilePermissionType(int filetypeOrd) {
    if (filetypeOrd == 0) {
      return FilePermissionType.READ;
    }
    if (filetypeOrd == 1) {
      return FilePermissionType.READWRITE;
    }
    if (filetypeOrd == 2) {
      return FilePermissionType.WRITE;
    }
    return FilePermissionType.UNKNOWN;
  }

  /**
   *
   * @return int
   */
  public int hashCode() {
    return this.ordinalNumber;
  }

  /**
   *
   * @param other Object
   * @return boolean
   */
  public boolean equals(Object other) {
    boolean result = false;
    if (other instanceof FilePermissionType) {
      FilePermissionType ft = (FilePermissionType) other;
      if (ft.ordinalNumber == this.ordinalNumber) {
        result = true;
      }
    }
    return result;
  }

  /**
   *
   * @return String
   */
  public String toString() {
    return this.permissionType;
  }

}
