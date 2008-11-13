package org.contrib.ui.editorViews.toc.actions.geometry.qa;

import java.awt.Color;

import org.contrib.model.jump.adapter.FeatureCollectionAdapter;
import org.contrib.model.jump.adapter.FeatureCollectionDatasourceAdapter;
import org.contrib.model.jump.model.FeatureCollection;
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
import org.orbisgis.outputManager.OutputManager;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jcs.qa.InternalOverlapFinder;

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

			FeatureCollection fc = new FeatureCollectionAdapter(
					new SpatialDataSourceDecorator(layer.getDataSource()));
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
					ObjectMemoryDriver overlapingFeaturesdriver = new ObjectMemoryDriver(
							new FeatureCollectionDatasourceAdapter(
									overlapingFeatures));
					String overlapingFeatureslayer = dsf.getSourceManager()
							.nameAndRegister(overlapingFeaturesdriver);

					ObjectMemoryDriver overlapingIndicatorsdriver = new ObjectMemoryDriver(
							new FeatureCollectionDatasourceAdapter(
									overlapIndicators));
					String overlapingIndicatorslayer = dsf.getSourceManager()
							.nameAndRegister(overlapingIndicatorsdriver);

					ObjectMemoryDriver overlapingSizeIndicatorsdriver = new ObjectMemoryDriver(
							new FeatureCollectionDatasourceAdapter(
									overlapSizeIndicators));
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
					om.makeVisible();

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
