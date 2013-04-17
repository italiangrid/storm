package it.grid.storm.info;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class TestSpaceInfoManager {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		List<String> paths = new ArrayList<String>();
		paths.add(System.getProperty("user.dir"));
		// paths.add(System.getProperty("user.dir")+File.separator+"..");
		paths.add(System.getProperty("user.dir") + File.separator + ".."
			+ File.separator + "TreeTraversal");
		SpaceInfoManager.getInstance().startTest(paths);
		// try {
		// Thread.sleep(1500);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// SpaceInfoManager.stop();
	}

}
