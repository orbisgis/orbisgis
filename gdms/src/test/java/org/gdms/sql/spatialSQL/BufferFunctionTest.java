package org.gdms.sql.spatialSQL;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SpatialDataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.SyntaxException;
import org.gdms.data.driver.DriverException;

import com.hardcode.driverManager.DriverLoadException;

public class BufferFunctionTest {

	/**
	 * @param args
	 */

	static DataSourceFactory dsf = new DataSourceFactory();

	public static void main(String[] args) throws DriverLoadException, DataSourceCreationException, SyntaxException, DriverException, NoSuchTableException, ExecutionException {

		Long beginTime = System.currentTimeMillis();

		File src = new File("../datas2tests/shp/cantons.shp");


		DataSource ds = dsf.getDataSource(src);

		simpleBuffer(new SpatialDataSourceDecorator(ds));


		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);



	}


	public static void simpleBuffer(SpatialDataSource ds) throws DriverException, SyntaxException, DriverLoadException, NoSuchTableException, ExecutionException{

		ds.beginTrans();
		String sqlQuery = "select buffer(geom,20) from "
				+ ds.getName() + ";";

		dsf.executeSQL(sqlQuery);

		ds.rollBackTrans();

	}

}
