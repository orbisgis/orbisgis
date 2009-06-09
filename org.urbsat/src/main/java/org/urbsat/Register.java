/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
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
import org.urbsat.kmeans.KMeans;
import org.urbsat.landcoverIndicators.function.CircleCompacity;
import org.urbsat.landcoverIndicators.function.MeanSpacingBetweenBuildingsInACell;
import org.urbsat.utilities.BigCreateGrid;
import org.urbsat.utilities.CreateGrid;
import org.urbsat.utilities.CreateWebGrid;
import org.urbsat.utilities.GetZDEM;
import org.urbsat.utilities.MainDirections;

public class Register implements PluginActivator {
	public void start() throws Exception {
		QueryManager.registerQuery(CreateGrid.class);
		QueryManager.registerQuery(BigCreateGrid.class);
		QueryManager.registerQuery(CreateWebGrid.class);
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

		QueryManager.registerQuery(GetZDEM.class);

		FunctionManager.addFunction(MeanSpacingBetweenBuildingsInACell.class);
		FunctionManager.addFunction(CircleCompacity.class);

		QueryManager.registerQuery(KMeans.class);

		QueryManager.registerQuery(MainDirections.class);

	}

	public void stop() throws Exception {
	}

	public boolean allowStop() {
		return true;
	}
}