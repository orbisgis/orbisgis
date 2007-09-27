package org.urbsat;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DataSourceFactoryEvent;
import org.gdms.data.DataSourceFactoryListener;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.urbsat.Register;

public class GDMSTests extends TestCase {
	public void testScript1() throws Exception {
		Class.forName(Register.class.getName());

		DataSourceFactory dsf = new DataSourceFactory();
		dsf.addDataSourceFactoryListener(new DataSourceFactoryListener() {

			public void sqlExecuted(DataSourceFactoryEvent event) {
				System.out.println(event.getName());
			}

			public void sourceRemoved(DataSourceFactoryEvent e) {
			}

			public void sourceNameChanged(DataSourceFactoryEvent e) {
			}

			public void sourceAdded(DataSourceFactoryEvent e) {
			}

		});
		dsf.registerDataSource("landcover2000", new FileSourceDefinition(
				new File(SourceTest.externalData
						+ "/shp/mediumshape2D/landcover2000.shp")));
		dsf.getIndexManager().buildIndex("landcover2000", "the_geom",
				SpatialIndex.SPATIAL_INDEX);
		dsf.executeSQL("call register('/tmp/grid.shp', 'grid');");
		dsf.executeSQL("call register('/tmp/madata.shp', 'madata');");
		dsf.executeSQL("create table grid as call " + "CREATEGRID from "
				+ "landcover2000 values (1000, 100);");
		dsf.executeSQL("create table madata as select "
				+ "Intersection(a.the_geom, b.the_geom)"
				+ " from grid a, landcover2000 b where"
				+ " Intersects(a.the_geom, b.the_geom) "
				+ "and Dimension(Intersection(a.the_geom, b.the_geom))=2;");
		dsf.getIndexManager().buildIndex("madata", "the_geom",
				SpatialIndex.SPATIAL_INDEX);
		DataSource ds = dsf.getDataSource("madata");
		ds.open();
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		for (int i = 0; i < sds.getRowCount(); i++) {
			assertTrue(!sds.getGeometry(i).isEmpty());
		}
		ds.cancel();
	}
}
