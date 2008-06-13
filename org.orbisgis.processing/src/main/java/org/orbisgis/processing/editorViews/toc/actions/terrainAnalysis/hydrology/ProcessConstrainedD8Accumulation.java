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
package org.orbisgis.processing.editorViews.toc.actions.terrainAnalysis.hydrology;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.hydrology.D8OpAccumulation;
import org.grap.processing.operation.hydrology.D8OpConstrainedAccumulation;
import org.grap.processing.operation.hydrology.D8OpDirection;
import org.grap.processing.operation.hydrology.D8OpStrahlerStreamOrder;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.processing.editorViews.toc.actions.utilities.AbstractGray16And32Process;
import org.orbisgis.ui.sif.RasterLayerCombo;
import org.sif.UIFactory;
import org.sif.multiInputPanel.IntType;
import org.sif.multiInputPanel.MultiInputPanel;

public class ProcessConstrainedD8Accumulation extends
		AbstractGray16And32Process implements ILayerAction {

	private MultiInputPanel mip;

	private MapContext mapContext;

	private ILayer rasterDir;

	private GeoRaster grCT;

	private ILayer rasterCT;

	private GeoRaster grDir;

	boolean checked = false;

	public void execute(MapContext mapContext, ILayer resource) {
		try {

			this.mapContext = mapContext;
			initUIPanel();

			if (UIFactory.showDialog(mip)) {

				rasterDir = mapContext.getLayerModel().getLayerByName(
						mip.getInput("direction"));

				rasterCT = mapContext.getLayerModel().getLayerByName(
						mip.getInput("constrained"));

				grDir = rasterCT.getRaster();

				grCT = rasterDir.getRaster();

				// save the computed GeoRaster in a tempFile
				final DataSourceFactory dsf = ((DataManager) Services
						.getService("org.orbisgis.DataManager")).getDSF();

				final String tempFile = dsf.getTempFile() + ".tif";
				// populate the GeoView TOC with a new RasterLayer
				DataManager dataManager = (DataManager) Services
						.getService("org.orbisgis.DataManager");

				final Operation grResult = new D8OpConstrainedAccumulation(grCT);
				final GeoRaster grLSFactor = grDir.doOperation(grResult);
				grLSFactor.save(tempFile);
				final ILayer newLayer = dataManager.createLayer(new File(
						tempFile));

				newLayer.setName("Constrained D8 accumulation");
				mapContext.getLayerModel().insertLayer(newLayer, 0);

			}

		} catch (IOException e) {
			Services.getErrorManager().error(
					"Cannot compute " + getClass().getName() + ": "
							+ resource.getName(), e);
		} catch (OperationException e) {
			Services.getErrorManager().error(
					"Cannot compute " + getClass().getName() + ": "
							+ resource.getName(), e);
		} catch (LayerException e) {
			Services.getErrorManager().error(
					"Cannot compute " + getClass().getName() + ": "
							+ resource.getName(), e);
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot compute " + getClass().getName() + ": "
							+ resource.getName(), e);
		}
	}

	private void initUIPanel() throws DriverException {
		mip = new MultiInputPanel("Build a constrained grid accumulation");

		mip.addInput("direction", "D8 direction", new RasterLayerCombo(
				mapContext));

		mip.addInput("constrained", "Constrained grid", new RasterLayerCombo(
				mapContext));

	}

	protected GeoRaster evaluateResult(GeoRaster geoRasterSrc)
			throws OperationException, IOException {

		return null;
	}

}