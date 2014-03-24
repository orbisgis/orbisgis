/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.map.tools;

import java.util.Observable;

 
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;


import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import javax.swing.ImageIcon;
import org.gdms.data.DataSource;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 * Draw a polygon
 */
public class PolygonTool extends AbstractPolygonTool {

	@Override
	public void update(Observable o, Object arg) {
		//PlugInContext.checkTool(this);
	}

	@Override
	protected void polygonDone(com.vividsolutions.jts.geom.Polygon pol,
			MapContext mc, ToolManager tm) throws TransitionException {
		Geometry g = pol;
		if (ToolUtilities.geometryTypeIs(mc, TypeFactory.createType(Type.MULTIPOLYGON))) {
			g = ToolManager.toolsGeometryFactory
					.createMultiPolygon(new Polygon[] { pol });
		}
		DataSource sds = mc.getActiveLayer().getDataSource();
		try {
			Value[] row = new Value[sds.getMetadata().getFieldCount()];
			row[sds.getSpatialFieldIndex()] = ValueFactory.createValue(g);
			row = ToolUtilities.populateNotNullFields(sds, row);
			sds.insertFilledRow(row);
		} catch (DriverException e) {
			throw new TransitionException("Cannot insert polygon :"+e.getMessage(), e);
		}
	}

	@Override
	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return ToolUtilities.geometryTypeIs(vc, TypeFactory.createType(Type.POLYGON),
                TypeFactory.createType(Type.MULTIPOLYGON))
				&& ToolUtilities.isActiveLayerEditable(vc);
	}

	@Override
	public boolean isVisible(MapContext vc, ToolManager tm) {
		return isEnabled(vc, tm);
	}

	@Override
	public double getInitialZ(MapContext mapContext) {
		return ToolUtilities.getActiveLayerInitialZ(mapContext);
	}

    @Override
    public String getTooltip() {
        return i18n.tr("Draw a polygon");
    }

    @Override
	public String getName() {
		return i18n.tr("Draw a polygon");
	}

    @Override
    public ImageIcon getImageIcon() {
        return OrbisGISIcon.getIcon("edition/drawpolygon");
    }

}
