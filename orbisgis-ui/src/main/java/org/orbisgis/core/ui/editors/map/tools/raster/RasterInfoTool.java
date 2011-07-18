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
package org.orbisgis.core.ui.editors.map.tools.raster;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Observable;

import javax.swing.AbstractButton;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.generic.GenericObjectDriver;
import org.grap.model.GeoRaster;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.editors.map.tools.AbstractPointTool;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.plugins.views.information.InformationManager;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.gdms.driver.driverManager.DriverManager;

public class RasterInfoTool extends AbstractPointTool {
	public final static String[] LABELS = new String[] { I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.pixelX"), I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.pixelY"), //$NON-NLS-1$ //$NON-NLS-2$
			I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.pixelValue"), I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.rasterWidth"), I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.rasterHeight"), I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.realworldX"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.realworldY") }; //$NON-NLS-1$

	public boolean isEnabled(MapContext vc, ToolManager tm) {
		try {
			if ((vc.getSelectedLayers().length == 1)
					&& vc.getSelectedLayers()[0].isRaster()
					&& vc.getSelectedLayers()[0].isVisible()) {
				return true;
			}
		} catch (DriverException e) {
		}
		return false;
	}

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

	@Override
	protected void pointDone(Point point, MapContext vc, ToolManager tm)
			throws TransitionException {
		try {
			final GeoRaster geoRaster = vc.getSelectedLayers()[0].getRaster();
			final Coordinate realWorldCoord = point.getCoordinate();

			final Point2D pixelGridCoord = geoRaster.fromRealWorldToPixel(
					realWorldCoord.x, realWorldCoord.y);

			final int pixelX = (int) pixelGridCoord.getX();
			final int pixelY = (int) pixelGridCoord.getY();

			final float pixelValue = geoRaster.getImagePlus().getProcessor()
					.getPixelValue(pixelX, pixelY);
			final int width = geoRaster.getWidth();
			final int height = geoRaster.getHeight();

			// populate the PixelInfoView...
			InformationManager im = Services
					.getService(InformationManager.class);
			String[] columnsNames = new String[] { I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.column"), I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.row"), I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.value"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.width"), I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.height"), I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.x"), I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.y") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			Type[] types = new Type[columnsNames.length];
			for (int i = 0; i < types.length; i++) {
				types[i] = TypeFactory.createType(Type.STRING);
			}
			GenericObjectDriver omd = new GenericObjectDriver(columnsNames, types);
			omd.addValues(new Value[] { ValueFactory.createValue(pixelX),
					ValueFactory.createValue(pixelY),
					ValueFactory.createValue(pixelValue),
					ValueFactory.createValue(width),
					ValueFactory.createValue(height),
					ValueFactory.createValue(realWorldCoord.x),
					ValueFactory.createValue(realWorldCoord.y) });
			DataManager dataManager = Services.getService(DataManager.class);
			im.setContents(dataManager.getDataSourceFactory().getDataSource(omd, DriverManager.DEFAULT_SINGLE_TABLE_NAME));
		} catch (IOException e) {
			Services.getErrorManager().error(
					I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.cannotAccessingGeorasterDatas"), e); //$NON-NLS-1$
		} catch (DriverLoadException e) {
			Services.getErrorManager().error(
					I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.problemWithGenericDriver"), e); //$NON-NLS-1$
		} catch (DriverException e) {
			Services.getErrorManager().error(
					I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.cannotAccessingGeorasterDatas"), e); //$NON-NLS-1$
		}
	}

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
	
	public String getName() {
		return I18N.getString("orbisgis.org.orbisgis.ui.rasterInfoTool.getPixelValue"); //$NON-NLS-1$
	}
}