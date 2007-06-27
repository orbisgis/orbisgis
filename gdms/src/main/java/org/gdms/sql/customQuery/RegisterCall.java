package org.gdms.sql.customQuery;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.values.Value;

public class RegisterCall implements CustomQuery {

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		if (values.length == 2) {
			String file = values[0].toString();
			String name = values[1].toString();
			dsf.registerDataSource(name, new FileSourceDefinition(file));
		} else if ((values.length == 6) || (values.length == 8)) {
			String vendor = values[0].toString();
			String host = values[1].toString();
			String port = values[2].toString();
			String dbName = values[3].toString();
			String user = values[4].toString();
			String password = values[5].toString();
			String tableName = null;
			String name = null;
			if (values.length == 8) {
				tableName = values[6].toString();
				name = values[7].toString();
			}

			if (tableName == null) {
				throw new ExecutionException("Not implemented yet");
			}
			dsf.registerDataSource(name, new DBTableSourceDefinition(
					new DBSource(host, Integer.parseInt(port), dbName, user,
							password, tableName, vendor)));
		}

		return null;
	}

	public String getName() {
		return "register";
	}

}
