package org.gdms;

import java.io.File;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.values.Value;
import org.gdms.sql.strategies.FirstStrategy;

import com.vividsolutions.jts.geom.Envelope;

public class IndexTest extends TestCase {
	public void testname() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.registerDataSource("roads", new FileSourceDefinition(new File(
				"../../datas2tests/shp/bigshape2D/communes.shp")));
		dsf.getIndexManager().buildIndex("roads", "the_geom",
				SpatialIndex.SPATIAL_INDEX);
		DataSource ds = dsf.getDataSource("roads");
		ds.open();
		SpatialIndexQuery query = new SpatialIndexQuery(new Envelope(),
				"the_geom");
		ds.queryIndex(query);
		ds.cancel();

		ds = dsf.executeSQL("select * from roads where Contains("
				+ "GeomFromText('POLYGON (( 250000 2300000, "
				+ "300000 2300000, " + "300000 2330000, " + "250000 2330000, "
				+ "250000 2300000))')" + ", the_geom);");

		FirstStrategy.indexes = false;
		long t1 = System.currentTimeMillis();
		ds = dsf.executeSQL("select * from roads where Contains("
				+ "GeomFromText('POLYGON (( 250000 2300000, "
				+ "300000 2300000, " + "300000 2330000, " + "250000 2330000, "
				+ "250000 2300000))')" + ", the_geom);");
		long t2 = System.currentTimeMillis();
		System.out.println((t2 - t1) + " ms");

		FirstStrategy.indexes = true;
		t1 = System.currentTimeMillis();
		DataSource ds2 = dsf.executeSQL("select * from roads where Contains("
				+ "GeomFromText('POLYGON (( 250000 2300000, "
				+ "300000 2300000, " + "300000 2330000, " + "250000 2330000, "
				+ "250000 2300000))')" + ", the_geom);");
		t2 = System.currentTimeMillis();
		System.out.println((t2 - t1) + " ms");

		Set<Value> contents = new TreeSet<Value>(new Comparator<Value>() {

			public int compare(Value o1, Value o2) {
				return o1.toString().compareTo(o2.toString());
			}

		});
		ds.open();
		System.out.println(ds.getRowCount());
		for (int i = 0; i < ds.getRowCount(); i++) {
			contents.add(ds.getFieldValue(i, 0));
		}
		ds.cancel();

		ds2.open();
		System.out.println(ds2.getRowCount());
		for (int i = 0; i < ds2.getRowCount(); i++) {
			contents.remove(ds2.getFieldValue(i, 0));
		}
		ds2.cancel();

		System.out.println(contents.size());

	}
}
