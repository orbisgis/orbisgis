/**
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
package org.orbisgis.collections.twoqueue;

/**
 * This is a synchronized implementation of the TwoQueueBuffer structure.
 * 
 * See {@link TwoQueueBuffer } for more information on the structure.
 * 
 * It is required that users synchronized on the instance of this class itself when iterating over the elements
 * of the buffer using {@link #iterator() }. If not the behavior is undefined and probably bad.
 * 
 * @param <I> the type of the keys
 * @param <B> the type of the elements
 * @author Antoine Gourlay
 */
public abstract class SynchronizedTwoQueueBuffer<I, B> extends TwoQueueBuffer<I, B> {

        /**
         * Instantiate a new buffer that can hold up to <tt>maxSize</tt> items.
         * @param maxSize the maximum number of loaded blocks
         */
        public SynchronizedTwoQueueBuffer(int maxSize) {
                super(maxSize);
        }

        @Override
        public void clear() {
                synchronized (this) {
                        super.clear();
                }
        }

        @Override
        public B get(I key) {
                synchronized (this) {
                        return super.get(key);
                }
        }

        @Override
        public boolean isEmpty() {
                synchronized (this) {
                        return super.isEmpty();
                }
        }

        @Override
        public int size() {
                synchronized (this) {
                        return super.size();
                }
        }

        @Override
        public boolean remove(I key) {
                synchronized (this) {
                        return super.remove(key);
                }
        }
}
