/**
 * Class that represents the TStorageSystemInfo.
 *
 * @author:  CNAF Bologna
 * @version: 1.0
 * @date:    May 2005
 */
package it.grid.storm.srm.types;

import java.io.Serializable;

public class TStorageSystemInfo implements Serializable{

	private String info  = null;
	private boolean empty = true;


    public String toString() {
        return info;
    }


	public TStorageSystemInfo(String s, boolean empty) {
		this.info = s  ;
		this.empty = empty;
	}

	public static TStorageSystemInfo makeEmpty() {
		return new TStorageSystemInfo("",true);
	}

	public static TStorageSystemInfo make(String s) {
		return new TStorageSystemInfo(s,false);
	}

	public boolean isEmpty() {
		return empty;
	}

	public String getValue() {
		return info;
	}
}

