package org.gdms.manual;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;

public class CubeBug {
	public static void main(String[] args) throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.executeSQL("call register('../../datas2tests/shp/smallshape3D/cube.shp','cube');");
		DataSource dataSource = dsf.getDataSource("cube");
		dataSource.open();
		System.out.println(dataSource.getAsString());
		dataSource.cancel();
	}
}
