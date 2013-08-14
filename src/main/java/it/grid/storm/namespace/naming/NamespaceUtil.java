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

package it.grid.storm.namespace.naming;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NamespaceUtil {

	private static final Logger log = LoggerFactory
		.getLogger(NamespaceUtil.class);

	/**
	 * PRIVATE Constructor
	 */
	private NamespaceUtil() {

	}

	/**
	 * Compute the distance between two path. Return -1 when the two path are
	 * different completely.
	 * 
	 * @param path1
	 *          String
	 * @param path2
	 *          String
	 * @return int
	 */
	public static int computeDistanceFromPath(String path1, String path2) {

		return (new Path(path1)).distance(new Path(path2));
	}

	/**
	 * Retrieve all path elements within path
	 * 
	 * @param path
	 *          String
	 * @return Collection
	 */
	public static Collection<String> getPathElement(String path) {

		return (new Path(path)).getPathElements();
	}

	/**
	 * getFileName
	 * 
	 * @param stfn
	 *          String
	 * @return String
	 */
	public static String getFileName(String stfn) {

		if (stfn != null) {
			if (stfn.endsWith(NamingConst.SEPARATOR)) {
				return "";
			} else {
				Path path = new Path(stfn);
				int length = path.getLength();
				if (length > 0) {
					PathElement elem = path.getElementAt(length - 1);
					return elem.toString();
				} else {
					return "";
				}
			}
		} else {
			return "";
		}
	}

	/**
	 * Return all the VFS residing on a specified path (mount-point)
	 * 
	 * @param mountPointPath
	 * @return the set
	 */
	public static Collection<VirtualFSInterface> getResidentVFS(
		String mountPointPath) {

		Collection<VirtualFSInterface> vfsSet = Collections.emptySet();
		try {
			vfsSet = NamespaceDirector.getNamespace().getAllDefinedVFS();
		} catch (NamespaceException e) {
			log
				.error("Unable to add NamespaceFSAssociationCheck, a NamespaceException occurred during vfsSet retriving : "
					+ e.getMessage());
			return vfsSet;
		}
		for (VirtualFSInterface vfs : vfsSet) {
			String vfsRootPath;
			boolean enclosed;
			
				vfsRootPath = vfs.getRootPath();
				enclosed = NamespaceUtil.isEnclosed(mountPointPath, vfsRootPath);
				if (!enclosed) {
					vfsSet.remove(vfs);
				}
		}
		return vfsSet;
	}

	public static String consumeFileName(String file) {

		if (file != null) {
			if (file.endsWith(NamingConst.SEPARATOR)) {
				return file;
			} else {
				Path path = new Path(file);
				int length = path.getLength();
				if (length > 1) {
					return path.getSubPath(length - 1).getPath() + NamingConst.SEPARATOR;
				} else {
					return Path.PATH_SEPARATOR;
				}
			}
		} else {
			return Path.PATH_SEPARATOR;
		}
	}

	/**
	 * get
	 * 
	 * @param stfn
	 *          String
	 * @return String
	 */
	public static String getStFNPath(String stfn) {

		return consumeFileName(stfn);
	}

	public static String consumeElement(String stfnPath) {

		Path path = new Path(stfnPath);
		int length = path.getLength();
		if (length > 1) {
			return path.getSubPath(length - 1).getPath() + NamingConst.SEPARATOR;
		} else {
			return ""; // Path.PATH_SEPARATOR;
		}
	}

	public static String extractRelativePath(String root, String absolute) {

		if (absolute.startsWith(root)) {
			Path rootPath = new Path(root);
			int rootLength = rootPath.getLength();

			Path absPath = new Path(absolute);
			ArrayList<PathElement> elem = new ArrayList<PathElement>();

			for (int i = 0; i < absPath.getLength(); i++) {
				// Why use length and not compare single element?
				if (i >= rootLength) {
					elem.add(absPath.getElementAt(i));
				}
			}
			Path result = new Path(elem, false);

			return result.getPath();
		} else {
			return absolute;
		}
	}

	/**
	 * Is the first path within the second one?
	 * 
	 * @param root
	 * @param wrapperCandidate
	 * @return
	 */
	public static boolean isEnclosed(String root, String wrapperCandidate) {

		boolean result = false;
		Path rootPath = new Path(root);
		Path wrapperPath = new Path(wrapperCandidate);
		result = rootPath.isEnclosed(wrapperPath);
		return result;
	}

	/**
	 * ===================== INNER CLASSES ======================
	 */

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
	 */
	static class PathElement {

		private final String pathChunk;

		public PathElement(String path) {

			this.pathChunk = path;
		}

		public String getPathChunk() {

			return this.pathChunk;
		}

		@Override
		public int hashCode() {

			return this.pathChunk.hashCode();
		}

		@Override
		public boolean equals(Object obj) {

			boolean result = true;
			if (!(obj instanceof PathElement)) {
				result = false;
			} else {
				PathElement other = (PathElement) obj;
				result = (this.getPathChunk()).equals(other.getPathChunk());
			}
			return result;
		}

		@Override
		public String toString() {

			return pathChunk;
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
	 */
	private static class Path {

		private ArrayList<PathElement> path;
		private static String PATH_SEPARATOR = "/";
		public static final String[] EMPTY_STRING_ARRAY = {};
		public boolean directory;
		public boolean absolutePath;

		public Path() {

			this.path = new ArrayList<PathElement>();
			this.directory = false;
			this.absolutePath = true;
		}

		public Path(ArrayList<PathElement> path, boolean absolutePath) {

			this.path = path;
			this.directory = false;
			this.absolutePath = absolutePath;
		}

		public Path(String path) {

			// Factorize path into array of PathElement...
			if (path.startsWith(PATH_SEPARATOR)) {
				this.absolutePath = true;
			} else {
				this.absolutePath = false;
			}
			if (path.endsWith(PATH_SEPARATOR)) {
				this.directory = true;
			} else {
				this.directory = false;
			}

			String[] pathElements = factorizePath(path);
			if (pathElements != null) {
				// ...and build Path
				this.path = new ArrayList<PathElement>(pathElements.length);
				for (String pathElement : pathElements) {
					addPathElement(new PathElement(pathElement));
				}
			}
		}

		public String[] factorizePath(String path) {

			return toStringArray(path, PATH_SEPARATOR);
		}

		public Collection<String> getPathElements() {

			Collection<String> result = new ArrayList<String>(this.getLength());
			Iterator<PathElement> scan = path.iterator();
			PathElement p;
			while (scan.hasNext()) {
				p = (PathElement) scan.next();
				result.add(p.toString());
			}
			return result;
		}

		private String[] toStringArray(String value, String delim) {

			if (value != null) {
				return split(delim, value);
			} else {
				return EMPTY_STRING_ARRAY;
			}
		}

		private String[] split(String seperators, String list) {

			return split(seperators, list, false);
		}

		private String[] split(String seperators, String list, boolean include) {

			StringTokenizer tokens = new StringTokenizer(list, seperators, include);
			String[] result = new String[tokens.countTokens()];
			int i = 0;
			while (tokens.hasMoreTokens()) {
				result[i++] = tokens.nextToken();
			}
			return result;
		}

		public String getPath() {

			StringBuffer buf = new StringBuffer();
			if (this.absolutePath) {
				buf.append(PATH_SEPARATOR);
			}
			for (Iterator<PathElement> iter = path.iterator(); iter.hasNext();) {
				PathElement item = (PathElement) iter.next();
				buf.append(item.getPathChunk());
				if (iter.hasNext()) {
					buf.append(PATH_SEPARATOR);
				}
			}
			if (this.directory) {
				buf.append(PATH_SEPARATOR);
			}
			return buf.toString();
		}

		public int getLength() {

			if (path != null) {
				return path.size();
			} else {
				return 0;
			}
		}

		/**
		 * 
		 * @param position
		 *          int
		 * @return PathElement
		 */
		public PathElement getElementAt(int position) {

			if (position < getLength()) {
				return ((PathElement) this.path.get(position));
			} else {
				return null;
			}
		}

		/**
		 * 
		 * @param obj
		 *          Object
		 * @return boolean
		 */
		@Override
		public boolean equals(Object obj) {

			boolean result = true;
			if (!(obj instanceof Path)) {
				result = false;
			} else {
				Path other = (Path) obj;
				if (other.getLength() != this.getLength()) {
					result = false;
				} else {
					int size = this.getLength();
					for (int i = 0; i < size; i++) {
						if (!(this.getElementAt(i)).equals(other.getElementAt(i))) {
							result = false;
							break;
						}
					}
				}
			}
			return result;
		}

		/**
		 * 
		 * @param pathChunk
		 *          PathElement
		 */
		public void addPathElement(PathElement pathChunk) {

			this.path.add(pathChunk);
		}

		/**
		 * 
		 * @param elements
		 *          int
		 * @return Path
		 */
		public Path getSubPath(int elements) {

			Path result = new Path();
			for (int i = 0; i < elements; i++) {
				result.addPathElement(this.getElementAt(i));
			}
			return result;
		}

		/**
		 * 
		 * @param wrapperCandidate
		 *          Path
		 * @return boolean
		 */
		public boolean isEnclosed(Path wrapperCandidate) {

			boolean result = false;
			if (this.getLength() > wrapperCandidate.getLength()) {
				result = false;
			} else {
				Path other = wrapperCandidate.getSubPath(this.getLength());
				result = other.equals(this);
			}
			return result;
		}

		/**
		 * 
		 * @param other
		 *          Path
		 * @return int
		 */
		public int distance(Path other) {

			int result = -1;
			Path a;
			Path b;
			if (this.getLength() > other.getLength()) {
				a = this;
				b = other;
			} else {
				a = other;
				b = this;
			}
			if (b.isEnclosed(a)) {
				result = (a.getLength() - b.getLength());
			} else {
				result = a.getLength() + b.getLength();
			}
			return result;
		}

		/**
		 * 
		 * @return String
		 */
		@Override
		public String toString() {

			StringBuffer buf = new StringBuffer();
			buf.append("[");
			for (int i = 0; i < this.getLength(); i++) {
				buf.append(" ");
				buf.append(this.getElementAt(i).getPathChunk());
			}
			buf.append(" ]");
			return buf.toString();
		}
	}

}
