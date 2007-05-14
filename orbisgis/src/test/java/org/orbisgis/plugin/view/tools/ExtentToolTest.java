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
package org.orbisgis.plugin.view.tools;

import java.awt.geom.Rectangle2D;

import junit.framework.TestCase;

import org.orbisgis.plugin.view.tools.Primitive;
import org.orbisgis.plugin.view.tools.ToolManager;
import org.orbisgis.plugin.view.tools.instances.PanTool;
import org.orbisgis.plugin.view.tools.instances.SelectionTool;
import org.orbisgis.plugin.view.tools.instances.ZoomInTool;
import org.orbisgis.plugin.view.tools.instances.ZoomOutTool;


public class ExtentToolTest extends TestCase {
	private TestEditionContext ec;

	private ToolManager tm;

	private SelectionTool defaultTool;

	public void testZoomIn() throws Exception {
		tm.setTool(new ZoomInTool());
		tm.setValues(new double[] { 0, 0 });
		tm.transition("point");
		tm.setValues(new double[] { 100, 100 });
		tm.transition("point");

		assertTrue(ec.getExtent()
				.equals(new Rectangle2D.Double(0, 0, 100, 100)));

	}

	public void testZoomOut() throws Exception {
		Rectangle2D previous = ec.getExtent();
		tm.setTool(new ZoomOutTool());
		tm.setValues(new double[] { 0, 0 });
		tm.transition("point");

		assertTrue(ec.getExtent().contains(previous));
	}

	public void testPan() throws Exception {
		Rectangle2D previous = ec.getExtent();
		tm.setTool(new PanTool());
		tm.setValues(new double[] { 0, 0 });
		tm.transition("point");
		tm.setValues(new double[] { 100, 100 });
		tm.transition("point");

		assertTrue(ec.getExtent().equals(
				new Rectangle2D.Double(previous.getMinX() - 100, previous
						.getMinY() - 100, 100, 100)));
	}

	/**
	 * Tests that on error, the default tool is selected
	 * @throws Exception
	 */
	public void testToolReinicializationOnError() throws Exception {
		PanTool pt = new PanTool();
		tm.setTool(pt);
		tm.setValues(new double[] { 0, 0 });
		tm.transition("point");
		tm.setValues(new double[] { 100 });
		try {
			tm.transition("point");
		} catch (Throwable t) {
			assertTrue(tm.getTool().getClass().equals(defaultTool.getClass()));
		}
	}

	@Override
	protected void setUp() throws Exception {
		defaultTool = new SelectionTool();
		ec = new TestEditionContext(Primitive.LINE_GEOMETRY_TYPE);
		tm = new ToolManager(defaultTool, ec);
	}

}
