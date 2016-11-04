/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2016 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.commons.collections.twoqueue;

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
