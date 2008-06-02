package org.orbisgis.processing.action.utilities;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.MapContext;

public abstract class AbstractRasterProcess implements ILayerAction {
	public void execute(MapContext mapContext, ILayer resource) {
		try {
			final GeoRaster geoRasterSrc = resource.getRaster();
			final GeoRaster geoRasterResult = evaluateResult(geoRasterSrc);

			if (null != geoRasterResult) {
				// save the computed GeoRaster in a tempFile
				final DataSourceFactory dsf = ((DataManager) Services
						.getService("org.orbisgis.DataManager")).getDSF();
				final String tempFile = dsf.getTempFile() + ".tif";
				geoRasterResult.save(tempFile);

				// populate the GeoView TOC with a new RasterLayer
				DataManager dataManager = (DataManager) Services
						.getService("org.orbisgis.DataManager");
				final ILayer newLayer = dataManager.createLayer(new File(
						tempFile));
				mapContext.getLayerModel().insertLayer(newLayer, 0);
			}
		} catch (IOException e) {
			Services.getErrorManager().error(
					"Cannot compute " + resource.getName(), e);
		} catch (LayerException e) {
			Services.getErrorManager().error(
					"Cannot insert resulting layer based on "
							+ resource.getName(), e);
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot read the raster from the layer ", e);
		} catch (DriverLoadException e) {
			Services.getErrorManager().error(
					"Cannot create the resulting layer of raster type ", e);
		} catch (OperationException e) {
			Services.getErrorManager().error(
					"Error during the raster operation", e);
		}
	}

	protected abstract GeoRaster evaluateResult(final GeoRaster geoRasterSrc)
			throws OperationException, IOException;

	public final boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount >= 1;
	}

	public boolean accepts(ILayer layer) {
		try {
			return layer.isRaster();
		} catch (DriverException e) {
			return false;
		}
	}
}