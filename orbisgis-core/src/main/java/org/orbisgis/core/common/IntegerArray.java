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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Nicolas Fortin
 */
public class IntegerArray implements List<Integer> {

        private final static float BUFFER_SIZE_FACTOR = 1.5f;
        private int[] intervals = new int[0];
        private int size = 0;

        public int getSize() {
                return size;
        }

        private static int getBufferedSize(int targetSize) {
                return Math.round((targetSize - 1) * BUFFER_SIZE_FACTOR);
        }

        private void add(int value) {
                insert(size, value);
        }

        public boolean isEmpty() {
                return size==0;
        }
        
        private void insert(int index, int value) {
                if (size == intervals.length) {
                }
        }

        private void remove(int index) {
                if (index >= size) {
                        throw new ArrayIndexOutOfBoundsException("Index out of bound " + index + " >= " + size);
                }
                if (index < size - 1) {
                        int[] destIntervals = new int[getBufferedSize(size - 1)];
                        System.arraycopy(intervals, index + 1, destIntervals, index, size - index);
                }
                size--;
        }

        @Override
        public int size() {
                return size;
        }

        @Override
        public boolean contains(Object o) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Iterator<Integer> iterator() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Object[] toArray() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> T[] toArray(T[] ts) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean add(Integer e) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean remove(Object o) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean containsAll(Collection<?> clctn) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addAll(Collection<? extends Integer> clctn) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addAll(int i, Collection<? extends Integer> clctn) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean removeAll(Collection<?> clctn) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean retainAll(Collection<?> clctn) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void clear() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Integer get(int i) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Integer set(int i, Integer e) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void add(int i, Integer e) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Integer remove(int i) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int indexOf(Object o) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int lastIndexOf(Object o) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ListIterator<Integer> listIterator() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ListIterator<Integer> listIterator(int i) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<Integer> subList(int i, int i1) {
                throw new UnsupportedOperationException("Not supported yet.");
        }
}
