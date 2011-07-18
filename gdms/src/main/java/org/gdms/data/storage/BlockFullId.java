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
 * Represents the Id of a block
 * @author Antoine Gourlay
 */
public class BlockFullId {

        /**
         * Creates a new Id
         * @param groupId Id of the group of blocks it belongs to
         * @param blockId Id of this block within the group
         */
        public BlockFullId(int groupId, long blockId) {
                this.groupId = groupId;
                this.blockId = blockId;
        }
        private int groupId = -1;
        private long blockId = -1;

        /**
         * Gets the Id of the group the block belongs to
         * @return the groupId
         */
        public int getGroupId() {
                return groupId;
        }

        /**
         * Gets the Id of the block within the group
         * @return the blockId
         */
        public long getBlockId() {
                return blockId;
        }

        @Override
        public boolean equals(Object obj) {
                if (obj == null) {
                        return false;
                }
                if (getClass() != obj.getClass()) {
                        return false;
                }
                final BlockFullId other = (BlockFullId) obj;
                if (this.groupId != other.groupId) {
                        return false;
                }
                if (this.blockId != other.blockId) {
                        return false;
                }
                return true;
        }

        @Override
        public int hashCode() {
                int hash = 7;
                hash = 53 * hash + this.groupId;
                hash = 53 * hash + (int) (this.blockId ^ (this.blockId >>> 32));
                return hash;
        }
}
