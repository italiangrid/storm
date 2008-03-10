/*
 * AuthorizationDecision
 *
 * Copyright (c) 2005, Riccardo Murri <riccardo.murri@ictp.it>
 * 
 * You may copy, distribute and modify this file under the terms of
 * the LICENSE.txt file at the root of the StoRM backend source tree.
 *
 */
package it.grid.storm.authorization;

/**
 * Defines the constant and immutable objects
 * <code>AuthorizationDecision.Permit</code>,
 * <code>AuthorizationDecision.Deny</code>,
 * <code>AuthorizationDecision.Indeterminate</code> and
 * <code>AuthorizationDecision.NotApplicable</code>, which are used as
 * return values by the <code>can*</code> methods in the
 * <code>AuthorizationQuery*</code> interface.
 *
 * <p>An <code>AuthorizationDecision</code> instance should be queried
 * using the <code>isPermit()</code>, <code>isDeny()</code>,
 * <code>isNotApplicable</code> and <code>isIndeterminate()</code>
 * boolean methods to determine the outcome.  This is actually a
 * workaround for Java 1.4 lack of proper enumerative types.
 * 
 * <p>Since 2 of 4 possible outcomes are no decisions, this class is a
 * bit of a misnomer.
 *
 * <p>FIXME: when status is <code>Indeterminate</code>, then some
 * error has occurred while evaluating the policy... how should we
 * report it?  Better query the source to get the error, IMHO.
 *
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 */

public final class AuthorizationDecision
{
	/* internal representation of possible decisions */
	private static final int __PERMIT = 0;
	private static final int __DENY = 1;
	private static final int __NOT_APPLICABLE = 2;
	private static final int __INDETERMINATE = 3;
	private final int __decision;

	private AuthorizationDecision (final int decision) {
		__decision = decision;
	}


	/**
	 * A <code>Permit</code> decision.  Use this to allow access to a
	 * resource.
	 */
	public static final AuthorizationDecision
		Permit = new AuthorizationDecision(__PERMIT);

	/**
	 * A <code>Deny</code> decision.  Use this to deny access to a
	 * resource.
	 */
	public static final AuthorizationDecision
		Deny = new AuthorizationDecision(__DENY);

	/**
	 * A <code>Indeterminate</code> outcome.  Use this when some error
	 * occurred while evaluating the policy this decision is the
	 * outcome; no decision has been taken.
	 *
	 * <p>It is expected that the source holds details of the error
	 * occurred, they are not stored within the decision.
	 */
	public static final AuthorizationDecision
		Indeterminate = new AuthorizationDecision(__INDETERMINATE);

	/**
	 * A <code>NotApplicable</code> outcome.  Use this when no
	 * decision can be taken, that is, the policy under evaluation
	 * does not cover this case.
	 */
	public static final AuthorizationDecision
		NotApplicable = new AuthorizationDecision(__NOT_APPLICABLE);


	/**
	 * Returns <code>true</code> if the object represents the same
	 * decision.
	 */
	public boolean equals(Object obj) {
	    // This implementation might look cumbersome at first sight,
	    // please see
	    //   http://www.geocities.com/technofundo/tech/java/equalhash.html
	    // for an explanation of the pitfalls of reimplementing equals().
	    if(this == obj)
			return true;
	    if((obj == null) || (obj.getClass() != this.getClass()))
			return false;
	    return (__decision == ((AuthorizationDecision)obj).__decision);
	}

	/**
	 * Returns the <code>__decision</code> private field as a hash code.
	 *
	 * <p>This is so, because two objects that are <code>equals()</code>
	 * must also return the same hash code, by contract in the
	 * <code>java.lang.Object</code> class.
	 */
	public int hashCode() {
		return __decision;
	}

	/** Returns <code>true</code> if this decision is "Permit". */
	public boolean isPermit() {
		return (__PERMIT == __decision);
	}

	/** Returns <code>true</code> if this decision is "Deny". */
	public boolean isDeny() {
		return (__DENY == __decision);
	}

	/**
	 * Returns <code>true</code> if this decision is either
	 * <code>Permit</code> or <code>Deny</code>.
	 */
	public boolean isProper() {
		return (__PERMIT == __decision) || (__DENY == __decision);
	}

	/** Returns <code>true</code> if this decision is "Indeterminate". */
	public boolean isIndeterminate() {
		return (__INDETERMINATE == __decision);
	}

	/** Returns <code>true</code> if this decision is "Not Applicable". */
	public boolean isNotApplicable() {
		return (__NOT_APPLICABLE == __decision);
	}
}
