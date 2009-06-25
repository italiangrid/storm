package it.grid.storm.namespace.naming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

public class NamespaceUtil {

    /**
     * PRIVATE Constructor
     */
    private NamespaceUtil() {
    }

    /**
     * Compute the distance between two path.
     * Return -1 when the two path are different completly.
     *
     * @param path1 String
     * @param path2 String
     * @return int
     */
    public static int computeDistanceFromPath(String path1, String path2) {
        return (new Path(path1)).distance(new Path(path2));
    }

    /**
     * Retrieve all path elements within path
     *
     * @param path String
     * @return Collection
     */
    public static Collection<String> getPathElement(String path) {
        return (new Path(path)).getPathElements();
    }

    /**
     * getFileName
     *
     * @param stfn String
     * @return String
     */
    public static String getFileName(String stfn) {
        if (stfn != null) {
            if (stfn.endsWith(NamingConst.SEPARATOR)) {
                return "";
            }
            else {
                Path path = new Path(stfn);
                int length = path.getLength();
                if (length > 0) {
                    PathElement elem = path.getElementAt(length - 1);
                    return elem.toString();
                }
                else {
                    return "";
                }
            }
        }
        else {
            return "";
        }
    }

    public static String consumeFileName(String file) {
        if (file != null) {
            if (file.endsWith(NamingConst.SEPARATOR)) {
                return file;
            }
            else {
                Path path = new Path(file);
                int length = path.getLength();
                if (length > 1) {
                    return path.getSubPath(length - 1).getPath() + NamingConst.SEPARATOR;
                }
                else {
                    return Path.PATH_SEPARATOR;
                }
            }
        }
        else {
            return Path.PATH_SEPARATOR;
        }
    }

    /**
     * get
     *
     * @param stfn String
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
        }
        else {
            return ""; //Path.PATH_SEPARATOR;
        }
    }

    public static String extractRelativePath(String root, String absolute) {
        if (absolute.startsWith(root)) {
            Path rootPath = new Path(root);
            int rootLength = rootPath.getLength();

            Path absPath = new Path(absolute);
            ArrayList elem = new ArrayList();

            //System.out.println("root lenght: "+rootLength+"abs lenght:"+absPath.getLength());

            for (int i = 0; i < absPath.getLength(); i++) {
                //Why use lenght and not compare single elemnt?
                if (i >= rootLength) {
                    elem.add(absPath.getElementAt(i));
                }
            }
            Path result = new Path(elem, false);
            //System.out.println("Result:"+result.getPath());
            return result.getPath();
        }
        else {
            return absolute;
        }
    }

    public static boolean isEnclosed(String root, String wrapperCandidate) {
        boolean result = false;
        Path rootPath = new Path(root);
        Path wrapperPath = new Path(wrapperCandidate);
        result = rootPath.isEnclosed(wrapperPath);
        return result;
    }

    /**=====================
     * INNER CLASSES
     * ======================
     */


    /**
     *
     * <p>Title: </p>
     *
     * <p>Description: </p>
     *
     */
    private static class PathElement {

        private final String pathChunk;

        public PathElement(String path) {
            this.pathChunk = path;
        }

        public String getPathChunk() {
            return this.pathChunk;
        }

        @Override
        public boolean equals(Object obj) {
            boolean result = true;
            if (! (obj instanceof PathElement)) {
                result = false;
            }
            else {
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
     * <p>Title: </p>
     *
     * <p>Description: </p>
     *
     */
    private static class Path {

        private ArrayList path;
        private static String PATH_SEPARATOR = "/";
        public static final String[] EMPTY_STRING_ARRAY = {};
        public boolean directory;
        public boolean absolutePath;

        public Path() {
            this.path = new ArrayList();
            this.directory = false;
            this.absolutePath = true;
        }

        public Path(ArrayList path) {
            this.path = path;
            this.directory = false;
            this.absolutePath = true;
        }

        public Path(ArrayList path, boolean absolutePath) {
            this.path = path;
            this.directory = false;
            this.absolutePath = absolutePath;
        }

        public Path(String[] pathElements) {
            if (pathElements != null) {
                for (String pathElement : pathElements) {
                    addPathElement(new PathElement(pathElement));
                }
            }
            this.directory = false;
            this.absolutePath = true;
        }

        public Path(String path) {
            //Factorize path into array of PathElement...
            if (path.startsWith(PATH_SEPARATOR)) {
                this.absolutePath = true;
            }
            else {
                this.absolutePath = false;
            }
            if (path.endsWith(PATH_SEPARATOR)) {
                this.directory = true;
            }
            else {
                this.directory = false;
            }

            String[] pathElements = factorizePath(path);
            if (pathElements != null) {
                // ...and build Path
                this.path = new ArrayList(pathElements.length);
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
            Iterator scan = path.iterator();
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
            }
            else {
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
            for (Iterator iter = path.iterator(); iter.hasNext(); ) {
                PathElement item = (PathElement) iter.next();
                //DEBUG
                //System.out.println("Item: "+item.toString());
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
            }
            else {
                return 0;
            }
        }

        /**
         *
         * @param position int
         * @return PathElement
         */
        public PathElement getElementAt(int position) {
            if (position < getLength()) {
                return ( (PathElement)this.path.get(position));
            }
            else {
                return null;
            }
        }

        /**
         *
         * @param obj Object
         * @return boolean
         */
        @Override
        public boolean equals(Object obj) {
            boolean result = true;
            if (! (obj instanceof Path)) {
                result = false;
            }
            else {
                Path other = (Path) obj;
                if (other.getLength() != this.getLength()) {
                    result = false;
                }
                else {
                    int size = this.getLength();
                    for (int i = 0; i < size; i++) {
                        if (! (this.getElementAt(i)).equals(other.getElementAt(i))) {
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
         * @param pathChunk PathElement
         */
        public void addPathElement(PathElement pathChunk) {
            this.path.add(pathChunk);
        }

        /**
         *
         * @param elements int
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
         * @param wrapperCandidate Path
         * @return boolean
         */
        public boolean isEnclosed(Path wrapperCandidate) {
            boolean result = false;
            if (this.getLength() > wrapperCandidate.getLength()) {
                result = false;
            }
            else {
                Path other = wrapperCandidate.getSubPath(this.getLength());
                result = other.equals(this);
            }
            return result;
        }

        /**
         *
         * @param other Path
         * @return int
         */
        public int distance(Path other) {
            int result = -1;
            Path a;
            Path b;
            if (this.getLength() > other.getLength()) {
                a = this;
                b = other;
            }
            else {
                a = other;
                b = this;
            }
            if (b.isEnclosed(a)) {
                result = (a.getLength() - b.getLength());
            }
            else {
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
