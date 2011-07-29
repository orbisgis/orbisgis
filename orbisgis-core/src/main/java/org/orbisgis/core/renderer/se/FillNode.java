/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se;

import org.orbisgis.core.renderer.se.fill.Fill;

/**
 * Interface to be implemented by every node that can contain a <code>Fill</code> element.
 * @author maxence
 */
public interface FillNode {

	void setFill(Fill f);
	Fill getFill();
}
