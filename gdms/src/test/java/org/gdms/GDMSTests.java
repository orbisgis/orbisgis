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
package org.gdms;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.gdms.data.DataSourceFactoryTests;
import org.gdms.data.DataSourceTest;
import org.gdms.data.GettersTest;
import org.gdms.data.ListenerTest;
import org.gdms.data.command.CommandStackTests;
import org.gdms.data.db.DataBaseTests;
import org.gdms.data.edition.ConstraintTest;
import org.gdms.data.edition.EditionTests;
import org.gdms.data.edition.FailedEditionTest;
import org.gdms.data.edition.MetadataTest;
import org.gdms.data.edition.PKEditionTest;
import org.gdms.data.edition.UndoRedoTests;
import org.gdms.data.indexes.BTreeTest;
import org.gdms.data.indexes.RTreeTest;
import org.gdms.data.values.ValuesTest;
import org.gdms.drivers.CSVDriverTest;
import org.gdms.drivers.DBDriverTest;
import org.gdms.drivers.DBMetadataTest;
import org.gdms.drivers.ExportTest;
import org.gdms.drivers.GDMSDriverTest;
import org.gdms.drivers.RasterTest;
import org.gdms.drivers.ShapefileDriverTest;
import org.gdms.source.ChecksumTest;
import org.gdms.source.SourceManagementTest;
import org.gdms.spatial.SpatialDriverMetadataTest;
import org.gdms.spatial.SpatialEditionTest;
import org.gdms.sql.GrammarTest;
import org.gdms.sql.InstructionTest;
import org.gdms.sql.OptimizationTests;
import org.gdms.sql.ProcessorTest;
import org.gdms.sql.function.alphanumeric.AlphanumericFunctionTest;
import org.gdms.sql.function.spatial.convert.SpatialFunctionTest;
import org.gdms.sql.function.spatial.geometryProperties.PropertiesFunctionTest;
import org.gdms.sql.function.spatial.io.IOSpatialFunctionTest;
import org.gdms.sql.function.spatial.operators.OperatorsTest;
import org.gdms.sql.function.spatial.predicates.PredicatesTest;
import org.gdms.sql.function.statistics.StatisticFunctionsTest;
import org.gdms.sql.strategies.CustomQueriesTest;
import org.gdms.sql.strategies.SQLTest;

/**
 * @author Fernando Gonzalez Cortes
 */
public class GDMSTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.gdms.engine.test");
		// $JUnit-BEGIN$
		suite.addTestSuite(NoEmptyDataSetTest.class);
		suite.addTestSuite(MetadataTest.class);
		suite.addTestSuite(SpatialDriverMetadataTest.class);
		suite.addTestSuite(EditionTests.class);
		suite.addTestSuite(ConstraintTest.class);
		suite.addTestSuite(PKEditionTest.class);
		suite.addTestSuite(UndoRedoTests.class);
		suite.addTestSuite(ListenerTest.class);
		suite.addTestSuite(FailedEditionTest.class);
		suite.addTestSuite(ValuesTest.class);
		suite.addTestSuite(DataBaseTests.class);
		suite.addTestSuite(CommandStackTests.class);
		suite.addTestSuite(GettersTest.class);
		suite.addTestSuite(SpatialEditionTest.class);
		suite.addTestSuite(DataSourceTest.class);
		suite.addTestSuite(DataSourceFactoryTests.class);
		suite.addTestSuite(SourceManagementTest.class);
		suite.addTestSuite(ChecksumTest.class);
		// File drivers
		suite.addTestSuite(ShapefileDriverTest.class);
		suite.addTestSuite(CSVDriverTest.class);
		suite.addTestSuite(GDMSDriverTest.class);
		// Database drivers
		suite.addTestSuite(DBDriverTest.class);
		suite.addTestSuite(DBMetadataTest.class);
		suite.addTestSuite(ExportTest.class);
		// Raster drivers
		suite.addTestSuite(RasterTest.class);

		// Indexes
		suite.addTestSuite(BTreeTest.class);
		suite.addTestSuite(RTreeTest.class);
		// SQL related
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
		suite.addTestSuite(SpatialFunctionTest.class);
		suite.addTestSuite(CustomQueriesTest.class);
		// TODO suite.addTestSuite(ExplodeTest.class);

		// $JUnit-END$
		return suite;
	}
}