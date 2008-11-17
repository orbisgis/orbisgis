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
package org.orbisgis.editorViews.toc.actions.cui;

import javax.swing.JOptionPane;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.editorViews.toc.actions.cui.legend.EPLegendHelper;
import org.orbisgis.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.editorViews.toc.actions.cui.legend.ISymbolEditor;
import org.orbisgis.editors.map.MapEditor;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.map.MapTransform;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.views.editor.EditorManager;
import org.sif.UIFactory;

public class EditLayerAction implements ILayerAction {

	public boolean accepts(MapContext mc, ILayer layer) {
		try {
			return layer.isVectorial();
		} catch (DriverException e) {
			return false;
		}
	}

	public boolean acceptsSelectionCount(int layerCount) {
		return layerCount == 1;
	}

	public void execute(MapContext mapContext, ILayer layer) {
		try {
			Type typ = layer.getDataSource().getMetadata().getFieldType(
					layer.getDataSource().getSpatialFieldIndex());
			GeometryConstraint cons = (GeometryConstraint) typ
					.getConstraint(Constraint.GEOMETRY_TYPE);

			LegendsPanel pan = new LegendsPanel();
			// Obtain MapTransform
			EditorManager em = (EditorManager) Services
					.getService(EditorManager.class);
			MapTransform mt = null;
			// Find the map editor editing mapContext
			IEditor editor = em.getEditors("org.orbisgis.editors.Map",
					mapContext)[0];
			mt = ((MapEditor) editor).getMapTransform();
			if (mt == null) {
				JOptionPane.showMessageDialog(null,
						"Cannot find a map editor, 1:1 scale used");
			}

			Legend[] legend = layer.getVectorLegend();
			Legend[] copies = new Legend[legend.length];
			for (int i = 0; i < copies.length; i++) {
				Object obj = legend[i].getJAXBObject();
				Legend copy = legend[i].newInstance();
				copy.setJAXBObject(obj);
				copies[i] = copy;
			}
			ILegendPanel[] legends = EPLegendHelper.getLegendPanels(pan);
			ISymbolEditor[] symbolEditors = EPLegendHelper.getSymbolPanels();
			pan.init(mt, cons, copies, legends, symbolEditors, layer);
			if (UIFactory.showDialog(pan)) {
				try {
					layer.setLegend(pan.getLegends());
				} catch (DriverException e) {
					Services.getErrorManager().error("Driver exception ...", e);
				}
			}
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot access the layer legend",
					e);
		}
	}
}