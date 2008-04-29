package org.orbisgis.geoview.rasterProcessing.action.utilities;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.views.toc.ILayerAction;
import org.orbisgis.pluginManager.PluginManager;

public abstract class AbstractRasterProcess implements ILayerAction {
	public final void executeAll(GeoView2D view, ILayer[] layers) {
	}

	public void execute(GeoView2D view, ILayer resource) {
		try {
			final GeoRaster geoRasterSrc = resource.getRaster();
			final GeoRaster geoRasterResult = evaluateResult(geoRasterSrc);

			if (null != geoRasterResult) {
				// save the computed GeoRaster in a tempFile
				final DataSourceFactory dsf = OrbisgisCore.getDSF();
				final String tempFile = dsf.getTempFile() + ".tif";
				geoRasterResult.save(tempFile);

				// populate the GeoView TOC with a new RasterLayer
				final ILayer newLayer = LayerFactory.createLayer(new File(
						tempFile));
				view.getViewContext().getLayerModel().insertLayer(newLayer, 0);
			}
		} catch (GeoreferencingException e) {
			PluginManager.error("Cannot compute " + resource.getName(), e);
		} catch (IOException e) {
			PluginManager.error("Cannot compute " + resource.getName(), e);
		} catch (LayerException e) {
			PluginManager.error("Cannot insert resulting layer based on "
					+ resource.getName(), e);
		} catch (CRSException e) {
			PluginManager.error(
					"Problem while trying to insert resulting layer based on "
							+ resource.getName(), e);
		} catch (DriverException e) {
			PluginManager.error("Cannot read the raster from the layer ", e);
		} catch (DriverLoadException e) {
			PluginManager.error(
					"Cannot create the resulting layer of raster type ", e);
		} catch (NoSuchTableException e) {
			PluginManager.error(
					"Cannot create the resulting layer of raster type ", e);
		} catch (DataSourceCreationException e) {
			PluginManager.error(
					"Cannot create the resulting layer of raster type ", e);
		} catch (OperationException e) {
			PluginManager.error("Error during the raster operation", e);
		}
	}

	protected abstract GeoRaster evaluateResult(final GeoRaster geoRasterSrc)
			throws OperationException, GeoreferencingException, IOException;

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

	public final boolean acceptsAll(ILayer[] layer) {
		return true;
	}
}