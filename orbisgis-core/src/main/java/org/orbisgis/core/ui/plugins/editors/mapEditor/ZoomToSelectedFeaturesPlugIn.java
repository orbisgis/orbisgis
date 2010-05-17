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
 * Copyright (C) 2009 Erwan BOCHER, Pierre-yves FADET
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
 *    Pierre-Yves.Fadet_at_ec-nantes.fr
 *    thomas.leduc _at_ cerma.archi.fr
 */

package org.orbisgis.core.ui.plugins.editors.mapEditor;

import javax.swing.JButton;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.MapEditorPlugIn;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class ZoomToSelectedFeaturesPlugIn extends AbstractPlugIn {

	private JButton btn;

	public ZoomToSelectedFeaturesPlugIn() {
		btn = new JButton(getIcon(IconNames.MAP_ZOOM_SELECTED_ICON));
	}

	public boolean execute(PlugInContext context) throws Exception {
		MapEditorPlugIn mapEditor = (MapEditorPlugIn) getPlugInContext().getActiveEditor();
		MapContext mc = (MapContext) mapEditor.getElement().getObject();
		ILayer[] layers = mc.getLayerModel().getLayersRecursively();
		Envelope rect = null;
		for (ILayer lyr : layers) {
			try {
				int[] selectedRow = lyr.getSelection();

				SpatialDataSourceDecorator sds = lyr.getDataSource();

				Geometry geometry = null;
				Envelope geometryEnvelope = null;
				for (int i = 0; i < selectedRow.length; i++) {
					if (sds.isDefaultVectorial()) {
						geometry = sds.getGeometry(selectedRow[i]);
						if (geometry != null) {
							geometryEnvelope = geometry.buffer(0.01)
									.getEnvelopeInternal();
						}
					} else if (sds.isDefaultRaster()) {
						geometryEnvelope = sds.getRaster(selectedRow[i])
								.getMetadata().getEnvelope();
					}

					if (rect == null) {
						rect = new Envelope(geometryEnvelope);
					} else {
						rect.expandToInclude(geometryEnvelope);
					}

				}
			} catch (DriverException e) {
				Services.getService(ErrorManager.class).error(
						"Cannot compute envelope", e);
			}
		}

		if (rect != null) {
			mapEditor.getMapTransform().setExtent(rect);

		}
		return true;
	}
	
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getInfoToolBar().addPlugIn(this,
				btn, context);
	}

	public boolean isEnabled() {
		boolean isEnabled = false;
		MapEditorPlugIn mapEditor = null;
		if((mapEditor=getPlugInContext().getMapEditor()) != null){
				MapContext mc = (MapContext) mapEditor.getElement().getObject();
				ILayer[] layers = mc.getLayerModel().getLayersRecursively();
				for (ILayer lyr : layers) {
				if (!lyr.isWMS()) {
					lyr.getSelection();
					if (lyr.getSelection().length > 0)
						isEnabled = true;
				}
			}
		}
		btn.setEnabled(isEnabled);
		return isEnabled;
	}
	
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}
}
