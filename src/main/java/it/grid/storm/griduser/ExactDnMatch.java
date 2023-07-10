/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.griduser;

/** Match a proxy DN against a fixed-string pattern. */
public class ExactDnMatch implements DnMatch {

  /**
   * Return <code>true</code> if <code>fixedDn</code> is the initial segment of <code>proxyDn</code>
   * , and the remaining part of <code>proxyDn</code> consists only of "CN=..." fields. (That is,
   * <code>proxyDn</code> may be a proxy DN derived from <code>fixedDn</code> by a proxy delegation
   * process, according to RFC3820.
   *
   * @return <code>true</code> if the DNs do match.
   */
  public boolean match(final String proxyDn, final String fixedDn) {

    assert (null != proxyDn);
    assert (null != fixedDn);

    if (!proxyDn.startsWith(fixedDn)) return false;

    if (!(proxyDn.charAt(1 + fixedDn.length()) == '/'))
      /* fixedDn did not match up to DN field boundary, fail */
      return false;

    final String[] tails = proxyDn.substring(fixedDn.length()).split("/");
    for (int i = 1; i < tails.length; i++)
      if (!tails[i].toUpperCase().startsWith("CN=")) return false;

    return true;
  }
}
