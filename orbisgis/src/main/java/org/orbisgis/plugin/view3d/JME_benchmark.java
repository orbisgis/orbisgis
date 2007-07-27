package org.orbisgis.plugin.view3d;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;

import com.hardcode.driverManager.DriverLoadException;
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.util.geom.BufferUtils;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Started Date: Jul 20, 2004<br>
 * <br>
 * 
 * Simple Node object with a few Geometry manipulators.
 * 
 * @author Jack Lindamood
 */
public class JME_benchmark extends SimpleGame {
	public static void main(String[] args) {
		JME_benchmark app = new JME_benchmark();
		app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}

	protected void simpleInitGame() {

		/**
		 * Opens the file to display...
		 */
		DataSourceFactory dsf = new DataSourceFactory();
		File src = new File("../../datas2tests/shp/bigshape2d/communes.shp");
		DataSource ds;
		try {
			ds = dsf.getDataSource(src);

			SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);

			sds.open();
			long size = sds.getRowCount();
			System.out.println(size);
			for (long row = 0; row < size; row++) {
				int percent = Math.round(100 * (float) row / (float) size);
				if (row % 50 == 0) {
					System.out.println(percent + " % done...");
				}
				Geometry geometry = sds.getGeometry(row);
				if (geometry instanceof Polygon) {
					System.out.println("Poly");
					Polygon polygone = (Polygon) geometry;
					Polygone m = new Polygone(polygone);

					Vector3f[] vertexes = m.vertexes;
					// Normal directions for each vertex position
					Vector3f[] normals = m.normals;
					// Color for each vertex position
					ColorRGBA[] colors = m.colors;
					// Texture Coordinates for each position
					Vector2f[] texCoords = m.texCoords;
					// The indexes of Vertex/Normal/Color/TexCoord sets. Every 3
					// makes a triangle.
					int[] indexes = m.indexes;
					// Feed the information to the TriMesh
					m.reconstruct(BufferUtils.createFloatBuffer(vertexes),
							BufferUtils.createFloatBuffer(normals), BufferUtils
									.createFloatBuffer(colors), BufferUtils
									.createFloatBuffer(texCoords), BufferUtils
									.createIntBuffer(indexes));
					// Create a bounds
					m.setModelBound(new BoundingBox());
					m.updateModelBound();
					// Attach the mesh to my scene graph
					rootNode.attachChild(m);

				}
			}
			sds.cancel();

		} catch (DriverLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Let us see the per vertex colors
		lightState.setEnabled(false);
	}
}