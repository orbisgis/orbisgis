package org.orbisgis.geoview.tools;

import javax.swing.JLabel;

import org.orbisgis.editors.map.MapTransform;
import org.orbisgis.editors.map.tool.Primitive;
import org.orbisgis.editors.map.tool.ToolManager;
import org.orbisgis.editors.map.tools.SelectionTool;

import com.vividsolutions.jts.geom.Envelope;

import junit.framework.TestCase;

public class ToolTestCase extends TestCase {
	protected TestEditionContext ec;

	protected ToolManager tm;

	protected SelectionTool defaultTool;

	protected MapTransform mapTransform;

	@Override
	protected void setUp() throws Exception {
		defaultTool = new SelectionTool();
		mapTransform = new MapTransform();
		mapTransform.resizeImage(100, 100);
		mapTransform.setExtent(new Envelope(0, 100, 0, 100));

		ec = new TestEditionContext(Primitive.LINE_GEOMETRY_TYPE);
		tm = new ToolManager(defaultTool, ec, mapTransform, new JLabel());
	}

}
