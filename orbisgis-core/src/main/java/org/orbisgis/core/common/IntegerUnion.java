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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * This class aggregates consecutive integers.
 * 
 * The goal is to reduce memory usage, ordering by ascending index,
 * the tradeoff is the additional CPU cycle for insertion and deletion.
 * Values are returned sorted and without duplicates.
 * 
 * This class is not thread safe
 * 
 * @TODO Add function to push a range instead of a single
 * @author Nicolas Fortin
 */
public class IntegerUnion implements SortedSet<Integer>, Serializable {
        private static final long serialVersionUID = 1L;
        
        // int intervals ex: 0,15,50,60 for 0 to 15 and 50 to 60    
        protected List<Integer> intervals;

        /**
         * Default constructor
         */
        public IntegerUnion() {
                intervals = new ArrayList<Integer>();
        }

        
        public IntegerUnion(IntegerUnion externalSet) {
                this.intervals = new ArrayList<Integer>(externalSet.intervals.size());

        }
        
        public IntegerUnion(Set<Integer> externalSet) {
                this();
                if(externalSet instanceof IntegerUnion) {
                        copyExternalIntegerUnion((IntegerUnion)externalSet);
                } else {
                        for(Integer value : externalSet) {
                                internalAdd(value);
                        }
                }
        }
        
        private void copyExternalIntegerUnion(IntegerUnion externalSet) {
                intervals.addAll(externalSet.intervals);
        }
        /**
         *
         * @param value First value
         */
        public IntegerUnion(int value) {
                this();
                intervals.add(value);
                intervals.add(value);
        }

        /**
         * Insert all values of the range [valueBegin-valueEnd].
         *
         * @param valueBegin Included begin of range
         * @param valueEnd Included end of range
         * @throws IllegalArgumentException if valueEnd < valueBegin
         */
        public IntegerUnion(int valueBegin, int valueEnd) {
                this();
                if (valueEnd < valueBegin) {
                        throw new IllegalArgumentException("Begin value must be inferior or equal to the end value.");
                }
                intervals.add(valueBegin);
                intervals.add(valueEnd);
        }

        /**
         * Does this container has intervals
         *
         * @return True if this container is empty, false otherwise
         */
        @Override
        public boolean isEmpty() {
                return intervals.isEmpty();
        }

        @Override
        public String toString() {
                if(intervals.isEmpty()) {
                        return "[]";
                }
                StringBuilder ret = new StringBuilder();
                Iterator<Integer> it = intervals.iterator();
                while(it.hasNext()) {
                        ret.append(" [");
                        ret.append(it.next());
                        ret.append("-");
                        ret.append(it.next()+1);
                        ret.append("[");
                }
                return ret.toString();
        }
        
        protected final boolean internalRemove(Integer value) {
                int index = Collections.binarySearch(intervals, value);
                if(index>=0) {
                                if(index > 0 && intervals.get(index - 1).equals(value)) {
                                        intervals.remove(index-1);
                                        intervals.remove(index-1);
                                } else if(index + 1 < intervals.size() && intervals.get(index + 1).equals(value)) {
                                        intervals.remove(index);
                                        intervals.remove(index);
                                } else {
                                        if (index % 2 == 0) {
                                                intervals.set(index,value+1);
                                        } else {
                                                intervals.set(index,value-1);
                                        }
                                }
                                return true;
                } else {
                        index = -index - 1; //retrieve the nearest index by order
                        if (index % 2 == 0) {
                                //Not in the collection
                                return false;                                
                        } else {
                                //Split in two ranges
                                Integer endValue = intervals.get(index);
                                intervals.set(index, value-1);
                                intervals.add(index+1,value+1);
                                intervals.add(index+2,endValue);
                                return true;
                        }
                }                
        }
        
        @Override
        public boolean remove(Object o) {
                Integer value = (Integer) o;
                return internalRemove(value);
        }

        
        protected final boolean internalAdd(Integer value) {
               // Iterate over the value range array and find contiguous value
                //Find the first nearest value in ranges
                int index = Collections.binarySearch(intervals, value);
                if (index >= 0) {
                        return false;
                }
                index = -index - 1; //retrieve the nearest index by order
                // intervals[index] > value
                if (index % 2 == 0) {
                        //If index corresponding to begin of a range
                        boolean mergeFirst = index > 0 && intervals.get(index - 1).equals(value - 1);
                        boolean mergeSecond = index < intervals.size() && intervals.get(index).equals(value + 1);
                        if (mergeFirst && mergeSecond) {
                                //Merge two ranges and update the end of the first range
                                Integer endNextRange = intervals.get(index + 1);
                                intervals.remove(index);
                                intervals.remove(index);
                                intervals.set(index - 1, endNextRange);
                                return true;
                        } else if (mergeFirst) {
                                //Replace the value (merge to the previous range)
                                intervals.set(index - 1, value);
                                return true;
                        } else if (mergeSecond) {
                                //Replace the value (merge to the next range)
                                intervals.set(index, value);
                                return true;
                        }
                } else {
                        //If index corresponding to the end of a range
                        //the provided value is in a range
                        return false;
                }
                //New range
                intervals.add(index, new Integer(value));
                intervals.add(index,  new Integer(value));
                return true;                
        }
        
        @Override
        public boolean add(Integer value) {
                return internalAdd(value);
        }

        @Override
        public Iterator<Integer> iterator() {
                return new ValueIterator();
        }

        /**
         * Return the internal container
         *
         * @return intervals ex: 0,0,50,60 for [0] and [50-60]
         */
        public List<Integer> getValueRanges() {
                return Collections.unmodifiableList(intervals);
        }

        @Override
        public Comparator<? super Integer> comparator() {
                return new IntegerComparator();
        }

        @Override
        public SortedSet<Integer> subSet(Integer e, Integer e1) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public SortedSet<Integer> headSet(Integer e) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public SortedSet<Integer> tailSet(Integer e) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Integer first() {
                return intervals.get(0);
        }

        @Override
        public Integer last() {
                return intervals.get(intervals.size()-1);
        }

        @Override
        public int size() {
                Iterator<Integer> it = intervals.iterator();
                int count=0;
                while(it.hasNext()) {
                        count+=-it.next()+(it.next()+1);
                }
                return count;
        }

        @Override
        public boolean contains(Object o) {
                Integer value = (Integer)o;
                int index = Collections.binarySearch(intervals,value );
                if(index>=0) {
                        return true;
                } else {
                        //retrieve the nearest index by order
                        index = -index - 1;     
                        //value < than an end range
                        return index % 2 != 0; 
                }
                
        }

        @Override
        public Object[] toArray() {
                int arraySize = size();
                Object[] values = new Object[size()];
                Iterator<Integer> it = iterator();
                for(int i = 0; i < arraySize; i++) {
                        values[i]=it.next();
                }
                return values;
        }

        @Override
        public <T> T[] toArray(T[] ts) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean containsAll(Collection<?> clctn) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addAll(Collection<? extends Integer> clctn) {
                boolean isAllAdded=true;
                for(Object value : clctn) {
                        if(!(value instanceof Integer)) {
                                return false;
                        }
                        if(!add((Integer)value)) {
                                isAllAdded = false;
                        }
                }
                return isAllAdded;
        }

        @Override
        public boolean retainAll(Collection<?> clctn) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean removeAll(Collection<?> clctn) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void clear() {
                intervals.clear();
        }

        private class ValueIterator implements Iterator<Integer> {

                private Iterator<Integer> it;
                private Integer current = -1;
                private Integer itEnd = 0;

                public ValueIterator() {
                        it = intervals.iterator();
                        if (!intervals.isEmpty()) {
                                current = it.next() - 1;
                                itEnd = it.next() + 1;
                        }
                }

                @Override
                public boolean hasNext() {
                        return current + 1 < itEnd || it.hasNext();
                }

                @Override
                public Integer next() {
                        if (current + 1 < itEnd) {
                                return ++current;
                        } else {
                                current = it.next();
                                itEnd = it.next() + 1;
                                return current;
                        }
                }

                @Override
                public void remove() {
                        throw new UnsupportedOperationException("Not supported yet.");
                }
        }
        
        private class IntegerComparator implements Comparator<Integer> {

                @Override
                public int compare(Integer t, Integer t1) {
                        return (t<t1 ? -1 : (t.equals(t1) ? 0 : 1));
                }
                
        }
}
