/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common.types;

import java.io.Serializable;

public class VO implements Serializable {

  private String vo;

  public static final VO NO_VO = new VO("NO_VO");

  private VO(String vo) {

    this.vo = vo;
  }

  public static VO make(String newVo) {

    if (newVo.equals("NO_VO")) return NO_VO;
    else return new VO(newVo);
  }

  public static VO makeDefault() {

    return new VO("CNAF");
  }

  public static VO makeNoVo() {

    return NO_VO;
  }

  public String getValue() {

    return vo;
  }

  public String toString() {

    return vo;
  }

  public boolean equals(Object o) {

    if (!(o instanceof VO)) return false;
    if (o == this) return true;
    VO tmp = (VO) o;

    return (vo.equals(tmp.getValue()));
  }

  @Override
  public int hashCode() {

    int result = 17;
    result = 31 * result + (vo != null ? vo.hashCode() : 0);
    return result;
  }
}
