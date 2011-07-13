/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 * 
 * Team leader : Erwan BOCHER, scientific researcher,
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
 * This file is part of Gdms.
 * 
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://www.orbisgis.org/>
 * 
 * or contact directly:
 * info@orbisgis.org
 */
package org.orbisgis.collections.twoqueue;

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
 * The following paper has been used as reference to implement this buffer:
 * T. Johnson and D. Shasha , "2Q: A Low Overhead High Performance Management Replacement Algorithm", Proceedings of 
 * the 20th VLDB Conference Santiago, Chile, pages 439-450, 1994
 * 
 * @param <I> the key type used to index elements. Must be unique for an element.
 * @param <B> the element type
 * @since 2.0
 * @author Antoine Gourlay
 */
public abstract class TwoQueueBuffer<I, B> {
        
        private TwoQueueA1in<I, B> a1in;
        private TwoQueueA1out<I, B> a1out;
        private TwoQueueAm<I, B> am;

        /**
         * Instantiate a new buffer that can hold up to <tt>maxSize</tt> items.
         * @param maxSize the maximum number of loaded blocks
         */
        public TwoQueueBuffer(int maxSize) {
                final int inBuffer = Math.round(maxSize / 4.0f);
                a1in = new TwoQueueA1in<I, B>(inBuffer);
                a1out = new TwoQueueA1out<I, B>(inBuffer * 2);
                am = new TwoQueueAm<I, B>(maxSize - inBuffer);
        }

        /**
         * Gets the block associated with the id <tt>key</tt>.
         *
         * If the block is not in memory, it is loaded from disk.
         * @param key a block id
         * @return the loaded block.
         * @throws TwoQueueException 
         */
        public B get(I key) throws TwoQueueException {
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

        protected abstract B reclaim(I id) throws TwoQueueException;

        protected abstract void unload(B b) throws TwoQueueException;

        private void put(I i,B b) throws TwoQueueException {
                QueueValue<I, B> v = a1in.put(i, b);
                if (v != null) {
                        // save v out to disk
                        unload(b);

                        // and insert it in Aiout
                        a1out.put(i);
                }
        }

        /**
         * Gets the memory size of this buffer.
         * @return the number of blocks in memory
         */
        public final int size() {
                return a1in.size() + am.size();
        }

        /**
         * Clears all blocks from memory back to disk.
         */
        public final void clear() {
                am.clear();
                a1in.clear();
                a1out.clear();
        }

        /**
         * Checks if this buffer is empty.
         * @return true if there is no blocks in memory
         */
        public final boolean isEmpty() {
                return a1in.isEmpty();
        }
        
}
