package org.orbisgis.geoprocessing.editorViews.toc.actions.topology;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.geoalgorithm.orbisgis.topology.PlanarGraph;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editorViews.toc.action.ILayerAction;
import org.orbisgis.progress.NullProgressMonitor;

public class NetworkTopologyLayerAction implements ILayerAction {

	public boolean accepts(MapContext mc, ILayer layer) {
		try {

			return layer.isVectorial();
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Vector type unreadable for this layer", e);
		}
		return false;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount >= 1;
	}

	public void execute(MapContext mapContext, ILayer layer) {

		DataManager dataManager = (DataManager) Services
				.getService(DataManager.class);

		DataSourceFactory dsf = dataManager.getDSF();

		try {

			SpatialDataSourceDecorator sds = layer.getDataSource();

			PlanarGraph planarGraph = new PlanarGraph();

			ObjectMemoryDriver edges = planarGraph.createEdges(sds,
					new NullProgressMonitor());

			ObjectMemoryDriver nodes = planarGraph.createNodes(edges);

			String edgeslayer = dsf.getSourceManager().nameAndRegister(edges);

			String nodeslayer = dsf.getSourceManager().nameAndRegister(nodes);

			final ILayer edgesLayer = dataManager.createLayer(edgeslayer);

			final ILayer nodesLayer = dataManager.createLayer(nodeslayer);

			mapContext.getLayerModel().insertLayer(nodesLayer, 0);
			mapContext.getLayerModel().insertLayer(edgesLayer, 0);

		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot read the resulting datasource from the layer ", e);
		} catch (LayerException e) {
			Services.getErrorManager()
					.error(
							"Cannot insert resulting layer based on "
									+ layer.getName(), e);
		} catch (NonEditableDataSourceException e) {
			e.printStackTrace();
		}

	}

}
