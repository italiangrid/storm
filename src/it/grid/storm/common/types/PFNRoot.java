package it.grid.storm.common.types;

/**
 * This class represent a Physical File Name Root, the directory entry in FIle System assigned to a Virtual Organization.
 * 
 */


public class PFNRoot {
	
	private String pfnroot;

	public PFNRoot(String pfnroot) throws InvalidPFNRootAttributeException{
	        if ((pfnroot==null) || (pfnroot=="") || (pfnroot.charAt(0)!='/')) throw new InvalidPFNRootAttributeException(pfnroot);
       		 this.pfnroot=pfnroot.replaceAll(" ","");
 	
	}

	public String getValue() {
		return pfnroot;
	}
		
	public String toString() {
		return pfnroot;
	}
    
	public boolean equals(Object o) {
       	 if (o==this) return true;
       	 if (!(o instanceof PFNRoot)) return false;
       	 PFNRoot po = (PFNRoot) o;
       	 return pfnroot.equals(po.pfnroot);
    }

}
