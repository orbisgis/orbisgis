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

import java.io.File;

import junit.framework.TestCase;
import org.gdms.SQLBaseTest;

import org.gdms.data.SQLAllTypesObjectDriver;
import org.gdms.data.DataSource;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.sql.SQLSourceDefinition;
import org.gdms.source.SourceManager;
import org.gdms.sql.engine.SQLEngine;
import org.gdms.sql.engine.SqlStatement;
import org.orbisgis.progress.NullProgressMonitor;

public class InstructionTest extends TestCase {

	private SQLDataSourceFactory dsf;
	private File resultDir;
	private CancelledPM cancelPM;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dsf = new SQLDataSourceFactory();
		dsf.setTempDir(SQLBaseTest.internalData + "backup");
		resultDir = new File(SQLBaseTest.internalData,"backup");
		dsf.setResultDir(resultDir);
		SourceManager sm = dsf.getSourceManager();
		SQLAllTypesObjectDriver omd = new SQLAllTypesObjectDriver();
		sm.register("alltypes", omd);

		cancelPM = new CancelledPM();
	}

	public void testGetScriptInstructionMetadata() throws Exception {
		String script = "select * from alltypes; select * from alltypes;";
                SQLEngine engine = new SQLEngine(dsf);
                SqlStatement[] st = engine.parse(script);
                st[0].prepare(dsf);
                st[1].prepare(dsf);
		assertTrue(st[0].getResultMetadata() != null);
		assertTrue(st[1].getResultMetadata() != null);
                st[0].cleanUp();
                st[1].cleanUp();
	}

	public void testCommentsInTheMiddleOfTheScript() throws Exception {
		String script = "/*description*/\nselect * from mytable;\n/*select * from mytable*/;";
		SQLEngine engine = new SQLEngine(dsf);
		SqlStatement[] st = engine.parse(script);
		assertTrue(st.length == 1);

	}

	public void testSQLSource() throws Exception {
		SQLEngine engine = new SQLEngine(dsf);
		SqlStatement[] st = engine.parse("select * from alltypes;");
		DataSource ds = dsf.getDataSource(st[0], SQLDataSourceFactory.DEFAULT,
				null);
                assertTrue((ds.getSource().getType() & SourceManager.SQL) == SourceManager.SQL);
                String sql = ((SQLSourceDefinition)ds.getSource().getDataSourceDefinition()).getSQL();
		assertTrue(sql.equals("select * from alltypes;"));
	}

	public void testCancelledInstructions() throws Exception {
		SQLEngine engine = new SQLEngine(dsf);
		SqlStatement[] st = engine.parse("select * from alltypes;");
		DataSource ds = dsf.getDataSource(st[0], SQLDataSourceFactory.DEFAULT,
				cancelPM);
		assertTrue(ds == null);

		assertTrue(dsf.getDataSourceFromSQL("select * from alltypes;", cancelPM) == null);
	}

	private class CancelledPM extends NullProgressMonitor {

		@Override
		public boolean isCancelled() {
			return true;
		}

	}
}
