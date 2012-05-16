/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan Bocher, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.orbisgis.collections.twoqueue;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for TwoQueueBuffer.
 * 
 * @author Antoine Gourlay
 */
public class TwoQueueTest {
        
        private class TestAddClear2Q extends TwoQueueBuffer<Integer, Integer> {
                
                boolean passed;

                public TestAddClear2Q(int maxSize) {
                        super(maxSize);
                }

                @Override
                protected Integer reclaim(Integer id) {
                        passed = !passed;
                        
                        return id;
                }

                @Override
                protected void unload(Integer b) {
                        
                }
                
        }
        
        @Test
        public void testAddClear() {
                TestAddClear2Q b = new TestAddClear2Q(10);
                int g = b.get(10);
                assertEquals(10, g);
                assertTrue(b.passed);
                b.clear();
                assertTrue(b.passed);
                b.get(10);
                assertFalse(b.passed);
        }
        
        private class TestUnload2Q extends TwoQueueBuffer<Integer, Integer> {
                
                int lastUn = -1;

                public TestUnload2Q(int maxSize) {
                        super(maxSize);
                }

                @Override
                protected Integer reclaim(Integer id) {
                        return id;
                }

                @Override
                protected void unload(Integer b) {
                        lastUn = b;
                }
                
        }
        
        @Test
        public void testUnload() {
                TestUnload2Q b = new TestUnload2Q(8);
                b.get(0);
                b.get(1);
                assertEquals(-1, b.lastUn);
                b.get(8);
                assertEquals(0, b.lastUn);
                b.get(9);
                assertEquals(1, b.lastUn);
        }
        
        private class TestComplex2Q extends TwoQueueBuffer<Integer, Integer> {
                
                int lastUn = -1;
                int lastRec = -1;

                public TestComplex2Q(int maxSize) {
                        super(maxSize);
                }

                @Override
                protected Integer reclaim(Integer id) {
                        lastRec = id;
                        return id;
                }

                @Override
                protected void unload(Integer b) {
                        lastUn = b;
                }
                
        }
        
        @Test
        public void testComplex2Q() {
                TestComplex2Q b = new TestComplex2Q(8);
                
                // no duplicates
                b.get(0);
                b.get(1);
                b.get(0);
                b.get(1);
                b.get(0);
                b.get(1);
                b.get(0);
                b.get(1);
                b.get(0);
                b.get(1);
                assertEquals(-1, b.lastUn);
                
                b.clear();
                
                b.get(0);
                b.get(1);
                // now we start unloading
                b.get(2);
                assertEquals(2, b.lastRec);
                assertEquals(0, b.lastUn);
                b.get(1);
                assertEquals(2, b.lastRec);
                assertEquals(0, b.lastUn);
                b.get(3);
                assertEquals(3, b.lastRec);
                assertEquals(1, b.lastUn);
                // now we access one in A1out
                // and see it climb back into Am
                b.get(0);
                assertEquals(0, b.lastRec);
                assertEquals(1, b.lastUn);
        }
}
