/*
 * UrbSAT is a set of spatial functionalities to build morphological
 * and aerodynamic urban indicators. It has been developed on
 * top of GDMS and OrbisGIS. UrbSAT is distributed under GPL 3
 * license. It is produced by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of UrbSAT.
 *
 * UrbSAT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UrbSAT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UrbSAT. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.urbsat;

import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.pluginManager.PluginActivator;
import org.urbsat.function.CollectiveAverage;
import org.urbsat.function.CollectiveStandardDeviation;
import org.urbsat.kmeans.KMeans;
import org.urbsat.landcoverIndicators.function.Compacity;
import org.urbsat.landcoverIndicators.function.MeanSpacingBetweenBuildingsInACell;
import org.urbsat.utilities.CreateGrid;
import org.urbsat.utilities.CreateWebGrid;
import org.urbsat.utilities.CropRaster;
import org.urbsat.utilities.GetZDEM;
import org.urbsat.utilities.MainDirections;
import org.urbsat.utilities.RasterToPoints;
import org.urbsat.utilities.RasterToPolygons;

public class Register implements PluginActivator {
	public void start() throws Exception {
		QueryManager.registerQuery(new CreateGrid());
		QueryManager.registerQuery(new CreateWebGrid());
		// QueryManager.registerQuery(new Density());
		// QueryManager.registerQuery(new BuildNumber());
		// QueryManager.registerQuery(new BuildLenght());
		// QueryManager.registerQuery(new OldAverageBuildHeight());
		// QueryManager.registerQuery(new AverageBuildHeight());
		// QueryManager.registerQuery(new LateralDensity());
		// QueryManager.registerQuery(new FrontalDensity());
		// QueryManager.registerQuery(new BuildVolume());
		// QueryManager.registerQuery(new BuildArea());
		// QueryManager.registerQuery(new BalancedBuildVolume());
		// QueryManager.registerQuery(new StandardDeviationBuildBalanced());
		// QueryManager.registerQuery(new StandardDeviationBuildHeight());

		QueryManager.registerQuery(new GetZDEM());

		FunctionManager.addFunction(MeanSpacingBetweenBuildingsInACell.class);
		FunctionManager.addFunction(Compacity.class);

		FunctionManager.addFunction(CollectiveAverage.class);
		FunctionManager.addFunction(CollectiveStandardDeviation.class);

		QueryManager.registerQuery(new KMeans());
		QueryManager.registerQuery(new CropRaster());
		QueryManager.registerQuery(new RasterToPoints());
		QueryManager.registerQuery(new RasterToPolygons());

		QueryManager.registerQuery(new MainDirections());
	}

	public void stop() throws Exception {
	}

	public boolean allowStop() {
		return true;
	}
}