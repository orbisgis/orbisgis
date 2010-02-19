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
package org.gdms.sql;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.gdms.sql.function.alphanumeric.AlphanumericFunctionTest;
import org.gdms.sql.function.spatial.convert.SpatialFunctionTest;
import org.gdms.sql.function.spatial.geometryProperties.PropertiesFunctionTest;
import org.gdms.sql.function.spatial.io.IOSpatialFunctionTest;
import org.gdms.sql.function.spatial.operators.OperatorsTest;
import org.gdms.sql.function.spatial.predicates.PredicatesTest;
import org.gdms.sql.function.statistics.StatisticFunctionsTest;
import org.gdms.sql.strategies.CustomQueriesTest;
import org.gdms.sql.strategies.SQLTest;

public class SQLTestSuite extends TestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.gdms.engine.test");
		suite.addTestSuite(InstructionTest.class);
		suite.addTestSuite(ProcessorTest.class);
		suite.addTestSuite(GrammarTest.class);
		suite.addTestSuite(AlphanumericFunctionTest.class);
		suite.addTestSuite(SpatialFunctionTest.class);
		suite.addTestSuite(PropertiesFunctionTest.class);
		suite.addTestSuite(IOSpatialFunctionTest.class);
		suite.addTestSuite(OperatorsTest.class);
		suite.addTestSuite(OptimizationTests.class);
		suite.addTestSuite(PredicatesTest.class);
		suite.addTestSuite(StatisticFunctionsTest.class);
		suite.addTestSuite(SQLTest.class);
		suite.addTestSuite(CustomQueriesTest.class);
		return suite;
	}
}
