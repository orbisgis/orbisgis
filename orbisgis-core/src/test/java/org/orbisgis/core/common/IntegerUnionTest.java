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

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test of IntegerUnion
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

        
        
        
        /**
         * 
         */
        @Test
        public void testRemove() {
                IntegerUnion mergeTool = new IntegerUnion(0, 50);
                mergeTool.remove(0);
                check(mergeTool, new Integer[]{1, 50});
                mergeTool.remove(50);
                check(mergeTool, new Integer[]{1, 49});
                mergeTool.remove(2);
                check(mergeTool, new Integer[]{1, 1, 3, 49});
                mergeTool.remove(1);
                check(mergeTool, new Integer[]{3, 49});
                mergeTool.remove(48);
                check(mergeTool, new Integer[]{3, 47, 49, 49});
                mergeTool.remove(49);
                check(mergeTool, new Integer[]{3, 47});
        }
        

        @Test
        public void testIterator() {
                IntegerUnion mergeTool = new IntegerUnion();
                Set<Integer> origin = new TreeSet<Integer>();

                for (int i = 51; i < 60; i++) {
                        mergeTool.add(i);
                        origin.add(i);
                }
                for (int i = 75; i < 80; i++) {
                        mergeTool.add(i);
                        origin.add(i);
                }
                for (int i = 65; i < 74; i++) {
                        mergeTool.add(i);
                        origin.add(i);
                }

                Iterator<Integer> itMerge = mergeTool.iterator();
                Iterator<Integer> itOrigin = origin.iterator();

                while (itMerge.hasNext()) {
                        Integer mergeValue = itMerge.next();
                        Integer originValue = itOrigin.next();
                        if (mergeValue != originValue) {
                                Assert.assertTrue(mergeValue + "!=" + originValue, mergeValue == originValue);
                        }
                }
                Assert.assertTrue(itMerge.hasNext() == itOrigin.hasNext());
        }

        @Test
        public void testAdd() {
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
                mergeTool.add(12);
                check(mergeTool, new Integer[]{12, 12, 14, 53});
        }

        @BeforeClass
        public static void setUpClass() throws Exception {
        }

        @AfterClass
        public static void tearDownClass() throws Exception {
        }
}
