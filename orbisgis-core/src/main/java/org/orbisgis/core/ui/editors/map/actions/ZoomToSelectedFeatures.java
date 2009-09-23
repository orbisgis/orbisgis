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
package org.orbisgis.core.ui.editors.map.actions;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editor.action.IEditorAction;
import org.orbisgis.core.ui.editors.map.MapEditor;
import org.orbisgis.errorManager.ErrorManager;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class ZoomToSelectedFeatures implements IEditorAction {

	public void actionPerformed(IEditor editor) {
		MapContext mc = (MapContext) editor.getElement().getObject();
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
			((MapEditor) editor).getMapTransform().setExtent(rect);

		}

	}

	public boolean isEnabled(IEditor editor) {
		MapContext mc = (MapContext) editor.getElement().getObject();
		ILayer[] layers = mc.getLayerModel().getLayersRecursively();
		boolean flag = false;
		for (ILayer lyr : layers) {
			lyr.getSelection();
			if (lyr.getSelection().length > 0)
				flag = true;

		}
		return flag;
	}

	public boolean isVisible(IEditor editor) {

		return true;
	}
}