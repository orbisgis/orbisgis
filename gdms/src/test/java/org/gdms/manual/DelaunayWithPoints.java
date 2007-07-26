package org.gdms.manual;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.spatial.SpatialDataSourceDecorator;

import visad.Delaunay;
import visad.DelaunayFast;
// import visad.examples.DelaunayTest;

import com.vividsolutions.jts.geom.Coordinate;

public class DelaunayWithPoints {

	public static void main(String[] args) throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.registerDataSource("shape", new FileSourceDefinition(new File(
				"../../datas2tests/shp/mediumshape2D/landcover2000.shp")));

		DataSource ds = dsf.executeSQL("select * from shape");

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		DataSourceDefinition target = null;
		boolean shape = true;
		if (shape) {
			target = new FileSourceDefinition(new File("output.shp"));
			// TODO : cast foireux a surveiller !
			float[][] matrix = new float[2][(int) sds.getRowCount()];
			for (int i = 0; i < sds.getRowCount(); i++) {
				final Coordinate c = sds.getGeometry(i).getCoordinate();
				matrix[0][i] = (float) c.x;
				matrix[1][i] = (float) c.y;
				// matrix[2][i] = (float) c.z;
			}

			
			Delaunay delaun = (Delaunay) new DelaunayFast(matrix);

			sds.cancel();
		
			
			DelaunayTest.visTriang(delaun, matrix, 3);
			
			
			

		} else {

		}

		dsf.registerDataSource("output", target);
		dsf.saveContents("output", ds);

	}
}