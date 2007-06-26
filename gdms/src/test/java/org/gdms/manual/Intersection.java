package org.gdms.manual;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.utility.Utility;

public class Intersection {
	public static void main(String[] args) throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.registerDataSource("p1", new FileSourceDefinition(new File(
				"../../datas2tests/shp/smallshape2D/polygon2d_1.shp")));
		dsf.registerDataSource("p2", new FileSourceDefinition(new File(
				"../../datas2tests/shp/smallshape2D/polygon2d_2.shp")));

		DataSource sql = dsf
				.executeSQL("select Intersection(p1.the_geom, p2.the_geom) from p1, p2");

		sql.open();
		System.out.println(sql.getRowCount());

		new Utility().show(new DataSource[] {
				new SpatialDataSourceDecorator(dsf.getDataSource("p1")),
				new SpatialDataSourceDecorator(dsf.getDataSource("p2")) });
		new Utility().show(new DataSource[] { new SpatialDataSourceDecorator(
				sql) });
		sql.cancel();
	}
}
