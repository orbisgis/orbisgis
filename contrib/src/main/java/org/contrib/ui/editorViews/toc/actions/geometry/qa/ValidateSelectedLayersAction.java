package org.contrib.ui.editorViews.toc.actions.geometry.qa;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.MapContext;

public class ValidateSelectedLayersAction implements ILayerAction {

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

		ValidateSelectedLayers validateSelectedLayers = new ValidateSelectedLayers();
		validateSelectedLayers.execute(layer);

		DataSource resultDS = validateSelectedLayers.getDataSourcetoFeatures();

		try {
			if (resultDS!= null) {
				ObjectMemoryDriver resultdriver = new ObjectMemoryDriver(
						resultDS);
				String resultlayer = dsf.getSourceManager().nameAndRegister(
						resultdriver);
				final ILayer rsLayer = dataManager.createLayer(resultlayer);
				mapContext.getLayerModel().insertLayer(rsLayer, 0);
			}
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot read the resulting datasource from the layer ", e);
		} catch (LayerException e) {
			Services.getErrorManager()
					.error(
							"Cannot insert resulting layer based on "
									+ layer.getName(), e);
		}

	}

}
