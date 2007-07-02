package org.urbsat;

import org.gdms.sql.customQuery.QueryManager;
import org.urbsat.custom.BuildDensity;
import org.urbsat.custom.BuildLenght;
import org.urbsat.custom.BuildNumber;
import org.urbsat.custom.CreateGrid;
import org.urbsat.custom.Density;

public class Register {
	static {
		QueryManager.registerQuery(new CreateGrid());
		QueryManager.registerQuery(new Density());
		QueryManager.registerQuery(new BuildNumber());
		QueryManager.registerQuery(new BuildDensity());
		QueryManager.registerQuery(new BuildLenght());
	}
}