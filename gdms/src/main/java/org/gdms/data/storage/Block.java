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

/**
 * In-memory representation of a block of data
 * @author Antoine Gourlay
 */
public class Block {

        private BlockFullId id;
        private byte[] content;
        private int blockMaxSize = -1;
        private boolean dirty = false;

        /**
         * Creates a new block with the specified ID
         * @param id
         * @param maxSize
         */
        public Block(BlockFullId id, int maxSize) {
                this.id = id;
                this.blockMaxSize = maxSize;
        }

        /**
         * Creates a new block with the specified ID and content.
         *
         * The maximum size is set to the length of the <tt>content</tt> arg.
         * @param id
         * @param content
         */
        public Block(BlockFullId id, byte[] content) {
                this.id = id;
                this.content = content;
                this.blockMaxSize = content.length;
        }

        /**
         * Gets the id of this block
         * @return the id of the block
         */
        public BlockFullId getId() {
                return id;
        }

        /**
         * Gets the content of this block
         * @return the content of the block
         */
        public byte[] getContent() {
                return content;
        }

        /**
         * Sets the content of this file
         * @param content the content to set
         */
        public void setContent(byte[] content) {
                if (content.length > blockMaxSize) {
                        throw new IllegalArgumentException("Byte array too large. Found length: " + content.length
                                + " ; Expected < " + blockMaxSize);
                }
                this.dirty = true;
                this.content = content;
        }

        /**
         * Gets the memory state of the block.
         *
         * If true, the block has been modified in memory
         * but not flushed to disk yet.
         *
         * @return the dirty flag
         */
        public boolean isDirty() {
                return dirty;
        }

        @Override
        public int hashCode() {
                return 17 * id.hashCode();
        }

        /**
         * Indicates whether an object is equal to this block.
         *
         * Note : two blocks are equals if they have the same FullId. They do NOT
         * need to have the same content. This ensures that a Set of blocks does
         * not contains different block objects for the same disk block.
         *
         * @param obj {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
                if (obj == null) {
                        return false;
                }
                if (getClass() != obj.getClass()) {
                        return false;
                }
                final Block other = (Block) obj;
                if (!id.equals(other.id)) {
                        return false;
                }
                return true;
        }

        /**
         * Returns the actual size of the content of the block.
         *
         * Note that if this block was read from disk, the content is exactly
         * the size of the fixed block size of the <code>BlockProvider</code>
         * it comes from. If fewer bytes were stored, they were padded with empty
         * (not necessarily all 0s) bytes to match the fixed block size.
         * However in any other case, this size can be smaller than the block size.
         *
         * @return the actual size of the content of this block.
         */
        public int getSize() {
                return Math.min(content.length, blockMaxSize);
        }
}
