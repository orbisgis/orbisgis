/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se;

import org.orbisgis.core.renderer.se.fill.Fill;

/**
 *
 * @author maxence
 */
public interface FillNode {

	void setFill(Fill f);
	Fill getFill();
}
