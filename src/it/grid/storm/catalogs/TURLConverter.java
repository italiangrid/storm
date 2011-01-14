/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TTURL;

/**
 * Class that handles DPM DB representation of a TTURL, in particular it takes
 * care of the NULL/EMPTY logic of DPM. Indeed DPM uses 0/null to mean an empty field,
 * whereas StoRM uses the type TTURL.makeEmpty(); in particular StoRM converts
 * an empty String or a null to an Empty TTURL!
 *
 * @author EGRID ICTP
 * @version 1.0
 * @date March 2006
 */
public class TURLConverter {

    private static TURLConverter stc = new TURLConverter(); //only instance

    private TURLConverter() {}

    /**
     * Method that returns the only instance of SizeInBytesIntConverter
     */
    public static TURLConverter getInstance() {
        return stc;
    }

    /**
     * Method that transaltes the Empty TTURL into the empty representation
     * of DPM which is a null! Any other String is left as is.
     */
    public String toDB(String s) {
        if (s.equals(TTURL.makeEmpty().toString())) return null;
        return s;
    }

    /**
     * Method that translates DPMs "" or null String as the
     * Empty TTURL String representation. Any other String is left as is.
     */
    public String toStoRM(String s) {
        if ((s==null) || (s.equals(""))) return TTURL.makeEmpty().toString();
        return s;
    }
}
