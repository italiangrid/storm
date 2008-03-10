package it.grid.storm.common.types;
import java.io.Serializable;

public class VO  implements Serializable {

	private String vo;
	
	public static final VO NO_VO = new VO("NO_VO");

	private VO(String vo) {
		this.vo = vo;
	}

	public static VO make(String newVo) {
		if(newVo == "NO_VO")
			return NO_VO;
		else	
			return new VO(newVo);
	}

	public static  VO makeDefault() {
		return new VO("CNAF");
	}
	
	public static  VO makeNoVo() {
		return NO_VO;
	}
	

	public String getValue() {
		return vo;
	}

	public String toString() {
		return vo;
	}

        public boolean equals(Object o) {
		if (o==this) return true;
  		if (!(o instanceof VO)) return false;
		VO tmp = (VO) o;
     		return (vo.equals(tmp.getValue()));
    }

}
