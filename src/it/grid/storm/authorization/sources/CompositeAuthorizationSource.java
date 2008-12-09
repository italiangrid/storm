/* (Despite its name, this is -*- Java -*- )
 *
 * CompositeAuthorizationSource
 *
 * Copyright (c) 2005, Riccardo Murri <riccardo.murri@ictp.it>
 *
 * You may copy, distribute and modify this file under the terms of
 * the LICENSE.txt file at the root of the StoRM backend source tree.
 *
 * $Id: CompositeAuthorizationSource.jappo,v 1.4 2006/03/31 06:43:20 rmurri Exp $
 */
/*-
 *
 * This source file uses cpp macros to code some nearly-identical
 * methods; to get the real Java source file, preprocess this one
 * with:
 *
 *    cpp -P -C -o CompositeAuthorizationSource.java \
 *        CompositeAuthorizationSource.jappo
 *
 * Or you can use the Jappo Java preprocessor (see
 * http://jappo.opensourcefinland.org/), which has cpp-compatible
 * syntax.
 */
package it.grid.storm.authorization.sources;


import it.grid.storm.config.Configuration;
import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.authorization.AuthorizationQueryInterface;
import it.grid.storm.authorization.DecisionCombiningAlgorithm;
import it.grid.storm.namespace.StoRI;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import it.grid.storm.griduser.GridUserInterface;


/**
 * Queries a list of sources and takes a final decision according to
 * a chosen <code>DecisionCombiningAlgorithm</code>.
 *
 * <p>Is a Composite for the <code>AuthorizationQueryInterface</code>,
 * as its whole prupose is to act as a single
 * <code>AuthorizationQueryInterface</code> masking the access to
 * different <code>AuthorizationSource</code>s.
 *
 * @see AuthorizationDecision
 * @see AuthorizationQueryInterface
 * @see it.grid.storm.authorization.combiners
 *
 * @author Riccardo Murri
 */

public class CompositeAuthorizationSource
    implements AuthorizationQueryInterface {


    // --- protected --- //

    /** The algorithm to apply for combining decisions by different
     * sources. */
    protected Class decisionCombinerClass;

    /** List holding all authorization sources that should be
     * queried. */
    protected Vector sources;


    // --- public --- //

    /** Constructor, taking the combining algorithm and list of other
     * authorization sources.
     *
     * <p>The list of authorization sources is not copied, rather used
     * directly.
     */
    public CompositeAuthorizationSource(Class algorithmClass,
                                        Collection sources)
    {
        setCombiningAlgorithm(algorithmClass);
        this.sources = new Vector(LIST_INITIAL_CAPACITY, LIST_INCREMENT);
        addAllSources(sources);
    }

    /** Set the algorithm used to combine decisions from different sources */
    private void setCombiningAlgorithm(Class algorithmClass) {
        this.decisionCombinerClass = algorithmClass;
    }

    /** Add a source to the internal list. */
    public void addSource(AuthorizationQueryInterface source) {
        assert (null != this.sources)
            : "Null sources list in CompositeAuthorizationSource.addSource";
        assert (null != source)
            : "Null 'source' parameter passed to CompositeAuthorizationSource.addSource";
        assert (source instanceof AuthorizationQueryInterface) :
            "Contract violation: Object "
            + source.toString()
            + " passed to CompositeAuthorizationSources.addSource()"
            + "is not instance of AuthorizationQueryInterface";
        sources.add(source);
    }

    /** Add a collection of sources to the internal list. */
    public void addAllSources(Collection sources) {
        assert (null != this.sources)
            : "Null sources list in CompositeAuthorizationSource.addAllSources";
        assert (null != sources)
            : "Null 'sources' list passed to CompositeAuthorizationSource.addAllSources";
        for (Iterator i=sources.iterator();
             i.hasNext();) {
            Object element = i.next();
            assert (element instanceof AuthorizationQueryInterface) :
                "Contract violation: Object "
                + element.toString()
                + " passed to AuthorizationSourcesList.addAll()"
                + "is not instance of AuthorizationQueryInterface";
        }
        this.sources.addAll(sources);
    }


    // Since all methods below have the same scheme, let's use
    // some preprocessor trick to do the job; the real source
    // to this file is in the CompositeAuthorizationSource.java.cpp
    // file in CVS... I could find no way of doing this other than
    // preprocessor macros...
    //
    // cpp -P -C CompositeAuthorizationSource.jappo \
    //     -o CompositeAuthorizationSource.java
    //
    // WARNING: do not end macro with a ';'
    // or 'javac' will complain about
    // unreachable statements and fail!!
    //
    public AuthorizationDecision canUseStormAtAll(final GridUserInterface gridUser) {
        assert (null != sources) : "Null this.sources list in CompositeAuthorizationSource"; DecisionCombiningAlgorithm algo; try { algo = (DecisionCombiningAlgorithm) decisionCombinerClass.newInstance(); } catch (Exception x) { throw new RuntimeException(x); } for(Iterator i=sources.iterator(); i.hasNext() && !algo.isDecisionTaken();) algo.combine(((AuthorizationQueryInterface) i.next()).canUseStormAtAll(gridUser)); return algo.getDecision();
    }

    public AuthorizationDecision
        canReadFile(final GridUserInterface gridUser, final StoRI file) {
      assert (null != sources) : "Null this.sources list in CompositeAuthorizationSource"; DecisionCombiningAlgorithm algo; try { algo = (DecisionCombiningAlgorithm) decisionCombinerClass.newInstance(); } catch (Exception x) { throw new RuntimeException(x); } for(Iterator i=sources.iterator(); i.hasNext() && !algo.isDecisionTaken();) algo.combine(((AuthorizationQueryInterface) i.next()).canReadFile(gridUser, file)); return algo.getDecision();
    }

    public AuthorizationDecision
        canWriteFile(final GridUserInterface gridUser, final StoRI existingFile) {
      assert (null != sources) : "Null this.sources list in CompositeAuthorizationSource"; DecisionCombiningAlgorithm algo; try { algo = (DecisionCombiningAlgorithm) decisionCombinerClass.newInstance(); } catch (Exception x) { throw new RuntimeException(x); } for(Iterator i=sources.iterator(); i.hasNext() && !algo.isDecisionTaken();) algo.combine(((AuthorizationQueryInterface) i.next()).canWriteFile(gridUser, existingFile)); return algo.getDecision();
    }

    public AuthorizationDecision
        canCreateNewFile(final GridUserInterface gridUser, final StoRI targetFile){
      assert (null != sources) : "Null this.sources list in CompositeAuthorizationSource"; DecisionCombiningAlgorithm algo; try { algo = (DecisionCombiningAlgorithm) decisionCombinerClass.newInstance(); } catch (Exception x) { throw new RuntimeException(x); } for(Iterator i=sources.iterator(); i.hasNext() && !algo.isDecisionTaken();) algo.combine(((AuthorizationQueryInterface) i.next()).canCreateNewFile(gridUser, targetFile)); return algo.getDecision();
    }

    public AuthorizationDecision
        canChangeAcl(final GridUserInterface gridUser,
                     final StoRI fileOrDirectory) {
      assert (null != sources) : "Null this.sources list in CompositeAuthorizationSource"; DecisionCombiningAlgorithm algo; try { algo = (DecisionCombiningAlgorithm) decisionCombinerClass.newInstance(); } catch (Exception x) { throw new RuntimeException(x); } for(Iterator i=sources.iterator(); i.hasNext() && !algo.isDecisionTaken();) algo.combine(((AuthorizationQueryInterface) i.next()).canChangeAcl(gridUser, fileOrDirectory)); return algo.getDecision();
    }

    public AuthorizationDecision
        canGiveaway(final GridUserInterface gridUser, final StoRI fileOrDirectory) {
      assert (null != sources) : "Null this.sources list in CompositeAuthorizationSource"; DecisionCombiningAlgorithm algo; try { algo = (DecisionCombiningAlgorithm) decisionCombinerClass.newInstance(); } catch (Exception x) { throw new RuntimeException(x); } for(Iterator i=sources.iterator(); i.hasNext() && !algo.isDecisionTaken();) algo.combine(((AuthorizationQueryInterface) i.next()).canGiveaway(gridUser, fileOrDirectory)); return algo.getDecision();
    }

    public AuthorizationDecision
        canListDirectory(final GridUserInterface gridUser, final StoRI directory) {
      assert (null != sources) : "Null this.sources list in CompositeAuthorizationSource"; DecisionCombiningAlgorithm algo; try { algo = (DecisionCombiningAlgorithm) decisionCombinerClass.newInstance(); } catch (Exception x) { throw new RuntimeException(x); } for(Iterator i=sources.iterator(); i.hasNext() && !algo.isDecisionTaken();) algo.combine(((AuthorizationQueryInterface) i.next()).canListDirectory(gridUser, directory)); return algo.getDecision();
    }

    public AuthorizationDecision
        canTraverseDirectory(final GridUserInterface gridUser,
                             final StoRI directory) {
      assert (null != sources) : "Null this.sources list in CompositeAuthorizationSource"; DecisionCombiningAlgorithm algo; try { algo = (DecisionCombiningAlgorithm) decisionCombinerClass.newInstance(); } catch (Exception x) { throw new RuntimeException(x); } for(Iterator i=sources.iterator(); i.hasNext() && !algo.isDecisionTaken();) algo.combine(((AuthorizationQueryInterface) i.next()).canTraverseDirectory(gridUser, directory)); return algo.getDecision();
    }

    public AuthorizationDecision
        canRename(final GridUserInterface gridUser, final StoRI file) {
      assert (null != sources) : "Null this.sources list in CompositeAuthorizationSource"; DecisionCombiningAlgorithm algo; try { algo = (DecisionCombiningAlgorithm) decisionCombinerClass.newInstance(); } catch (Exception x) { throw new RuntimeException(x); } for(Iterator i=sources.iterator(); i.hasNext() && !algo.isDecisionTaken();) algo.combine(((AuthorizationQueryInterface) i.next()).canRename(gridUser, file)); return algo.getDecision();
    }

    public AuthorizationDecision
        canDelete(final GridUserInterface gridUser, final StoRI file) {
      assert (null != sources) : "Null this.sources list in CompositeAuthorizationSource"; DecisionCombiningAlgorithm algo; try { algo = (DecisionCombiningAlgorithm) decisionCombinerClass.newInstance(); } catch (Exception x) { throw new RuntimeException(x); } for(Iterator i=sources.iterator(); i.hasNext() && !algo.isDecisionTaken();) algo.combine(((AuthorizationQueryInterface) i.next()).canDelete(gridUser, file)); return algo.getDecision();
    }

    public AuthorizationDecision
        canMakeDirectory(final GridUserInterface gridUser,
                         final StoRI targetDirectory) {
      assert (null != sources) : "Null this.sources list in CompositeAuthorizationSource"; DecisionCombiningAlgorithm algo; try { algo = (DecisionCombiningAlgorithm) decisionCombinerClass.newInstance(); } catch (Exception x) { throw new RuntimeException(x); } for(Iterator i=sources.iterator(); i.hasNext() && !algo.isDecisionTaken();) algo.combine(((AuthorizationQueryInterface) i.next()).canMakeDirectory(gridUser, targetDirectory)); return algo.getDecision();
    }


    // --- private --- //

    /** Initial capcity of the authorization sources vector. We assume
     * a vector with very few elements. */
    private static int LIST_INITIAL_CAPACITY = 4;

    /** Default increment for authorization sources vector resize
     * operations. We assume a vector with very few elements, so keep
     * the increment chunk small. */
    private static int LIST_INCREMENT = 1;
}
