/*
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
package org.orbisgis.core.common;

import java.util.HashSet;
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
                        if (!value.equals(check[i])) {
                                Assert.assertTrue("Union fails, different values " + value + "!=" + check[i], value == check[i]);
                        }
                        i++;
                }
        }

        @Test
        public void testContains() {
                IntegerUnion mergeTool = new IntegerUnion(0, 50);
                mergeTool.remove(25);
                mergeTool.remove(26);
                mergeTool.remove(27);
                
                Assert.assertFalse(mergeTool.contains(26));
                Assert.assertFalse(mergeTool.contains(25));
                Assert.assertFalse(mergeTool.contains(27));
                
                Assert.assertTrue(mergeTool.contains(24));
                Assert.assertTrue(mergeTool.contains(28));
                Assert.assertTrue(mergeTool.contains(0));
                Assert.assertTrue(mergeTool.contains(50));
        }
        
        @Test
        public void testRemove() {
                IntegerUnion mergeTool = new IntegerUnion(0, 50);
                Assert.assertTrue(mergeTool.remove(0));
                check(mergeTool, new Integer[]{1, 50});
                Assert.assertTrue(mergeTool.remove(50));
                check(mergeTool, new Integer[]{1, 49});
                Assert.assertTrue(mergeTool.remove(2));
                check(mergeTool, new Integer[]{1, 1, 3, 49});
                Assert.assertTrue(mergeTool.remove(1));
                check(mergeTool, new Integer[]{3, 49});
                Assert.assertTrue(mergeTool.remove(48));
                check(mergeTool, new Integer[]{3, 47, 49, 49});
                Assert.assertTrue(mergeTool.remove(49));
                check(mergeTool, new Integer[]{3, 47});
                Assert.assertTrue(mergeTool.remove(44));
                check(mergeTool, new Integer[]{3, 43, 45, 47});
                Assert.assertTrue(mergeTool.remove(43));
                check(mergeTool, new Integer[]{3, 42, 45, 47});
                Assert.assertTrue(mergeTool.remove(46));
                check(mergeTool, new Integer[]{3, 42, 45, 45, 47, 47});
                Assert.assertFalse(mergeTool.remove(46));
                Assert.assertTrue(mergeTool.remove(45));
                check(mergeTool, new Integer[]{3, 42, 47, 47});
                Assert.assertTrue(mergeTool.remove(47));
                check(mergeTool, new Integer[]{3, 42});

                mergeTool = new IntegerUnion(0);
                Assert.assertTrue(mergeTool.remove(0));
                check(mergeTool, new Integer[]{});                
                Assert.assertFalse(mergeTool.remove(0));
                
                
                mergeTool = new IntegerUnion();
                mergeTool.add(51);
                mergeTool.add(49);
                mergeTool.remove(48);
                mergeTool.remove(49);
                mergeTool.remove(50);
                check(mergeTool, new Integer[]{51,51});
                
                mergeTool = new IntegerUnion();
                HashSet<Integer> set = new HashSet<Integer>();
                
                                
                for (int i = 51; i < 60; i+=3) {
                        Assert.assertTrue(mergeTool.add(i)==set.add(i));
                }
                for (int i = 45; i < 50; i++) {
                        Assert.assertTrue(mergeTool.add(i)==set.add(i));             
                }
                for (int i = 45; i < 65; i+=2) {
                        Assert.assertTrue("i="+i,mergeTool.remove(i)==set.remove(i));
                }
                Assert.assertTrue(set.containsAll(mergeTool));
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
                        if (!mergeValue.equals(originValue)) {
                                Assert.assertTrue(mergeValue + "!=" + originValue, mergeValue.equals(originValue));
                        }
                }
                Assert.assertTrue(itMerge.hasNext() == itOrigin.hasNext());
        }

        @Test
        public void testAdd() {
                IntegerUnion mergeTool = new IntegerUnion(15, 50);

                Assert.assertTrue(mergeTool.add(51));
                check(mergeTool, new Integer[]{15, 51});
                Assert.assertTrue(mergeTool.add(14));
                check(mergeTool, new Integer[]{14, 51});
                Assert.assertTrue(mergeTool.add(53));
                check(mergeTool, new Integer[]{14, 51, 53, 53});
                Assert.assertTrue(mergeTool.add(52));
                check(mergeTool, new Integer[]{14, 53});
                Assert.assertFalse(mergeTool.add(14));
                check(mergeTool, new Integer[]{14, 53});
                Assert.assertFalse(mergeTool.add(53));
                check(mergeTool, new Integer[]{14, 53});
                Assert.assertFalse(mergeTool.add(15));
                Assert.assertFalse(mergeTool.add(52));
                check(mergeTool, new Integer[]{14, 53});
                Assert.assertTrue(mergeTool.add(12));
                check(mergeTool, new Integer[]{12, 12, 14, 53});
        }

        @BeforeClass
        public static void setUpClass() throws Exception {
        }

        @AfterClass
        public static void tearDownClass() throws Exception {
        }
}
