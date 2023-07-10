/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * @file AclLockPool.java
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *     <p>The it.grid.storm.filesystem.AclLockPool class
 */
/*
 * Copyright (c) 2006 Riccardo Murri <riccardo.murri@ictp.it> for the EGRID/INFN
 * joint project StoRM.
 *
 * You may copy, modify and distribute this file under the same terms as StoRM
 * itself.
 */

package it.grid.storm.filesystem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maps path names to lock objects. Expect each map value to maintain a usage count; on {@link
 * #remove(String)}, the usage count is checked, and the entry is effectively removed only if the
 * usage count has dropped at -or below- zero.
 *
 * <p>This class' purpose is to provide a shared storage for lock objects used by the {@link
 * it.grid.storm.filesystem.File} class.
 *
 * @author Riccardo Murri <riccardo.murri@ictp.it> @version $Revision: 1.6 $
 */
class AclLockPool {

  // ---- constructors ---- //

  /**
   * Creates a new, empty pool with the specified initial capacity, load factor, and
   * concurrencyLevel.
   *
   * @see java.util.concurrent.ConcurrentHashMap;
   */
  AclLockPool(final int initialCapacity, final float loadFactor, final int concurrencyLevel) {

    assert (initialCapacity >= 0)
        : "Negative initialCapacity passed to AclLockPool(int,float,int) constructor";
    assert (loadFactor >= 0)
        : "Negative loadFactor passed to AclLockPool(int,float,int) constructor";
    assert (concurrencyLevel >= 0)
        : "Negative concurrencyLevel passed to AclLockPool(int,float,int) constructor";

    __map = new ConcurrentHashMap(initialCapacity, loadFactor, concurrencyLevel);
  }

  /**
   * Creates a new, empty pool with the specified initial capacity, and the default load factor and
   * concurrencyLevel (from {@link java.util.concurrent.ConcurrentHashMap})
   *
   * @see java.util.concurrent.ConcurrentHashMap;
   */
  AclLockPool(final int initialCapacity) {

    assert (initialCapacity >= 0)
        : "Negative initialCapacity passed to AclLockPool(int,float) constructor";

    __map = new ConcurrentHashMap(initialCapacity);
  }

  /**
   * Creates a new, empty pool with the default initial capacity, load factor and concurrencyLevel
   * (from {@link java.util.concurrent.ConcurrentHashMap})
   *
   * @see java.util.concurrent.ConcurrentHashMap;
   */
  AclLockPool() {

    __map = new ConcurrentHashMap();
  }

  // --- public methods --- //

  /**
   * Return the lock object associated with the given path name; if the map contains no lock for the
   * given pathname, a new one is created and returned. The usage counter for the associated element
   * is incremented, so {@link get()} invocations should match exactly {@link remove(String)}
   * invocations.
   */
  public synchronized AclLockPoolElement get(final String pathname) {

    if (!__map.containsKey(pathname)) __map.put(pathname, new AclLockPoolElement());
    AclLockPoolElement lock = __map.get(pathname);
    lock.incrementUsageCount();
    return lock;
  }

  /**
   * Remove the element associated with the given path name. The usage counter associated with the
   * given pathname is decremented; if it drops at zero, the associated element is effectively
   * removed from the map.
   */
  public synchronized void remove(final String pathname) {

    AclLockPoolElement e = __map.get(pathname);
    if (null != e) {
      int count = e.decrementUsageCountAndGetIt();
      if (0 >= count) __map.remove(pathname);
    }
  }

  /** Return <code>true</code> if an element is assciated with the given path name. */
  public synchronized boolean contains(final String pathname) {

    return __map.containsKey(pathname);
  }

  // --- private instance variables --- //

  /** Table of mappings. */
  private final Map<String, AclLockPoolElement> __map;
}
