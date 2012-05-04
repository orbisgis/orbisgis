/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
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
package org.orbisgis.view.map.tools;

import java.util.Observable;

import javax.swing.AbstractButton;
 
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;


import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import javax.swing.ImageIcon;
import org.gdms.data.DataSource;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.orbisgis.view.icons.OrbisGISIcon;

public class PointTool extends AbstractPointTool {

	@Override
	public void update(Observable o, Object arg) {
		//PlugInContext.checkTool(this);
	}

        @Override
	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return ToolUtilities.geometryTypeIs(vc, TypeFactory.createType(Type.POINT),
                                TypeFactory.createType(Type.MULTIPOINT))
				&& ToolUtilities.isActiveLayerEditable(vc);
	}

        @Override
	public boolean isVisible(MapContext vc, ToolManager tm) {
		return isEnabled(vc, tm);
	}

	@Override
	protected void pointDone(Point point, MapContext mc, ToolManager tm)
			throws TransitionException {
		Geometry g = point;
		if (ToolUtilities.geometryTypeIs(mc, TypeFactory.createType(Type.MULTIPOINT))) {
			g = ToolManager.toolsGeometryFactory
					.createMultiPoint(new Point[] { point });
		}

		DataSource sds = mc.getActiveLayer().getDataSource();
		try {
			Value[] row = new Value[sds.getMetadata().getFieldCount()];
			row[sds.getSpatialFieldIndex()] = ValueFactory.createValue(g);
			row = ToolUtilities.populateNotNullFields(sds, row);
			sds.insertFilledRow(row);
		} catch (DriverException e) {
			throw new TransitionException("Cannot insert polygon", e);
		}
	}

	@Override
	public double getInitialZ(MapContext mapContext) {
		return ToolUtilities.getActiveLayerInitialZ(mapContext);
	}

        @Override
	public String getName() {
		return I18N.tr("orbisgis.core.ui.editors.map.tool.point_tooltip");
	}

        @Override
        public ImageIcon getImageIcon() {
            return OrbisGISIcon.getIcon("point");
        }
}
