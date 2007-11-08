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
package org.orbisgis.tools.instances;

import java.awt.geom.Rectangle2D;

import org.orbisgis.tools.FinishedAutomatonException;
import org.orbisgis.tools.TransitionException;

/**
 * Tool to zoom in
 *
 * @author Fernando Gonzalez Cortes
 */
public class ZoomInTool extends AbstractRectangleTool {

	/**
	 * @see org.estouro.tools.generated.ZoomIn#transitionTo_RectangleDone()
	 */
	@Override
	public void transitionTo_RectangleDone() throws TransitionException,
			FinishedAutomatonException {
		double[] v = tm.getValues();

		double minx = Math.min(firstPoint[0], v[0]);
		double miny = Math.min(firstPoint[1], v[1]);

		Rectangle2D newExtent = new Rectangle2D.Double(minx, miny, Math
				.abs(v[0] - firstPoint[0]), Math.abs(v[1] - firstPoint[1]));

		ec.setExtent(newExtent);

		transition("init"); //$NON-NLS-1$
	}

	public boolean isEnabled() {
		return ec.atLeastNThemes(1);
	}

	public boolean isVisible() {
		return true;
	}
}
