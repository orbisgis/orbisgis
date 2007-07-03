package org.urbsat;

import org.gdms.sql.customQuery.QueryManager;
import org.urbsat.custom.AverageBuildHeight;
import org.urbsat.custom.Compacity;
import org.urbsat.custom.BuildLenght;
import org.urbsat.custom.BuildNumber;
import org.urbsat.landcoverIndicators.custom.Density;
import org.urbsat.utilities.CreateGrid;

public class Register {
	static {
		QueryManager.registerQuery(new CreateGrid());
		QueryManager.registerQuery(new Density());
		QueryManager.registerQuery(new BuildNumber());
		QueryManager.registerQuery(new Compacity());
		QueryManager.registerQuery(new BuildLenght());
		QueryManager.registerQuery(new AverageBuildHeight());
	}
}