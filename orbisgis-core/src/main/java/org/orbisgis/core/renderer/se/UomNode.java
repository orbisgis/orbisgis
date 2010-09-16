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
	void setUom(Uom u);
	public Uom getOwnUom();
}
