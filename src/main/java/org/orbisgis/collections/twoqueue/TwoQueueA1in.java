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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Special FIFO Linked Queue for use by {@link TwoQueueBuffer}.
 * 
 * @since 2.0
 * @author Antoine Gourlay
 */
final class TwoQueueA1in<I, B> implements Iterable<Entry<I, DoubleQueueValue<I, B>>> {

        private Map<I, DoubleQueueValue<I, B>> map = new HashMap<I, DoubleQueueValue<I, B>>();
        private DoubleQueueValue<I, B> newest;
        private int maxSize;

        TwoQueueA1in(int maxSize) {
                this.maxSize = maxSize;
        }

        /**
         * Gets the size of the queue.
         * @return the number of items in the queue.
         */
        public int size() {
                return map.size();
        }

        /**
         * Checks if the queue is empty.
         * @return true if the queue is empty
         */
        public boolean isEmpty() {
                return map.isEmpty();
        }

        /**
         * Gets the block with the given id
         * @param key the id of a block
         * @return the associated block, or null if it is not found
         */
        public B get(I key) {
                final DoubleQueueValue<I, B> get = map.get(key);
                return get == null ? null : get.val;
        }

        /**
         * Puts a block in this queue.
         * @param b a block to add in this queue
         * @return the block that was removed to add this block, or null
         *      if no removal was necessary
         */
        public DoubleQueueValue<I, B> put(I i, B b) {
                final DoubleQueueValue<I, B> q = new DoubleQueueValue<I, B>(i, b);
                map.put(q.key, q);
                return insertAndTrim(q);
        }

        private DoubleQueueValue<I, B> insertAndTrim(DoubleQueueValue<I, B> v) {
                DoubleQueueValue<I, B> removed = trimOldest();
                insertUpFront(v);
                return removed;
        }
        
        private void insertUpFront(DoubleQueueValue<I, B> v) {
                if (newest != null) {
                        v.previous = newest.previous;
                        v.next = newest;
                        v.previous.next = v;
                        newest.previous = v;
                } else {
                        v.next = v;
                        v.previous = v;
                        
                }
                
                newest = v;
        }

        private DoubleQueueValue<I, B> trimOldest() {
                if (map.size() == maxSize + 1) {
                        DoubleQueueValue<I, B> v = newest.previous;
                        map.remove(v.key);
                        newest.previous = v.previous;
                        newest.previous.next = newest;
                        return v;
                } else {
                        return null;
                }
        }

        /**
         * Clears all elements in this queue.
         */
        public void clear() {
                map.clear();
                newest = null;
        }
        
        void remove(DoubleQueueValue<I, B> v) {
                if (map.isEmpty()) {
                        newest = null;
                } else {
                        v.next.previous = v.previous;
                        v.previous.next = v.next;
                        if (v == newest) {
                                newest = v.next;
                        }
                }
        }

        B remove(I key) {
                DoubleQueueValue<I, B> v = map.remove(key);
                if (v != null) {
                        remove(v);
                        return v.val;
                } else {
                        return null;
                }
        }

        @Override
        public Iterator<Entry<I, DoubleQueueValue<I, B>>> iterator() {
                return map.entrySet().iterator();
        }
}
