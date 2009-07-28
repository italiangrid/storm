package it.grid.storm.jna;

public class Errno {
    
    public static final int ENOENT = 2;             /* No such file or directory */
    public static final int EEXIST = 17;            /* File exists */
    public static final int ENOTDIR = 20;           /* Not a directory */
    public static final int ENOSPC = 28;            /* No space left on device */
    public static final int ERANGE = 34;            /* Math result not representable */
    public static final int ENODATA = 61;           /* No data available */
    public static final int ENOATTR = ENODATA;      /* No such attribute */
    public static final int EOPNOTSUPP = 95;        /* Operation not supported on transport endpoint */
    public static final int ENOTSUP = EOPNOTSUPP;
    public static final int EDQUOT = 122;           /* Quota exceeded */

}
