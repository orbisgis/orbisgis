/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
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

import java.awt.event.MouseEvent;

import junit.framework.TestCase;

import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.instances.SelectionTool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class SelectionTest extends TestCase {

	private TestEditionContext ec;

	private ToolManager tm;

	private GeometryFactory gf = new GeometryFactory();

	private Geometry p1 = gf.createPoint(new Coordinate(50, 50));

	private Geometry p2 = gf.createLineString(new Coordinate[] {
			new Coordinate(65, 65), new Coordinate(85, 85) });

	private Envelope envelope;

	public void testDeleteSelection() throws Exception {
		int total = ec.features.size();
		tm.setTool(new SelectionTool());
		tm.setValues(new double[] { envelope.getMinX(), envelope.getMinY() });
		tm.transition("point");
		tm.setValues(new double[] { envelope.getMaxX(), envelope.getMaxY() });
		tm.transition("point");
		assertTrue(ec.selected.size() == total);
		ec.removeSelected();
		assertTrue(ec.features.size() == 0);
	}

	public void testCtrlSelection() throws Exception {
		tm.setTool(new SelectionTool());
		tm
				.setValues(new double[] { p1.getCoordinate().x,
						p1.getCoordinate().y });
		tm.transition("point");
		assertTrue(ec.selected.size() == 1);
		tm
				.setValues(new double[] { p2.getCoordinate().x,
						p2.getCoordinate().y });
		tm.transition("point");
		assertTrue(ec.selected.size() == 1);
		tm.setMouseModifiers(MouseEvent.CTRL_DOWN_MASK);
		tm
				.setValues(new double[] { p1.getCoordinate().x,
						p1.getCoordinate().y });
		tm.transition("point");
		assertTrue(ec.selected.size() == 2);
		tm.setMouseModifiers(MouseEvent.CTRL_DOWN_MASK);
		tm
				.setValues(new double[] { p2.getCoordinate().x,
						p2.getCoordinate().y });
		tm.transition("point");
		assertTrue(ec.selected.size() == 1);
	}

	public void testSelectionToRight() throws Exception {
		tm.setTool(new SelectionTool());
		tm.setValues(new double[] { 55, 55 });
		tm.transition("point");
		tm.setValues(new double[] { 95, 95 });
		tm.transition("point");
		assertTrue(ec.selected.size() == 1);

		tm.setValues(new double[] { 55, 55 });
		tm.transition("point");
		tm.setValues(new double[] { 80, 80 });
		tm.transition("point");
		assertTrue(ec.selected.size() == 0);
	}

	public void testSelectionToLeft() throws Exception {
		tm.setTool(new SelectionTool());
		tm.setValues(new double[] { 95, 55 });
		tm.transition("point");
		tm.setValues(new double[] { 75, 95 });
		tm.transition("point");
		assertTrue(ec.selected.size() == 1);
	}

	public void testCtrlSelectionWithSquare() throws Exception {
		tm.setTool(new SelectionTool());
		tm.setValues(new double[] { 95, 55 });
		tm.transition("point");
		tm.setValues(new double[] { 75, 95 });
		tm.transition("point");
		assertTrue(ec.selected.size() == 1);

		tm.setMouseModifiers(MouseEvent.CTRL_DOWN_MASK);
		tm.setTool(new SelectionTool());
		tm.setValues(new double[] { 95, 55 });
		tm.transition("point");
		tm.setValues(new double[] { 75, 95 });
		tm.transition("point");
		assertTrue(ec.selected.size() == 0);
	}

	public void testSelectionWithPointWithSelection() throws Exception {
		testSelectionToLeft();

		tm
				.setValues(new double[] { p1.getCoordinate().x,
						p1.getCoordinate().y });
		tm.transition("point");
		assertTrue(ec.selected.size() == 1);
	}

	public void testMultiPointSelectionWithPoint() throws Exception {
		ec.features.add(gf.createMultiPoint(new Coordinate[] {
				new Coordinate(10, 10), new Coordinate(100, 100) }));

		tm.setTool(new SelectionTool());
		tm.setValues(new double[] { 10, 10 });
		tm.transition("point");
		assertTrue(ec.selected.size() == 1);
	}

	public void testSelectionInEmptyTheme() throws Exception {
		tm.setTool(new SelectionTool());
		tm.setValues(new double[] { 10, 10 });
		tm.transition("point");
		assertTrue(ec.selected.size() == 0);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ec = new TestEditionContext(Primitive.LINE_GEOMETRY_TYPE);
		ec.features.add(p1);
		ec.features.add(p2);
		tm = new ToolManager(new SelectionTool(), ec);
		envelope = new Envelope(p1.getEnvelopeInternal());
		envelope.expandToInclude(p2.getEnvelopeInternal());
		envelope.expandBy(tm.getTolerance() * 4);
	}
}
