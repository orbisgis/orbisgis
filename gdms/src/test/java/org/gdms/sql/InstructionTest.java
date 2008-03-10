package org.gdms.sql;

import junit.framework.TestCase;

import org.gdms.data.AllTypesObjectDriver;
import org.gdms.data.DataSourceFactory;
import org.gdms.source.SourceManager;
import org.gdms.sql.strategies.Instruction;
import org.gdms.sql.strategies.SQLProcessor;

public class InstructionTest extends TestCase {

	private DataSourceFactory dsf;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		SourceManager sm = dsf.getSourceManager();
		AllTypesObjectDriver omd = new AllTypesObjectDriver();
		sm.register("alltypes", omd);
	}

	public void testGetScriptInstructionMetadata() throws Exception {
		String script = "select * from alltypes; select * from alltypes";
		SQLProcessor sqlProcessor = new SQLProcessor(dsf);
		Instruction[] instructions = sqlProcessor.prepareScript(script);
		assertTrue(instructions[0].getResultMetadata() != null);
		assertTrue(instructions[1].getResultMetadata() != null);
	}

	public void testScriptComments() throws Exception {
		String commentContent = "This is a\nsuper\r\ncomment";
		String scriptBody = "select *\nfrom\nmytable;";
		String script = "/*" + commentContent + "*/" + scriptBody;
		SQLProcessor pr = new SQLProcessor(dsf);
		assertTrue(pr.getScriptComment(script).equals(commentContent));
		assertTrue(pr.getScriptBody(script).equals(scriptBody));
		assertTrue(pr.getScriptComment(scriptBody) == null);
		assertTrue(pr.getScriptBody(scriptBody).equals(scriptBody));
	}
}
