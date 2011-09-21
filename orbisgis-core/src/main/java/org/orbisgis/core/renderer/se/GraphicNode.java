/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se;

import org.orbisgis.core.renderer.se.graphic.GraphicCollection;

/**
 * This interface must be implemented by each SE node that embeds a 
 * {@link GraphicCollection}.
 * @author maxence, alexis
 */
public interface GraphicNode {
        
        /**
         * Gets the {@link GraphicCollection} associated to this node.
         * @return 
         * A {@link GraphicCollection} instance.
         */
	GraphicCollection getGraphicCollection();
        
        /**
         * Sets the {@link GraphicCollection} associated to this node.
         * @param gc 
         */
	void setGraphicCollection(GraphicCollection gc);
}
