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
package org.contrib.gdms.sql;

import org.contrib.algorithm.triangulation.triangleCLib.Generate2DMesh;
import org.contrib.gdms.sql.customQuery.spatial.geometry.jgrapht.ShortestPath;
import org.contrib.gdms.sql.customQuery.spatial.geometry.tin.BuildTIN2;
import org.contrib.gdms.sql.customQuery.spatial.geometry.tin.Cdt;
import org.contrib.gdms.sql.customQuery.spatial.geometry.tin.CheckDelaunayProperty;
import org.contrib.gdms.sql.customQuery.spatial.geometry.tin.CheckSpatialEquivalence;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.pluginManager.PluginActivator;

public class Register implements PluginActivator {
	public void start() throws Exception {

		// Raster processing

		// Vector processing

		QueryManager.registerQuery(Generate2DMesh.class);
		QueryManager.registerQuery(ShortestPath.class);
		QueryManager.registerQuery(BuildTIN2.class);
		QueryManager.registerQuery(Cdt.class);
		QueryManager.registerQuery(CheckDelaunayProperty.class);
		QueryManager.registerQuery(CheckSpatialEquivalence.class);
	}

	public void stop() throws Exception {
	}

	public boolean allowStop() {
		return true;
	}
}