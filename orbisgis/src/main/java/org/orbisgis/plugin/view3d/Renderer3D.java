package org.orbisgis.plugin.view3d;

import java.util.HashMap;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;

import com.hardcode.driverManager.DriverLoadException;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.state.ZBufferState;
import com.vividsolutions.jts.geom.Geometry;

public class Renderer3D {

	private MyImplementor simpleCanvas = null;

	private GeomUtilities utilities = null;

	private HashMap<ILayer, Node> nodes = null;

	protected Renderer3D(MyImplementor simpleCanvas) {
		this.simpleCanvas = simpleCanvas;
		utilities = new GeomUtilities();
		nodes = new HashMap<ILayer, Node>();
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
	 * This will draw a Vector layer. We read all the data entries of the
	 * datasource of the layer, the we attach all of them in a node, then we
	 * store the node in tha HashMap "nodes" and finally we attach it to the
	 * root node...
	 * 
	 * @param layer
	 */
	private void processVectorLayer(VectorLayer layer) {
		try {
			DataSource ds = layer.getDataSource();
			SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
			sds.open();

			long size = sds.getRowCount();
			Node geomNode = new Node(layer.getName());

			for (long row = 0; row < size; row++) {
				if (row % 50 == 0) {
					int percent = Math.round(100 * (float) row / (float) size);
					System.out.println(percent + " % done...");
				}
				Geometry geometry = sds.getGeometry(row);
				geomNode.attachChild(utilities.processGeometry(geometry));

			}
			sds.cancel();

			nodes.put(layer, geomNode);

			processLayerVisibility(layer);

			// ZBufferState zbuf =
			// simpleCanvas.getRenderer().createZBufferState();
			// zbuf.setWritable(false);
			// zbuf.setEnabled(true);
			// zbuf.setFunction(ZBufferState.CF_LEQUAL);

			// geomNode.setRenderState(zbuf);
			// geomNode.updateRenderState();
			// geomNode.setCullMode(SceneElement.CULL_DYNAMIC);
			
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

	/**
	 * Checks if the specified layer should be visible or not, then set the
	 * visibility. NB : we don't free memory at all here.
	 * 
	 * @param layer :
	 *            the layer to process
	 */
	protected void processLayerVisibility(ILayer layer) {
		if (layer != null && nodes.containsKey(layer)) {
			if (layer.isVisible()) {
				simpleCanvas.getRootNode().attachChild(nodes.get(layer));
			} else {
				simpleCanvas.getRootNode().detachChild(nodes.get(layer));
			}
		}
	}

	/**
	 * Delete a specified layer. This action should free some memory...
	 * 
	 * TODO : No memory is made free here, fix it...
	 * 
	 * @param layer :
	 *            the layer to delete
	 */
	protected void deleteLayer(ILayer layer) {
		if (layer != null && nodes.containsKey(layer)) {
			nodes.remove(layer);
		}
	}
}
