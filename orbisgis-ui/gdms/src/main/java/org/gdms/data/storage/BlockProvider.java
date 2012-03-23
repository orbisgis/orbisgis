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

import java.io.Closeable;
import java.io.IOException;

/**
 * Base interface for providers of <code>Block</code> objects.
 * @author Antoine Gourlay
 */
public interface BlockProvider extends Closeable {

        /**
         * Gets the common group Id of the provided blocks.
         * @return an id.
         */
        int getGroupId();

        /**
         * Writes a block to the underlying source of blocks.
         * @param b a block
         * @throws IOException if there is a problem while writing
         * @throws IllegalArgumentException if the content of the block is too large
         *      or if the block does not belong to this <code>BlockProvider</code>
         */
        void writeBlock(Block b) throws IOException;

        /**
         * Reads the block that holds the given Id
         * @param blockId the id of the block
         * @return the block
         * @throws IOException if there is a problem while reading
         * @throws IllegalArgumentException if there is no block with that Id
         */
        Block readBlock(long blockId) throws IOException;

        /**
         * Allocates a new block at the end.
         * @return the block
         * @throws IOException if there is a problem while accessing the source
         */
        Block newBlock() throws IOException;

        /**
         * Truncates the source to the given block (included). All blocks with
         * a higher Id are removed
         * @param blockId the Id of a block
         * @throws IOException
         */
        void truncateToBlock(long blockId) throws IOException;

        /**
         * Gets the number of blocks currently present in the source.
         * @return the number of blocks
         */
        long getNumberOfBlocks();
}
