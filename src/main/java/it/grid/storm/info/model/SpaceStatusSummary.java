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
package it.grid.storm.info.model;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.space.StorageSpaceData;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.stream.XMLStreamException;

import org.codehaus.jettison.AbstractXMLStreamWriter;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceStatusSummary {

	protected final String saAlias;
	/** defined in config/db (static value) **/
	protected final long totalSpace;
	/** defined in config/db (static value) **/
	// published by DIP

	protected long usedSpace = -1;
	/** info retrieved by sensors **/
	// published by DIP
	protected long unavailableSpace = -1;
	/** info retrieved by sensors **/
	protected long reservedSpace = -1;
	/** info retrieved from DB **/
	// published by DIP SETTED TO ZERO BECAUSE CURRENTLY RETURN FAKE VALUES
	// For now do not consider the reserved space, a better management is needed

	private static final ReservedSpaceCatalog catalog = ReservedSpaceCatalog.getInstance();

	private static final Logger log = LoggerFactory
		.getLogger(SpaceStatusSummary.class);

	/*****************************
	 * Constructors
	 */

	/**
	 * @param saAlias
	 * @param totalSpace
	 * @throws IllegalArgumentException
	 */
	public SpaceStatusSummary(String saAlias, long totalSpace)
		throws IllegalArgumentException {

		if (totalSpace < 0 || saAlias == null) {
			log
				.error("Unable to create SpaceStatusSummary. Received illegal parameter: saAlias: "
					+ saAlias + " totalSpace: " + totalSpace);
			throw new IllegalArgumentException(
				"Unable to create SpaceStatusSummary. Received illegal parameter");
		}
		this.saAlias = saAlias;
		this.totalSpace = totalSpace;
	}

	private SpaceStatusSummary(String saAlias, long usedSpace,
		long unavailableSpace, long reservedSpace, long totalSpace) {

		this.saAlias = saAlias;
		this.usedSpace = usedSpace;
		this.unavailableSpace = unavailableSpace;
		this.reservedSpace = reservedSpace;
		this.totalSpace = totalSpace;
	}

	/**
	 * Produce a SpaceStatusSummary with fields matching exactly the ones
	 * available on the database
	 * 
	 * @param saAlias
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SpaceStatusSummary createFromDB(String saAlias)
		throws IllegalArgumentException {

		StorageSpaceData storageSpaceData = catalog.getStorageSpaceByAlias(saAlias);
		if (storageSpaceData == null) {
			throw new IllegalArgumentException(
				"Unable to find a storage space row for alias \'" + saAlias
					+ "\' from storm Database");
		} else {
			if (!storageSpaceData.isInitialized()) {
				log
					.warn("Building the SpaceStatusSummary from non initialized space with alias \'"
						+ saAlias + "\'");
			}
			SpaceStatusSummary summary = new SpaceStatusSummary(saAlias,
				storageSpaceData.getUsedSpaceSize().value(), storageSpaceData
					.getUnavailableSpaceSize().value(), storageSpaceData
					.getReservedSpaceSize().value(), storageSpaceData.getTotalSpaceSize()
					.value());
			return summary;
		}
	}

	/*****************************
	 * GETTER methods
	 ****************************/

	/**
	 * @return the saAlias
	 */
	public String getSaAlias() {

		return saAlias;
	}

	/**
	 * busySpace = used + unavailable + reserved
	 * 
	 * @return the busySpace
	 */
	public long getBusySpace() {

		return this.usedSpace + this.reservedSpace + this.unavailableSpace;
	}

	/**
	 * availableSpace = totalSpace - busySpace
	 * 
	 * @return
	 */
	public long getAvailableSpace() {

		return this.totalSpace - this.getBusySpace();
	}

	/**
	 * @return the usedSpace
	 */
	public long getUsedSpace() {

		return usedSpace;
	}

	/**
	 * @return the unavailableSpace
	 */
	public long getUnavailableSpace() {

		return unavailableSpace;
	}

	/**
	 * @return the reservedSpace
	 */
	public long getReservedSpace() {

		return reservedSpace;
	}

	/**
	 * @return the totalSpace
	 */
	public long getTotalSpace() {

		return totalSpace;
	}

	/**
	 * Real One freeSpace = totalSpace - used - reserved For now... freeSpace =
	 * totalSpace - used
	 * 
	 * @return the freeSpace
	 */
	public long getFreeSpace() {

		if (this.totalSpace >= 0) {
			// For now do not consider the reserved space, a better management is
			// needed
			// this.freeSpace = this.totalSpace - this.usedSpace - this.reservedSpace;
			return this.totalSpace - this.usedSpace;
		} else {
			return -1;
		}
	}

	/*****************************
	 * SETTER methods
	 ****************************/

	/**
	 * @param usedSpace
	 *          the usedSpace to set
	 */
	public void setUsedSpace(long usedSpace) {

		this.usedSpace = usedSpace;
	}

	/**
	 * @param unavailableSpace
	 *          the unavailableSpace to set
	 */
	public void setUnavailableSpace(long unavailableSpace) {

		this.unavailableSpace = unavailableSpace;
	}

	/**
	 * @param reservedSpace
	 *          the reservedSpace to set
	 */
	public void setReservedSpace(long reservedSpace) {

		this.reservedSpace = reservedSpace;
	}

	/*******************************
	 * JSON Building
	 */

	/**
	 * String saAlias; long busySpace; // busySpace = used + unavailable +
	 * reserved long usedSpace; //info retrieved by sensors long unavailableSpace;
	 * // info retrieved by sensors long reservedSpace; // info retrieved from DB
	 * long totalSpace; // defined in config/db (static value) long freeSpace; //
	 * freeSpace = totalSpace - used - reserved;
	 */
	public String getJsonFormat() {

		String result = "";
		StringWriter strWriter = new StringWriter();
		Configuration config = new Configuration();
		MappedNamespaceConvention con = new MappedNamespaceConvention(config);

		try {
			AbstractXMLStreamWriter w = new MappedXMLStreamWriter(con, strWriter);
			w.writeStartDocument();
			// start main element
			w.writeStartElement("sa-status");
			// Alias
			w.writeStartElement("alias");
			w.writeCharacters(this.getSaAlias());
			w.writeEndElement();
			// busy space
			w.writeStartElement("busy-space");
			w.writeCharacters("" + this.getBusySpace());
			w.writeEndElement();
			// used space
			w.writeStartElement("used-space");
			w.writeCharacters("" + this.getUsedSpace());
			w.writeEndElement();
			// unavailable space
			w.writeStartElement("unavailable-space");
			w.writeCharacters("" + this.getUnavailableSpace());
			w.writeEndElement();
			// reserved space
			w.writeStartElement("reserved-space");
			w.writeCharacters("" + this.getReservedSpace());
			w.writeEndElement();
			// total space
			w.writeStartElement("total-space");
			w.writeCharacters("" + this.getTotalSpace());
			w.writeEndElement();
			// free space
			w.writeStartElement("free-space");
			w.writeCharacters("" + this.getFreeSpace());
			w.writeEndElement();
			// available space
			w.writeStartElement("available-space");
			w.writeCharacters("" + this.getAvailableSpace());
			w.writeEndElement();
			// end main element
			w.writeEndElement();
			w.writeEndDocument();
			w.close();
		} catch (XMLStreamException e) {
			log
				.error("Unable to produce Json representation of the object. XMLStreamException: "
					+ e.getMessage());
		}
		try {
			strWriter.close();
		} catch (IOException e) {
			log
				.error("Unable to close the StringWriter for Json representation of the object. IOException: "
					+ e.getMessage());
		}
		result = strWriter.toString();
		return result;
	}

	@Override
	public String toString() {

		return "SpaceStatusSummary [getSaAlias()=" + getSaAlias()
			+ ", getBusySpace()=" + getBusySpace() + ", getAvailableSpace()="
			+ getAvailableSpace() + ", getUsedSpace()=" + getUsedSpace()
			+ ", getUnavailableSpace()=" + getUnavailableSpace()
			+ ", getReservedSpace()=" + getReservedSpace() + ", getTotalSpace()="
			+ getTotalSpace() + ", getFreeSpace()=" + getFreeSpace() + "]";
	}
}
