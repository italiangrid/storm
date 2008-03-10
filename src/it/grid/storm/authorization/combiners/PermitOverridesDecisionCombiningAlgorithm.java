/*
 * PermitOverridesDecisionCombiningAlgorithm
 *
 * Copyright (c) 2005, Riccardo Murri <riccardo.murri@ictp.it>
 * 
 * You may copy, distribute and modify this file under the terms of
 * the LICENSE.txt file at the root of the StoRM backend source tree.
 *
 * $Id: PermitOverridesDecisionCombiningAlgorithm.java,v 1.5 2006/03/31 06:43:20 rmurri Exp $
 *
 */
package it.grid.storm.authorization.combiners;


import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.authorization.DecisionCombiningAlgorithm;


/**
 * Interface to a "Permit-overrides" decision combining algorithm.
 * Applies the XACML "Permit-overrides" policy-combining algorithm
 * (Appendix C.2 of the XACML 1.0 spec,
 * http://www.oasis-open.org/committees/download.php/2406/oasis-xacml-1.0.pdf
 * ) to take a decision based on the input decisions list; the input
 * decisions list correspond to the results of evaluating a list of
 * policies in XACML.
 *
 * @see     it.grid.storm.authorization.AuthorizationCollector
 * @see     it.grid.storm.authorization.AuthorizationDecision
 * @see     it.grid.storm.authorization.DecisionCombiningAlgorithm
 * @see     it.grid.storm.authorization.combiners.DenyOverridesDecisionCombiningAlgorithm
 * @see     it.grid.storm.authorization.combiners.FirstApplicableDecisionCombiningAlgorithm
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 * @version 0.1
 */
public class PermitOverridesDecisionCombiningAlgorithm 
    extends DecisionCombiningAlgorithm
{
    /**
     * Evaluates a list of <code>AuthorizationDecision</code>s
     * according to the XACML "Permit-overrides" policy-combining
     * algorithm.  Applies the XACML "Permit-overrides"
     * policy-combining algorithm: if a decision in the input list is
     * <code>Permit</code>, then return <code>Permit</code>, otherwise
     * if any decision were found to be <code>Deny</code>, then return
     * <code>Deny</code>, otherwise if any decision were
     * <code>Indeterminate</code> then result is
     * <code>Indeterminate</code>, otherwise returns
     * <code>NotApplicable</code>.
     */
    public void combine(final AuthorizationDecision ad) {
        if (decisionTaken)
            // decision taken, ignore further input
            return;

        if (ad.isPermit()) {
            // take  decision
            decisionTaken = true;
            current = ad;
        }
        else if (ad.isDeny()) {
            // "Deny" has precedence over "Indeterminate"
            current = ad;       
        }
        else if (ad.isIndeterminate()) {
            if (current.isNotApplicable())
                // override N/A with "Indeterminate"
                current = ad; 
        }
        else if (current.isNotApplicable()) {
            // no change to current status
        }
    }


    /** Constructor.  This is private; new instances should be gotten
     * via the newInstance() method. */
    public PermitOverridesDecisionCombiningAlgorithm() {
        current = AuthorizationDecision.NotApplicable;
        decisionTaken = false;
    }
}
