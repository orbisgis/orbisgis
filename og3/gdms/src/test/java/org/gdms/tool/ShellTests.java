package org.gdms.tool;

import org.gdms.SourceTest;

public class ShellTests extends SourceTest {

	public void testSimpleScript() throws Exception {

		new Shell().execute(internalData + "simpleScript.sql");

	}
}
