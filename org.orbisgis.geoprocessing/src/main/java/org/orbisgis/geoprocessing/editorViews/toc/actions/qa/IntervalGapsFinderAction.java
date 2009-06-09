package org.orbisgis.geoprocessing.editorViews.toc.actions.qa;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.geoalgorithm.orbisgis.qa.InternalGapFinder;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editorViews.toc.action.ILayerAction;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;

public class IntervalGapsFinderAction implements ILayerAction {

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

		BackgroundManager bm = (BackgroundManager) Services
				.getService(BackgroundManager.class);
		bm.backgroundOperation(new ExecuteProcessing(mapContext, layer));

	}

	private class ExecuteProcessing implements BackgroundJob {

		private ILayer layer;

		private MapContext mapContext;

		public ExecuteProcessing(MapContext mapContext, ILayer layer) {
			this.mapContext = mapContext;
			this.layer = layer;
		}

		public String getTaskName() {
			return "Find gaps";
		}

		public void run(IProgressMonitor pm) {

			DataManager dataManager = (DataManager) Services
					.getService(DataManager.class);

			final DataSourceFactory dsf = dataManager.getDSF();

			InternalGapFinder internalGapFinder = new InternalGapFinder(
					new SpatialDataSourceDecorator(layer.getDataSource()), pm);

			try {
				ObjectMemoryDriver gapDriver = internalGapFinder
						.getObjectMemoryDriver();

				String gaplayer = dsf.getSourceManager().nameAndRegister(
						gapDriver);

				final ILayer gapLayer = dataManager.createLayer(gaplayer);

				mapContext.getLayerModel().insertLayer(gapLayer, 0);

			} catch (DriverLoadException e) {
				Services.getErrorManager().error(
						"Cannot create the resulting layer of geometry type ",
						e);
			} catch (IllegalStateException e) {
				Services.getErrorManager().error("Cannot get the layer ", e);
			} catch (LayerException e) {
				Services.getErrorManager().error(
						"Cannot insert resulting layer based on "
								+ layer.getName(), e);
			}

		}
	}

}
