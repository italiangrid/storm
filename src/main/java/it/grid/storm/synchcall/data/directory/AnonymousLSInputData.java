package it.grid.storm.synchcall.data.directory;

import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.synchcall.data.AbstractInputData;

public class AnonymousLSInputData extends AbstractInputData implements
	LSInputData {

	private final ArrayOfSURLs surlArray;
	private final Boolean fullDetailedList;
	private final Boolean allLevelRecursive;
	private final Integer numOfLevels;
	private final Integer offset;
	private final Integer count;
	private final boolean storageTypeSpecified;

	public AnonymousLSInputData(ArrayOfSURLs surlArray,
		TFileStorageType fileStorageType, Boolean fullDetList, Boolean allLev,
		Integer numOfLev, Integer offset, Integer count)
		throws IllegalArgumentException {

		if (surlArray == null) {
			throw new IllegalArgumentException(
				"Unable to create the object, invalid arguments: surlArray="
					+ surlArray);
		}
		this.surlArray = surlArray;
		this.storageTypeSpecified = (fileStorageType != null && !fileStorageType
			.equals(TFileStorageType.EMPTY));
		this.fullDetailedList = fullDetList;
		this.allLevelRecursive = allLev;
		this.numOfLevels = numOfLev;
		this.offset = offset;
		this.count = count;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.synchcall.data.directory.LSInputData#getSurlArray()
	 */

	@Override
	public ArrayOfSURLs getSurlArray() {

		return surlArray;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.synchcall.data.directory.LSInputData#getStorageTypeSpecified
	 * ()
	 */
	@Override
	public boolean getStorageTypeSpecified() {

		return this.storageTypeSpecified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.synchcall.data.directory.LSInputData#getFullDetailedList()
	 */
	@Override
	public Boolean getFullDetailedList() {

		return this.fullDetailedList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.synchcall.data.directory.LSInputData#getAllLevelRecursive()
	 */
	@Override
	public Boolean getAllLevelRecursive() {

		return this.allLevelRecursive;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.synchcall.data.directory.LSInputData#getNumOfLevels()
	 */
	@Override
	public Integer getNumOfLevels() {

		return this.numOfLevels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.synchcall.data.directory.LSInputData#getOffset()
	 */
	@Override
	public Integer getOffset() {

		return this.offset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.synchcall.data.directory.LSInputData#getCount()
	 */
	@Override
	public Integer getCount() {

		return this.count;
	}
}
