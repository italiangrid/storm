package it.grid.storm.namespace.naming;

public class NameParser {

    public NameParser() {
    }

    /**
     * Get the basename of an URI.   It's possibly an empty string.
     *
     * @param uri a string regarded an URI
     * @return the basename string; an empty string if the path ends with slash
     */
    public String getName(String uri) {
        if (uri == null || uri.length() == 0) {
            return uri;
        }
        String path = this.getPath(uri);
        int at = path.lastIndexOf("/");
        int to = path.length();
        return (at >= 0) ? path.substring(at + 1, to) : path;
    }

    /**
     * Get the path of an URI.
     *
     * @param uri a string regarded an URI
     * @return the path string
     */
    public String getPath(String uri) {
        if (uri == null) {
            return null;
        }
        // consider of net_path
        int at = uri.indexOf("//");
        int from = uri.indexOf(
            "/",
            at >= 0 ? (uri.lastIndexOf("/", at - 1) >= 0 ? 0 : at + 2) : 0
            );
        // the authority part of URI ignored
        int to = uri.length();
        // check the query
        if (uri.indexOf('?', from) != -1) {
            to = uri.indexOf('?', from);
        }
        // check the fragment
        if (uri.lastIndexOf("#") > from && uri.lastIndexOf("#") < to) {
            to = uri.lastIndexOf("#");
        }
        // get only the path.
        return (from < 0) ? (at >= 0 ? "/" : uri) : uri.substring(from, to);
    }

    /**
     * Get the query of an URI.
     *
     * @param uri a string regarded an URI
     * @return the query string; <code>null</code> if empty or undefined
     */
    public String getQuery(String uri) {
        if (uri == null || uri.length() == 0) {
            return null;
        }
        // consider of net_path
        int at = uri.indexOf("//");
        int from = uri.indexOf(
            "/",
            at >= 0 ? (uri.lastIndexOf("/", at - 1) >= 0 ? 0 : at + 2) : 0
            );
        // the authority part of URI ignored
        int to = uri.length();
        // reuse the at and from variables to consider the query
        at = uri.indexOf("?", from);
        if (at >= 0) {
            from = at + 1;
        }
        else {
            return null;
        }
        // check the fragment
        if (uri.lastIndexOf("#") > from) {
            to = uri.lastIndexOf("#");
        }
        // get the path and query.
        return (from < 0 || from == to) ? null : uri.substring(from, to);
    }

    /**
     * Get the path and query of an URI.
     *
     * @param uri a string regarded an URI
     * @return the path and query string
     */
    public String getPathQuery(String uri) {
        if (uri == null) {
            return null;
        }
        // consider of net_path
        int at = uri.indexOf("//");
        int from = uri.indexOf(
            "/",
            at >= 0 ? (uri.lastIndexOf("/", at - 1) >= 0 ? 0 : at + 2) : 0
            );
        // the authority part of URI ignored
        int to = uri.length();
        // Ignore the '?' mark so to ignore the query.
        // check the fragment
        if (uri.lastIndexOf("#") > from) {
            to = uri.lastIndexOf("#");
        }
        // get the path and query.
        return (from < 0) ? (at >= 0 ? "/" : uri) : uri.substring(from, to);
    }

    /**
     * Get the path of an URI and its rest part.
     *
     * @param uri a string regarded an URI
     * @return the string from the path part
     */
    public String getFromPath(String uri) {
        if (uri == null) {
            return null;
        }
        // consider of net_path
        int at = uri.indexOf("//");
        int from = uri.indexOf(
            "/",
            at >= 0 ? (uri.lastIndexOf("/", at - 1) >= 0 ? 0 : at + 2) : 0
            );
        // get the path and its rest.
        return (from < 0) ? (at >= 0 ? "/" : uri) : uri.substring(from);
    }

    /**
     * This method counts the slashes after the scheme.
     *
     * @param filename
     * @return nuof slashes
     */
    protected int countSlashes(final String filename) {
        int state = 0;
        int nuofSlash = 0;
        for (int pos = 0; pos < filename.length(); pos++) {
            char c = filename.charAt(pos);
            if (state == 0) {
                if (c >= 'a' && c <= 'z') {
                    continue;
                }
                if (c == ':') {
                    state++;
                    continue;
                }
            }
            else if (state == 1) {
                if (c == '/') {
                    nuofSlash++;
                }
                else {
                    System.out.println("I = " + pos);
                    return nuofSlash;
                }
            }
        }
        return nuofSlash;
    }

}
