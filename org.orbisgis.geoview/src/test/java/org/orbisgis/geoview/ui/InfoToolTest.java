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
		viewContext.getRootLayer().remove(vector1);
		viewContext.getRootLayer().remove(vector2);

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
