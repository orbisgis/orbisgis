package org.orbisgis.geoprocessing.editorViews.toc.actions.qa;

import java.awt.Color;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.model.FeatureCollection;
import org.gdms.model.FeatureCollectionDecorator;
import org.gdms.model.FeatureCollectionModelUtils;
import org.geoalgorithm.jcs.qa.InternalOverlapFinder;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.outputManager.OutputManager;
import org.orbisgis.core.ui.editorViews.toc.action.ILayerAction;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;


public class IntervalOverlapsFinderAction implements ILayerAction {

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
			return "Overlaps finders";
		}

		public void run(IProgressMonitor pm) {

			DataManager dataManager = (DataManager) Services
					.getService(DataManager.class);

			final DataSourceFactory dsf = dataManager.getDSF();

			FeatureCollection fc = new FeatureCollectionDecorator(layer.getDataSource());
			InternalOverlapFinder internalOverlapFinder = new InternalOverlapFinder(
					fc, pm);

			internalOverlapFinder.computeOverlaps();

			FeatureCollection overlapingFeatures = internalOverlapFinder
					.getOverlappingFeatures();

			FeatureCollection overlapIndicators = internalOverlapFinder
					.getOverlapIndicators();

			FeatureCollection overlapSizeIndicators = internalOverlapFinder
					.getOverlapSizeIndicators();

			try {
				if (overlapingFeatures.size() > 0) {
					ObjectMemoryDriver overlapingFeaturesdriver = FeatureCollectionModelUtils.getObjectMemoryDriver(
									overlapingFeatures);
					String overlapingFeatureslayer = dsf.getSourceManager()
							.nameAndRegister(overlapingFeaturesdriver);

					ObjectMemoryDriver overlapingIndicatorsdriver = FeatureCollectionModelUtils.getObjectMemoryDriver(
									overlapIndicators);
					String overlapingIndicatorslayer = dsf.getSourceManager()
							.nameAndRegister(overlapingIndicatorsdriver);

					ObjectMemoryDriver overlapingSizeIndicatorsdriver = FeatureCollectionModelUtils.getObjectMemoryDriver(
									overlapSizeIndicators);
					String overlapingSizeIndicatorslayer = dsf
							.getSourceManager().nameAndRegister(
									overlapingSizeIndicatorsdriver);

					final ILayer overlapingFeaturesLayer = dataManager
							.createLayer(overlapingFeatureslayer);

					final ILayer overlapingSizeIndicatorsLayer = dataManager
							.createLayer(overlapingSizeIndicatorslayer);

					final ILayer overlapingIndicatorsLayer = dataManager
							.createLayer(overlapingIndicatorslayer);

					mapContext.getLayerModel().insertLayer(
							overlapingFeaturesLayer, 0);
					mapContext.getLayerModel().insertLayer(
							overlapingIndicatorsLayer, 0);
					mapContext.getLayerModel().insertLayer(
							overlapingSizeIndicatorsLayer, 0);
				}

				else {
					OutputManager om = (OutputManager) Services
							.getService(OutputManager.class);
					Color color = Color.black;
					om.println("Report ----------------------------------", color);

					color = Color.red;
					om.println("No overlaping geometries in the layer  : " + layer.getName(), color);

					color = Color.black;
					om.println("----------------------------------", color);
				}

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
			} catch (DriverException e) {
				Services.getErrorManager().error(
						"Cannot read the resulting datasource from the layer ",
						e);
			}

		}
	}

}
