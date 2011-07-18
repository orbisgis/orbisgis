/** OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : #name, scientific researcher,
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
package org.gdms.data.storage;

import java.io.IOException;
import org.orbisgis.collections.twoqueue.TwoQueueBuffer;

/**
 * BlockBuffer using the 2Q replacement algorithm.
 * 
 * {@inheritDoc}
 * 
 * @since 2.0
 * @author Antoine Gourlay
 */
public class TwoQueueBlockBuffer extends TwoQueueBuffer<BlockFullId, Block> {

        private BufferManager bm;

        /**
         * Instantiate a new buffer that can hold up to <tt>maxSize</tt> items and
         * that uses the given <code>BufferManager</code> to read/write blocks.
         * @param maxSize the maximum number of loaded blocks
         * @param bufferManager the buffer manager
         */
        public TwoQueueBlockBuffer(int maxSize, BufferManager bufferManager) {
                super(maxSize);
                this.bm = bufferManager;
        }

        @Override
        protected Block reclaim(BlockFullId id) {
                BlockProvider p = bm.getProviderForId(id);
                if (p == null) {
                        throw new IllegalStateException("The given block Id does not correspond to any registered provider.");
                }
                try {
                        return p.readBlock(id.getBlockId());
                } catch (IOException ex) {
                        throw new IllegalStateException(ex);
                }

        }

        @Override
        protected void unload(Block b) {
                BlockProvider p = bm.getProviderForId(b.getId());
                if (p == null) {
                        throw new IllegalStateException("The given block Id does not correspond to any registered provider.");
                }

                if (b.isDirty()) {
                        try {
                                p.writeBlock(b);
                        } catch (IOException ex) {
                                throw new IllegalStateException(ex);
                        }
                }
        }
}
