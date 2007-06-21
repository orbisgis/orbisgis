package org.urbsat;

import org.gdms.sql.customQuery.QueryManager;
import org.urbsat.custom.CreateGrid;

public class Register {
	static {
		QueryManager.registerQuery(new CreateGrid());
	}
}