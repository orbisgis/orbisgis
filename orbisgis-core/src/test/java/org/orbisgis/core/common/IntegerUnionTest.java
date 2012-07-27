/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Nicolas Fortin
 */
public class IntegerUnionTest {

        public IntegerUnionTest() {
        }

        private void check(IntegerUnion union, Integer[] check) {
                List<Integer> ints = union.getValueRanges();
                Assert.assertTrue("Union fail, check arrays had differents size",
                        ints.size() == check.length);
                int i = 0;
                for (Integer value : ints) {
                        if (value != check[i]) {
                                Assert.assertTrue("Union fails, different values " + value + "!=" + check[i], value == check[i]);
                        }
                        i++;
                }
        }

        @Test
        public void testDev() {
                IntegerUnion mergeTool = new IntegerUnion(15, 50);

                mergeTool.add(51);
                check(mergeTool, new Integer[]{15, 51});
                mergeTool.add(14);
                check(mergeTool, new Integer[]{14, 51});
                mergeTool.add(53);
                check(mergeTool, new Integer[]{14, 51, 53, 53});
                mergeTool.add(52);
                check(mergeTool, new Integer[]{14, 53});
                mergeTool.add(14);
                check(mergeTool, new Integer[]{14, 53});
                mergeTool.add(53);
                check(mergeTool, new Integer[]{14, 53});
                mergeTool.add(15);
                mergeTool.add(52);
                check(mergeTool, new Integer[]{14, 53});
        }

        @Test
        public void testRowMerge() {
                long deb = System.nanoTime();
                IntegerUnion mergeTool = new IntegerUnion();

                mergeTool.add(50);

                for (int i = 51; i < 200; i++) {
                        mergeTool.add(i);
                }

                for (int i = 300; i < 400; i++) {
                        mergeTool.add(i);
                }
                for (int i = 450; i < 1500; i++) {
                        mergeTool.add(i);
                }
                for (int i = 2000; i < 20000; i++) {
                        mergeTool.add(i);
                }
                for (int i = 401; i < 450; i++) {
                        mergeTool.add(i);
                }
                //Add already pushed rows id, the result of intervals must be the same
                mergeTool.add(401);
                mergeTool.add(410);
                mergeTool.add(19999);

                mergeTool.add(400);
                double timeadd = ((System.nanoTime() - deb) / 1e6);
                //Test if results is correct
                Iterator<Integer> it = mergeTool.getValueRanges().iterator();
                System.out.println("Ranges :");
                List<Integer> correctRanges = new ArrayList<Integer>();
                correctRanges.add(50);
                correctRanges.add(199);
                correctRanges.add(300);
                correctRanges.add(1499);
                correctRanges.add(2000);
                correctRanges.add(19999);

                while (it.hasNext()) {
                        int begin = it.next();
                        int end = it.next();
                        System.out.print("[" + begin + "-" + end + "]");
                        Assert.assertTrue(correctRanges.contains(begin));
                        Assert.assertTrue(correctRanges.contains(end));
                        Assert.assertTrue(!(300 < begin && begin < 1499));
                        Assert.assertTrue(!(300 < end && end < 1499));
                }
                System.out.println("");
                System.out.println("Merging of rows took :" + timeadd + " ms");

        }

        @BeforeClass
        public static void setUpClass() throws Exception {
        }

        @AfterClass
        public static void tearDownClass() throws Exception {
        }
}
