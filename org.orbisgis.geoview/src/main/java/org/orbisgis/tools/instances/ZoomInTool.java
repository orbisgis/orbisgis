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

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.net.URL;

import org.orbisgis.tools.TransitionException;

/**
 * Tool to zoom in
 *
 * @author Fernando Gonzalez Cortes
 */
public class ZoomInTool extends AbstractRectangleTool {

	@Override
	protected void rectangleDone(Rectangle2D rect) throws TransitionException {
		ec.setExtent(rect);
	}

	@Override
	public URL getMouseCursorURL() {
		return this.getClass().getResource("generated/zoom_in.gif");
	}

	@Override
	public Point getHotSpotOffset() {
		return new Point(5, 5);
	}

	public boolean isEnabled() {
		return ec.atLeastNThemes(1);
	}

	public boolean isVisible() {
		return true;
	}

}
