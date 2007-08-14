package org.orbisgis.plugin.view3d;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.HashMap;

import javax.media.jai.PlanarImage;
import javax.swing.JFrame;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.geotools.coverage.grid.GridCoverage2D;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;
import org.orbisgis.plugin.view3d.controls.ToolsPanel;
import org.orbisgis.plugin.view3d.geometries.GeomUtilities;

import sun.text.normalizer.CharTrie.FriendAgent;

import com.hardcode.driverManager.DriverLoadException;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.ImageBasedHeightMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class Renderer3D {

	private SimpleCanvas3D simpleCanvas = null;

	private GeomUtilities utilities = null;

	private HashMap<ILayer, Node> nodes = null;

	// When we add the first layer, let's set a good camera. For the next
	// layers, we won't change the camera settings.
	private boolean firstCamera = true;

	protected Renderer3D(SimpleCanvas3D simpleCanvas) {
		this.simpleCanvas = simpleCanvas;
		utilities = new GeomUtilities();
		nodes = new HashMap<ILayer, Node>();

		JFrame frame = new JFrame("3DTools");
		frame.setContentPane(new ToolsPanel(simpleCanvas));
		frame.pack();
		frame.setVisible(true);

	}

	/**
	 * Dispatches processing according to the type of layer to process
	 * 
	 * @param layer
	 */
	protected void processLayer(ILayer layer) {

		if (layer instanceof VectorLayer) {
			// Let's (maybe) set the cam...
			processCamera(layer);
			VectorLayer vlayer = (VectorLayer) layer;
			processVectorLayer(vlayer);

		} else if (layer instanceof RasterLayer) {
			RasterLayer rlayer = (RasterLayer) layer;
			processRasterLayer(rlayer);

		} else {
			throw new Error("Not a supported type of layer " + layer.getClass());
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

		} catch (DriverLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processRasterLayer(RasterLayer layer) {
		//TODO : lots...
		System.err.println("Only square raster please...");

		Node rasterNode = new Node(layer.getName());

		GridCoverage2D gcin = (GridCoverage2D) layer.getGridCoverage();
		gcin.geophysics(false);
		PlanarImage plim = (PlanarImage) gcin.getRenderedImage();
		Image miche = Toolkit.getDefaultToolkit().createImage(
				plim.getAsBufferedImage().getSource());

		ImageBasedHeightMap heightMap = new ImageBasedHeightMap(miche);
		Vector3f terrainScale = new Vector3f(10, 1, 10);
		heightMap.setHeightScale(0.001f);
		TerrainBlock tb = new TerrainBlock("Terrain", heightMap.getSize(),
				terrainScale, heightMap.getHeightMap(), new Vector3f(0, 0, 0),
				false);

		rasterNode.attachChild(tb);
		nodes.put(layer, rasterNode);

		processLayerVisibility(layer);

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

	private void processCamera(ILayer layer) {
		if (firstCamera) {
			// Remember next time we won't change cam's settings
			firstCamera = false;
			Envelope enveloppe = layer.getEnvelope();
			Coordinate coord = enveloppe.centre();

			// Take care because coord.z is probably NaN
			if (Double.isNaN(coord.z)) {
				coord.z = 0;
			}

			double size = Math.max(enveloppe.getHeight(), enveloppe.getWidth());
			coord.z = Math.min(coord.z + size / 0.707, 9990);

			simpleCanvas.getCamera().setLocation(
					new Vector3f((float) coord.x, (float) coord.y,
							(float) coord.z));
			simpleCanvas.getCamera().setDirection(new Vector3f(0, 0, -1));

		}
	}
}
