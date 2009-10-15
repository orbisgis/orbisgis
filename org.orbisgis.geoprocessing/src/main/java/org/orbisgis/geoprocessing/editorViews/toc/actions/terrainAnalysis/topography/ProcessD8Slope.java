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
package org.orbisgis.geoprocessing.editorViews.toc.actions.terrainAnalysis.topography;

import java.io.IOException;

import org.gdms.driver.DriverException;
import org.grap.processing.OperationException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.geoprocessing.editorViews.toc.actions.utilities.AbstractGray16And32Process;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.ComboBoxChoice;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;

public class ProcessD8Slope extends AbstractGray16And32Process {
	private MultiInputPanel mip;

	@Override
	protected String evaluateResult(ILayer layer, MapContext mapContext)
			throws OperationException, IOException, DriverException {
		init();
		String defaultGeom = layer.getDataSource().getDefaultGeometry();
		String layerName = layer.getName();
		String sql = null;
		if (UIFactory.showDialog(mip)) {
			String options = mip.getInput("unit");

			if (options.equals("radian")) {
				sql = "select D8Slope(" + defaultGeom
						+ ", 'radian') as raster from  " + layerName + "";
			} else if (options.equals("degree")) {
				sql = "select D8Slope(" + defaultGeom
						+ ", 'degree') as raster from  " + layerName + "";
			} else if (options.equals("percent")) {
				sql = "select D8Slope(" + defaultGeom
						+ ", 'percent') as raster from  " + layerName
						+ ";";
			}
		}
		return sql;
	}

	public void init() {
		mip = new MultiInputPanel("Calculate a slope grid (D8 method)");
		mip.addInput("unit", "slope unit", new ComboBoxChoice(new String[] {
				"radian", "degree", "percent" }));
	}
}