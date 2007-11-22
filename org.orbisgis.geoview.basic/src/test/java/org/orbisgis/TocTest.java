package org.orbisgis;

import org.gdms.data.NoSuchTableException;
import org.gdms.source.SourceManager;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.toc.EPTocLayerActionHelper;

public class TocTest extends UITest {

	public void testAddLayer() throws Exception {
		// Assert toc is empty
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == true);

		// Add a vectorial layer
		addLayer("vectorial");
		// Add a raster layer
		addLayer("tif");

		// assert they have been added
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == false);
		ILayer[] layers = viewContext.getRootLayer().getChildren();
		assertTrue(OrbisgisCore.getDSF().getSourceManager().getSource(
				layers[0].getName()) != null);
		assertTrue(OrbisgisCore.getDSF().getSourceManager().getSource(
				layers[1].getName()) != null);
	}

	public void testAddSQLLayer() throws Exception {
		String sql = "select * from " + viewContext.getLayers()[0].getName();
		String sourceName = "sqlResult";
		OrbisgisCore.getDSF().getSourceManager().register(sourceName, sql);
		ILayer layer = LayerFactory.createLayer(sourceName);
		viewContext.getRootLayer().put(layer);
		viewContext.getRootLayer().remove(layer);
		OrbisgisCore.getDSF().getSourceManager().remove(sourceName);
	}

	public void testGroupLayers() throws Exception {
		ILayer[] layers = viewContext.getLayers();
		viewContext.setSelectedLayers(layers);

		EPTocLayerActionHelper.execute(geoview,
				"org.orbisgis.geoview.toc.GroupLayersAction", layers);

		ILayer[] children = viewContext.getRootLayer().getChildren();
		ILayer group = children[0];
		assertTrue(children.length == 1);
		assertTrue(CollectionUtils.contains(group.getChildren(), layers[0]));
		assertTrue(CollectionUtils.contains(group.getChildren(), layers[1]));
		assertTrue(group.getChildren().length == 2);

		layers[0].moveTo(viewContext.getRootLayer());
		layers[1].moveTo(viewContext.getRootLayer());
		viewContext.getRootLayer().remove(group);
	}

	public void testRename() throws Exception {
		// get the raster layer and it's name
		ILayer layer = viewContext.getRootLayer().getChildren()[1];
		String mainName = layer.getName();

		// Change the name
		String alias = "newName";
		layer.setName(alias);

		// Assert the alias has been added
		String mainNameDSF = OrbisgisCore.getDSF().getSourceManager()
				.getMainNameFor(alias);
		assertTrue(mainName.equals(mainNameDSF));
	}

	public void testDeleteLayer() throws Exception {
		// Iterate over layers and remove everything
		ILayer root = viewContext.getRootLayer();
		ILayer[] layers = root.getChildren();
		for (ILayer layer : layers) {
			SourceManager sourceManager = OrbisgisCore.getDSF()
					.getSourceManager();
			String alias = layer.getName();
			boolean isMainName = sourceManager.getMainNameFor(alias).equals(
					alias);
			root.remove(layer);

			// Assert the alias has been removed from data source factory
			if (!isMainName) {
				try {
					sourceManager.getMainNameFor(alias);
					assertTrue(false);
				} catch (NoSuchTableException e) {
				}
			}
		}
	}

}
