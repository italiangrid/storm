package it.grid.storm.common.types;
/**
 * This class represent a Storage File Name Root.
 * A virtual directory path assigned to a single Virtual Oraganization, so each SURL of this VO must start with correct StFNRoot.
 */
public class StFNRoot {
	private String stfnroot;

	public StFNRoot(String stfnroot)throws InvalidStFNRootAttributeException {
        	if ((stfnroot==null) || (stfnroot.equals("")) || (stfnroot.charAt(0)!='/')) throw new InvalidStFNRootAttributeException(stfnroot);
       		 this.stfnroot=stfnroot.replaceAll(" ","");
 	}

	public String getValue() {
		return stfnroot;
	}
		
	public String toString() {
		return stfnroot;
	}
        public boolean equals(Object o) {
       	 if (o==this) return true;
       	 if (!(o instanceof StFNRoot)) return false;
       	 StFNRoot po = (StFNRoot) o;
       	 return stfnroot.equals(po.stfnroot);
   	 }
}
