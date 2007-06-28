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
				"../../datas2tests/shp/bigshape2D/communes.shp")));

		DataSource sql = dsf
				.executeSQL("select * from shape");

		DataSourceDefinition target;
		boolean shape = true;
		if (shape) {
			target = new FileSourceDefinition(new File(
					"output.shp"));
		} else {
			target = new DBTableSourceDefinition(new DBSource(null,
					0, "writers-output", null, null, "output", "jdbc:h2"));
		}
		dsf.registerDataSource("output", target);
		dsf.saveContents("output", sql);
		DataSource ds1 = dsf.executeSQL("select the_geom from output");
		DataSource ds2 = dsf.executeSQL("select the_geom from shape");
		ds1.open();
		ds2.open();
		System.out.println(ds1.getAsString().equals(ds2.getAsString()));
		ds1.cancel();
		ds2.cancel();
	}
}
