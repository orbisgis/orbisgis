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

import org.gdms.data.AllTypesObjectDriver;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.source.SourceManager;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.Instruction;
import org.gdms.sql.strategies.SQLProcessor;
import org.orbisgis.progress.NullProgressMonitor;

public class InstructionTest extends TestCase {

	private DataSourceFactory dsf;
	private File resultDir;
	private CancelledPM cancelPM;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		resultDir = new File("src/test/resources/backup");
		dsf.setResultDir(resultDir);
		SourceManager sm = dsf.getSourceManager();
		AllTypesObjectDriver omd = new AllTypesObjectDriver();
		sm.register("alltypes", omd);

		cancelPM = new CancelledPM();
	}

	public void testGetScriptInstructionMetadata() throws Exception {
		String script = "select * from alltypes; select * from alltypes";
		SQLProcessor sqlProcessor = new SQLProcessor(dsf);
		String[] instructions = sqlProcessor.getScriptInstructions(script);
		assertTrue(sqlProcessor.prepareInstruction(instructions[0])
				.getResultMetadata() != null);
		assertTrue(sqlProcessor.prepareInstruction(instructions[1])
				.getResultMetadata() != null);
	}

	public void testScriptComments() throws Exception {
		String commentContent = "/*This is a\nsuper\r\ncomm\nent*/";
		String scriptBody = "select *\nfrom\nmytable;";
		testComments(commentContent, scriptBody);
		commentContent = "/*one line comment*/";
		testComments(commentContent, scriptBody);
		commentContent = "";
		testComments(commentContent, scriptBody);
	}

	private void testComments(String commentContent, String scriptBody)
			throws ParseException {
		String script = commentContent + scriptBody;
		SQLProcessor pr = new SQLProcessor(dsf);
		commentContent = commentContent.replaceAll("\\Q/*\\E", "");
		commentContent = commentContent.replaceAll("\\Q*/\\E", "");
		assertTrue(pr.getScriptComment(script).equals(commentContent));
		assertTrue(pr.getScriptBody(script).equals(scriptBody));
		assertTrue(pr.getScriptComment(scriptBody).equals(""));
		assertTrue(pr.getScriptBody(scriptBody).equals(scriptBody));
	}

	public void testCommentsInTheMiddleOfTheScript() throws Exception {
		String script = "/*description*/\nselect * from mytable;\n/*select * from mytable*/;";
		SQLProcessor pr = new SQLProcessor(dsf);
		String[] instructions = pr.getScriptInstructions(script);
		assertTrue(instructions.length == 1);

	}

	public void testSQLSource() throws Exception {
		SQLProcessor pr = new SQLProcessor(dsf);
		Instruction instr = pr.prepareInstruction("select * from alltypes");
		DataSource ds = dsf.getDataSource(instr, DataSourceFactory.DEFAULT,
				null);
		assertTrue(ds.getSource().getSQL().equals("select * from alltypes"));
	}

	public void testCancelledInstructions() throws Exception {
		SQLProcessor pr = new SQLProcessor(dsf);
		Instruction instr = pr.prepareInstruction("select * from alltypes");
		DataSource ds = dsf.getDataSource(instr, DataSourceFactory.DEFAULT,
				cancelPM);
		assertTrue(ds == null);

		assertTrue(dsf.getDataSourceFromSQL("select * from alltypes", cancelPM) == null);
	}

	private class CancelledPM extends NullProgressMonitor {

		@Override
		public boolean isCancelled() {
			return true;
		}

	}
}
