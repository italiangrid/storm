package it.grid.storm.wrapper;

import java.io.*;

/**
 * Class for Compact space native Library.
 *
 */
public class CompactSpaceWrapper
{

native int compactSpace(String pathToFile);
static  {
	//	System.out.println("File: "+pathToFile+", size = "+ size );
		System.loadLibrary("compnativelib");
	}


}
