package org.gdms;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.indexes.SpatialIndexQuery;

import com.vividsolutions.jts.geom.Envelope;

public class IndexTest extends TestCase {
	public void testname() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.registerDataSource("roads", new FileSourceDefinition(new File(
				"/root/carto/subparce.shp")));
		dsf.getIndexManager().buildIndex("roads", "the_geom",
				SpatialIndex.SPATIAL_INDEX);
		DataSource ds = dsf.getDataSource("roads");
		ds.open();
		SpatialIndexQuery query = new SpatialIndexQuery(new Envelope(), "the_geom");
		ds.queryIndex(query);
		ds.cancel();

		long t1 = System.currentTimeMillis();
		ds = dsf
				.executeSQL("select * from roads where Contains(GeomFromText('POLYGON (( 670000 4300000, 673000 4300000, 673000 4303000, 670000 4303000, 670000 4300000))'), the_geom);");
		long t2 = System.currentTimeMillis();
		System.out.println((t2 - t1));

	}
}
