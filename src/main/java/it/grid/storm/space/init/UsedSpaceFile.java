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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class UsedSpaceFile {

	private static Logger log = LoggerFactory.getLogger(UsedSpaceFile.class);

	private static final String PATTERN_RFC2822 = "EEE, dd MMM yyyy HH:mm:ss Z";
	private static final String PATTERN_DEFAULT = "EEE MMM dd HH:mm:ss z yyyy";

	private enum Key {
		usedsize, checktime
	}
	
	private Ini iniFile;

	public UsedSpaceFile(String iniFilePath) throws InvalidFileFormatException,
		IOException {

		Preconditions.checkNotNull(iniFilePath, "Received null iniFilePath");
		iniFile = new Ini(new File(iniFilePath));
	}

	public File getIniFile() {
		
		return iniFile.getFile();
	}
	
	public List<String> getDefinedSA() {
		
		return new ArrayList<String>(iniFile.keySet());
	}
	
	public SaUsedSize getSAUsedSize(String saName) {
		
		Preconditions.checkNotNull(saName,"Received null saName parameter");
		Preconditions.checkArgument(hasSA(saName), 
			saName + " section not found into used-space ini file");
		
		Section section = iniFile.get(saName);
		Long usedSpace = null;
		Date updateTime = null;
		for (String optionKey : section.keySet()) {
			switch (Key.valueOf(optionKey)) {
			case usedsize:
				try {
					usedSpace = Long.parseLong(section.get(optionKey));
				} catch (NumberFormatException e) {
					log.error(
						"{}.{} is not a valid Long value. NumberFormatException: {}",
						saName, optionKey, e.getMessage());
				}
				break;
			case checktime:
				try {
					updateTime = parseDate(section.get(optionKey));
				} catch (ParseException e) {
					log.error("{}.{} is not a valid Date value. ParseException: {}",
						saName, optionKey, e.getMessage());
				}
				break;
			default:
				log.error("{}.{} is not recognized as a valid key.", saName, optionKey);
				break;
			}
		}
		return new SaUsedSize(saName, usedSpace, updateTime);
	}
	
	public boolean hasSA(String saName) {
		
		return getDefinedSA().contains(saName);
	}

	/**
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	private Date parseDate(String dateStr) throws ParseException {

		Preconditions.checkNotNull(dateStr,"Received null dateStr parameter");
		
		SimpleDateFormat formatRFC2822 = new SimpleDateFormat(PATTERN_RFC2822);
		SimpleDateFormat formatDefault = new SimpleDateFormat(PATTERN_DEFAULT);

		try {
			return formatRFC2822.parse(dateStr);
		} catch (ParseException e) {
			log.debug("Unable to parse date {} using RFC2822 "
				+ "formatter.ParseException: {} Attempting with default formatter", 
				dateStr, e.getMessage(), e);
			try {
				return formatDefault.parse(dateStr);
			} catch (ParseException e2) {
				log.warn("Unable to parse the date {} with default formatter. "
					+ "ParseException: {}", dateStr, e.getMessage());
				throw e;
			}
		}
	}
	
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

			Preconditions.checkNotNull(saName, "Received null saName");
			Preconditions.checkNotNull(usedSize, "Received null usedSize");
			this.saName = saName;
			this.usedSize = usedSize;
			this.updateTime = updateTime; //optional
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

			return updateTime != null;
		}
	}
	
}
