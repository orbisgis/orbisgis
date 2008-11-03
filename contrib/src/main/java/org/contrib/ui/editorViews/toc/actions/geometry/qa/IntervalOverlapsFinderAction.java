package org.contrib.ui.editorViews.toc.actions.geometry.qa;

import org.contrib.model.jump.adapter.FeatureCollectionAdapter;
import org.contrib.model.jump.adapter.FeatureCollectionDatasourceAdapter;
import org.contrib.model.jump.adapter.TaskMonitorAdapter;
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

import com.vividsolutions.jcs.qa.InternalOverlapFinder;

public class IntervalOverlapsFinderAction implements ILayerAction {

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

		DataManager dataManager = (DataManager) Services
				.getService(DataManager.class);

		final DataSourceFactory dsf = dataManager.getDSF();

		FeatureCollection fc = new FeatureCollectionAdapter(
				new SpatialDataSourceDecorator(layer.getDataSource()));
		InternalOverlapFinder internalOverlapFinder = new InternalOverlapFinder(
				fc, new TaskMonitorAdapter());

		internalOverlapFinder.computeOverlaps();

		FeatureCollection overlapingFeatures = internalOverlapFinder
				.getOverlappingFeatures();

		FeatureCollection overlapIndicators = internalOverlapFinder
				.getOverlapIndicators();

		FeatureCollection overlapSizeIndicators = internalOverlapFinder
				.getOverlapSizeIndicators();

		try {
			ObjectMemoryDriver overlapingFeaturesdriver = new ObjectMemoryDriver(
					new FeatureCollectionDatasourceAdapter(overlapingFeatures));
			String overlapingFeatureslayer = dsf.getSourceManager()
					.nameAndRegister(overlapingFeaturesdriver);

			ObjectMemoryDriver overlapingIndicatorsdriver = new ObjectMemoryDriver(
					new FeatureCollectionDatasourceAdapter(overlapIndicators));
			String overlapingIndicatorslayer = dsf.getSourceManager()
					.nameAndRegister(overlapingIndicatorsdriver);

			ObjectMemoryDriver overlapingSizeIndicatorsdriver = new ObjectMemoryDriver(
					new FeatureCollectionDatasourceAdapter(
							overlapSizeIndicators));
			String overlapingSizeIndicatorslayer = dsf.getSourceManager()
					.nameAndRegister(overlapingSizeIndicatorsdriver);

			final ILayer overlapingFeaturesLayer = dataManager
					.createLayer(overlapingFeatureslayer);

			final ILayer overlapingSizeIndicatorsLayer = dataManager
					.createLayer(overlapingSizeIndicatorslayer);
			final ILayer overlapingIndicatorsLayer = dataManager
					.createLayer(overlapingIndicatorslayer);
			mapContext.getLayerModel().insertLayer(overlapingFeaturesLayer, 0);
			mapContext.getLayerModel()
					.insertLayer(overlapingIndicatorsLayer, 0);
			mapContext.getLayerModel().insertLayer(
					overlapingSizeIndicatorsLayer, 0);

		} catch (DriverLoadException e) {
			Services.getErrorManager().error(
					"Cannot create the resulting layer of geometry type ", e);
		} catch (IllegalStateException e) {
			Services.getErrorManager().error("Cannot get the layer ", e);
		} catch (LayerException e) {
			Services.getErrorManager()
					.error(
							"Cannot insert resulting layer based on "
									+ layer.getName(), e);
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot read the resulting datasource from the layer ", e);
		}

	}

}
