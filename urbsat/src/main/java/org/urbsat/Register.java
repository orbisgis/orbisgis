package org.urbsat;

import org.gdms.sql.customQuery.QueryManager;
import org.urbsat.custom.AverageBuildHeight;
import org.urbsat.custom.BalancedBuildVolume;
import org.urbsat.custom.BuildArea;
import org.urbsat.custom.BuildVolume;
import org.urbsat.custom.FrontalDensity;
import org.urbsat.custom.OldAverageBuildHeight;
import org.urbsat.custom.Compacity;
import org.urbsat.custom.BuildLenght;
import org.urbsat.custom.BuildNumber;
import org.urbsat.custom.LateralDensity;
import org.urbsat.custom.StandardDeviationBuildBalanced;
import org.urbsat.custom.StandardDeviationBuildHeight;
import org.urbsat.landcoverIndicators.custom.Density;
import org.urbsat.utilities.CreateGrid;

public class Register {
	static {
		QueryManager.registerQuery(new CreateGrid());
		QueryManager.registerQuery(new Density());
		QueryManager.registerQuery(new BuildNumber());
		QueryManager.registerQuery(new Compacity());
		QueryManager.registerQuery(new BuildLenght());
		QueryManager.registerQuery(new OldAverageBuildHeight());
		QueryManager.registerQuery(new AverageBuildHeight());
		QueryManager.registerQuery(new LateralDensity());
		QueryManager.registerQuery(new FrontalDensity());
		QueryManager.registerQuery(new BuildVolume());
		QueryManager.registerQuery(new BuildArea());
		QueryManager.registerQuery(new BalancedBuildVolume());
		QueryManager.registerQuery(new StandardDeviationBuildBalanced());
		QueryManager.registerQuery(new StandardDeviationBuildHeight());
	}
}