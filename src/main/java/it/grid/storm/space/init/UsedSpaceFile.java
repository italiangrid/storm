/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.space.init;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import it.grid.storm.config.IniReader;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 * 
 */
public class UsedSpaceFile {

	private static Logger log = LoggerFactory.getLogger(UsedSpaceFile.class);
	public static final String INI_FILENAME = "used-space.ini";

	private static final String PATTERN_RFC2822 = "EEE, dd MMM yyyy HH:mm:ss Z";
	private static final String PATTERN_DEFAULT = "EEE MMM dd HH:mm:ss z yyyy";

	private final List<String> saNames;
	private final Ini iniFile;

	/**
	 * @author Michele Dibenedetto
	 * 
	 */
	private enum Key {
		USED_SIZE("usedsize"), CHECK_TIME("checktime"), FAKE_KEY(null), ;

		private final String name;

		private Key(String name) {

			this.name = name;
		}

		String getName() {

			return this.name;
		}

		/**
		 * @param keyName
		 * @return
		 */
		private static Key getKey(String keyName) throws IllegalArgumentException {

			if (keyName == null) {
				log.error("Received null keyName parameter");
				throw new IllegalArgumentException("Received null keyName parameter");
			}
			Key key = Key.FAKE_KEY;
			for (Key keyElement : Key.values()) {
				if (!keyElement.equals(Key.FAKE_KEY)
					&& keyElement.getName().equals(keyName.trim())) {
					key = keyElement;
					break;
				}
			}
			return key;
		}
	}

	/**
	 * @param saNames
	 */
	public UsedSpaceFile(List<String> saNames) {

		this.saNames = new ArrayList<String>();
		this.saNames.addAll(saNames);
		this.iniFile = retrieveUsedSpaceIniFile();
	}

	/**
	 * @return
	 */
	private Ini retrieveUsedSpaceIniFile() {

		Ini iniFile = null;
		try {
			iniFile = new IniReader().getIniFile(INI_FILENAME);
		} catch (IllegalArgumentException e) {
			log.error("Unable to get the Ini file handle for file {}  in the default ini folder {}" , INI_FILENAME
				, IniReader.DEFAULT_CONF_PATH , e);
		}
		return iniFile;
	}

	/**
	 * @author Michele Dibenedetto
	 * 
	 */
	public class SaUsedSize {

		private final String saName;
		private final Long usedSize;
		private final Date updateTime;

		/**
		 * @param saName
		 * @param usedSize
		 * @param updateTime
		 */
		public SaUsedSize(String saName, Long usedSize, Date updateTime) {

			this.saName = saName;
			this.usedSize = usedSize;
			this.updateTime = updateTime;
		}

		/**
		 * @return the saName
		 */
		public String getSaName() {

			return saName;
		}

		/**
		 * @return the usedSize
		 */
		public Long getUsedSize() {

			return usedSize;
		}

		/**
		 * @return the updateTime
		 */
		public Date getUpdateTime() {

			return updateTime;
		}

		public boolean hasUpdateTime() {

			return this.updateTime != null;
		}
	}

	/**
	 * Main method used to parse INI file and update the Database - retrieve all
	 * the SA defined in INI file - consider only SA defined also in Namespace.xml
	 * - for each SA get the used-size and update-time (when defined)
	 * 
	 * @return
	 */
	public List<SaUsedSize> getDefinedSizes() {

		LinkedList<SaUsedSize> saUsedSizeList = new LinkedList<SaUsedSize>();
		log.debug("Initializing '{}' not initialized storage areas" , saNames.size());
		/* Log initialized storage areas */

		if (iniFile != null) {
			List<String> storageAreas = getSAdefined(iniFile);
			for (String saName : storageAreas) {
				Section section = iniFile.get(saName);
				if (section != null) {
					Long usedSpace = null;
					Date updateTime = null;
					for (String optionKey : section.keySet()) {
						switch (Key.getKey(optionKey)) {
						case USED_SIZE:
							if (section.length(optionKey) > 1) {
								log.warn("Unable to get {} value from section [{}] . Check for repeated declarations of this Section or repeated declarations of {} in this section" , Key.USED_SIZE.getName()
									, saName , Key.USED_SIZE.getName());
							} else {
								try {
									usedSpace = Long.parseLong(section.get(optionKey));
								} catch (NumberFormatException e) {
									log
										.warn("{} from section [{}] doesn't contain a valid Long value. NumberFormatException: " , Key.USED_SIZE.getName() , saName , e.getMessage());
								}
							}
							break;
						case CHECK_TIME:
							if (section.length(optionKey) > 1) {
								log.warn("Unable to get {} value from section [{}]. Check for repeated declarations of this Section or repeated declarations of {} in this section" , Key.CHECK_TIME.getName()	, saName
									, Key.CHECK_TIME.getName());
							} else {
								try {
									updateTime = parseDate(section.get(optionKey));
								} catch (ParseException e) {
									log.warn("{} from section [{}] doesn't contain a valid Date value. ParseException: {}" , Key.CHECK_TIME.getName() , saName , e.getMessage());
								}
							}
							break;
						default:
							log.warn("Key: '{}' from section [{}] is not recognized as a valid key." , optionKey , saName);
							break;
						}
					}
					if (usedSpace != null) {
						saUsedSizeList.add(new SaUsedSize(saName, usedSpace, updateTime));
					}
				} else {
					log.warn("Unable to find section named {} in the ini file" , saName);
				}
			}
		} else {
			log
				.info("Unable to get a valid Ini file handle, no storage areas can be initialized");
		}
		return saUsedSizeList;
	}

	/**
	 * @param iniFile
	 * @return
	 * @throws IllegalArgumentException
	 */
	private ArrayList<String> getSAdefined(Ini iniFile)
		throws IllegalArgumentException {

		if (iniFile == null) {
			log.error("Received null iniFile argument");
			throw new IllegalArgumentException("Received null iniFile argument");
		}
		ArrayList<String> storageAreas = new ArrayList<String>();

		log.debug("Number of ini file sections: {}" , iniFile.size());
		for (String sectionName : iniFile.keySet()) {
			log.debug("SA candidate from ini file is [{}]" , sectionName);
			// Check the existence in namespace.xml
			if (saNames.contains(sectionName)) {
				storageAreas.add(sectionName);
			} else {
				log.info("SA defined in the ini file is [{}] is not going to be updated" , sectionName);
			}
		}
		if (storageAreas.size() > 0) {
			log.info("Found '{}' valid storage area defined in used-space.ini" , storageAreas.size());
		} else {
			log
				.info("Found no defined Storage Area to initialize using used-size.ini file.");
		}
		return storageAreas;
	}

	/**
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	private Date parseDate(String dateStr) throws ParseException {

		SimpleDateFormat formatRFC2822 = new SimpleDateFormat(PATTERN_RFC2822);
		SimpleDateFormat formatDefault = new SimpleDateFormat(PATTERN_DEFAULT);
		ParseException moreSignificantException;

		Date dateUpdate = new Date();
		try {
			dateUpdate = formatRFC2822.parse(dateStr);
			return dateUpdate;
		} catch (ParseException e) {
			log.debug("Unable to parse date {} using RFC2822 formatter.ParseException: {} Attempting with default formatter" , dateStr , e.getMessage(),e);
			moreSignificantException = e;
		}
		try {
			dateUpdate = formatDefault.parse(dateStr);
		} catch (ParseException e) {
			log.warn("Unable to parse the date {} with default formatter. ParseException: {}" , dateStr , e.getMessage());
			throw moreSignificantException;
		}
		return dateUpdate;
	}
}
