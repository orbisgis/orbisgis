package org.orbisgis.core.beanshell;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author ebocher
 */
public class BeanShellScriptTest {

        @Test
        public void testNullFileScript() throws Exception {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                PrintStream psbak = System.out;
                System.setOut(ps);
                BeanshellScript.main(new String[]{"-f", ""});
                String out = baos.toString();
                assertTrue(out.equals("The second parameter must be not null.\n"));
                System.setOut(psbak);
        }

        @Test
        public void testGetHelp() throws Exception {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                PrintStream psbak = System.out;
                System.setOut(ps);
                BeanshellScript.main(new String[]{});
                String out = baos.toString();
                assertTrue(out.equals(BeanshellScript.getHelp()));
                System.setOut(psbak);
        }

        @Test
        public void testWrongParameter() throws Exception {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                PrintStream psbak = System.out;
                System.setOut(ps);
                BeanshellScript.main(new String[]{"youhou", "youhou"});
                String out = baos.toString();
                assertTrue(out.equals(BeanshellScript.getHelp()));
                System.setOut(psbak);
        }

        @Test
        public void testSimpleFileScript() throws Exception {
                BeanshellScript.main(new String[]{"-f", "src/test/resources/beanshell/helloWorld.bsh"});
                assertTrue(true);
        }

        @Test
        public void testMapDisplayScript() throws Exception {
                BeanshellScript.main(new String[]{"-f", "src/test/resources/beanshell/mapDisplayDatasource.bsh"});
                Thread.sleep(3000);
                assertTrue(true);
        }

        @Test
        public void testMapToPng() throws Exception {
                BeanshellScript.main(new String[]{"-f", "src/test/resources/beanshell/datatsourceTopng.bsh"});
                assertTrue(true);
        }
}
