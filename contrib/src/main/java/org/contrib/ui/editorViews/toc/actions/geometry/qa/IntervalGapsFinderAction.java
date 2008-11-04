package org.contrib.ui.editorViews.toc.actions.geometry.qa;

import org.contrib.algorithm.qa.InternalGapFinder;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;

public class IntervalGapsFinderAction implements ILayerAction {

	public boolean accepts(ILayer layer) {

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
			return "Gaps finders";
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
