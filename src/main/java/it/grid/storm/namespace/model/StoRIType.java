/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

/**
 * Title:
 *
 * <p>Description: data children attr physical FILE T F T T FOLDER F T T T LINK F F T T SPACE F F T
 * T/F SPACE_BOUND T F T T IMAGINARY F F F F
 *
 * <p>Copyright: Copyright (c) 2006
 *
 * <p>Company:
 *
 * @author not attributable
 * @version 1.0
 */
public class StoRIType {

  private final String typeName;
  private boolean dataContent;
  private boolean children;
  private boolean attributes;
  private boolean physical;

  // Means that StoRI has the correspondent FILE in underlying file system.
  public static final StoRIType FILE = new StoRIType("file", true, false, true, true);
  // Means that StoRI corresponds to a FOLDER in underlying file system.
  public static final StoRIType FOLDER = new StoRIType("folder", false, true, true, true);
  // Means that StoRI corresponds to a LINK in underlying file system.
  public static final StoRIType LINK = new StoRIType("link", false, false, true, true);
  // Means that StoRI corresponds to a FILE representing a SPACE in virtual file
  // system.
  // and the SPACE is not alloted for a Logical File, that is could not exists
  // the physical file for it.
  public static final StoRIType SPACE = new StoRIType("space", false, false, true, false);
  // Means that StoRI corresponds to a FILE representing a SPACE in underlying
  // file system.
  // and the SPACE is alloted for a Logical File, that is exists a physical file
  // for it.
  public static final StoRIType SPACE_BOUND = new StoRIType("spaceBound", true, false, true, true);
  // Means that StoRI has the correspondent FILE in underlying file system.
  public static final StoRIType IMAGINARY = new StoRIType("imaginary", false, false, false, false);

  public static final StoRIType UNKNOWN = new StoRIType("unknown", false, false, false, false);

  private StoRIType(
      String type, boolean dataContent, boolean children, boolean attributes, boolean physical) {

    this.typeName = type;
    this.dataContent = dataContent;
    this.children = children;
    this.attributes = attributes;
    this.physical = physical;
  }

  public boolean holdsChildren() {

    return this.children;
  }

  public boolean containData() {

    return this.dataContent;
  }

  public boolean holdsAttributes() {

    return this.attributes;
  }

  public boolean isPhysical() {

    return this.physical;
  }

  public String toString() {

    return typeName;
  }

  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }
    if (obj instanceof StoRIType) {
      StoRIType storyType = (StoRIType) obj;
      if (storyType.toString().toLowerCase().equals(this.toString().toLowerCase())) {
        return true;
      }
    } else {
      return false;
    }
    return false;
  }

  @Override
  public int hashCode() {

    int result = 17;
    result = 31 * result + (typeName != null ? typeName.hashCode() : 0);
    result = 31 * result + (dataContent ? 1 : 0);
    result = 31 * result + (children ? 1 : 0);
    result = 31 * result + (attributes ? 1 : 0);
    result = 31 * result + (physical ? 1 : 0);
    return result;
  }
}
