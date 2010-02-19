package org.gdms.manual;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;

public class TestOperators {

	static DataSourceFactory dsf = new DataSourceFactory();

	private static final String FILE_PATH1 = "/home/ebocher/Documents/projets/anr_avupur/data_nantes/bdtopo_x191_shp_l2e/a_reseau_routier/route.shp";

	private static final String FILE_PATH2 = "/home/ebocher/Documents/projets/anr_avupur/data_nantes/admin/nantes_agglo.shp";

	public static void main(String[] args) throws Exception {

		long start = System.currentTimeMillis();

		dsf.getSourceManager().register("datain", new File(FILE_PATH1));

		dsf.getSourceManager().register("zone", new File(FILE_PATH2));

		
		String sql = "select st_intersection(a.the_geom, b.the_geom) as the_geom from datain a, zone b where st_intersects(a.the_geom, b.the_geom)";
		DataSource ds = dsf.getDataSourceFromSQL(sql);
		File gdmsFile = new File("/tmp/operator.gdms");
		gdmsFile.delete();
		dsf.getSourceManager().register("operator", gdmsFile);

		ds.open();
		dsf.saveContents("operator", ds);
		ds.close();

		long end = System.currentTimeMillis();

		System.out.println("Total time : " + (end - start));

	}
}
