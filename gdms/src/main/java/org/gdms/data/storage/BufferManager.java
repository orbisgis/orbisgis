/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
package org.gdms.data.storage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Class the handles the available in-memory buffer for data-blocks
 * @author Antoine Gourlay
 */
public final class BufferManager {

        private TwoQueueBlockBuffer blocks = new TwoQueueBlockBuffer(10000, this);
        private Map<Integer, BlockProvider> providers = new HashMap<Integer, BlockProvider>();
        private Set<BlockFullId> pinned = new HashSet<BlockFullId>();
        private long totalAvailableBytes = Long.MAX_VALUE;
        private static final Logger LOG = Logger.getLogger(BufferManager.class);

        /**
         * Creates a buffer manager with (if possible) the given buffer size.
         * @param bufferSize the buffer size, in bytes.
         */
        public BufferManager(long bufferSize) {
                totalAvailableBytes = bufferSize;
                init();
                LOG.info("Initialized with a buffer of " + totalAvailableBytes + " bytes.");
        }

        /**
         * Creates a buffer manager with a default size of 100 MB.
         */
        public BufferManager() {
                this(100 * 1024 * 1024); // 100 MB
        }

        private void init() {

                // worst case: 50 MB
                if (getBufferSize() < 50 * 1024 * 1024) {
                        totalAvailableBytes = 50 * 1024 * 1024;
                } else {
                        Runtime r = Runtime.getRuntime();
                        long available = r.freeMemory() + (r.maxMemory() - r.totalMemory());
                        if (getBufferSize() > available * 0.75) {
                                totalAvailableBytes = available / 2;
                        }
                }
        }

        public BlockProvider getProviderForId(BlockFullId id) {
                return providers.get(id.getGroupId());
        }

        /**
         * Gets the block with the given Id
         * @param id an id
         * @return the associated block
         * @throws BufferManagementException if there is a problem with a <code>BlockProvider</code>
         * @throws IllegalArgumentException if the block is not in cache and there is no
         *      provider associated with its groupId.
         */
        public Block getBlock(BlockFullId id) {
                return blocks.get(id);
        }

        public void saveBlock(Block b) throws IOException {
                BlockProvider p = providers.get(b.getId().getGroupId());
                if (p == null) {
                        throw new IllegalArgumentException("The given Id does not correspond to any registered provider.");
                }
                p.writeBlock(b);
        }

        /**
         * Pins a block into memory. Such a bloc cannot be removed from cached
         * as long as it is pinned.
         * @param id a block id
         */
        public void pinBlock(BlockFullId id) {
                pinned.add(id);
        }

        /**
         * Unpins the block. It can be reclaimed and cleared if memory is need.
         *
         * This method does nothing if the block is not actually pinned.
         *
         * @param id a block id
         */
        public void unPinBlock(BlockFullId id) {
                pinned.remove(id);
        }

        /**
         * Checks whether the block is pinned of not
         * @param id a block id
         * @return true if the block is pinned, false in any other case.
         */
        public boolean isPinned(BlockFullId id) {
                return pinned.contains(id);
        }

        /**
         * Registers a block provider. This provider will be called it a block is
         * needed or needs to be flushed to its source.
         * @param pr
         */
        public void registerBlockProvider(BlockProvider pr) {
                providers.put(pr.getGroupId(), pr);
        }

        /**
         * Unregisters a block provider.
         * @param pr
         */
        public void unRegisterBlockProvider(BlockProvider pr) {
                providers.remove(pr.getGroupId());
        }

        /**
         * Gets the size of the memory buffer
         * @return the size of the cache in bytes
         */
        public long getBufferSize() {
                return totalAvailableBytes;
        }
}
