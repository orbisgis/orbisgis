package org.orbisgis;

import org.orbisgis.editors.map.tools.PanTool;
import org.orbisgis.editors.map.tools.ZoomInTool;
import org.orbisgis.editors.map.tools.ZoomOutTool;

import com.vividsolutions.jts.geom.Envelope;

public class ExtentToolTest extends AbstractToolTest {

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
				new Envelope(previous.getMinX() - 100, 0,
						previous.getMinY() - 100, 0)));
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
