package it.grid.storm.srm.types;


/**
 * This class represents an TDirOption Object.
 * TDirOption contains information about directory visit.
 *
 * @author  Magnoni Luca
 * @author  CNAF - INFN Bologna
 * @date    Avril 20
 * @version 1.0
 */
public class  TDirOption {
	private boolean isASourceDirectory;
	private boolean allLevelRecursive;
	private int numOfLevels = 0;
	//private File fileh

    /**
     * Constructor that requires boolean isDirectory indicating whether the SURL
     * refers to a Directory or not, and a boolean allLevel to indicate if
     * recursion on all subdirectories is wanted. If allLevel is false,
     * an InvalidTDirOptionAttributesException is thrown.
     */
	public TDirOption(boolean isDirectory, boolean allLevel) throws InvalidTDirOptionAttributesException {
		//fileh = new File(pathName);
		this.allLevelRecursive = allLevel;
		this.isASourceDirectory = isDirectory;
		if (allLevelRecursive == false)
			throw new InvalidTDirOptionAttributesException(allLevel, -1);
	}


    /**
     * Constructor that requires boolean isDirectory, boolean allLevel, int
     * numLevel. An exception is thrown if allLevel is true, and numLevel>0.
     */
	public TDirOption(boolean isDirectory, boolean allLevel, int numLevel) throws InvalidTDirOptionAttributesException {
		if ((allLevel==true) && (numLevel > 0)) throw new InvalidTDirOptionAttributesException(allLevel, numLevel);
		//fileh = new File(pathName);
		allLevelRecursive = allLevel;
		numOfLevels = numLevel;
		isASourceDirectory = isDirectory;
	}


	/**
	 * Return True if SURL associated with TDirOption is a valid directory for visit.
	 */
	public boolean isDirectory() {
		return isASourceDirectory; 
	}
		
	/**
	 * Return true if allLevelRecursive is true
	 */
	public boolean isAllLevelRecursive() {
		return allLevelRecursive;
	}
	
	/**
	 * Return num of recursive level to visit. If isAllLevelRecursive then
     * 0 is returned.
	 */
	public int getNumLevel() {
		if (!allLevelRecursive)
			return numOfLevels;
		else
			return 0;
	}


    public String toString() {
        return "isASourceDirectory="+isASourceDirectory+" allLevelRecursive="+allLevelRecursive+" numOfLevels="+numOfLevels;
    }

    // WARNING!!! The contract of the constructors _must_ be reconsidered!
    // There appear to be inconsistencies with the Exception Throwing policies!!!



    /*public static void main(String[] args) {
        //Testing constructors
        System.out.println("Testing constructors...");
        try {
            TDirOption d1 = new TDirOption(true,true); System.out.println(1+" "+d1+"; isASourceDirectory:"+d1.isDirectory()+"; isAllLevelRecursive:"+d1.isAllLevelRecursive()+"; numLevel:"+d1.getNumLevel());
            TDirOption d2 = new TDirOption(false,true); System.out.println(2+" "+d2+"; isASourceDirectory:"+d2.isDirectory()+"; isAllLevelRecursive:"+d2.isAllLevelRecursive()+"; numLevel:"+d2.getNumLevel());
            TDirOption d3 = new TDirOption(true,true,0); System.out.println(3+" "+d3+"; isASourceDirectory:"+d3.isDirectory()+"; isAllLevelRecursive:"+d3.isAllLevelRecursive()+"; numLevel:"+d3.getNumLevel());
            TDirOption d4 = new TDirOption(true,false,10); System.out.println(4+" "+d4+"; isASourceDirectory:"+d4.isDirectory()+"; isAllLevelRecursive:"+d4.isAllLevelRecursive()+"; numLevel:"+d4.getNumLevel());
            TDirOption d5 = new TDirOption(false,false,-10); System.out.println(5+" "+d5+"; isASourceDirectory:"+d5.isDirectory()+"; isAllLevelRecursive:"+d5.isAllLevelRecursive()+"; numLevel:"+d5.getNumLevel());
        } catch (Exception e) {
            System.out.println("Should not see this!"+e);
        }
        System.out.println("\n\nTesting Exception throwing...");
        System.out.print("Creating TDirOption with isDirectory=true and allLevel=false... ");
        try {
            new TDirOption(true,false);
            System.out.println("Should not see this!");
        } catch (InvalidTDirOptionAttributesException e) {
            System.out.println("OK creation failed as expected! "+e);
        }
        System.out.print("Creating TDirOption with isDirectory=true and allLevel=true and numLevel>0... ");
        try {
            new TDirOption(true,true,10);
            System.out.println("Should not see this!");
        } catch (InvalidTDirOptionAttributesException e) {
            System.out.println("OK creation failed as expected! "+e);
        }
    }*/
}
