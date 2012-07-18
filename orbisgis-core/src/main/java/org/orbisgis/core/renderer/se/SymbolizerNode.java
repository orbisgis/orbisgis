package org.orbisgis.core.renderer.se;

import java.util.HashSet;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;

/**
 * SymbolizerNode allow to browse the styling tree
 * It's mainly used to fetch the nearest Uom definition of any element
 *
 * @todo extract getUom() and add void update(), then every element should implement this (even parameters)
 *
 * @author maxence
 */
public interface SymbolizerNode{
    /**
     * Get the unit of measure associated with the current node.
     * @return 
     */
    Uom getUom();

    /**
     * get the parent of this current <code>SymbolizerNode</code>
     * @return 
     */
    SymbolizerNode getParent();

    /**
     * Set the parent of this <code>SymbolizerNode</code>
     * @param node 
     */
    void setParent(SymbolizerNode node);

    /**
     * Get a set containing the name of the features that are referenced in 
     * this {@code Style}. We use a {@code HashSet}. This way, we can be sure
     * that features are not referenced twice.
     * @return 
     * The names of all the needed features, in a {@code HashSet} instance.
     */
    HashSet<String> dependsOnFeature();

    /**
     * Retrieve an object describing the type of analysis made in the
     * symbolizer.
     * @return
     */
    UsedAnalysis getUsedAnalysis();
}
