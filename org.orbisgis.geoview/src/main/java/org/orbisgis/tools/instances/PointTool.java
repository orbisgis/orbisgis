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
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class PointTool extends AbstractPointTool {

	public boolean isEnabled() {
		String geometryType = ec.getActiveThemeGeometryType();
		return ((geometryType.equals(Primitive.POINT_GEOMETRY_TYPE))
				|| (geometryType.equals(Primitive.MULTIPOINT_GEOMETRY_TYPE)) || ec
				.isActiveThemeWritable());
	}

	public boolean isVisible() {
		return true;
	}

	@Override
	protected void pointDone(Point createPoint) throws TransitionException {
		try {
			String geometryType = ec.getActiveThemeGeometryType();
			if (geometryType.equals(Primitive.POINT_GEOMETRY_TYPE)) {
				ec.newGeometry(ToolManager.toolsGeometryFactory
						.createPoint(new Coordinate(tm.getValues()[0], tm
								.getValues()[1])));
			} else if (geometryType.equals(Primitive.MULTIPOINT_GEOMETRY_TYPE)) {
				ec.newGeometry(ToolManager.toolsGeometryFactory
						.createPoint(new Coordinate(tm.getValues()[0], tm
								.getValues()[1])));
			}
		} catch (EditionContextException e) {
			throw new TransitionException(e);
		}
	}

}
