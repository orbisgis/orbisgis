/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.plugins.editors.mapEditor.raster;

import java.io.File;
import java.io.IOException;

import javax.swing.JButton;

import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.grap.processing.operation.GeoRasterCalculator;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.multiInputPanel.ComboBoxChoice;
import org.orbisgis.core.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.core.ui.components.sif.RasterLayerCombo;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

public class RasterAlgebraPlugIn extends AbstractPlugIn {

	private JButton btn;

	public RasterAlgebraPlugIn() {
		btn = new JButton(OrbisGISIcon.RASTERALGEBRA);
		btn.setToolTipText(Names.RASTERALGEBRA_TOOTIP);
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		try {
			IEditor editor = Services.getService(EditorManager.class)
					.getActiveEditor();
			MapContext mapContext = (MapContext) editor.getElement()
					.getObject();

			final MultiInputPanel mip = new MultiInputPanel(I18N
					.getString("orbisgis.org.orbisgis.imageCalculator"));
			mip.addInput("source1", I18N
					.getString("orbisgis.org.orbisgis.ui.layer1"),
					new RasterLayerCombo(mapContext));
			mip.addInput("method", I18N
					.getString("orbisgis.org.orbisgis.ui.method"),
					new ComboBoxChoice(GeoRasterCalculator.operators.keySet()
							.toArray(new String[0])));
			mip.addInput("source2", I18N
					.getString("orbisgis.org.orbisgis.ui.layer2"),
					new RasterLayerCombo(mapContext));

			if (UIFactory.showDialog(mip)) {
				final ILayer raster1 = mapContext.getLayerModel()
						.getLayerByName(mip.getInput("source1"));
				final ILayer raster2 = mapContext.getLayerModel()
						.getLayerByName(mip.getInput("source2"));
				final String method = mip.getInput("method");

				final GeoRaster grResult = raster1.getRaster().doOperation(
						new GeoRasterCalculator(raster2.getRaster(),
								GeoRasterCalculator.operators.get(method)));

				// save the computed GeoRaster in a tempFile
				final DataSourceFactory dsf = ((DataManager) Services
						.getService(DataManager.class)).getDataSourceFactory();
				final String tempFile = dsf.getTempFile() + ".tif";
				grResult.save(tempFile);

				// populate the GeoView TOC with a new RasterLayer
				DataManager dataManager = (DataManager) Services
						.getService(DataManager.class);
				final ILayer newLayer = dataManager.createLayer(new File(
						tempFile));
				mapContext.getLayerModel().insertLayer(newLayer, 0);
			}

		} catch (OperationException e) {
			ErrorMessages.error(I18N
					.getString("orbisgis.org.orbisgis.raster.cannotDoOperation"),
					e);
		} catch (IOException e) {
			ErrorMessages.error(ErrorMessages.ErrorIn + " "
					+ this.getClass().getSimpleName(), e);
		} catch (LayerException e) {
			ErrorMessages.error(ErrorMessages.ErrorIn + " "
					+ this.getClass().getSimpleName(), e);
		} catch (DriverException e) {
			ErrorMessages
					.error(ErrorMessages.ProblemWhileAccessingGeoRaster, e);
		} catch (DriverLoadException e) {
			ErrorMessages.error(ErrorMessages.CannotCreateGeoRaster, e);
		}
		return false;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getRasterToolBar().addPlugIn(this,
				btn, context);
	}

	@Override
	public boolean isEnabled() {
		boolean isEnabled = false;
		IEditor editor = Services.getService(EditorManager.class)
				.getActiveEditor();
		if (editor != null && editor instanceof MapEditorPlugIn) {
			try {
				MapContext mc = (MapContext) editor.getElement().getObject();
				ILayer[] selectedLayers = mc.getSelectedLayers();
				if ((selectedLayers.length == 1)
						&& selectedLayers[0].isRaster()
						&& selectedLayers[0].isVisible()) {
					isEnabled = true;
					btn.setEnabled(isEnabled);
					return true;
				}
			} catch (DriverException e) {
			}
		}
		btn.setEnabled(isEnabled);

		return isEnabled;
	}
}