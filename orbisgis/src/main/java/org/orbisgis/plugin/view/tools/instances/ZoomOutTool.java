/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.orbisgis.plugin.view.tools.instances;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import org.orbisgis.plugin.view.tools.FinishedAutomatonException;
import org.orbisgis.plugin.view.tools.TransitionException;
import org.orbisgis.plugin.view.tools.instances.generated.ZoomOut;

/**
 * Tool to zoom out
 *
 * @author Fernando Gonzalez Cortes
 */
public class ZoomOutTool extends ZoomOut {

	/**
	 * @see org.estouro.tools.generated.ZoomOut#transitionTo_Standby()
	 */
	@Override
	public void transitionTo_Standby() throws TransitionException {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomOut#transitionTo_Done()
	 */
	@Override
	public void transitionTo_Done() throws TransitionException, FinishedAutomatonException {
		Rectangle2D extent = ec.getExtent();
		double width = 2 * extent.getWidth();
		double height = 2 * extent.getHeight();
		Rectangle2D newExtent = new Rectangle2D.Double(tm
				.getValues()[0]
				- width / 2, tm.getValues()[1] - height
				/ 2, width, height);
		ec.setExtent(newExtent);

		transition("init"); //$NON-NLS-1$
	}

	/**
	 * @see org.estouro.tools.generated.ZoomOut#transitionTo_Cancel()
	 */
	@Override
	public void transitionTo_Cancel() throws TransitionException {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomOut#drawIn_Standby(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Standby(Graphics g) {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomOut#drawIn_Done(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Done(Graphics g) {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomOut#drawIn_Cancel(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Cancel(Graphics g) {
	}

    public boolean isEnabled() {
        return ec.atLeastNThemes(1);
    }

    public boolean isVisible() {
        return true;
    }
}
