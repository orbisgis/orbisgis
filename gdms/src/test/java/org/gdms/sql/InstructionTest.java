package org.gdms.sql;

import junit.framework.TestCase;

import org.gdms.data.AllTypesObjectDriver;
import org.gdms.data.DataSourceFactory;
import org.gdms.source.SourceManager;
import org.gdms.sql.parser.ParseException;
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
}
