package org.urbsat.function;


import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.DriverException;

import com.hardcode.driverManager.DriverLoadException;

public class MakeQuery {
	
	
	public static void execute(String query) throws SyntaxException, DriverLoadException, NoSuchTableException, ExecutionException, DriverException {
		String dataname;
		int pos = query.lastIndexOf("from");
		//String dataname = query
		//DataSource result = dsf.executeSQL(query);

		dataname = query.substring(pos+5);
		
		
		while (dataname.indexOf(" ")==0) {
			dataname = dataname.substring(1, dataname.length());
			
		}
		int tst = dataname.indexOf(" ");
		if (tst>=0) {
			dataname = dataname.substring(0, tst);
		}
		if (dataname.lastIndexOf(";")!=-1) {
			dataname = dataname.substring(0, dataname.lastIndexOf(";"));
		}
		DataSourceFactory dsf = DataSaved.getDatasource(dataname);
		String neoQuery = query.replaceAll("\\)", ",'"+dataname+"')");
		
		DataSource result = dsf.executeSQL(neoQuery);
		System.out.println(result.getFieldValue(0, 0));
		
	}
	
	
}
