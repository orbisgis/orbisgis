package org.gdms.tool;

import org.gdms.SQLBaseTest;

public class ShellTests extends SQLBaseTest {

	public void testSimpleScript() throws Exception {

		Shell.main(new String[] { internalData + "simpleScript.sql"});

	}
}
