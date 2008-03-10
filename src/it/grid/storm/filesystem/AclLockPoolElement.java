/**
 * @file   AclLockPoolElement.java
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * The it.grid.storm.filesystem.AclLockPoolElement class
 */
/*
 * Copyright (c) 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the same terms
 * as StoRM itself.
 */

package it.grid.storm.filesystem;


import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;
import edu.emory.mathcs.backport.java.util.concurrent.Semaphore;


/** Usage-counted semaphore object.
 *
 * <p>Each {@link #incrementUsageCountAndReturnSelf()} request
 * increments the usage counter, and each {@link
 * #decrementUsageCountAndGetIt()} request decrements it.
 *
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.5 $
 */
class AclLockPoolElement 
    extends Semaphore
{
    // ---- constructors ----

    /** Default constructor.  The semaphore is initialized for
     * allowing only 1 permit at a time (thus serializing accesses
     * through the acquire() and release() calls), and with the
     * default fairness setting.  The usage count is initialized to
     * <code>0</code>.
     *
     * @see java.util.concurrent.AtomicInteger;
     * @see java.util.concurrent.Semaphore;
     */
    public AclLockPoolElement()
    {
        super(1);
        usageCount = new AtomicInteger();
    }



    // --- public methods ---
    
    /** Return the lock object associated with the given file name, or
     * create a new one if no mapping for the given path name is
     * already in this map.
     */
    public void incrementUsageCount()
    {
        usageCount.incrementAndGet();
    }


    /** Return the stored usage count. */
    public int getUsageCount()
    {
        return usageCount.intValue();
    }


    /** Decrement the stored usage count. */
    public int decrementUsageCountAndGetIt()
    {
        return usageCount.decrementAndGet();
    }


    // --- private instance variables --- //

    /** Usage counter. */
    private final AtomicInteger usageCount;
}


