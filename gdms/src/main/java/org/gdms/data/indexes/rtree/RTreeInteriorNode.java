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
/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer,
Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT
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
package org.gdms.data.indexes.rtree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import org.gdms.data.indexes.tree.IndexVisitor;

/**
 * @author Fernando Gonzalez Cortes
 *
 */
public class RTreeInteriorNode extends AbstractRTreeNode {

        private List<ChildReference> children;
        private Envelope envelope = null;

        public RTreeInteriorNode(DiskRTree tree, long address, long parentAddress) {
                super(tree, address, parentAddress);
                // one place for overload management
                children = new ArrayList<ChildReference>(tree.getN() + 1);
        }

        public RTreeInteriorNode(DiskRTree tree, long address, long parentAddress,
                RTreeNode left, RTreeNode right) throws IOException {
                this(tree, address, parentAddress);
                addChild(left);
                addChild(right);
        }

        /**
         * Add a node to this' children.
         * @param node
         */
        private void addChild(RTreeNode node) {
                children.add(new ChildReference(tree, node));
                if (node != null) {
                        node.setParentAddress(address);
                }
        }

        /**
         * Insert the node node at the index i.
         * @param i
         * @param node
         */
        private void insertChild(int i, RTreeNode node) {
                children.add(i, new ChildReference(tree, node));
                if (node != null) {
                        node.setParentAddress(address);
                }
        }

        /**
         * This element is not a leaf. Return false.
         * @return
         */
        @Override
        public boolean isLeaf() {
                return false;
        }

        @Override
        public RTreeNode splitNode() throws IOException {
                RTreeInteriorNode m = tree.createInteriorNode(address, getParentAddress());

                // Get a reference to sort the nodes
                Envelope ref = getEnvelope(0);
                int refIndex = getFarthestGeometry(0, ref);
                ref = getEnvelope(refIndex);

                // Sort nodes by its distance
                TreeSet<ChildReferenceDistance> distances = new TreeSet<ChildReferenceDistance>(
                        new DistanceComparator());
                for (int i = 0; i < children.size(); i++) {
                        distances.add(new ChildReferenceDistance(i, ref.distance(getEnvelope(i))));
                }
                ArrayList<ChildReference> sortedChildren = new ArrayList<ChildReference>();
                for (ChildReferenceDistance childReferenceDistance : distances) {
                        sortedChildren.add(children.get(childReferenceDistance.childIndex));
                }

                // Add the minimum to the left node
                Envelope leftEnv = null;
                children.clear();
                int leftIndex = 0;
                while (!validIfNotRoot(children.size())) {
                        ChildReference child = sortedChildren.get(leftIndex);
                        child.resolve();
                        children.add(child);
                        leftIndex++;
                        if (leftEnv == null) {
                                leftEnv = new Envelope(child.getEnvelope());
                        } else {
                                leftEnv.expandToInclude(child.getEnvelope());
                        }
                }

                // Add the minimum to the right node
                Envelope rightEnv = null;
                m.children.clear();
                int rightIndex = sortedChildren.size() - 1;
                while (!validIfNotRoot(m.children.size())) {
                        ChildReference child = sortedChildren.get(rightIndex);
                        m.children.add(child);
                        child.resolve();
                        child.getReference().setParentAddress(m.address);
                        rightIndex--;
                        if (rightEnv == null) {
                                rightEnv = new Envelope(child.getEnvelope());
                        } else {
                                rightEnv.expandToInclude(child.getEnvelope());
                        }
                }

                // Insert the remaining children in the first node until the impact is
                // greater than inserting in the second one
                int index = leftIndex;
                while ((index <= rightIndex)
                        && (getExpandImpact(leftEnv, sortedChildren.get(index).getEnvelope()) < getExpandImpact(rightEnv,
                        sortedChildren.get(index).getEnvelope()))) {
                        ChildReference child = sortedChildren.get(index);
                        children.add(child);
                        leftEnv.expandToInclude(child.getEnvelope());
                        index++;
                }
                this.envelope = null;

                // Insert the remaining in m
                for (int i = index; i <= rightIndex; i++) {
                        ChildReference child = sortedChildren.get(index);
                        child.resolve();
                        child.getReference().setParentAddress(m.address);
                        m.children.add(child);
                }
                m.envelope = null;
                return m;
        }

        /**
         * get the child stored at index i.
         * @param i
         * @return
         * @throws IOException
         */
        private RTreeNode getChild(int i) throws IOException {
                if (!children.get(i).isLoaded()) {
                        children.get(i).resolve();
                }
                return children.get(i).getReference();
        }

        private void insertValueAndReferenceAfter(RTreeNode refNode, RTreeNode node)
                throws IOException {
                // Look the place to insert the new value
                int index = getIndexOf(refNode);

                // insert at index
                insertChild(index + 1, node);
                node.setParentAddress(address);
        }

        /**
         * {@inheritDoc }
         * @param v {@inheritDoc }
         * @param rowIndex {@inheritDoc }
         * @return the inserted Envelope, i.e. <tt>v</tt>
         * @throws IOException
         */
        @Override
        public Envelope insert(Envelope v, int rowIndex) throws IOException {
                // See the children that contain the geometry
                for (int i = 0; i < children.size(); i++) {
                        if (getEnvelope(i).contains(v)) {
                                doInsert(v, rowIndex, i);
                                return v;
                        }
                }

                // Take the child with less impact
                double min = Double.MAX_VALUE;
                int argmin = -1;
                for (int i = 0; i < children.size(); i++) {
                        Envelope test = new Envelope(getEnvelope(i));
                        double initialArea = test.getWidth() * test.getHeight();
                        test.expandToInclude(v);
                        double finalArea = test.getWidth() * test.getHeight();
                        double diff = finalArea - initialArea;
                        if (diff < min) {
                                argmin = i;
                                min = diff;
                        }
                }
                doInsert(v, rowIndex, argmin);
                return v;
        }

        private void doInsert(Envelope v, int rowIndex, int i) throws IOException {
                getChild(i).insert(v, rowIndex);
                Envelope newEnvelope = getChild(i).getEnvelope();
                getEnvelope().expandToInclude(newEnvelope);
                children.get(i).getEnvelope().expandToInclude(newEnvelope);
                if (!getChild(i).isValid()) {
                        // If it's invalid the parent will split it
                        RTreeNode newNode = getChild(i).splitNode();
                        newNode.setParentAddress(address);
                        insertValueAndReferenceAfter(getChild(i), newNode);
                        children.get(i).setEnvelope(null);
                        children.get(i + 1).setEnvelope(null);
                        invalidateEnvelope();
                        // If this node is the root we need a new one
                        if (!isValid() && (getParentAddress() == -1)) {
                                RTreeNode m = splitNode();
                                tree.createInteriorNode(address, -1, this, m);
                        }
                }
        }

        private void invalidateEnvelope() throws IOException {
                envelope = null;
                if (getParentAddress() != -1) {
                        getParent().invalidateEnvelope();
                }
        }

        public int getRow(Envelope value) {
                throw new UnsupportedOperationException("Cannot get the row "
                        + "in an interior node");
        }

        @Override
        public String toString() {
                try {

                        StringBuilder strValues = new StringBuilder("");
                        String separator = "";
                        for (int i = 0; i < children.size(); i++) {
                                Envelope v = getEnvelope(i);
                                strValues.append(separator).append(v);
                                separator = ", ";
                        }
                        StringBuilder strChilds = new StringBuilder("");
                        separator = "";
                        for (int i = 0; i < children.size(); i++) {
                                RTreeNode v = getChild(i);
                                if (v != null) {
                                        strChilds.append(separator).append(
                                                ((AbstractRTreeNode) v).name);
                                } else {
                                        strChilds.append(separator).append("null");
                                }
                                separator = ", ";
                        }

                        StringBuilder ret = new StringBuilder();
                        ret.append(name).append("\n (").append(strValues.toString());
                        ret.append(") \n(").append(strChilds).append(")\n");
                        for (int i = 0; i < children.size(); i++) {
                                RTreeNode node = getChild(i);
                                if (node != null) {
                                        ret.append(node.toString());
                                }
                        }

                        return ret.toString();
                } catch (IOException e) {
                        throw new IllegalStateException(e);
                }
        }

        /**
         * Gets the index of the leaf in the children array
         *
         * @param node
         * @return -1 if the node is not present
         * @throws IOException
         */
        private int getIndexOf(RTreeNode node) throws IOException {
                int childIndex = -1;
                for (int i = 0; i < children.size() + 1; i++) {
                        if (getChild(i) == node) {
                                childIndex = i;
                                break;
                        }
                }
                return childIndex;
        }

        /**
         * Merges the node with one of its neighbors.
         *
         * @param node
         * @return true if we can merge. False otherwise
         * @throws IOException
         */
        public boolean mergeWithNeighbour(RTreeNode node) throws IOException {
                int index = getIndexOf(node);
                RTreeNode smallest = null;
                RTreeNode rightNeighbour = getRightNeighbour(index);
                RTreeNode leftNeighbour = getLeftNeighbour(index);

                if ((rightNeighbour != null) && (leftNeighbour != null)) {
                        smallest = rightNeighbour;
                        if (leftNeighbour.getValueCount() < rightNeighbour.getValueCount()) {
                                smallest = leftNeighbour;
                        }
                } else if (rightNeighbour != null) {
                        smallest = rightNeighbour;
                } else if (leftNeighbour != null) {
                        smallest = leftNeighbour;
                } else {
                        return false;
                }
                if (smallest == leftNeighbour) {
                        node.mergeWithLeft(leftNeighbour);
                        // Remove the pointer to the left neighbour
                        children.remove(index - 1);
                        children.get(index - 1).setEnvelope(null);
                } else {
                        node.mergeWithRight(rightNeighbour);
                        // Remove the pointer to the right neighbour
                        children.remove(index + 1);
                        children.get(index).setEnvelope(null);
                }

                return true;
        }

        @Override
        public void mergeWithRight(RTreeNode rightNode) throws IOException {
                RTreeInteriorNode node = checkTreeNode(rightNode);
                ArrayList<ChildReference> newChildren = new ArrayList<ChildReference>();
                newChildren.addAll(children);
                // Change the parent in the moving children
                for (int i = 0; i < node.children.size(); i++) {
                        node.getChild(i).setParentAddress(address);
                }
                newChildren.addAll(node.children);

                children = newChildren;

                tree.removeNode(node.address);
        }

        private RTreeInteriorNode checkTreeNode(RTreeNode node) {
                if (node instanceof RTreeInteriorNode) {
                        return (RTreeInteriorNode) node;
                }
                throw new IllegalArgumentException("The given node is not of type RTreeInteriorNode.");
        }

        @Override
        public void mergeWithLeft(RTreeNode leftNode) throws IOException {
                RTreeInteriorNode node = checkTreeNode(leftNode);
                ArrayList<ChildReference> newChildren = new ArrayList<ChildReference>();
                // Change the parent in the moving children
                for (int i = 0; i < node.children.size(); i++) {
                        node.getChild(i).setParentAddress(address);
                }
                newChildren.addAll(node.children);
                newChildren.addAll(children);

                children = newChildren;

                tree.removeNode(node.address);
        }

        /**
         * Selects the neighbor and moves the nearest element into this node if it
         * is possible
         *
         * @param index
         * @return true if it is possible to move from a neighbor and false
         *         otherwise
         * @throws IOException
         */
        public boolean moveFromNeighbour(int index) throws IOException {
                RTreeNode rightNeighbour = getRightNeighbour(index);
                RTreeNode leftNeighbour = getLeftNeighbour(index);
                if ((rightNeighbour != null) && (rightNeighbour.canGiveElement())) {
                        rightNeighbour.moveFirstTo(getChild(index));
                        children.get(index + 1).setEnvelope(null);
                        children.get(index).setEnvelope(null);
                        return true;
                } else if ((leftNeighbour != null) && (leftNeighbour.canGiveElement())) {
                        leftNeighbour.moveLastTo(getChild(index));
                        children.get(index - 1).setEnvelope(null);
                        children.get(index).setEnvelope(null);
                        return true;
                } else {
                        return false;
                }
        }

        @Override
        public void moveFirstTo(RTreeNode node) throws IOException {
                RTreeInteriorNode n = checkTreeNode(node);
                RTreeNode first = getChild(0);
                children.remove(0);
                n.addChild(first);
                invalidateEnvelope();
        }

        @Override
        public void moveLastTo(RTreeNode node) throws IOException {
                RTreeInteriorNode n = checkTreeNode(node);
                n.insertChild(0, this.getChild(children.size() - 1));
                children.remove(children.size() - 1);
                invalidateEnvelope();
        }

        /**
         * Gets the neighbor on the left of the specified node
         *
         * @param index
         * @return
         * @throws IOException
         */
        private RTreeNode getLeftNeighbour(int index) throws IOException {
                if (index > 0) {
                        return getChild(index - 1);
                } else {
                        return null;
                }
        }

        /**
         * Gets the neighbor on the right of the specified node
         *
         * @param index
         * @return
         * @throws IOException
         */
        private RTreeNode getRightNeighbour(int index) throws IOException {
                if (index < children.size() - 1) {
                        return getChild(index + 1);
                } else {
                        return null;
                }
        }

        /**
         * Check whether this node is valid or not. It is valid if and only if
         *  - It is the root, and its number of children is greater than 1 and lesser than n
         *  - It is not the root, and its number of children is between (n+1)/2 and n.
         * Where n is the order of the tree.
         * @return
         * @throws IOException
         */
        @Override
        public boolean isValid() throws IOException {
                return isValid(children.size());
        }

        /**
         * This node can give one of its children if and only if it is still a
         * valid node with one child less.
         * @return
         * @throws IOException
         */
        @Override
        public boolean canGiveElement() throws IOException {
                return isValid(children.size() - 1);
        }

        /**
         * Check whether this node is valid or not. It is valid if and only if
         *  - It is the root, and valuecount of children is greater than 1 and lesser than n
         *  - It is not the root, and valueCount is between (n+1)/2 and n.
         * Where n is the order of the tree.
         * @param valueCount
         * @return
         * @throws IOException
         */
        public boolean isValid(int valueCount) throws IOException {
                if (getParentAddress() == -1) {
                        return (valueCount >= 1) && (valueCount <= tree.getN());
                } else {
                        return validIfNotRoot(valueCount);
                }
        }

        private boolean validIfNotRoot(int valueCount) {
                return (valueCount >= ((tree.getN() + 1) / 2))
                        && (valueCount <= tree.getN());
        }

        @Override
        public void checkTree() throws IOException {
                if (!isValid()) {
                        throw new IllegalStateException(this + " Not vaylid");
                } else {
                        for (int i = 0; i < children.size(); i++) {
                                Envelope test = children.get(i).getEnvelope();
                                children.get(i).setEnvelope(null);
                                if (!test.equals(children.get(i).getEnvelope())) {
                                        throw new IllegalStateException("Bad local envelope" + test);
                                }

                                if (getChild(i).getParent() != this) {
                                        throw new IllegalStateException(this + " parent is wrong");
                                }
                                getChild(i).checkTree();

                                if (!getChild(i).getEnvelope().equals(getEnvelope(i))) {
                                        throw new IllegalStateException("bad local envelope");
                                }
                        }
                        Envelope global = new Envelope(getChild(0).getEnvelope());
                        for (int i = 1; i < children.size(); i++) {
                                global.expandToInclude(getChild(i).getEnvelope());
                        }
                        if (!global.equals(getEnvelope())) {
                                throw new IllegalStateException("Bad global envelope");
                        }
                }
        }

        @Override
        public boolean delete(Envelope v, int row) throws IOException {
                // Look for the children that can contain the node
                for (int i = 0; i < children.size(); i++) {
                        if (getEnvelope(i).contains(v) && getChild(i).delete(v, row)) {
                                children.get(i).setEnvelope(null);
                                invalidateEnvelope();
                                if (!getChild(i).isValid() && !moveFromNeighbour(i)) {
                                        // move from neighbour
                                        if (!mergeWithNeighbour(getChild(i))) {
                                                // If we cannot merge create new root
                                                tree.removeNode(this.address);
                                                getChild(0).setParentAddress(-1);
                                        }

                                }
                                return true;

                        }
                }

                return false;
        }

        @Override
        public byte[] getBytes() throws IOException {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);

                // Write the number of values
                dos.writeInt(children.size());

                // Write the children direction
                if (!children.isEmpty()) {
                        for (int i = 0; i < children.size(); i++) {
                                dos.writeLong(children.get(i).getAddress());
                                Envelope theEnvelope = children.get(i).getEnvelope();
                                dos.writeDouble(theEnvelope.getMinX());
                                dos.writeDouble(theEnvelope.getMinY());
                                dos.writeDouble(theEnvelope.getMaxX());
                                dos.writeDouble(theEnvelope.getMaxY());
                        }
                }

                dos.close();

                return bos.toByteArray();
        }

        public static AbstractRTreeNode createInteriorNodeFromBytes(DiskRTree tree,
                long address, long parentAddress, int n, byte[] bytes) throws IOException {
                RTreeInteriorNode ret = new RTreeInteriorNode(tree, address, parentAddress);
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                DataInputStream dis = new DataInputStream(bis);

                // Read the number of values
                int valueCount = dis.readInt();

                // Read the children locations
                for (int i = 0; i < valueCount; i++) {
                        long nodeDir = dis.readLong();
                        double minx = dis.readDouble();
                        double miny = dis.readDouble();
                        double maxx = dis.readDouble();
                        double maxy = dis.readDouble();
                        Envelope env = new Envelope(new Coordinate(minx, miny),
                                new Coordinate(maxx, maxy));
                        ret.children.add(new ChildReference(tree, nodeDir, env));
                }

                dis.close();

                return ret;
        }

        private static class ChildReference {

                private RTreeNode object;
                private long address;
                private DiskRTree tree;
                private Envelope envelope;

                ChildReference(DiskRTree tree, RTreeNode object) {
                        this.tree = tree;
                        this.object = object;
                        if (object == null) {
                                this.address = -1;
                        } else {
                                this.address = ((AbstractRTreeNode) object).address;
                        }
                }

                public void setEnvelope(Envelope envelope) {
                        this.envelope = envelope;
                }

                ChildReference(DiskRTree tree, long dir, Envelope envelope) {
                        this.tree = tree;
                        this.address = dir;
                        this.object = null;
                        this.envelope = envelope;
                }

                public RTreeNode getReference() {
                        return object;
                }

                public boolean isLoaded() {
                        return object != null;
                }

                public void resolve() throws IOException {
                        if (address == -1) {
                                object = null;
                        } else {
                                object = tree.readNodeAt(address);
                        }
                }

                public long getAddress() {
                        return address;
                }

                public Envelope getEnvelope() throws IOException {
                        if (envelope == null) {
                                resolve();
                                envelope = getReference().getEnvelope();
                        }
                        return envelope;
                }
        }

        @Override
        public void save() throws IOException {
                tree.writeNodeAt(address, this);

                for (int i = 0; i < children.size(); i++) {
                        ChildReference childRef = children.get(i);
                        if (childRef.isLoaded()) {
                                childRef.getReference().save();
                        }
                }
        }

        @Override
        public Envelope getEnvelope() throws IOException {
                if (envelope == null) {
                        envelope = new Envelope(getChild(0).getEnvelope());
                        for (int i = 1; i < children.size(); i++) {
                                envelope.expandToInclude(getChild(i).getEnvelope());
                        }
                }

                return envelope;
        }

        @Override
        public Envelope[] getAllValues() throws IOException {
                ArrayList<Envelope> ret = new ArrayList<Envelope>();
                for (int i = 0; i < children.size(); i++) {
                        Envelope[] temp = getChild(i).getAllValues();
                        ret.addAll(Arrays.asList(temp));
                }

                return ret.toArray(new Envelope[ret.size()]);
        }

        @Override
        public int[] query(Envelope value) throws IOException {
                ArrayList<int[]> childrenResult = new ArrayList<int[]>();
                int size = 0;
                for (int i = 0; i < children.size(); i++) {
                        if (value.intersects(getEnvelope(i))) {
                                int[] rows = getChild(i).query(value);
                                size += rows.length;
                                childrenResult.add(rows);
                        }
                }

                int[] ret = new int[size];
                int currentPos = 0;
                for (int i = 0; i < childrenResult.size(); i++) {
                        int[] childRows = childrenResult.get(i);
                        System.arraycopy(childRows, 0, ret, currentPos, childRows.length);
                        currentPos += childRows.length;
                }

                return ret;
        }
        
        @Override
        public void query(Envelope value, IndexVisitor<Envelope> v) throws IOException {
                for (int i = 0; i < children.size(); i++) {
                        if (value.intersects(getEnvelope(i))) {
                                getChild(i).query(value, v);
                        }
                }
        }

        @Override
        public RTreeNode getNewRoot() throws IOException {
                if (getParentAddress() != -1) {
                        return getParent().getNewRoot();
                } else {
                        if (getChild(0).getParent() == null) {
                                return getChild(0).getNewRoot();
                        } else {
                                return this;
                        }
                }
        }

        @Override
        public int getValueCount() {
                return children.size();
        }

        @Override
        protected Envelope getEnvelope(int index) throws IOException {
                return children.get(index).getEnvelope();
        }

        @Override
        public void updateRows(int row, int inc) throws IOException {
                for (int i = 0; i < children.size(); i++) {
                        getChild(i).updateRows(row, inc);
                }
        }
}
