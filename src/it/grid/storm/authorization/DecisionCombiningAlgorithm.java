/*
 * DecisionCombiningAlgorithm
 *
 * Copyright (c) 2005, Riccardo Murri <riccardo.murri@ictp.it>
 * 
 * You may copy, distribute and modify this file under the terms of
 * the LICENSE.txt file at the root of the StoRM backend source tree.
 *
 * $Id: DecisionCombiningAlgorithm.java,v 1.7 2006/03/31 06:43:20 rmurri Exp $
 *
 */
package it.grid.storm.authorization;


import it.grid.storm.authorization.AuthorizationDecision;


/**
 * Interface to a decision combining algorithm.  Decision combining
 * algorithms are used in <code>CompositeAuthorizationSource</code> class to
 * reduce a collection of <code>AuthorizationDecision</code>s to a
 * single final one.
 *
 * @see     it.grid.storm.authorization.AuthorizationDecision
 * @see     it.grid.storm.authorization.sources.CompositeAuthorizationSource
 * @see     it.grid.storm.authorization.combiners.DenyOverridesDecisionCombiningAlgorithm
 * @see     it.grid.storm.authorization.combiners.PermitOverridesDecisionCombiningAlgorithm
 * @see     it.grid.storm.authorization.combiners.FirstApplicableDecisionCombiningAlgorithm
 * @see     it.grid.storm.authorization.combiners.FirstProperDecisionCombiningAlgorithm
 *
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 */
public abstract class DecisionCombiningAlgorithm
{
    // --- public --- //

    /**
     * Add an <code>AuthorizationDecision</code> to the list of those
     * to be evaluated to get a final one.  How decisions are combined
     * to hold the final one is taken based on the input data is
     * exactly what is required by the implementation.
     */
    abstract public void combine(final AuthorizationDecision decision); 
    
    /**
     * Return <code>true</code> if a decision has been taken and
     * further invocations of {@link #combine(AuthorizationDecision)}
     * will not change it.  In other words, when this is
     * <code>true</code>, the decision returned by {@link
     * #getDecision()} is final.
     */
    public boolean isDecisionTaken() {
        return decisionTaken;
    }

    /** Return the result of combining
     * <code>AuthorizationDecision</code>s.  How the final decision is
     * taken based on the input data is dependent on implementation.
     *
     * <p>Return <code>AuthorizationDecision.NotApplicable</code> if
     * no decision has been added with the {@link
     * combine(AuthorizationDecision)} method.
     */
    public AuthorizationDecision getDecision() {
        return current;
    }


    // --- protected --- //

    /** The result of combining <code>AuthorizationDecision</code>s so
     * far.  This is what getDecision() will return. */
    protected AuthorizationDecision current = AuthorizationDecision.NotApplicable;

    /** Becomes <code>true</code> when adding more decisions through
     * the combine() method will not change the outcome. */
    protected boolean decisionTaken = false;
}
