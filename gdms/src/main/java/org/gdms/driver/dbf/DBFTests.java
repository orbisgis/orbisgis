package org.gdms.driver.dbf;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.ValueFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class DBFTests {

	public static void main(String[] args) throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		DataSource ds = dsf.getDataSource(new File("../../datas2tests/"
				+ "shp/bigshape2D/communes.dbf"));
		long t1 = System.currentTimeMillis();
		int numIterations = 10;
		for (int j = 0; j < numIterations; j++) {
			ds.open();
			for (int i = 0; i < ds.getRowCount(); i++) {
				ds.getFieldValue(i, 0);
			}
			ds.cancel();
		}
		long t2 = System.currentTimeMillis();
		System.out.println("Time to read: " + ((t2 - t1) / numIterations));
	}

	public static void main2(String[] args) throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("string", Type.STRING,
				new Constraint[] { ConstraintFactory.createConstraint(
						ConstraintNames.LENGTH, "10") });
		metadata.addField("the_geom", Type.GEOMETRY,
				new Constraint[] { new GeometryConstraint(
						GeometryConstraint.POINT_2D) });
		File file = new File("new.shp");
		file.delete();
		dsf.createDataSource(new FileSourceCreation(file, metadata));
		DataSource ds = dsf.getDataSource(file);
		ds.open();
		GeometryFactory gf = new GeometryFactory();
		for (int i = 0; i < 100000; i++) {
			ds.insertEmptyRow();
			ds.setFieldValue(i, 0, ValueFactory.createValue(gf
					.createPoint(new Coordinate(i, 0))));
			ds.setString(i, 1, Integer.toString(i));
		}
		long t1 = System.currentTimeMillis();
		ds.commit();
		long t2 = System.currentTimeMillis();
		System.out.println("Time to write: " + (t2 - t1));
		ds.open();
		t1 = System.currentTimeMillis();
		int numIterations = 25;
		for (int i = 0; i < numIterations; i++) {
			gdmsProcess(ds);
		}
		t2 = System.currentTimeMillis();
		System.out.println((t2 - t1) / numIterations);
		ds.cancel();
	}

	private static void gdmsProcess(DataSource ds) throws Exception {
		for (int i = 0; i < ds.getRowCount(); i++) {
			ds.getFieldValue(i, 0);
		}
	}

}
