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
package org.orbisgis.renderer.manual;

import java.io.File;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.DataManager;
import org.orbisgis.DefaultDataManager;
import org.orbisgis.DefaultExtendedWorkspace;
import org.orbisgis.DefaultSymbolManager;
import org.orbisgis.ExtendedWorkspace;
import org.orbisgis.Services;
import org.orbisgis.SymbolManager;
import org.orbisgis.editorViews.toc.actions.cui.LegendsPanel;
import org.orbisgis.editorViews.toc.actions.cui.extensions.ILegendPanelUI;
import org.orbisgis.editorViews.toc.actions.cui.extensions.PnlUniqueSymbolLegend;
import org.orbisgis.editorViews.toc.actions.cui.extensions.PnlIntervalLegend;
import org.orbisgis.editorViews.toc.actions.cui.extensions.PnlProportionalLegend;
import org.orbisgis.editorViews.toc.actions.cui.extensions.PnlUniqueValueLegend;
import org.orbisgis.errorManager.DefaultErrorManager;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.pluginManager.workspace.DefaultWorkspace;
import org.orbisgis.pluginManager.workspace.Workspace;
import org.sif.UIFactory;

public class CUITest {

	public static void main(String[] args) throws Throwable {
		DataSourceFactory dsf = new DataSourceFactory("target/sources",
				"target/temp");

		Services.registerService("org.orbisgis.DataManager", DataManager.class,
				"", new DefaultDataManager(dsf));
		Services.registerService("org.orbisgis.SymbolManager",
				SymbolManager.class, "", new DefaultSymbolManager(new File(
						"collection.xml")));
		Services.registerService("org.orbisgis.Workspace", Workspace.class, "",
				new DefaultWorkspace() {

					@Override
					public File getFile(String name) {
						return new File(name);
					}

				});
		Services.registerService("org.orbisgis.ExtendedWorkspace",
				ExtendedWorkspace.class, "", new DefaultExtendedWorkspace() {

					@Override
					public File getFile(String name) {
						return new File(name);
					}

				});
		Services.registerService("org.orbisgis.ErrorManager",
				ErrorManager.class, "", new DefaultErrorManager() {

					@Override
					public void error(String userMsg) {
						System.err.println(userMsg);
					}

					@Override
					public void error(String userMsg, Throwable exception) {
						System.err.println(userMsg);
						exception.printStackTrace();
					}

				});

		ILayer layer = getDataManager().createLayer(
				new File("/home/gonzales/workspace"
						+ "/datas2tests/shp/smallshape2D/points.shp"));
		layer.open();
		Type typ = layer.getDataSource().getMetadata().getFieldType(
				layer.getDataSource().getSpatialFieldIndex());
		GeometryConstraint cons = (GeometryConstraint) typ
				.getConstraint(Constraint.GEOMETRY_TYPE);

		LegendsPanel pan = new LegendsPanel();
		pan.init(cons, layer.getVectorLegend(),
				new ILegendPanelUI[] { new PnlUniqueSymbolLegend(true, pan),
						new PnlUniqueValueLegend(pan),
						new PnlIntervalLegend(pan),
						new PnlProportionalLegend(pan)}, layer);
		if (UIFactory.showDialog(pan)) {
			try {
				layer.setLegend(pan.getLegends());
			} catch (DriverException e) {
				Services.getErrorManager().error("Driver exception ...", e);
			}
		}
		layer.close();

	}

	public static DataManager getDataManager() {
		return (DataManager) Services.getService("org.orbisgis.DataManager");
	}

}
