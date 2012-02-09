package org.orbisgis.core.beanshell;

import org.junit.Test;

/**
 *
 * @author ebocher
 */
public class BeanShellScriptTest {

        @Test
        public void testNullFileScript() throws Exception {
                BeanshellScript.main(new String[]{"-f", ""});
        }

        @Test
        public void testSimpleFileScript() throws Exception {

                BeanshellScript.main(new String[]{"-f", "src/test/resources/beanshell/helloWorld.bsh"});
        }

        @Test
        public void testMapDisplayScript() throws Exception {
                BeanshellScript.main(new String[]{"-f", "src/test/resources/beanshell/mapDisplayDatasource.bsh"});
                Thread.sleep(3000);
        }
}
