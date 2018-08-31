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

package it.grid.storm.synchcall;

import static it.grid.storm.filesystem.RandomWaitFilesystemAdapter.maybeWrapFilesystem;

import org.slf4j.Logger;

import it.grid.storm.filesystem.Filesystem;
import it.grid.storm.filesystem.FilesystemIF;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.MetricsFilesystemAdapter;
import it.grid.storm.filesystem.swig.genericfs;
import it.grid.storm.metrics.StormMetricRegistry;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;

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
public class FileSystemUtility {

	private static Logger log = NamespaceDirector.getLogger();

	public static LocalFile getLocalFileByAbsolutePath(String absolutePath)
		throws NamespaceException {

		LocalFile file = null;
		VirtualFSInterface vfs = null;
		genericfs fsDriver = null;
		FilesystemIF fs = null;
		try {
			vfs = NamespaceDirector.getNamespace().resolveVFSbyAbsolutePath(
				absolutePath);
		} catch (NamespaceException ex) {
			log.error("Unable to retrieve VFS by Absolute Path", ex);
		}
		if (vfs == null) {
			throw new NamespaceException("No VFS found in StoRM for this file :'"
				+ absolutePath + "'");
		}

		try {
			fsDriver = (genericfs) (vfs.getFSDriver()).newInstance();
			
			FilesystemIF wrappedFs = new Filesystem(fsDriver);
			
			wrappedFs = maybeWrapFilesystem(wrappedFs);
			
			fs = new MetricsFilesystemAdapter(wrappedFs, 
			  StormMetricRegistry.INSTANCE.getRegistry());
			
			file = new LocalFile(absolutePath, fs);
		} catch (NamespaceException ex1) {
			log.error("Error while retrieving FS driver", ex1);
		} catch (IllegalAccessException ex1) {
			log.error("Error while using reflection in FS Driver", ex1);
		} catch (InstantiationException ex1) {
			log.error("Error while instancing new FS driver", ex1);
		}

		return file;
	}

}
