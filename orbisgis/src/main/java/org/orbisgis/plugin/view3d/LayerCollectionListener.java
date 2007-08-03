package org.orbisgis.plugin.view3d;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.LayerCollectionEvent;
import org.orbisgis.plugin.view.layerModel.LayerListenerEvent;
import org.orbisgis.plugin.view.layerModel.VectorLayer;

import com.hardcode.driverManager.DriverLoadException;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.util.geom.BufferUtils;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class LayerCollectionListener implements
		org.orbisgis.plugin.view.layerModel.LayerCollectionListener {

	private LayerListener layerListener = null;
	private Renderer3D renderer = null;

	public LayerCollectionListener(MyImplementor simpleCanvas) {
		layerListener = new LayerListener();
		renderer = new Renderer3D(simpleCanvas);
	}

	public void layerAdded(LayerCollectionEvent listener) {
		for (ILayer layer : listener.getAffected()) {
			layer.addLayerListener(layerListener);
			renderer.processLayer(layer);
		}
	}

	public void layerMoved(LayerCollectionEvent listener) {
		// TODO Auto-generated method stub
	}

	public void layerRemoved(LayerCollectionEvent listener) {
		// TODO : undraw
		for (ILayer layer : listener.getAffected()) {
			layer.removeLayerListener(layerListener);
		}
	}

	private class LayerListener implements
			org.orbisgis.plugin.view.layerModel.LayerListener {

		public void nameChanged(LayerListenerEvent e) {
			// TODO Auto-generated method stub
		}

		public void styleChanged(LayerListenerEvent e) {
			// TODO Auto-generated method stub
		}

		public void visibilityChanged(LayerListenerEvent e) {
			System.err.println("TODO !!");
		}

	}

}
