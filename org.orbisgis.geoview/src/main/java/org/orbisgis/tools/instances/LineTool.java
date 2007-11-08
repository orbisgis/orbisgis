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

import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.TransitionException;

import com.vividsolutions.jts.geom.LineString;

public class LineTool extends AbstractLineTool {

	public boolean isEnabled() {
		return ((ec.getActiveThemeGeometryType() == Primitive.LINE_GEOMETRY_TYPE) || (ec
				.getActiveThemeGeometryType() == Primitive.MULTILINE_GEOMETRY_TYPE))
				&& ec.isActiveThemeWritable();
	}

	public boolean isVisible() {
		return true;
	}

	@Override
	protected void lineDone(LineString ls) throws TransitionException {
		try {
			ec.newGeometry(ls);
		} catch (EditionContextException e) {
			throw new TransitionException(e);
		}
	}
}
