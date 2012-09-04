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
package org.orbisgis.view.table.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.progress.NullProgressMonitor;

/**
 *
 * @author Nicolas Fortin
 */
public class SortValueCachedComparatorTest {
                
        /**
         * Test of compare method, of class SortValueCachedComparator.
         * @throws DriverException 
         */
        @Test
        public void testCompare() throws DriverException {
                Value[] cache = new Value[] {
                        ValueFactory.createValue(5), // 0
                        ValueFactory.createValue(7), // 1
                        ValueFactory.createValue(3), // 2
                        ValueFactory.createValue(1), // 3
                        ValueFactory.createValue(9)};// 4 
                Integer[] expected = new Integer[] {3,2,0,1,4};
                Comparator<Integer> comp = new SortValueCachedComparator(cache);
                Collection<Integer> sorted = SortJob.sortArray(new IntegerUnion(0,cache.length - 1), comp, new NullProgressMonitor());
                //Ascending order
                Integer[] result = sorted.toArray(new Integer[expected.length]);
                Assert.assertArrayEquals(expected,result);
                //Descending order
                comp = Collections.reverseOrder(comp);
                Collection<Integer> sortedDesc = SortJob.sortArray(new IntegerUnion(0,cache.length - 1), comp, new NullProgressMonitor());
                List<Integer> sortedDescReversed = new ArrayList<Integer>(sortedDesc);
                Collections.reverse(sortedDescReversed);
                Assert.assertArrayEquals(sortedDescReversed.toArray(new Integer[expected.length]),sorted.toArray(new Integer[expected.length]));
        }
}
