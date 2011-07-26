package org.gdms.tool;

import org.gdms.SQLBaseTest;
import org.junit.Test;

public class ShellTests extends SQLBaseTest {

        @Test
        public void testSimpleScript() throws Exception {
                Shell.main(new String[]{internalData + "simpleScript.sql"});
        }
}
