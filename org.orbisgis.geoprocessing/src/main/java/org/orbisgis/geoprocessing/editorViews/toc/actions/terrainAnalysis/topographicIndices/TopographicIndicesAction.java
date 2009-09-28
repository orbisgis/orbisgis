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
package org.orbisgis.geoprocessing.editorViews.toc.actions.terrainAnalysis.topographicIndices;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.topographicIndices.LSFactorOp;
import org.grap.processing.operation.topographicIndices.StreamPowerIndexOp;
import org.grap.processing.operation.topographicIndices.WetnessIndexOp;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.components.sif.RasterLayerCombo;
import org.orbisgis.core.ui.editorViews.toc.action.ILayerAction;
import org.orbisgis.geoprocessing.editorViews.toc.actions.utilities.AbstractGray16And32Process;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.CheckBoxChoice;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;

public class TopographicIndicesAction extends AbstractGray16And32Process
		implements ILayerAction {

	private static final Integer WETNESS = 1;

	private static final Integer STREAMPOWERINDEX = 2;

	private static final Integer LSFACTOR = 3;

	private static final Integer DEFAULT = 0;

	private MultiInputPanel mip;


	private MapContext mapContext;

	private ILayer rasterAccflow;

	private GeoRaster grAccflow;

	private ILayer rasterSlope;

	private GeoRaster grSlope;

	boolean checked = false;

	public void execute(MapContext mapContext, ILayer resource) {
		try {

			this.mapContext = mapContext;
			initUIPanel();

			if (UIFactory.showDialog(mip)) {

				rasterAccflow = mapContext.getLayerModel().getLayerByName(
						mip.getInput("accflow"));

				rasterSlope = mapContext.getLayerModel().getLayerByName(
						mip.getInput("slope"));

				grSlope = rasterSlope.getRaster();

				grAccflow = rasterAccflow.getRaster();

				// save the computed GeoRaster in a tempFile
				final DataSourceFactory dsf = ((DataManager) Services
						.getService(DataManager.class)).getDSF();

				final String tempFilewetness = dsf.getTempFile() + "wetness"
						+ ".tif";
				final String tempFileSPI = dsf.getTempFile() + "spi" + ".tif";
				final String tempFileLS = dsf.getTempFile() + "lsfactor"
						+ ".tif";

				// populate the GeoView TOC with a new RasterLayer
				DataManager dataManager = (DataManager) Services
						.getService(DataManager.class);

				if (new Boolean(mip.getInput("wetness"))) {

					final Operation opwetness = new WetnessIndexOp(grAccflow);
					final GeoRaster grwetness = grSlope.doOperation(opwetness);
					grwetness.save(tempFilewetness);
					final ILayer newLayer = dataManager.createLayer(new File(
							tempFilewetness));

					newLayer.setName("Wetness");
					mapContext.getLayerModel().insertLayer(newLayer, 0);

				}
				if (new Boolean(mip.getInput("streampowerindex"))) {

					final Operation streamPowerIndex = new StreamPowerIndexOp(
							grAccflow);
					final GeoRaster grstreamPowerIndex = grSlope
							.doOperation(streamPowerIndex);
					grstreamPowerIndex.save(tempFileSPI);
					final ILayer newLayer = dataManager.createLayer(new File(
							tempFileSPI));

					newLayer.setName("StreamPowerIndex");
					mapContext.getLayerModel().insertLayer(newLayer, 0);

				}
				if (new Boolean(mip.getInput("lsfactor"))) {

					final Operation lSFactor = new LSFactorOp(grAccflow);
					final GeoRaster grLSFactor = grSlope.doOperation(lSFactor);
					grLSFactor.save(tempFileLS);
					final ILayer newLayer = dataManager.createLayer(new File(
							tempFileLS));

					newLayer.setName("LSFactor");
					mapContext.getLayerModel().insertLayer(newLayer, 0);

				}

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
		mip = new MultiInputPanel("Topographic indices");

		mip.addInput("slope", "Slope grid (in radians)", new RasterLayerCombo(
				mapContext));

		mip.addInput("accflow", "Accumulation grid", new RasterLayerCombo(
				mapContext));

		mip.addInput("wetness", "Wetness", null, new CheckBoxChoice(true));
		mip.addInput("streampowerindex", "Stream power index", null,
				new CheckBoxChoice(true));
		mip.addInput("lsfactor", "LS factor", null, new CheckBoxChoice(true));

		mip.group("Data", new String[] { "slope", "accflow" });
		mip.group("Indices", new String[] { "wetness", "streampowerindex",
				"lsfactor" });

		// TODO Talk with fergonco
		/*
		 * mip.addValidationExpression( "wetness=true or streampowerindex=true
		 * or lsfactor=true", "At leat one indice must be checked");
		 */
	}

	@Override
	protected String evaluateResult(ILayer layer, MapContext mapContext) throws OperationException, IOException, DriverException {
		// TODO Auto-generated method stub
		return null;
	}

}