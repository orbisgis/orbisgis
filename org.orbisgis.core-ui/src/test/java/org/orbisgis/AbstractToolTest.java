package org.orbisgis;

import java.awt.image.BufferedImage;

import javax.swing.JLabel;

import junit.framework.TestCase;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.orbisgis.editors.map.tool.ToolManager;
import org.orbisgis.editors.map.tools.SelectionTool;
import org.orbisgis.layerModel.DefaultMapContext;
import org.orbisgis.map.MapTransform;

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
		Services.registerService("org.orbisgis.DataManager", DataManager.class,
				"", dataManager);

		createSource("mixed", TypeFactory.createType(Type.GEOMETRY,
				new GeometryConstraint(GeometryConstraint.MIXED)));

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
