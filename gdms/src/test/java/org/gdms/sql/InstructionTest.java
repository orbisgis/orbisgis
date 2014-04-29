/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql;

import org.junit.Before;
import org.junit.Test;
import org.orbisgis.progress.NullProgressMonitor;

import static org.junit.Assert.*;

import org.gdms.TestBase;
import org.gdms.data.AllTypesObjectDriver;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.sql.SQLSourceDefinition;
import org.gdms.source.SourceManager;
import org.gdms.sql.engine.Engine;
import org.gdms.sql.engine.SQLScript;
import org.gdms.sql.engine.SQLStatement;

public class InstructionTest extends TestBase {

        private CancelledPM cancelPM = new CancelledPM();

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();
                AllTypesObjectDriver omd = new AllTypesObjectDriver();
                sm.register("alltypes", omd);
        }

        @Test
        public void testCommentsInTheMiddleOfTheScript() throws Exception {
                String script = "/*description*/\nselect * from mytable;\n/*select * from mytable*/";
                SQLScript st = Engine.parseScript(script, dsf.getProperties());
                assertEquals(st.getSize(), 1);

        }

        @Test
        public void testSQLSource() throws Exception {
                SQLStatement st = Engine.parse("select * from alltypes;", dsf.getProperties());
                DataSource ds = dsf.getDataSource(st, DataSourceFactory.DEFAULT,
                        null);
                assertEquals((ds.getSource().getType() & SourceManager.SQL), SourceManager.SQL);
                String sql = ((SQLSourceDefinition) ds.getSource().getDataSourceDefinition()).getSQL();
                assertEquals("select * from alltypes;", sql);
        }

        @Test
        public void testCancelledInstructions() throws Exception {
                SQLStatement st = Engine.parse("select * from alltypes;", dsf.getProperties());
                DataSource ds = dsf.getDataSource(st, DataSourceFactory.DEFAULT,
                        cancelPM);
                assertNull(ds);

                assertNull(dsf.getDataSourceFromSQL("select * from alltypes;", cancelPM));
        }

        private class CancelledPM extends NullProgressMonitor {

                @Override
                public boolean isCancelled() {
                        return true;
                }
        }
}
