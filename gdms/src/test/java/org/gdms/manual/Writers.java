package org.gdms.manual;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.file.FileSourceDefinition;

public class Writers {

	public static void main(String[] args) throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.registerDataSource("shape", new FileSourceDefinition(new File(
				"../../datas2tests/shp/mediumshape2D/hedgerow.shp")));

		DataSource sql = dsf
				.executeSQL("select * from shape");

		boolean shape = false;
		if (shape) {
			DataSourceDefinition target = new FileSourceDefinition(new File(
					"output.shp"));
			dsf.registerContents("output", target, sql);
		} else {
			DataSourceDefinition target = new DBTableSourceDefinition(new DBSource(null,
					0, "writers-output", null, null, "output", "jdbc:h2"));
			dsf.registerContents("output", target, sql);
			DataSource ds1 = dsf.executeSQL("select the_geom from output");
			DataSource ds2 = dsf.executeSQL("select the_geom from shape");
			ds1.open();
			ds2.open();
			System.out.println(ds1.getAsString().equals(ds2.getAsString()));
			ds1.cancel();
			ds2.cancel();
		}
	}
}
