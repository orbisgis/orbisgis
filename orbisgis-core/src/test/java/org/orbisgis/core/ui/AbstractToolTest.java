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
package org.orbisgis.core.ui;

import java.awt.image.BufferedImage;

import javax.swing.JLabel;

import junit.framework.TestCase;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.DefaultDataManager;
import org.orbisgis.core.layerModel.DefaultMapContext;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tools.SelectionTool;

import com.vividsolutions.jts.geom.Envelope;

public class AbstractToolTest extends TestCase {

	protected DefaultMapContext mapContext;
	protected MapTransform mapTransform;
	protected ToolManager tm;
	protected SelectionTool defaultTool;
	private DefaultDataManager dataManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		DataSourceFactory dsf = new DataSourceFactory(
				"src/test/resources/backup", "src/test/resources/backup");

		dataManager = new DefaultDataManager(dsf);
		Services.registerService(DataManager.class, "", dataManager);

		createSource("mixed", TypeFactory.createType(Type.GEOMETRY));

		mapContext = new DefaultMapContext();
		mapContext.getLayerModel().addLayer(dataManager.createLayer("mixed"));
		mapTransform = new MapTransform();
		mapTransform.setImage(new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_ARGB));
		mapTransform.setExtent(new Envelope(0, 100, 0, 100));
		defaultTool = new SelectionTool();
		tm = new ToolManager(defaultTool, mapContext, mapTransform,
				new JLabel());
	}

	private void createSource(String name, Type geomType) {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(
				new String[] { "the_geom" }, new Type[] { geomType });
		dataManager.getSourceManager().register(name, omd);
	}

}
