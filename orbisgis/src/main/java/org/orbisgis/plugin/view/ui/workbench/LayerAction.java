/**
 * 
 */
package org.orbisgis.plugin.view.ui.workbench;

import org.gdms.data.AlreadyClosedException;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSource;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.ILayerAction;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;

import com.vividsolutions.jts.geom.Envelope;

public class LayerAction implements ILayerAction {
	private Envelope globalEnvelope = null;

	public void action(ILayer layer) {
		if (layer instanceof VectorLayer) {
			VectorLayer vl = (VectorLayer) layer;
			SpatialDataSource dataSource = vl.getDataSource();
			if (null != dataSource) {
				Envelope env = null;
				try {
					dataSource.open();
					env = new Envelope(dataSource.getFullExtent());
				} catch (DriverException e) {
					// TODO
					e.printStackTrace();
				} finally {
					try {
						dataSource.cancel();
					} catch (AlreadyClosedException e) {
					} catch (DriverException e) {
					}
				}
				if (null == globalEnvelope) {
					globalEnvelope = env;
				} else {
					globalEnvelope.expandToInclude(env);
				}
			}
		} else if (layer instanceof RasterLayer) {
			RasterLayer rl = (RasterLayer) layer;
			if (null != rl.getGridCoverage()) {
				org.opengis.spatialschema.geometry.Envelope envTmp = rl
						.getGridCoverage().getEnvelope();
				double[] lowerCorner = envTmp.getLowerCorner().getCoordinates();
				double[] upperCorner = envTmp.getUpperCorner().getCoordinates();
				Envelope env = new Envelope(lowerCorner[0], upperCorner[0],
						lowerCorner[1], upperCorner[1]);
				if (null == globalEnvelope) {
					globalEnvelope = env;
				} else {
					globalEnvelope.expandToInclude(env);
				}
			}
		}
	}

	public Envelope getGlobalEnvelope() {
		return globalEnvelope;
	}
}