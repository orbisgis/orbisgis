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
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.orbisgis.progress.NullProgressMonitor;

import static org.junit.Assert.*;

import org.gdms.TestBase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.sql.engine.Engine;
import org.gdms.sql.engine.SQLStatement;

/**
 *
 * @author Antoine Gourlay
 */
public class EngineTest extends TestBase {
        
        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();
        }
        
        @Test
        public void testSerializationOfQuery() throws Exception {
                final String sql = "SELECT abs(42), 'toto';";
                
                SQLStatement[] c = Engine.parse(sql, dsf.getProperties());
                File a = File.createTempFile("sql", null);
                a.delete();
                
                FileOutputStream s = new FileOutputStream(a);
                c[0].save(s);
                s.close();
                
                FileInputStream is = new FileInputStream(a);
                SQLStatement st = SQLStatement.load(is, dsf.getProperties());
                assertEquals(sql, st.getSQL());
                DataSource d = dsf.getDataSource(st, DataSourceFactory.DEFAULT, new NullProgressMonitor());
                
                d.open();
                assertEquals(1, d.getRowCount());
                assertEquals(42, d.getInt(0, 0));
                assertEquals("toto", d.getString(0, 1));
                d.close();
                
                a.delete();
        }
}
