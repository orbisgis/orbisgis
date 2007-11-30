package org.orbisgis.geoview.layerModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.gdms.spatial.NullCRS;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.persistence.LayerCollectionType;
import org.orbisgis.geoview.persistence.LayerType;
import org.orbisgis.pluginManager.PluginManager;

public class LayerFactory {

	public static LayerCollection createLayerCollection(String name) {
		return new LayerCollection(name);
	}

	public static VectorLayer createVectorialLayer(String registerName,
			DataSource ds) {
		return new VectorLayer(registerName, ds, NullCRS.singleton);
	}

	public static RasterLayer createRasterLayer(String registerName,
			GeoRaster georaster) {
		return new RasterLayer(registerName, NullCRS.singleton, georaster);
	}

	public static ILayer createLayer(String sourceName)
			throws FileNotFoundException, IOException, DriverLoadException,
			NoSuchTableException, DataSourceCreationException {
		Source src = OrbisgisCore.getDSF().getSourceManager().getSource(
				sourceName);
		int type = src.getType();
		if ((type & SourceManager.RASTER) == SourceManager.RASTER) {
			if (src.isFileSource()) {
				GeoRaster gr = GeoRasterFactory.createGeoRaster(src.getFile()
						.getAbsolutePath());
				return createRasterLayer(sourceName, gr);
			} else {
				throw new UnsupportedOperationException("Can "
						+ "only understand file rasters");
			}
		} else if ((type & SourceManager.VECTORIAL) == SourceManager.VECTORIAL) {
			DataSource ds = OrbisgisCore.getDSF().getDataSource(sourceName);
			return LayerFactory.createVectorialLayer(sourceName, ds);
		} else {
			throw new UnsupportedOperationException(
					"Cannot understand source type: " + type);
		}
	}

	public static ILayer createLayer(LayerType layer) {
		if (layer instanceof LayerCollectionType) {
			LayerCollectionType xmlLayerCollection = (LayerCollectionType) layer;
			ILayer ret = createLayerCollection(layer.getName());
			List<LayerType> xmlChildren = xmlLayerCollection.getLayer();
			for (LayerType layerType : xmlChildren) {
				ILayer lyr = createLayer(layerType);
				if (lyr != null) {
					try {
						ret.put(lyr);
					} catch (LayerException e) {
						PluginManager.error("Cannot add layer to collection: "
								+ lyr.getName(), e);
					} catch (CRSException e) {
						PluginManager.error("Cannot add layer to collection: "
								+ lyr.getName(), e);
					}
				}
			}

			return ret;
		} else {
			try {
				return createLayer(layer.getName());
			} catch (FileNotFoundException e) {
				PluginManager.error("Cannot recover layer " + layer.getName(),
						e);
			} catch (DriverLoadException e) {
				PluginManager.error("Cannot recover layer " + layer.getName(),
						e);
			} catch (IOException e) {
				PluginManager.error("Cannot recover layer " + layer.getName(),
						e);
			} catch (NoSuchTableException e) {
				PluginManager.error("Cannot recover layer " + layer.getName(),
						e);
			} catch (DataSourceCreationException e) {
				PluginManager.error("Cannot recover layer " + layer.getName(),
						e);
			}

			return null;
		}
	}
}
