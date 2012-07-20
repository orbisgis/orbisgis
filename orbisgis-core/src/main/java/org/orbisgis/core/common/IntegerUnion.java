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
    private int[] intervals=new int[0];
    int size=0;
    
    private void add(int value) {
            
    }
    
    private void insert(int index,int value) {
            
    }
    
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
     * @return The number of Integer in this instance
     */
    public int getItemCount() {
        return intervals.size();
    }
    
    /**
     * Does this container has intervals
     * @return True if this container is empty, false otherwise
     */
    public boolean isEmpty() {
        return this.intervals.isEmpty();
    }
    /**
     * Add a row index in the list
     * @param row The row index. Duplicates are not pushed, and do not raise errors.
     * @TODO Add function to push a range instead of a single row index
     * @TODO refactor, use only one call of binarySearch !
     */
    public void addRow(int row) {
        // Iterate over the row range array and find contiguous row
        boolean inserted = false;
        //Find the first nearest value in ranges
        int index = Collections.binarySearch(intervals, row);
        
    }

    @Override
    public Iterator<Integer> iterator() {
        return new RowIterator(intervals.iterator());
    }
    private class RowIterator implements Iterator<Integer> {
        int index = 0;
        int current = 0;
        int itEnd = 0;

        public RowIterator() {
            if(size!=0) {
                    current = intervals[0];
                    itEnd = intervals[1];
                    index = 1;
            }
        }
        
        @Override
        public boolean hasNext() {
            return current<itEnd && itR.hasNext();
        }

        @Override
        public Integer next() {
            return new RowInterval(itR.next(),itR.next()+1);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}
