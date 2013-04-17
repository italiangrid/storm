package it.grid.storm.space.sensor;

import java.io.File;

public class TrivialDU {

	private static String path;
	private static long nrFiles = 0;
	private static long nrNodes = 0;
	private static long size = 0;
	private static int maxDepth = -1;
	private static int maxBreadth = -1;
	private static long leaves = 0;

	public static void visitAllFiles(File dir, int depth) {

		if (depth > maxDepth) {
			maxDepth = depth;
		}
		if (dir.isDirectory()) {
			nrNodes++;
			String[] children = dir.list();
			if (children != null) {
				int breadth = children.length;
				if (breadth > maxBreadth) {
					maxBreadth = breadth;
				}
				for (int i = 0; i < children.length; i++) {
					visitAllFiles(new File(dir, children[i]), depth + 1);
				}
			} else {
				leaves++;
			}
		} else {
			process(dir);
		}
	}

	private static void process(File dir) {

		size += dir.length();
		nrFiles++;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if ((args == null) || (args.length == 0)) {
			path = System.getProperty("user.dir");
			System.out.println("path  : " + path);
		} else {
			path = args[0];
		}
		visitAllFiles(new File(path), 0);
		System.out.println("path     : " + path);
		System.out.println(" - nrFiles    : " + nrFiles);
		System.out.println(" - nrNodes    : " + nrNodes);
		System.out.println(" - size       : " + size);
		System.out.println(" - maxDepth   : " + maxDepth);
		System.out.println(" - maxBreadth : " + maxDepth);
		System.out.println(" - leaves     : " + leaves);

	}

}
