/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.ui;

import org.gdms.driver.DriverException;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.views.table.InfoTool;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;

import com.vividsolutions.jts.geom.Envelope;

public class InfoToolTest extends UITest {

	public void testInfoTool() throws Exception {
		ILayer vector1 = addLayer("vectorial");
		ILayer vector2 = addLayer("hedgerow");

		// Query all the extent
		ToolManager toolManager = viewContext.getToolManager();
		toolManager.setTool(new InfoTool());

		// Assert there are some selected features
		long affectedRows = selectAll(vector1, toolManager);
		assertTrue(affectedRows > 0);
		// Assert the result is different depending on the selected layer
		assertTrue(affectedRows != selectAll(vector2, toolManager));

		// Remove layers
		viewContext.getViewModel().remove(vector1);
		viewContext.getViewModel().remove(vector2);

		// Clean the catalog
		IResource root = catalog.getTreeModel().getRoot();
		IResource[] childs = root.getResources();
		for (IResource resource : childs) {
			root.removeResource(resource);
		}
	}

	private long selectAll(ILayer vector, ToolManager toolManager)
			throws TransitionException, DriverException {
		viewContext.setSelectedLayers(new ILayer[] { vector });
		Envelope envelope = vector.getEnvelope();
		toolManager.setValues(new double[] { envelope.getMinX(),
				envelope.getMinY() });
		toolManager.transition(ToolManager.PRESS);
		toolManager.setValues(new double[] { envelope.getMaxX(),
				envelope.getMaxY() });
		toolManager.transition(ToolManager.RELEASE);
		long affectedRows = table.getContents().getRowCount();
		return affectedRows;
	}
}
