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
package org.orbisgis.geoprocessing.editorViews.toc.actions.terrainAnalysis.hydrology;

import java.io.IOException;

import org.gdms.driver.DriverException;
import org.grap.processing.OperationException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editorViews.toc.action.ILayerAction;
import org.orbisgis.geoprocessing.editorViews.toc.actions.utilities.AbstractGray16And32Process;
import org.orbisgis.geoprocessing.ui.sif.RasterGray16And32LayerCombo;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;

public class ProcessConstrainedD8Accumulation extends
		AbstractGray16And32Process implements ILayerAction {

	private MultiInputPanel mip;

	private ILayer rasterDir;

	private ILayer rasterCT;

	@Override
	protected String evaluateResult(ILayer layer, MapContext mapContext)
			throws OperationException, IOException, DriverException {

		initUIPanel(mapContext);

		String sql = null;
		if (UIFactory.showDialog(mip)) {

			rasterDir = mapContext.getLayerModel().getLayerByName(
					mip.getInput("direction"));

			rasterCT = mapContext.getLayerModel().getLayerByName(
					mip.getInput("constrained"));

			sql = "select D8ConstrainedAccumulation(d."
					+ rasterDir.getDataSource().getDefaultGeometry()
					+ ", c."
					+ rasterCT.getDataSource().getDefaultGeometry()
					+ ") from " + rasterDir.getName() + " d, "
					+ rasterCT.getName() + " c";

		}
		return sql;
	}

	private void initUIPanel(MapContext mapContext) throws DriverException,
			IOException {
		mip = new MultiInputPanel("Build a constrained grid accumulation");

		mip.addInput("direction", "D8 direction",
				new RasterGray16And32LayerCombo(mapContext));
		mip.addInput("constrained", "Constrained grid",
				new RasterGray16And32LayerCombo(mapContext));
	}

}