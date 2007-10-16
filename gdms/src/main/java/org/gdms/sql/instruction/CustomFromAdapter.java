package org.gdms.sql.instruction;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.parser.SQLEngineConstants;
import org.gdms.sql.parser.Token;

public class CustomFromAdapter extends Adapter {

	public DataSource[] getTables() throws DriverLoadException,
			NoSuchTableException, DataSourceCreationException {
		ArrayList<String> tablesArray = new ArrayList<String>();
		Token first = getEntity().first_token.next;
		String image = first.image;
		while (first != getEntity().last_token.next) {
			if (first.kind == SQLEngineConstants.ID) {
				tablesArray.add(image);
			}
			first = first.next;
			image = first.image;
		}
		String[] tablesName = tablesArray.toArray(new String[0]);
		DataSource[] ret = new DataSource[tablesName.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = getInstructionContext().getDSFactory().getDataSource(
					tablesName[i]);
		}

		return ret;
	}

}
