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
/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.orbisgis.geoview.tools;

import org.orbisgis.editors.map.tools.PanTool;
import org.orbisgis.editors.map.tools.ZoomInTool;
import org.orbisgis.editors.map.tools.ZoomOutTool;

import com.vividsolutions.jts.geom.Envelope;

public class ExtentToolTest extends ToolTestCase {

	public void testZoomIn() throws Exception {
		tm.setTool(new ZoomInTool());
		tm.setValues(new double[] { 0, 0 });
		tm.transition("press");
		tm.setValues(new double[] { 100, 100 });
		tm.transition("release");

		assertTrue(mapTransform.getExtent()
				.equals(new Envelope(0, 100, 0, 100)));

	}

	public void testZoomOut() throws Exception {
		Envelope previous = mapTransform.getExtent();
		tm.setTool(new ZoomOutTool());
		tm.setValues(new double[] { 0, 0 });
		tm.transition("point");

		assertTrue(mapTransform.getExtent().contains(previous));
	}

	public void testPan() throws Exception {
		Envelope previous = mapTransform.getExtent();
		tm.setTool(new PanTool());
		tm.setValues(new double[] { 0, 0 });
		tm.transition("press");
		tm.setValues(new double[] { 100, 100 });
		tm.transition("release");

		assertTrue(mapTransform.getExtent().equals(
				new Envelope(previous.getMinX() - 100, 0, previous
						.getMinY() - 100, 0)));
	}

	/**
	 * Tests that on error, the default tool is selected
	 *
	 * @throws Exception
	 */
	public void testToolReinicializationOnError() throws Exception {
		PanTool pt = new PanTool();
		tm.setTool(pt);
		tm.setValues(new double[] { 0, 0 });
		tm.transition("press");
		tm.setValues(new double[] { 100 });
		try {
			tm.transition("release");
		} catch (Throwable t) {
			assertTrue(tm.getTool().getClass().equals(defaultTool.getClass()));
		}
	}
}
