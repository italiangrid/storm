/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.common.types;

import java.io.Serializable;

public class VO implements Serializable {

  /**
  * 
  */
  private static final long serialVersionUID = 1L;

  private String vo;

  public static final VO NO_VO = new VO("NO_VO");

  private VO(String vo) {

    this.vo = vo;
  }

  public static VO make(String newVo) {

    if (newVo.equals("NO_VO"))
      return NO_VO;
    else
      return new VO(newVo);
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

    if (!(o instanceof VO))
      return false;
    if (o == this)
      return true;
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
