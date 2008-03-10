/*
 * FirstProperDecisionCombiningAlgorithm
 *
 * Copyright (c) 2005, Riccardo Murri <riccardo.murri@ictp.it>
 * 
 * You may copy, distribute and modify this file under the terms of
 * the LICENSE.txt file at the root of the StoRM backend source tree.
 *
 * $Id: FirstProperDecisionCombiningAlgorithm.java,v 1.5 2006/03/31 06:43:20 rmurri Exp $
 *
 */
package it.grid.storm.authorization.combiners;


import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.authorization.DecisionCombiningAlgorithm;


/**
 * Interface to a variant of the "First-applicable" decision combining
 * algorithm.  Applies a variant of the XACML "First-applicable"
 * policy-combining algorithm (Appendix C.3 of the XACML 1.0 spec,
 * http://www.oasis-open.org/committees/download.php/2406/oasis-xacml-1.0.pdf
 * ) to take a decision based on the input decisions list; the input
 * decisions list correspond to the results of evaluating a list of
 * policies in XACML.
 *
 * @see     it.grid.storm.authorization.AuthorizationDecision
 * @see     it.grid.storm.authorization.sources.CompositeAuthorizationSource
 * @see     it.grid.storm.authorization.DecisionCombiningAlgorithm
 * @see     it.grid.storm.authorization.combiners.DenyOverridesDecisionCombiningAlgorithm
 * @see     it.grid.storm.authorization.combiners.PermitOverridesDecisionCombiningAlgorithm
 *
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 */
public class FirstProperDecisionCombiningAlgorithm 
    extends DecisionCombiningAlgorithm
{
    /**
     * Evaluates a list of <code>AuthorizationDecision</code>s
     * according to a variant of the XACML "First-applicable"
     * algorithm.  Applies a variant of the XACML "First-applicable"
     * algorithm: orderly browse the list and return
     * <code>Deny</code>, <code>Permit</code> depending on which of
     * these decisions is found first; if any
     * <code>Indeterminate</code> result is in the list and no
     * <code>Permit</code> nor <code>Deny</code>, then return
     * <code>Indeterminate</code>; if the whole list is comprised of
     * <code>NotApplicable</code>, then return
     * <code>NotApplicable</code>.
     */
    public void combine(final AuthorizationDecision ad) {
        if (decisionTaken)
            // decision taken, ignore further input
            return;

        if (ad.isProper()) {
            // take  decision
            decisionTaken = true;
            current = ad;
        }
        else if (ad.isIndeterminate()) {
            current = ad;
        }
        else if (current.isNotApplicable()) {
            // no change to current status
        }
    }


    /** Constructor.  This is private; new instances should be gotten
     * via the newInstance() method. */
    public FirstProperDecisionCombiningAlgorithm() {
        current = AuthorizationDecision.NotApplicable;
        decisionTaken = false;
    }
}
