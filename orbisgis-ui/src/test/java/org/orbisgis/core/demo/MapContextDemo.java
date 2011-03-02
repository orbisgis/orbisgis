package org.orbisgis.core.demo;

import java.io.File;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.geometryUtils.EnvelopeUtil;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.DefaultDataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.DefaultMapContext;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;

import com.vividsolutions.jts.geom.Envelope;

public class MapContextDemo {

	/**
	 * A demo to play with a mapcontext
	 */
	static DataSourceFactory dsf = new DataSourceFactory();

	public static void main(String[] args) throws IllegalStateException,
			LayerException, DriverException {

		registerDataManager();
		MapContext mc = new DefaultMapContext();
		mc.open(null);
		ILayer layer = getDataManager().createLayer(
				new File("src/test/resources/data/bv_sap.shp"));
		mc.getLayerModel().addLayer(layer);

		DefaultMetadata metadata = new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.GEOMETRY) }, new String[] {
				"location", "the_geom" });

		GenericObjectDriver driver = new GenericObjectDriver(metadata);

		for (int i = 0; i < mc.getLayerModel().getLayerCount(); i++) {
			layer = mc.getLayerModel().getLayer(i);
			String layerName = layer.getName();

			Envelope enveloppe = layer.getSpatialDataSource().getFullExtent();

			driver.addValues(new Value[]{ValueFactory.createValue(layerName + ".tiff"),
					ValueFactory
							.createValue(EnvelopeUtil.toGeometry(enveloppe))});
		}
		dsf.getSourceManager().register("mosaic", driver);
	}

	public static void registerDataManager() {
		// Installation of the service
		Services
				.registerService(
						DataManager.class,
						"Access to the sources, to its properties (indexes, etc.) and its contents, either raster or vectorial",
						new DefaultDataManager(dsf));
	}

	private static DataManager getDataManager() {
		return (DataManager) Services.getService(DataManager.class);
	}
}
