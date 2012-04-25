package org.gdms.tool;

import org.gdms.TestBase;
import org.junit.Test;

public class ShellTests extends TestBase {

        @Test
        public void testSimpleScript() throws Exception {
                Shell.main(new String[]{internalData + "simpleScript.sql"});
        }
}
