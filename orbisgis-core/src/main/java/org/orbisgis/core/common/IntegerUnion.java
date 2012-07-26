/**
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class aggregates consecutive integers.
 * 
 * The goal is to reduce memory usage, ordering by ascending index,
 * the tradeoff is the additional CPU cycle for insertion.
 * Values are returned sorted and without duplicates.
 * @author Nicolas Fortin
 */
public class IntegerUnion implements Iterable<Integer> {
    // int intervals ex: 0,15,50,60 for 0 to 15 and 50 to 60    
    private List<Integer> intervals=new ArrayList<Integer>();
        
    /**
     * Default constructor
     */
    IntegerUnion() {
        
    }
    /**
     * 
     * @param row First row id
     */
    IntegerUnion(int row) {
        intervals.add(row);
        intervals.add(row);
    }
    
    /**
     * 
     * @param rowbegin
     * @param rowend 
     */
    IntegerUnion(int rowbegin, int rowend) {
        if(rowend<rowbegin) {
            throw new IllegalArgumentException("Begin row index must be inferior or equal to end row index.");
        }
        intervals.add(rowbegin);
        intervals.add(rowend);
    }
    
    /**
     * Does this container has intervals
     * @return True if this container is empty, false otherwise
     */
    public boolean isEmpty() {
        return intervals.isEmpty();
    }
    /**
     * Add a row index in the list
     * @param row The row index. Duplicates are not pushed, and do not raise errors.
     * @TODO Add function to push a range instead of a single int
     */
    public void add(int row) {
        // Iterate over the row range array and find contiguous row
        boolean inserted = false;
        
        //Find the first nearest value in ranges
        int index = Collections.binarySearch(intervals, row);
        if(index>=0) {
                return;
        }
        index=-index-1; //retrieve the nearest index by order
        // intervals[index] > row
        if(index % 2==0) {
                //If index corresponding to begin of a range
                
        } else {
                //If index corresponding to the end of a range
        }        
        if(!inserted) {
                //New range
                intervals.add(index,row);
                intervals.add(index,row);            
        }        
    }

    @Override
    public Iterator<Integer> iterator() {
        return new RowIterator();
    }
    /**
     * 
     * @return intervals ex: 0,0,50,60 for [0] and [50-60]
     */
    public List<Integer> getRowRanges() {
            return intervals;
    }
    private class RowIterator implements Iterator<Integer> {
        private Iterator<Integer> it;
        private Integer current = 0;
        private Integer itEnd = 0;

        public RowIterator() {
            it = intervals.iterator();
            if(!intervals.isEmpty()) {
                    current = it.next();
                    itEnd = it.next();
            }
        }
        
        @Override
        public boolean hasNext() {
            return current<itEnd && it.hasNext();
        }

        @Override
        public Integer next() {
            return current++;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}
