package org.orbisgis.core.beanshell;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
/**
 * @author Nicolas Fortin
 */
public class BeanShellMainTest {


    @Test
    public void testNullFileScript() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream psbak = System.err;
        System.setErr(ps);
        try {
            BeanshellScript.main(new String[]{""});
            String err = baos.toString();
            assertEquals("The second parameter must be not null.\n",err);
        } finally {
            System.setErr(psbak);
        }
    }

    @Test
    public void testGetHelp() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream psbak = System.out;
        System.setOut(ps);
        try {
            BeanshellScript.main(new String[]{});
            String out = baos.toString();
            assertTrue(out.equals(BeanshellScript.getHelp()));
        } finally {
            System.setOut(psbak);
        }
    }

    @Test
    public void testWrongParameter() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream psbak = System.out;
        System.setOut(ps);
        try {
            BeanshellScript.main(new String[]{ "youhou", "../src/test/resources/beanshell/helloWorld.bsh"});
            String out = baos.toString();
            assertTrue(out.endsWith(BeanshellScript.getHelp()));
        } finally {
            System.setOut(psbak);
        }
    }
}
