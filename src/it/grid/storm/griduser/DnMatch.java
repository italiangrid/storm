/* 
 * DnMatch
 *
 * Copyright (c) 2005, Riccardo Murri <riccardo.murri@ictp.it>
 * 
 * You may copy, distribute and modify this file under the terms of
 * the INFN GRID licence.
 *
 * $Id: DnMatch.java,v 1.1 2005/07/21 01:30:54 rmurri Exp $
 *
 */
package it.grid.storm.griduser;


/**
 * Interface that the DN matching algorithms must implement.
 */
public interface DnMatch {
    /**
     * Compare two DNs and return true if they match, according
     * to the implemented criterion.
     *
     * @return <code>true</code> if the DNs do match.
     */
    public boolean match(final String proxyDn, final String fixedDn);
}
