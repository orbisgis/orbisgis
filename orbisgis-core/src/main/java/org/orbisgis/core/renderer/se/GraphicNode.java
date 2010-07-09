/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se;

import org.orbisgis.core.renderer.se.graphic.GraphicCollection;

/**
 *
 * @author maxence
 */
public interface GraphicNode {
	GraphicCollection getGraphicCollection();
	void setGraphicCollection(GraphicCollection gc);
}
