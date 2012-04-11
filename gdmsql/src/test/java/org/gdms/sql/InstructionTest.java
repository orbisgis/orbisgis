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

import org.junit.Before;
import org.junit.Test;
import java.io.File;

import org.gdms.SQLBaseTest;

import org.gdms.data.SQLAllTypesObjectDriver;
import org.gdms.data.DataSource;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.sql.SQLSourceDefinition;
import org.gdms.source.SourceManager;
import org.gdms.sql.engine.SQLEngine;
import org.gdms.sql.engine.SqlStatement;
import org.orbisgis.progress.NullProgressMonitor;

import static org.junit.Assert.*;

public class InstructionTest {

        private SQLDataSourceFactory dsf;
        private File resultDir;
        private CancelledPM cancelPM;

        @Before
        public void setUp() throws Exception {
                dsf = new SQLDataSourceFactory();
                dsf.setTempDir(SQLBaseTest.backupDir.getAbsolutePath());
                dsf.setResultDir(SQLBaseTest.backupDir);
                SourceManager sm = dsf.getSourceManager();
                SQLAllTypesObjectDriver omd = new SQLAllTypesObjectDriver();
                sm.register("alltypes", omd);

                cancelPM = new CancelledPM();
        }

        @Test
        public void testGetScriptInstructionMetadata() throws Exception {
                String script = "select * from alltypes; select * from alltypes;";
                SQLEngine engine = new SQLEngine(dsf);
                SqlStatement[] st = engine.parse(script);
                st[0].prepare(dsf);
                st[1].prepare(dsf);
                assertNotNull(st[0].getResultMetadata());
                assertNotNull(st[1].getResultMetadata());
                st[0].cleanUp();
                st[1].cleanUp();
        }

        @Test
        public void testCommentsInTheMiddleOfTheScript() throws Exception {
                String script = "/*description*/\nselect * from mytable;\n/*select * from mytable*/";
                SQLEngine engine = new SQLEngine(dsf);
                SqlStatement[] st = engine.parse(script);
                assertEquals(st.length, 1);

        }

        @Test
        public void testSQLSource() throws Exception {
                SQLEngine engine = new SQLEngine(dsf);
                SqlStatement[] st = engine.parse("select * from alltypes;");
                DataSource ds = dsf.getDataSource(st[0], SQLDataSourceFactory.DEFAULT,
                        null);
                assertEquals((ds.getSource().getType() & SourceManager.SQL), SourceManager.SQL);
                String sql = ((SQLSourceDefinition) ds.getSource().getDataSourceDefinition()).getSQL();
                assertEquals(sql, "select * from alltypes;");
        }

        @Test
        public void testCancelledInstructions() throws Exception {
                SQLEngine engine = new SQLEngine(dsf);
                SqlStatement[] st = engine.parse("select * from alltypes;");
                DataSource ds = dsf.getDataSource(st[0], SQLDataSourceFactory.DEFAULT,
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
