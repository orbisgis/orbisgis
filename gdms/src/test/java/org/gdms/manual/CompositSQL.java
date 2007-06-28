package org.gdms.manual;

import java.io.File;

import javax.xml.crypto.Data;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.driver.DriverException;

import com.hardcode.driverManager.DriverLoadException;

public class CompositSQL {

	/**
	 * @param args
	 * @throws DataSourceCreationException 
	 * @throws DriverLoadException 
	 * @throws ExecutionException 
	 * @throws NoSuchTableException 
	 * @throws SyntaxException 
	 * @throws DriverException 
	 */
	public static void main(String[] args) throws DriverLoadException, DataSourceCreationException, SyntaxException, NoSuchTableException, ExecutionException, DriverException {
		
		
		long beginTime = System.currentTimeMillis();

		File src1 = new File(
				"../../datas2tests/shp/mediumshape2D/bzh5_communes.shp");
		
		
		DataSourceFactory dsf= new DataSourceFactory();
		
		DataSource dsshape = dsf.getDataSource(src1);
		
		
		DataSource dsh2 = dsf.getDataSource(new DBSource(null,
					0, "/tmp/erwan/h2_1", null, null, "communes", "jdbc:h2"));
		
		
		DataSource dsresult = dsf.executeSQL("select * from " + dsshape.getName() + " , "+ dsh2.getName() + " ;");
		
		dsresult.open();
		System.out.println(dsresult.getFieldCount());
		//dsresult.cancel();
		

	}

}
