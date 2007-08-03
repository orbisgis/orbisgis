package org.orbisgis.plugin.view3d;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Geometry;

public class Renderer3D {

	private MyImplementor simpleCanvas = null;
	private GeomUtilities utilities = null;

	protected Renderer3D(MyImplementor simpleCanvas) {
		this.simpleCanvas = simpleCanvas;
		utilities = new GeomUtilities();
	}

	/**
	 * Dispatches processing according to the type of layer to process
	 * 
	 * @param layer
	 */
	protected void processLayer(ILayer layer) {
		if (layer instanceof VectorLayer) {
			VectorLayer vlayer = (VectorLayer) layer;
			processVectorLayer(vlayer);
		} else if (layer instanceof RasterLayer) {
			RasterLayer rlayer = (RasterLayer) layer;
			processRasterLayer(rlayer);
		} else {
			System.err.println("Not a supported type of layer "
					+ layer.getClass());
		}
	}

	/**
	 * This will draw a Vector layer
	 * 
	 * @param layer
	 */
	private void processVectorLayer(VectorLayer layer) {
		try {
			DataSource ds = layer.getDataSource();

			SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);

			sds.open();

			long size = sds.getRowCount();
			for (long row = 0; row < size; row++) {
				int percent = Math.round(100 * (float) row / (float) size);
				if (row % 50 == 0) {
					System.out.println(percent + " % done...");
				}
				Geometry geometry = sds.getGeometry(row);
				
				simpleCanvas.getRootNode().attachChild(utilities.processGeometry(geometry));
				
			}
			sds.cancel();

		} catch (DriverLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processRasterLayer(RasterLayer layer) {
		// TODO : implement raster layer
		System.err.println("Not implemented yet");
	}

}
