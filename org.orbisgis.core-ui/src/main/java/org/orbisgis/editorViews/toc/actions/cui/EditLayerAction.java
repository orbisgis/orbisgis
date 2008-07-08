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

import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.editorViews.toc.actions.cui.gui.ILegendPanelUI;
import org.orbisgis.editorViews.toc.actions.cui.gui.JPanelUniqueSymbolLegend;
import org.orbisgis.editorViews.toc.actions.cui.gui.PnlIntervalLegend;
import org.orbisgis.editorViews.toc.actions.cui.gui.PnlLabelLegend;
import org.orbisgis.editorViews.toc.actions.cui.gui.PnlProportionalLegend;
import org.orbisgis.editorViews.toc.actions.cui.gui.PnlUniqueValueLegend;
import org.orbisgis.editorViews.toc.actions.cui.ui.LegendsPanel;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.sif.UIFactory;

public class EditLayerAction implements ILayerAction {

	public boolean accepts(ILayer layer) {
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
			pan.init(cons, layer.getVectorLegend(), new ILegendPanelUI[] {
					new JPanelUniqueSymbolLegend(true, pan),
					new PnlUniqueValueLegend(pan),
					new PnlIntervalLegend(pan),
					new PnlProportionalLegend(pan),
					new PnlLabelLegend(pan)}, layer);
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