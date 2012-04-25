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
package org.gdms.data.indexes.btree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gdms.data.indexes.tree.IndexVisitor;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;

public class BTreeLeaf extends AbstractBTreeNode {

        private List<Integer> rows;

        /**
         * Create a new BTreeLeaf. tree is the parent tree, dir is the adress of this object in
         * the file and parentDir is the adress of the parent node.
         * @param tree
         * @param dir
         * @param parentAddress
         */
        public BTreeLeaf(DiskBTree tree, long dir, long parentAddress) {
                super(tree, dir, parentAddress);
                rows = new ArrayList<Integer>();
        }

        /**
         * Insert the value v at the index rowIndex in the list ov values
         * associated to this object.
         * @param v
         * @param rowIndex
         * @return
         * @throws IOException
         */
        @Override
        public Value insert(Value v, int rowIndex) throws IOException {
                int index = getIndexOf(v);

                // insert in index
                Value ret = null;
                values.add(index, v);
                rows.add(index, rowIndex);
                if (index == 0) {
                        ret = v;
                } else if (values.get(index - 1).isNull()) {
                        ret = v;
                }

                if (!isValid() && (getParentAddress() == -1)) {
                        // new root
                        tree.createInteriorNode(address, -1, this, splitNode());
                }

                return ret;
        }

        @Override
        public boolean isLeaf() {
                return true;
        }

        @Override
        public String toString() {
                StringBuilder strValues = new StringBuilder("");
                String separator = "";
                for (int i = 0; i < values.size(); i++) {
                        Value v = values.get(i);
                        strValues.append(separator).append(v);
                        separator = ", ";
                }

                return name + " (" + strValues.toString() + ") ";
        }

        @Override
        public Value getSmallestValueNotIn(BTreeNode treeNode) throws IOException {
                for (int i = 0; i < values.size(); i++) {
                        if (!(treeNode.contains(values.get(i)))) {
                                return values.get(i);
                        } else if (values.get(values.size() - 1).equals(values.get(i)).getAsBoolean()) {
                                return ValueFactory.createNullValue();
                        }
                }

                return ValueFactory.createNullValue();

        }

        @Override
        public Value[] getAllValues() throws IOException {
                return values.toArray(new Value[values.size()]);

        }

        @Override
        public BTreeLeaf getFirstLeaf() {
                return this;
        }

        private BTreeLeaf checkTreeNode(BTreeNode node) {
                if (node instanceof BTreeLeaf) {
                        return (BTreeLeaf) node;
                }
                throw new IllegalArgumentException("The given node is not of type BTreeLeaf.");
        }

        @Override
        public void mergeWithLeft(BTreeNode leftNode) throws IOException {
                BTreeLeaf leaf = checkTreeNode(leftNode);
                values.addAll(0, leaf.values);
                rows.addAll(0, leaf.rows);
                tree.removeNode(leaf.address);
        }

        @Override
        public void mergeWithRight(BTreeNode rightNode) throws IOException {
                BTreeLeaf leaf = checkTreeNode(rightNode);
                values.addAll(leaf.values);
                rows.addAll(leaf.rows);
                tree.removeNode(leaf.address);
        }

        @Override
        public void moveFirstTo(BTreeNode node) {
                BTreeLeaf leaf = checkTreeNode(node);
                leaf.values.add(values.remove(0));
                leaf.rows.add(rows.remove(0));
        }

        @Override
        public void moveLastTo(BTreeNode node) {
                BTreeLeaf leaf = checkTreeNode(node);
                leaf.values.add(0, values.remove(values.size() - 1));
                leaf.rows.add(0, rows.remove(rows.size() - 1));
        }

        @Override
        public boolean delete(Value v, int row) throws IOException {
                int index = getIndexOf(v, row);
                if (index != -1) {
                        values.remove(index);
                        rows.remove(index);
                        return true;
                } else {
                        return false;
                }
        }

        private int getIndexOf(Value v, int row) throws IOException {
                int index = getIndexOf(v);
                // If we don't find the value return -1
                if ((index == -1) || (index >= values.size())) {
                        return -1;
                } else {
                        // Look for the pair value-row
                        while ((index < values.size())
                                && (values.get(index).equals(v).getAsBoolean())) {
                                if (rows.get(index) == row) {
                                        return index;
                                }
                                index++;
                        }
                        return -1;
                }
        }

        @Override
        protected boolean isValid(int valueCount) throws IOException {
                if (getParentAddress() == -1) {
                        return (valueCount >= 0) && (valueCount <= tree.getN());
                } else {
                        return (valueCount >= ((tree.getN() + 1) / 2))
                                && (valueCount <= tree.getN());
                }
        }

        @Override
        public void checkTree() throws IOException {
                if (!isValid()) {
                        throw new IllegalStateException(this + " is not valid");
                }
        }

        @Override
        public byte[] getBytes() throws IOException {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);

                // Write the number of values
                dos.writeInt(values.size());

                // Write a ValueCollection with the used values
                ValueCollection vc = ValueFactory.createValue(values.toArray(new Value[values.size()]));
                byte[] valuesBytes = vc.getBytes();
                dos.writeInt(valuesBytes.length);
                dos.write(valuesBytes);

                // Write the row indexes
                for (int i = 0; i < rows.size(); i++) {
                        dos.writeInt(rows.get(i));
                }

                dos.close();

                return bos.toByteArray();
        }

        /**
         * Creates a Leaf from its byte representation
         * @param tree
         * @param address
         * @param parentAddress
         * @param n
         * @param bytes
         * @return
         * @throws IOException
         */
        public static BTreeLeaf createLeafFromBytes(DiskBTree tree, long address,
                long parentAddress, int n, byte[] bytes) throws IOException {
                BTreeLeaf ret = new BTreeLeaf(tree, address, parentAddress);
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                DataInputStream dis = new DataInputStream(bis);

                // Read the number of values
                int valueCount = dis.readInt();

                // Read the values
                int valuesBytesLength = dis.readInt();
                byte[] valuesBytes = new byte[valuesBytesLength];
                dis.read(valuesBytes);
                ValueCollection vc = (ValueCollection) ValueFactory.createValue(
                        Type.COLLECTION, valuesBytes);
                Value[] readvalues = vc.getValues();
                ret.values.addAll(Arrays.asList(readvalues));

                // Read the rowIndexes
                for (int i = 0; i < valueCount; i++) {
                        ret.rows.add(dis.readInt());
                }

                dis.close();

                return ret;
        }

        @Override
        public void save() throws IOException {
                tree.writeNodeAt(address, this);
        }

        public BTreeLeaf getChildNodeFor(Value v) {
                return this;
        }

        @Override
        public int[] query(RangeComparator[] comparators) throws IOException {
                RangeComparator minComparator = comparators[0];
                RangeComparator maxComparator = comparators[1];
                if (values.isEmpty()) {
                        return new int[0];
                } else {
                        int[] thisNode = new int[tree.getN()];
                        int index = 0;
                        Value lastValueInNode = values.get(values.size() - 1);
                        if (minComparator.isInRange(lastValueInNode)) {
                                boolean inRange = false;
                                for (int i = 0; i < values.size(); i++) {
                                        if (minComparator.isInRange(values.get(i))
                                                && maxComparator.isInRange(values.get(i))) {
                                                inRange = true;
                                                thisNode[index] = rows.get(i);
                                                index++;
                                        } else {
                                                if (inRange) {
                                                        // We have finished our range
                                                        break;
                                                }
                                        }
                                }
                        }

                        int[] ret = new int[index];
                        System.arraycopy(thisNode, 0, ret, 0, index);
                        return ret;
                }
        }
        
        @Override
        public void query(RangeComparator[] comparators, IndexVisitor<Value> visitor) throws IOException {
                RangeComparator minComparator = comparators[0];
                RangeComparator maxComparator = comparators[1];
                if (!values.isEmpty()) {
                        Value lastValueInNode = values.get(values.size() - 1);
                        if (minComparator.isInRange(lastValueInNode)) {
                                boolean inRange = false;
                                for (int i = 0; i < values.size(); i++) {
                                        final Value v = values.get(i);
                                        if (minComparator.isInRange(v)
                                                && maxComparator.isInRange(v)) {
                                                inRange = true;
                                                visitor.visitElement(rows.get(i), v);
                                        } else {
                                                if (inRange) {
                                                        // We have finished our range
                                                        break;
                                                }
                                        }
                                }
                        }
                }
        }

        @Override
        public boolean isValid() throws IOException {
                return isValid(values.size());
        }

        @Override
        public BTreeNode splitNode() throws IOException {
                // insert the value, split the node and reorganize the tree
                BTreeLeaf right = tree.createLeaf(address, getParentAddress());
                for (int i = (tree.getN() + 1) / 2; i < values.size();) {
                        right.insert(values.remove(i), rows.remove(i));
                }

                return right;
        }

        @Override
        public BTreeNode getNewRoot() throws IOException {
                if (getParentAddress() != -1) {
                        return getParent().getNewRoot();
                } else {
                        return this;
                }
        }

        @Override
        public Value getSmallestValue() throws IOException {
                return values.get(0);
        }

        @Override
        public boolean contains(Value value) {
                for (Value testValue : values) {
                        if (testValue.equals(value).getAsBoolean()) {
                                return true;
                        }
                }

                return false;
        }

        @Override
        public void updateRows(int row, int inc) throws IOException {
                for (int i = 0; i < rows.size(); i++) {
                        Integer currentRow = rows.get(i);
                        if (currentRow >= row) {
                                rows.set(i, currentRow + inc);
                        }
                }
        }

        @Override
        public int getValueCount() {
                return values.size();
        }
}
