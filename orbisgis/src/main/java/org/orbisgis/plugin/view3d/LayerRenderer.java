package org.orbisgis.plugin.view3d;

import java.util.HashMap;

import javax.swing.JFrame;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;
import org.orbisgis.plugin.view3d.controls.ToolsPanel;
import org.orbisgis.plugin.view3d.geometries.GeomUtilities;
import org.orbisgis.plugin.view3d.geometries.TerrainBlock3D;

import com.hardcode.driverManager.DriverLoadException;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * This class is responsible for rendering layers from our layer model. It is
 * mostly a dispatcher because GeomUtilities really do the stuff. We also create
 * a 3DToolbox. TODO : the toolbox needs to move. see in the constructor
 * comments
 * 
 * @author Samuel CHEMLA
 * 
 */
public class LayerRenderer {

	// We need our implementor to get acces to the root node
	private SceneImplementor sceneImplementor = null;

	// The utilities will really process the JTS geometries
	private GeomUtilities utilities = null;

	// To each layer we attach a node so we can easily toogle visibility or do
	// any transformation
	private HashMap<ILayer, Node> nodes = null;

	/**
	 * This is the listener for our layer collection.
	 */
	private LayerCollectionListener lcl = null;

	// When we add the first layer, we need to set a good camera location and
	// direction so we can see our layer. For the next
	// layers, we won't change the camera settings.
	private boolean firstCamera = true;

	/**
	 * Constructor
	 * 
	 */
	protected LayerRenderer() {
		// initializes and registers the layer collection listener
		lcl = new LayerCollectionListener(this);
		TempPluginServices.lc.addCollectionListener(lcl);

		// initializes utilities
		utilities = new GeomUtilities();

		// initializes the nodes map
		nodes = new HashMap<ILayer, Node>();

		/**
		 * Here we create the toolbox. As you can see it just needs the
		 * sceneImplementor to be initialised and that's it.TODO : maybe we
		 * should create the toolbox in a more appropriate class
		 */
		JFrame frame = new JFrame("3DTools");
		frame.setContentPane(new ToolsPanel(sceneImplementor));
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
			// We first need to retrieve the datasource
			DataSource ds = layer.getDataSource();

			// TODO : do we need to test for a spatial datasource ??
			SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
			sds.open();

			long size = sds.getRowCount();

			// Here we create a node with the name of the layer
			Node geomNode = new Node(layer.getName());

			// Attach the geometries...Each geometry is itself a node, so you
			// can easily pick it
			for (long row = 0; row < size; row++) {
				if (row % 50 == 0) {
					// TODO : progress bar.
					int percent = Math.round(100 * (float) row / (float) size);
					System.out.println(percent + " % done...");
				}
				Geometry geometry = sds.getGeometry(row);
				geomNode.attachChild(utilities.processGeometry(geometry));

			}
			sds.cancel();

			// Now we register the node containing all the layer's geometries
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

	/**
	 * This will draw a Vector layer. TODO : It needs further investigation
	 * 
	 * @param layer
	 */
	private void processRasterLayer(RasterLayer layer) {
		System.err.println("Only square raster please...");

		Node rasterNode = new Node(layer.getName());

		TerrainBlock3D tb = new TerrainBlock3D(layer);
		rasterNode.attachChild(tb);

		// Now we register the node containing all the layer's geometries
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
				sceneImplementor.getRootNode().attachChild(nodes.get(layer));
			} else {
				sceneImplementor.getRootNode().detachChild(nodes.get(layer));
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

	/**
	 * Set up good camera parameters for a specified layer so wa can see it.
	 * TODO : It is sometimes buggy with some layers : need more investigation
	 * 
	 * @param layer
	 */
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

			sceneImplementor.getCamera().setLocation(
					new Vector3f((float) coord.x, (float) coord.y,
							(float) coord.z));
			sceneImplementor.getCamera().setDirection(new Vector3f(0, 0, -1));

		}
	}

	protected void setImplementor(SceneImplementor sceneImplementor) {
		this.sceneImplementor = sceneImplementor;
	}
}
