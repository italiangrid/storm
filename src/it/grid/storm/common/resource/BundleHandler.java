package it.grid.storm.common.resource;

import java.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * Some lines of code was taken from HSQLDB code.
 * Please, see the license
 *   http://hsqldb.sourceforge.net/web/hsqlLicense.html

 *
 * @author R.Zappi
 * @version 1.0
 */
public final class BundleHandler {

    /** Used to synchronize access */
    private static final Object mutex = new Object();

    /** The Locale used internally to fetch resource bundles. */
    private static Locale locale = Locale.getDefault();

    /** Map:  Integer object handle => <code>ResourceBundle</code> object. */
    private static HashMap bundleHandleMap = new HashMap();

    /** List whose elements are <code>ResourceBundle</code> objects */
    private static ArrayList bundleList = new ArrayList();

    public static final String newline = System.getProperty("line.separator");

    /**
     * The resource path prefix of the <code>ResourceBundle</code> objects
     * handled by this class.
     */
    private static final String prefix = "it/grid/storm/common/resource/";

    private BundleHandler() {
    }

    /**
     * Taken from HSQLDB source code, and adapted for StoRM
     * See HSQL license:
     * http://hsqldb.sourceforge.net/web/hsqlLicense.html
     *
     * Retrieves an <code>int</code> handle to the <code>ResourceBundle</code>
     * object corresponding to the specified name and current
     * <code>Locale</code> <p>
     *
     * @return <code>int</code> handle to the <code>ResourceBundle</code>
     *        object corresponding to the specified name and
     *        current <code>Locale</code>, or -1 if no such bundle
     *        can be found
     *
     * @param name of the desired bundle
     */
    public static int getBundleHandle(String name) {

        Integer bundleHandle;
        ResourceBundle bundle;
        String bundleName;
        String bundleKey;

        bundleName = prefix + name;

        synchronized (mutex) {
            bundleKey = locale.toString() + bundleName;
            bundleHandle = (Integer) bundleHandleMap.get(bundleKey);

            if (bundleHandle == null) {
                try {
                    bundle = getBundle(bundleName);

                    bundleList.add(bundle);

                    bundleHandle = new Integer(bundleList.size() - 1);

                    bundleHandleMap.put(bundleKey, bundleHandle);
                }
                catch (Exception e) {

                }
            }
        }

        return bundleHandle == null ? -1 : bundleHandle.intValue();
    }

    /**
     * Retrieves a resource bundle using the specified base name
     *
     * @param name the base name of the resource bundle, a fully  qualified class name
     */
    public static ResourceBundle getBundle(String name) throws NullPointerException, MissingResourceException {
        return ResourceBundle.getBundle(name, locale);
    }

    /**
     * Taken from HSQLDB source code, and adapted for StoRM
     * See HSQL license:
     * http://hsqldb.sourceforge.net/web/hsqlLicense.html
     *
     * Retrieves, from the <code>ResourceBundle</code> object corresponding
     * to the specified handle, the <code>String</code> value corresponding
     * to the specified key.  <code>null</code> is retrieved if either there
     *  is no <code>ResourceBundle</code> object for the handle or there is no
     * <code>String</code> value for the specified key. <p>
     *
     * @param handle an <code>int</code> handle to a
     *      <code>ResourceBundle</code> object
     * @param key A <code>String</code> key to a <code>String</code> value
     * @return The String value correspoding to the specified handle and key.
     */
    public static String getString(int handle, String key) {

        ResourceBundle bundle;
        String s;

        synchronized (mutex) {
            if (handle < 0 || handle >= bundleList.size() || key == null) {
                bundle = null;
            }
            else {
                bundle = (ResourceBundle) bundleList.get(handle);
            }
        }

        if (bundle == null) {
            s = null;
        }
        else {
            try {
                s = replace(bundle.getString(key),"@@",newline);
            }
            catch (Exception e) {
                s = null;
            }
        }

        return s;
    }

    private static String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();

        while ( (e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }


}
