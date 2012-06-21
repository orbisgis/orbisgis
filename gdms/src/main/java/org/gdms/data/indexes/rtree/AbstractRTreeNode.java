/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.data.indexes.rtree;

import java.io.IOException;

import com.vividsolutions.jts.geom.Envelope;

public abstract class AbstractRTreeNode implements RTreeNode {

        private static int nodes = 0;
        private long parentAddress;
        protected String name;
        protected DiskRTree tree;
        protected long address;
        private RTreeInteriorNode parent;

        public AbstractRTreeNode(DiskRTree btree, long address, long parentAddress) {
                this.tree = btree;
                this.address = address;
                this.parentAddress = parentAddress;
                this.name = "node-" + nodes;
                nodes++;
        }

        @Override
        public void setParentAddress(long parentAddress) {
                if (this.parentAddress != parentAddress) {
                        this.parentAddress = parentAddress;
                        this.parent = null;
                }
        }

        @Override
        public RTreeInteriorNode getParent() throws IOException {
                if ((parent == null) && (parentAddress != -1)) {
                        parent = (RTreeInteriorNode) tree.readNodeAt(parentAddress);
                }
                return parent;
        }

        @Override
        public long getParentAddress() {
                return parentAddress;
        }

        @Override
        public long getAddress() {
                return address;
        }

        protected double getExpandImpact(int ref, int i) throws IOException {
                Envelope op1 = new Envelope(getEnvelope(ref));
                double initialArea = op1.getWidth() * op1.getHeight();
                op1.expandToInclude(getEnvelope(i));
                double finalArea = op1.getWidth() * op1.getHeight();
                return finalArea - initialArea;
        }

        protected abstract Envelope getEnvelope(int index) throws IOException;

        protected int getFarthestGeometry(int ref, Envelope refEnvelope)
                throws IOException {
                double maxDistance = Double.NEGATIVE_INFINITY;
                int argmax = -1;
                for (int i = 0; i < getValueCount(); i++) {
                        if (i == ref) {
                                continue;
                        } else {
                                double distance = getEnvelope(i).distance(refEnvelope);
                                if (distance > maxDistance) {
                                        maxDistance = distance;
                                        argmax = i;
                                }

                        }
                }
                return argmax;
        }

        protected double getExpandImpact(Envelope op1, Envelope op2)
                throws IOException {
                double initialArea = op1.getWidth() * op1.getHeight();
                op1.expandToInclude(op2);
                double finalArea = op1.getWidth() * op1.getHeight();
                return finalArea - initialArea;
        }
}
