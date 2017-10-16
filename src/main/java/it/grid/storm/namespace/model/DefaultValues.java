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

package it.grid.storm.namespace.model;

import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.namespace.DefaultValuesInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceType;

import org.slf4j.Logger;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: INFN-CNAF and ICTP/eGrid project
 * </p>
 * 
 * @author Riccardo Zappi
 * @version 1.0
 */
public class DefaultValues implements DefaultValuesInterface {

	private Logger log = NamespaceDirector.getLogger();
	private SpaceDefault spaceDefault;
	private FileDefault fileDefault;

	public DefaultValues(SpaceDefault spaceDefault, FileDefault fileDefault) {

		this.spaceDefault = spaceDefault;
		this.fileDefault = fileDefault;
	}

	public DefaultValues() {

		try {
			this.spaceDefault = new SpaceDefault();
		} catch (NamespaceException ex) {
			log.error("Something was wrong building default Space Default Values");
		}
		try {
			this.fileDefault = new FileDefault();
		} catch (NamespaceException ex1) {
			log.error("Something was wrong building default File Default Values");
		}
	}

	public void setSpaceDefaults(String type, long lifetime, long guarsize,
		long totalsize) throws NamespaceException {

		this.spaceDefault = new SpaceDefault(type, lifetime, guarsize, totalsize);
	}

	public void setFileDefaults(String type, long lifetime)
		throws NamespaceException {

		this.fileDefault = new FileDefault(type, lifetime);
	}

	public TLifeTimeInSeconds getDefaultSpaceLifetime() {

		return spaceDefault.lifetime;
	}

	public TSpaceType getDefaultSpaceType() {

		return spaceDefault.type;
	}

	public TSizeInBytes getDefaultGuaranteedSpaceSize() {

		return spaceDefault.guarsize;
	}

	public TSizeInBytes getDefaultTotalSpaceSize() {

		return spaceDefault.totalsize;
	}

	public TLifeTimeInSeconds getDefaultFileLifeTime() {

		return fileDefault.lifetime;
	}

	public TFileStorageType getDefaultFileType() {

		return fileDefault.type;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		String sep = System.getProperty("line.separator");
		sb.append("   DEF. Space Lifetime       : "
			+ this.getDefaultSpaceLifetime() + sep);
		sb.append("   DEF. Space Guar. size     : "
			+ this.getDefaultGuaranteedSpaceSize() + sep);
		sb.append("   DEF. Space Tot. size      : "
			+ this.getDefaultTotalSpaceSize() + sep);
		sb.append("   DEF. Space Type           : " + this.getDefaultSpaceType()
			+ sep);
		sb.append("   DEF. File Lifetime        : " + this.getDefaultFileLifeTime()
			+ sep);
		sb.append("   DEF. File Type            : " + this.getDefaultFileType()
			+ sep);
		return sb.toString();
	}

	/**************************************************************************
	 * INNER CLASS
	 **************************************************************************/

	/**
	 * 
	 * <p>
	 * Title:
	 * </p>
	 * 
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * <p>
	 * Copyright: Copyright (c) 2006
	 * </p>
	 * 
	 * <p>
	 * Company: INFN-CNAF and ICTP/eGrid project
	 * </p>
	 * 
	 * @author Riccardo Zappi
	 * @version 1.0
	 */
	public class SpaceDefault {

		private TSpaceType type = null;
		private TLifeTimeInSeconds lifetime;
		private TSizeInBytes guarsize;
		private TSizeInBytes totalsize;

		public SpaceDefault() throws NamespaceException {

			// Build space type
			this.type = TSpaceType.getTSpaceType(DefaultValues.DEFAULT_SPACE_TYPE);
			// Build lifetime
			try {
				this.lifetime = TLifeTimeInSeconds.make(DefaultValues.DEFAULT_SPACE_LT,
					TimeUnit.SECONDS);
			} catch (IllegalArgumentException ex) {
				log.error(" Default Space Lifetime was wrong ");
				throw new NamespaceException(
					"Space Lifetime invalid argument in Namespace configuration.", ex);
			}
			// Build of Guaranteed Space Size
			try {
				this.guarsize = TSizeInBytes.make(
					DefaultValues.DEFAULT_SPACE_GUAR_SIZE, SizeUnit.BYTES);
			} catch (InvalidTSizeAttributesException ex1) {
				log.error(" Default  Guaranteed Space Size was wrong ");
				throw new NamespaceException(
					" Guaranteed Space Size invalid argument in Namespace configuration.",
					ex1);
			}

			// Build of Total Space Size
			try {
				this.totalsize = TSizeInBytes.make(
					DefaultValues.DEFAULT_SPACE_TOT_SIZE, SizeUnit.BYTES);
			} catch (InvalidTSizeAttributesException ex2) {
				log.error(" Default Total Space Size was wrong ");
				throw new NamespaceException(
					"Total Space Size invalid argument in Namespace configuration.", ex2);
			}
		}

		public SpaceDefault(String type, long lifetime, long guarsize,
			long totalsize) throws NamespaceException {

			// Build space type
			this.type = TSpaceType.getTSpaceType(type);

			// Build lifetime
			try {
				this.lifetime = TLifeTimeInSeconds.make(lifetime, TimeUnit.SECONDS);
			} catch (IllegalArgumentException ex) {
				log.error(" Default Space Lifetime was wrong ");
				throw new NamespaceException(
					"Space Lifetime invalid argument in Namespace configuration.", ex);
			}

			// Checking of size
			if (guarsize > totalsize) {
				log
					.error(" Default Space Guaranteed Size is greater of Space Total Size !");
				throw new NamespaceException(
					"Space size (Guar and Total) are invalid in Namespace configuration.");
			}

			// Build of Guaranteed Space Size
			try {
				this.guarsize = TSizeInBytes.make(guarsize, SizeUnit.BYTES);
			} catch (InvalidTSizeAttributesException ex1) {
				log.error(" Default  Guaranteed Space Size was wrong ");
				throw new NamespaceException(
					" Guaranteed Space Size invalid argument in Namespace configuration.",
					ex1);
			}

			// Build of Total Space Size
			try {
				this.totalsize = TSizeInBytes.make(totalsize, SizeUnit.BYTES);
			} catch (InvalidTSizeAttributesException ex2) {
				log.error(" Default Total Space Size was wrong ");
				throw new NamespaceException(
					"Total Space Size invalid argument in Namespace configuration.", ex2);
			}
		}

		public TSpaceType getSpaceType() {

			return type;
		}

		public TLifeTimeInSeconds getLifetime() {

			return lifetime;
		}

		public TSizeInBytes guarsize() {

			return guarsize;
		}

		public TSizeInBytes totalsize() {

			return totalsize;
		}

	}

	/**
	 * 
	 * <p>
	 * Title:
	 * </p>
	 * 
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * <p>
	 * Copyright: Copyright (c) 2006
	 * </p>
	 * 
	 * <p>
	 * Company: INFN-CNAF and ICTP/eGrid project
	 * </p>
	 * 
	 * @author Riccardo Zappi
	 * @version 1.0
	 */
	public class FileDefault {

		private TFileStorageType type = null;
		private TLifeTimeInSeconds lifetime;

		public FileDefault() throws NamespaceException {

			// Build space type
			this.type = TFileStorageType
				.getTFileStorageType(DefaultValues.DEFAULT_FILE_TYPE);

			// Build lifetime
			try {
				this.lifetime = TLifeTimeInSeconds.make(DefaultValues.DEFAULT_FILE_LT,
					TimeUnit.SECONDS);
			} catch (IllegalArgumentException ex) {
				log.error(" Default Space Lifetime was wrong ");
				throw new NamespaceException(
					"Space Lifetime invalid argument in Namespace configuration.", ex);
			}

		}

		public FileDefault(String type, long lifetime) throws NamespaceException {

			// Build space type
			this.type = TFileStorageType.getTFileStorageType(type);

			// Build lifetime
			try {
				this.lifetime = TLifeTimeInSeconds.make(lifetime, TimeUnit.SECONDS);
			} catch (IllegalArgumentException ex) {
				log.error(" Default Space Lifetime was wrong ");
				throw new NamespaceException(
					"Space Lifetime invalid argument in Namespace configuration.", ex);
			}
		}

		public TFileStorageType getFileStorageType() {

			return type;
		}

		public TLifeTimeInSeconds getLifetime() {

			return lifetime;
		}

	}

}
