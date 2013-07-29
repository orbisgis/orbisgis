/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.beanshell;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Erwan Bocher
 */
public class BeanShellScriptTest {
        private static final String[] DEFAULT_ARGS = new String[] {"",
                BeanshellScript.ARG_APPFOLDER,"orbisgis",
                BeanshellScript.ARG_WORKSPACE,"workspace", "debug"};
        private static String[] mainParams(String scriptPath) {
            return mainParams(scriptPath,new String[0]);
        }
        private static String[] mainParams(String scriptPath, String... additionnalArgs) {
            String[] args = Arrays.copyOf(DEFAULT_ARGS, DEFAULT_ARGS.length + additionnalArgs.length);
            args[0] = scriptPath;
            int i=DEFAULT_ARGS.length;
            for(String param : additionnalArgs) {
                args[i++] = param;
            }
            return args;
        }


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

        @Test
        public void testSimpleFileScript() throws Exception {
                BeanshellScript.main(mainParams("../src/test/resources/beanshell/helloWorld.bsh"));
        }

        @Test
        public void testDataSourceFileScript() throws Exception {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream psbak = System.out;
            System.setOut(ps);
            try {
                BeanshellScript.main(mainParams("../src/test/resources/beanshell/basicDbProcessing.bsh"));
                String out = baos.toString();
                assertTrue(out,out.endsWith("rincevent\n"));
            } finally {
                System.setOut(psbak);
            }
        }

        @Test
        public void testSpatialDataSourceFileScript() throws Exception {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream psbak = System.out;
            System.setOut(ps);
            try {
                BeanshellScript.main(mainParams("../src/test/resources/beanshell/spatialDbProcessing.bsh"));
                String out = baos.toString();
                assertTrue(out,out.endsWith("POLYGON ((59 18, 67 18, 67 13, 59 13, 59 18))\n"));
            } finally {
                System.setOut(psbak);
            }
        }

// TODO
//        @Test
//        public void testMapDisplayScript() throws Exception {
//                assumeTrue(!GraphicsEnvironment.isHeadless());
//                BeanshellScript.main(new String[]{"src/test/resources/beanshell/mapDisplayDatasource.bsh"});
//                Thread.sleep(3000);
//                assertTrue(true);
//        }
//
//        @Test
//        public void testMapToPng() throws Exception {
//                BeanshellScript.main(new String[]{"src/test/resources/beanshell/datatsourceTopng.bsh"});
//                assertTrue(true);
//        }
        
        @Test
        public void testSeveralArguments() throws Exception {
                BeanshellScript.main(mainParams("../src/test/resources/beanshell/testSeveralArguments.bsh", "orbis","1"));
                assertTrue(true);
        }
}
