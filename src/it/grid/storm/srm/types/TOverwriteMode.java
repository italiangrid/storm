/**
 * This class represents the TOverwriteMode of an Srm request. 
 *
 * @author  Magnoni Luca
 * @author  CNAF - INFN  Bologna
 * @date    Avril, 2005
 * @version 1.0
 */

package it.grid.storm.srm.types;


public class TOverwriteMode {

	private String mode = null;

    public static final TOverwriteMode EMPTY =  new TOverwriteMode("Empty");
	public static final TOverwriteMode NEVER =  new TOverwriteMode("Never");
	public static final TOverwriteMode ALWAYS = new TOverwriteMode("Always");
	public static final TOverwriteMode WHENFILESAREDIFFERENT = new TOverwriteMode("WhenFilesAreDifferent");
	
	private TOverwriteMode(String mode) {
		this.mode = mode ;
	}

	public String toString() {
		return  mode;
	}
	
	public  String getValue() {
		return mode;
	}

	public static TOverwriteMode getTOverwriteMode(String mode) {
        if(mode.equals( EMPTY.getValue()))
            return EMPTY;
		if(mode.equals( NEVER.getValue()))
			return NEVER;
		if(mode.equals( ALWAYS.getValue()))
			return ALWAYS;
		if(mode.equals( WHENFILESAREDIFFERENT.getValue()))
			return WHENFILESAREDIFFERENT;
		return null;
	}

}

