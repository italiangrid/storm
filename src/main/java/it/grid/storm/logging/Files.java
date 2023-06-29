/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class Files {

	private Files() {}

	/**
	 * A convenience method for getting a file and requiring it to be a readable
	 * file. This is equivalent to calling
	 * <code>getFile(filePath, true, true, true, false)</code>.
	 * 
	 * @param filePath
	 *          the path to the file
	 * 
	 * @return the file
	 * 
	 * @throws IOException
	 *           thrown if the file is a directory, does not exist, or can not be
	 *           read
	 */
	public static File getReadableFile(String filePath) throws IOException {
		return getFile(filePath, true, true, true, false);
	}

	/**
	 * Gets the file object associated with the path.
	 * 
	 * @param filePath
	 *          the file path
	 * @param requireFile
	 *          whether the given path is required to be a file instead of a
	 *          directory
	 * @param requireExistance
	 *          whether the given file/directory must exist already
	 * @param requireReadable
	 *          whether the given file/directory must be readable
	 * @param requireWritable
	 *          whether the given file/directory must be writable
	 * 
	 * @return the created file
	 * 
	 * @throws IOException
	 *           thrown if existance, reabability, or writability is required but
	 *           not met
	 */
	public static File getFile(String filePath, boolean requireFile,
		boolean requireExistance, boolean requireReadable, boolean requireWritable)
		throws IOException {

		String path = Strings.safeTrimOrNullString(filePath);
		if (path == null) {
			throw new IOException("The file path may not be empty");
		}

		File file = new File(filePath);

		if (requireExistance && !file.exists()) {
			throw new IOException("The file '" + filePath + "' does not exist.");
		}

		if (requireFile && !file.isFile()) {
			throw new IOException("The path '" + filePath
				+ "' is a directory not a file");
		}

		if (requireReadable && !file.canRead()) {
			throw new IOException("The file '" + filePath + "' is not readable.");
		}

		if (requireWritable && !file.canWrite()) {
			throw new IOException("The file '" + filePath + "' is not writable.");
		}

		return file;
	}

	/**
	 * Reads the contents of a file in to a byte array.
	 * 
	 * @param file
	 *          file to read
	 * @return the byte contents of the file
	 * 
	 * @throws IOException
	 *           throw if there is a problem reading the file in to the byte array
	 */
	public static byte[] fileToByteArray(File file) throws IOException {

		long numOfBytes = file.length();

		if (numOfBytes > Integer.MAX_VALUE) {
			throw new IOException("File is to large to be read in to a byte array");
		}

		byte[] bytes = new byte[(int) numOfBytes];
		FileInputStream ins = new FileInputStream(file);
		int offset = 0;
		int numRead = 0;
		do {
			numRead = ins.read(bytes, offset, bytes.length - offset);
			offset += numRead;
		} while (offset < bytes.length && numRead >= 0);

		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}

		ins.close();
		return bytes;
	}
}