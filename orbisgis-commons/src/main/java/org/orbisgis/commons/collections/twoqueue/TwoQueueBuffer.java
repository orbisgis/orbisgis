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
package org.orbisgis.commons.collections.twoqueue;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * The TwoQueueBuffer is a queue-like structure based on the 2Q Replacement algorithm.
 * 
 * To create a buffer based on this class, one must extends <code>TwoQueueBuffer</code> and implements the two
 * methods: 
 *      - <code>reclaim</code>, that the buffer will call when an element has been requested but is not found.
 *      - <code>unload</code>, that the buffer will call when an element is removed from the queue.
 * 
 * This implementation is using the key type <tt>I</tt> as a key for HashMap objects. Thus the keys must have a consistent
 * <code>hashcode</code> value: no two different elements should be have the same key.
 * 
 * The <tt>maxSize</tt> parameter designates the maximum number of elements stored
 * in memory.
 * It is spread like this:
 *      - 25% to the initial <tt>A1in</tt> FIFO list of elements
 *      - 75% to the <tt>Am</tt> LRU list of recently used elements
 * An other 50% of that number determines the number of element keys that are
 * remembered in <tt>A1out</tt>.
 * 
 * WARNING: this buffer is not thread-safe! It must be synchronized externally.
 * Any combination of operations, even concurrent calls to {@link #get(I ) } only will 
 * cause undefined behavior.
 * 
 * The following paper has been used as reference to implement this buffer:
 * T. Johnson and D. Shasha , "2Q: A Low Overhead High Performance Management Replacement Algorithm", Proceedings of 
 * the 20th VLDB Conference Santiago, Chile, pages 439-450, 1994
 * 
 * @param <I> the key type used to index elements. Must be unique for an element.
 * @param <B> the element type
 * @since 2.0
 * @author Antoine Gourlay
 */
public abstract class TwoQueueBuffer<I, B> implements Iterable<DoubleQueueValue<I, B>> {

        private TwoQueueA1in<I, B> a1in;
        private TwoQueueA1out<I, B> a1out;
        private TwoQueueAm<I, B> am;

        /**
         * Instantiate a new buffer that can hold up to <tt>maxSize</tt> items.
         * @param maxSize the maximum number of loaded blocks
         */
        public TwoQueueBuffer(int maxSize) {
                if (maxSize < 8) {
                        throw new IllegalArgumentException("The maximum size cannot be < 8. Found: " + maxSize);
                }
                final int inBuffer = Math.round(maxSize / 4.0f);
                a1in = new TwoQueueA1in<I, B>(inBuffer);
                a1out = new TwoQueueA1out<I, B>(inBuffer * 2);
                am = new TwoQueueAm<I, B>(maxSize - inBuffer);
        }

        /**
         * Gets the block associated with the id <tt>key</tt>.
         *
         * If the block is not in memory, it is requested for loading using the {@link #reclaim(I } method.
         * @param key a block id
         * @return the loaded block.
         */
        public B get(I key) {
                // check Am
                B b = am.get(key);
                if (b != null) {
                        return b;
                }

                // check A1out

                if (a1out.retrieve(key)) {
                        // it was in a1out!!
                        // reclaim b from disk
                        b = reclaim(key);
                        // place it in Am
                        DoubleQueueValue<I, B> q = am.put(key, b);
                        if (q != null) {
                                // q got ejected
                                // save q to disk
                                unload(q.val);
                        }
                        
                        return b;
                }

                // check A1in

                b = a1in.get(key);
                if (b != null) {
                        return b;
                }

                // else, it is nowhere
                // reclaim b from disk
                b = reclaim(key);

                // and add it to the buffer;
                put(key, b);

                return b;
        }

        /**
         * This method is called when the Buffer needs the element with the specified id.
         * @param id an identifier
         * @return the element with this identifier
         */
        protected abstract B reclaim(I id);

        /**
         * This method is called when the buffer needs to get rid of the element b.
         * @param b an element
         */
        protected abstract void unload(B b);

        private void put(I i, B b) {
                DoubleQueueValue<I, B> v = a1in.put(i, b);
                if (v != null) {
                        // save v out to disk
                        unload(v.val);

                        // and insert it in Aiout
                        a1out.put(v.key);
                }
        }

        /**
         * Gets the memory size of this buffer.
         * @return the number of blocks in memory
         */
        public int size() {
                return a1in.size() + am.size();
        }

        /**
         * Clears all blocks from memory back to disk.
         */
        public void clear() {
                am.clear();
                a1in.clear();
                a1out.clear();
        }
        
        /**
         * Checks if this buffer is empty.
         * @return true if there is no blocks in memory
         */
        public boolean isEmpty() {
                return a1in.isEmpty() && am.isEmpty();
        }

        /**
         * Removes the element with the key {@code key }, if it has been loaded.
         * @param key a key
         * @return true if the element was found & unloaded, false if it was not loaded
         */
        public boolean remove(I key) {
                B b = am.remove(key);
                if (b != null) {
                        unload(b);
                        return true;
                } else {
                        b = a1in.remove(key);
                        if (b != null) {
                                unload(b);
                                return true;
                        } else {
                                return false;
                        }
                }
        }

        /**
         * Gets an iterator over the key/value pairs of elements loaded in this buffer.
         * 
         * The method {@link Iterator#remove() } is supported and works as expected.
         * 
         * @return 
         */
        @Override
        public Iterator<DoubleQueueValue<I, B>> iterator() {
                return new TwoQueueIterator();
        }
        
        

        private final class TwoQueueIterator implements Iterator<DoubleQueueValue<I, B>> {

                boolean changed = false;
                private Iterator<Entry<I, DoubleQueueValue<I, B>>> it;
                private DoubleQueueValue<I, B> next;

                private TwoQueueIterator() {
                        it = am.iterator();
                }

                @Override
                public boolean hasNext() {
                        if (it.hasNext()) {
                                return true;
                        } else if (changed) {
                                return false;
                        } else {
                                it = a1in.iterator();
                                changed = true;
                                return it.hasNext();

                        }
                }

                @Override
                public DoubleQueueValue<I, B> next() {
                        if (!changed && !it.hasNext()) {
                                it = a1in.iterator();
                                changed = true;
                        }
                        next = it.next().getValue();
                        return next;
                }

                @Override
                public void remove() {
                        it.remove();
                        if (!changed) {
                                am.remove(next);
                        } else {
                                a1in.remove(next);
                        }
                        unload(next.val);
                }
        }
}
