/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.authz.sa.model;

public class SpaceACE {

  public static final String ACE_PREFIX = "ace.";

  private int aceNumber;
  private SubjectType subjectType;
  private SubjectPattern subjectPattern;
  private SpaceAccessMask spaceAccessMask;
  private AceType aceType;

  public SpaceACE() {}

  /** @return the aceNumber */
  public int getAceNumber() {

    return aceNumber;
  }

  /** @param aceNumber the aceNumber to set */
  public void setAceNumber(int aceNumber) {

    this.aceNumber = aceNumber;
  }

  public void setSubjectType(SubjectType subjectType) {

    this.subjectType = subjectType;
  }

  public void setSubjectPattern(SubjectPattern subject) {

    subjectPattern = subject;
  }

  public void setSpaceAccessMask(SpaceAccessMask spAccessMask) {

    spaceAccessMask = spAccessMask;
  }

  public void setAceType(AceType aceType) {

    this.aceType = aceType;
  }

  public SubjectType getSubjectType() {

    return subjectType;
  }

  public SubjectPattern getSubjectPattern() {

    return subjectPattern;
  }

  /** @return the spacePermission */
  public SpaceAccessMask getSpaceAccessMask() {

    return spaceAccessMask;
  }

  public AceType getAceType() {

    return aceType;
  }

  @Override
  public String toString() {

    String spacePermissionStr = spaceAccessMask.toString();
    return "SpaceACE ("
        + getAceNumber()
        + "): "
        + getSubjectType()
        + ":"
        + getSubjectPattern()
        + ":"
        + spacePermissionStr
        + ":"
        + aceType;
  }
}
