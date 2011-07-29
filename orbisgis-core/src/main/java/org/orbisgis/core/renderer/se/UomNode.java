/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se;

import org.orbisgis.core.renderer.se.common.Uom;

/**
 * Define UOM setter
 *
 * @author maxence
 */
public interface UomNode {
        /**
         * Associates a unit of measure to this node
         * @param u 
         */
	void setUom(Uom u);
        /**
         * Get the Uom associated to this node. It differs from SymbolizerNode#getUom
         * in the sense that the method in SymbolizerNode will search for the nearest
         * Uom int the tree of Nodes, if this node does not contain one, while this
         * method is expected to return null if it can't find an Uom directly.
         * @return 
         * A Uom instance, if this has got one, null otherwise.
         */
	public Uom getOwnUom();
}
