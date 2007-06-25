package org.gdms.manual;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;

public class Writers {

	public static void main(String[] args) throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.registerDataSource("shape", new FileSourceDefinition(new File(
				"../../datas2tests/shp/mediumshape2D/landcover2000.shp")));

		DataSource sql = dsf
				.executeSQL("select Buffer(the_geom, 20) from shape");
		DataSourceCreation target = new FileSourceCreation(new File("output.shp"),
				sql.getMetadata());
		dsf.createDataSource(target, sql);
	}
}
