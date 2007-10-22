/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
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
package org.gdms;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.gdms.data.DataSourceFactoryTests;
import org.gdms.data.DataSourceTest;
import org.gdms.data.EditionListenerTest;
import org.gdms.data.GettersTest;
import org.gdms.data.command.CommandStackTests;
import org.gdms.data.db.DataBaseTests;
import org.gdms.data.edition.EditionTests;
import org.gdms.data.edition.FailedEditionTest;
import org.gdms.data.edition.MetadataTest;
import org.gdms.data.edition.PKEditionTest;
import org.gdms.data.edition.UndoRedoTests;
import org.gdms.data.values.ValuesTest;
import org.gdms.drivers.DBDriverTest;
import org.gdms.drivers.ShapefileDriverTest;
import org.gdms.newFunctionalities.NoEmptyDataSetTest;
import org.gdms.spatial.SpatialDriverMetadataTest;
import org.gdms.spatial.SpatialEditionTest;
import org.gdms.sql.strategies.CustomQueriesTest;
import org.gdms.sql.strategies.SQLTest;

/**
 * @author Fernando Gonzalez Cortes
 */
public class GDMSTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.gdms.engine.test");
		// $JUnit-BEGIN$
		suite.addTestSuite(NoEmptyDataSetTest.class);
		suite.addTestSuite(MetadataTest.class);
		suite.addTestSuite(SpatialDriverMetadataTest.class);
		suite.addTestSuite(EditionTests.class);
		suite.addTestSuite(PKEditionTest.class);
		suite.addTestSuite(UndoRedoTests.class);
		suite.addTestSuite(EditionListenerTest.class);
		suite.addTestSuite(FailedEditionTest.class);
		suite.addTestSuite(ValuesTest.class);
		suite.addTestSuite(DataBaseTests.class);
		suite.addTestSuite(SQLTest.class);
		suite.addTestSuite(CommandStackTests.class);
		suite.addTestSuite(GettersTest.class);
		suite.addTestSuite(SpatialEditionTest.class);
		suite.addTestSuite(DBDriverTest.class);
		suite.addTestSuite(DataSourceTest.class);
		suite.addTestSuite(DataSourceFactoryTests.class);
		suite.addTestSuite(ShapefileDriverTest.class);
		suite.addTestSuite(CustomQueriesTest.class);
		// $JUnit-END$
		return suite;
	}
}
