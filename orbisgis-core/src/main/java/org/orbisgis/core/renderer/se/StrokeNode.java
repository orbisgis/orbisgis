/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se;

import org.orbisgis.core.renderer.se.stroke.Stroke;

/**
 *
 * @author maxence
 */
public interface StrokeNode {
	void setStroke(Stroke s);
	Stroke getStroke();
}
