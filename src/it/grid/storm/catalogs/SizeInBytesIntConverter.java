package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TSizeInBytes;

/**
 * Class that handles DB representation of a TSizeInBytes, in particular it
 * takes care of the NULL logic of the DB: 0/null are used to mean an empty
 * field, whereas StoRM Object model uses the type TSizeInBytes.makeEmpty();
 * moreover StoRM does accept 0 as a valid TSizeInBytes, so it _is_ important
 * to use this converter!
 *
 * @author  EGRID ICTP
 * @version 2.0
 * @date    July 2005
 */
public class SizeInBytesIntConverter {

    private static SizeInBytesIntConverter stc = new SizeInBytesIntConverter(); //only instance

    private SizeInBytesIntConverter() {}

    /**
     * Method that returns the only instance of SizeInBytesIntConverter
     */
    public static SizeInBytesIntConverter getInstance() {
        return stc;
    }

    /**
     * Method that transaltes the Empty TSizeInBytes into the empty
     * representation of DB which is 0. Any other int is left as is.
     */
    public long toDB(long s) {
        if (s==TSizeInBytes.makeEmpty().value()) return 0;
        return s;
    }

    /**
     * Method that returns the int as is, except if it is 0 which
     * DB interprests as empty field: in that case it then returns
     * the Empty TSizeInBytes int representation.
     */
    public long toStoRM(long s) {
        if (s==0) return TSizeInBytes.makeEmpty().value();
        return s;
    }
}
