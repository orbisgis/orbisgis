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
package org.orbisgis.editorViews.toc.actions;

import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.editors.map.MapContextManager;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.MapContext;

public class CreateGroup implements ILayerAction {
	public boolean accepts(MapContext mc, ILayer layer) {
		return layer.acceptsChilds();
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		MapContextManager vcm = (MapContextManager) Services
				.getService(MapContextManager.class);
		return (vcm.getActiveView() != null) && (selectionCount <= 1);
	}

	public void execute(MapContext mapContext, ILayer resource) {
		MapContextManager vcm = (MapContextManager) Services
				.getService(MapContextManager.class);
		DataManager dataManager = (DataManager) Services
				.getService(DataManager.class);
		if (vcm.getActiveView() != null) {
			ILayer newLayerCollection = dataManager
					.createLayerCollection("group" + System.currentTimeMillis());

			if ((resource == null) || (!resource.acceptsChilds())) {
				resource = vcm.getActiveView().getLayerModel();
			}
			try {
				resource.addLayer(newLayerCollection);
			} catch (LayerException e) {
				throw new RuntimeException("bug!");
			}
		}
	}

}