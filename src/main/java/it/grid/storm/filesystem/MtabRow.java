/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package it.grid.storm.filesystem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michele Dibenedetto
 * 
 */
public class MtabRow {

	private final String device;
	private final String mountPoint;
	private final String fileSystem;
	private final List<String> mountOptions = new ArrayList<String>();
	private final boolean dump;
	private final int fsckOrderPosition;

	public MtabRow(String device, String mountPoint, String fileSystem,
		List<String> mountOptions, boolean dump, int fsckOrderPosition) {

		if (device == null || device.trim().equals("") || mountPoint == null
			|| mountPoint.trim().equals("") || fileSystem == null
			|| fileSystem.trim().equals("")) {
			throw new IllegalArgumentException(
				"Received invalid arguments : device = " + device + " mountPoint = "
					+ mountPoint + " fileSystem = " + fileSystem);
		}
		this.device = device;
		this.mountPoint = mountPoint;
		this.fileSystem = fileSystem;
		this.mountOptions.addAll(mountOptions);
		this.dump = dump;
		this.fsckOrderPosition = fsckOrderPosition;
	}

	/**
	 * Build from ordered parameter list
	 * 
	 * @param elementsList
	 *          [device , mountPoint, fileSystem, mountOptions, dump,
	 *          fsckOrderPosition]
	 * @throws IllegalArgumentException
	 */
	public MtabRow(List<String> elementsList) throws IllegalArgumentException {

		if (elementsList.size() < 5) {
			throw new IllegalArgumentException(
				"Received an argument list of few than 5 elements (mandatory) : "
					+ elementsList.toString());
		}
		for (int i = 0; i < 5; i++) {
			/*
			 * all the arguments must be strings not empty apart from the one at index
			 * 4 (mount options)
			 */
			if ((elementsList.get(i) == null || elementsList.get(i).trim().length() == 0)
				&& i != 3) {
				throw new IllegalArgumentException(
					"Received an argument list where element at index " + i
						+ " is invalid : \'" + elementsList.get(i) + "\'");
			}
		}
		this.device = elementsList.get(0);
		this.mountPoint = elementsList.get(1);
		this.fileSystem = elementsList.get(2);
		if (elementsList.get(3) != null && elementsList.get(3).trim().length() > 0) {
			String[] mountOptionsArray = elementsList.get(3).trim().split(",");
			for (String mountOption : mountOptionsArray) {
				if (mountOption.trim().length() > 0) {
					mountOptions.add(mountOption.trim());
				}
			}
		}
		Integer dumpValue;
		try {
			dumpValue = Integer.parseInt(elementsList.get(4));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
				"Received an illegal argument at index 4. "
					+ "It as to be an integer (the dump value), received value : "
					+ elementsList.get(4) + " . NumberFormatException : "
					+ e.getMessage());
		}

		if (dumpValue != 1 && dumpValue != 0) {
			throw new IllegalArgumentException(
				"Received an illegal argument at index 4. "
					+ "It can be only 0 or 1, received value : " + elementsList.get(4));
		}
		this.dump = (dumpValue == 0 ? false : true);
		try {
			this.fsckOrderPosition = Integer.parseInt(elementsList.get(5));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
				"Received an illegal argument at index 5. "
					+ "It as to be an integer (the fsck order position value), received value : "
					+ elementsList.get(4) + " . NumberFormatException : "
					+ e.getMessage());
		}
	}

	/**
	 * @return the device
	 */
	public final String getDevice() {

		return device;
	}

	/**
	 * @return the mountPoint
	 */
	public final String getMountPoint() {

		return mountPoint;
	}

	/**
	 * @return the fileSystem
	 */
	public final String getFileSystem() {

		return fileSystem;
	}

	/**
	 * @return the mountOptions
	 */
	public final List<String> getMountOptions() {

		return mountOptions;
	}

	/**
	 * @return the dump
	 */
	public final boolean isDump() {

		return dump;
	}

	/**
	 * @return the fsckOrderPosition
	 */
	public final int getFsckOrderPosition() {

		return fsckOrderPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "MtabRow [device=" + device + ", mountPoint=" + mountPoint
			+ ", fileSystem=" + fileSystem + ", mountOptions=" + mountOptions
			+ ", dump=" + dump + ", fsckOrderPosition=" + fsckOrderPosition + "]";
	}
}
