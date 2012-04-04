/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.plugins.editors.mapEditor;

import javax.swing.JButton;

import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.DataSource;

public class ZoomToSelectedFeaturesPlugIn extends AbstractPlugIn {

	private JButton btn;

	public ZoomToSelectedFeaturesPlugIn() {
		btn = new JButton(OrbisGISIcon.ZOOM_SELECTED);
		btn.setToolTipText(Names.POPUP_TABLE_ZOOMTOSELECTED_PATH1);
	}

	public boolean execute(PlugInContext context) throws Exception {
		final MapEditorPlugIn mapEditor = (MapEditorPlugIn) getPlugInContext()
				.getActiveEditor();
		final MapContext mc = (MapContext) mapEditor.getElement().getObject();

		BackgroundManager bm = Services.getService(BackgroundManager.class);
		bm.backgroundOperation(new BackgroundJob() {

			@Override
			public String getTaskName() {
				return I18N
						.getString("orbisgis.org.orbisgis.core.ui.plugins.editors.tableEditor.zoomToSelectedLayer.extent");
			}

			@Override
			public void run(ProgressMonitor pm) {
				ILayer[] layers = mc.getSelectedLayers();
				Envelope rect = null;
				for (ILayer lyr : layers) {
                                        try {
                                                if (!lyr.isStream()) {
                                                        int[] selectedRow = lyr.getSelection();

							DataSource sds = lyr
									.getDataSource();

							Geometry geometry = null;
							Envelope geometryEnvelope = null;
							for (int i = 0; i < selectedRow.length; i++) {
								if (sds.isVectorial()) {
									geometry = sds.getGeometry(selectedRow[i]);
									if (geometry != null) {
										geometryEnvelope = geometry
												.buffer(0.01)
												.getEnvelopeInternal();
									}
								} else if (sds.isRaster()) {
									geometryEnvelope = sds.getRaster(
											selectedRow[i]).getMetadata()
											.getEnvelope();
								}

								if (rect == null) {
									rect = new Envelope(geometryEnvelope);
								} else {
									rect.expandToInclude(geometryEnvelope);
								}

							}
                                                }
					} catch (DriverException e) {
						ErrorMessages.error(ErrorMessages.CannotComputeEnvelope, e);
					}
					
					if (rect != null) {
						mapEditor.getMapTransform().setExtent(rect);

					}
				}

			}

		});

		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getInfoToolBar().addPlugIn(this,
				btn, context);
	}

        @Override
	public boolean isEnabled() throws Exception{
		boolean isEnabled = false;
		MapEditorPlugIn mapEditor = null;
		if ((mapEditor = getPlugInContext().getMapEditor()) != null) {
			MapContext mc = (MapContext) mapEditor.getElement().getObject();
			ILayer[] layers = mc.getLayerModel().getLayersRecursively();
			for (ILayer lyr : layers) {
				if (!lyr.isStream()) {
					if (lyr.getSelection().length > 0)
						isEnabled = true;
				}
			}
		}
		btn.setEnabled(isEnabled);
		return isEnabled;
	}
}
