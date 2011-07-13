package it.grid.storm.space.sensor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NativeDU {

    private static final Logger log = LoggerFactory.getLogger(NativeDU.class);
	
	public static long getUsedSize(String path) {
		long result = 0;
		ExecCommand ec = new ExecCommand("du -s "+ path);
		String output = ec.getOutput();
		if (output!=null) {
			String[] outputArray = output.split(" ");
			try {
			  result = Long.valueOf(outputArray[0]);
			} catch (NumberFormatException nfe) {
				log.warn("Interrupt occours when acquiring Semaphore the execution on a native command. "+nfe.getMessage());				
			}	 
		}
		return result * 1024;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path;
		if ((args==null)||(args.length==0)) {			
			path = System.getProperty("user.dir");
			System.out.println("path  : "+path);
		} else {
			path =args[0];
		}
		ExecCommand ec = new ExecCommand("du -s "+ path);
		String output = ec.getOutput();
		System.out.println("du output = "+output);
		String error = ec.getError();
		if (error!=null){
			System.out.println("du error = "+error);	
		}
	}

}
