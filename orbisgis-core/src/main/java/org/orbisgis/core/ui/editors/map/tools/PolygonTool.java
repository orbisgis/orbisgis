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
package org.orbisgis.core.ui.editors.map.tools;

import java.util.Observable;

import javax.swing.AbstractButton;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class PolygonTool extends AbstractPolygonTool {

	AbstractButton button;

	@Override
	public AbstractButton getButton() {
		return button;
	}

	public void setButton(AbstractButton button) {
		this.button = button;
	}

	@Override
	public void update(Observable o, Object arg) {
		PlugInContext.checkTool(this);
	}

	@Override
	protected void polygonDone(com.vividsolutions.jts.geom.Polygon pol,
			MapContext mc, ToolManager tm) throws TransitionException {
		Geometry g = pol;
		if (ToolUtilities.geometryTypeIs(mc, GeometryConstraint.MULTI_POLYGON)) {
			g = ToolManager.toolsGeometryFactory
					.createMultiPolygon(new Polygon[] { pol });
		}

		SpatialDataSourceDecorator sds = mc.getActiveLayer().getDataSource();
		try {
			Value[] row = new Value[sds.getMetadata().getFieldCount()];
                        g.setSRID(sds.getSRID());
			row[sds.getSpatialFieldIndex()] = ValueFactory.createValue(g);
			row = ToolUtilities.populateNotNullFields(sds, row);
			sds.insertFilledRow(row);
		} catch (DriverException e) {
			throw new TransitionException("Cannot insert polygon", e);
		}
	}

	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return ToolUtilities.geometryTypeIs(vc, GeometryConstraint.POLYGON,
				GeometryConstraint.MULTI_POLYGON)
				&& ToolUtilities.isActiveLayerEditable(vc);
	}

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return isEnabled(vc, tm);
	}

	public double getInitialZ(MapContext mapContext) {
		return ToolUtilities.getActiveLayerInitialZ(mapContext);
	}

	public String getName() {
		return I18N
				.getText("orbisgis.core.ui.editors.map.tool.polygon_tooltip");
	}

}
