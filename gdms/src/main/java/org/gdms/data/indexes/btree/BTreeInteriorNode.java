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
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;

/**
 *
 */
public final class BTreeInteriorNode extends AbstractBTreeNode {

        private List<ChildReference> children;

        /**
         * Create a new BTreeInteriorNode.
         *
         * @param tree The containing tree
         * @param address The address of this node in the file
         * @param parentAddress The address of this node's parent in the file
         */
        public BTreeInteriorNode(DiskBTree tree, long address, long parentAddress) {
                super(tree, address, parentAddress);
                children = new ArrayList<ChildReference>();
        }

        /**
         * Create a new BTreeInteriorNode.
         *
         * @param tree The containing tree
         * @param address The address of this node in the file
         * @param parentAddress The address of this node's parent in the file
         * @param left
         * @param right
         * @throws IOException
         */
        public BTreeInteriorNode(DiskBTree tree, long address, long parentAddress,
                BTreeNode left, BTreeNode right) throws IOException {
                this(tree, address, parentAddress);
                values.add(right.getSmallestValueNotIn(left));
                addChild(left);
                addChild(right);

        }

        private void insertChild(int i, BTreeNode node) {
                children.add(i, new ChildReference(tree, node));
                if (node != null) {
                        node.setParentAddress(address);
                }
        }

        private void addChild(BTreeNode node) {
                children.add(new ChildReference(tree, node));
                if (node != null) {
                        node.setParentAddress(address);
                }
        }

        @Override
        public boolean isLeaf() {
                return false;
        }

        /**
         * get the BTreeNode stored at index i in children.
         *
         * @param i
         * @return
         * @throws IOException
         */
        private BTreeNode getChild(int i) throws IOException {
                ChildReference childReference = children.get(i);
                if (!childReference.isLoaded()) {
                        childReference.resolve();
                }
                return childReference.getReference();
        }

        private void insertValueAndReferenceAfter(BTreeNode refNode, Value v,
                BTreeNode node) throws IOException {
                // Look the place to insert the new value
                int index = getIndexOf(refNode);

                // insert at index
                values.add(index, v);
                insertChild(index + 1, node);
                node.setParentAddress(address);
        }

        /**
         * Gets the index of the leaf in the array of children
         *
         * @param node
         * @return -1 if the node is not present
         * @throws IOException
         */
        private int getIndexOf(BTreeNode node) throws IOException {
                int childIndex = -1;
                for (int i = 0; i < children.size(); i++) {
                        if (getChild(i) == node) {
                                childIndex = i;
                                break;
                        }
                }
                return childIndex;
        }

        /**
         * Insert the value v at the index roxIndex in the list of values.
         *
         * @param v
         * @param rowIndex
         * @return
         * @throws IOException
         */
        @Override
        public Value insert(Value v, int rowIndex) throws IOException {
                Value ret = null;

                // See the children that will contain the value
                int index = getChildForValue(v);

                while ((index < children.size() - 1)
                        && (getChild(index + 1).getSmallestValue().lessEqual(v).getAsBoolean())) {
                        index++;
                }

                // delegate the insert
                BTreeNode child = getChild(index);
                child.insert(v, rowIndex);

                // update the value
                if (index > 0) {
                        values.set(index - 1, child.getSmallestValueNotIn(getChild(index - 1)));
                }

                if (!child.isValid()) {
                        // If it's invalid the parent will split it
                        BTreeNode newNode = child.splitNode();
                        newNode.setParentAddress(address);
                        Value splitedSmallestValue = newNode.getSmallestValueNotIn(child);
                        insertValueAndReferenceAfter(child, splitedSmallestValue, newNode);

                        // update the value
                        if (index > 0) {
                                values.set(index - 1, child.getSmallestValueNotIn(getChild(index - 1)));
                        }

                        // If this node is the root we need a new one
                        if (!isValid() && (getParentAddress() == -1)) {
                                BTreeNode m = splitNode();
                                tree.createInteriorNode(address, -1, this, m);
                        }
                }

                return ret;
        }

        @Override
        public String toString() {
                try {
                        StringBuilder strValues = new StringBuilder("");
                        String separator = "";
                        for (int i = 0; i < values.size(); i++) {
                                Value v = values.get(i);
                                strValues.append(separator).append(v);
                                separator = ", ";
                        }
                        StringBuilder strChilds = new StringBuilder("");
                        separator = "";
                        for (int i = 0; i < children.size(); i++) {
                                BTreeNode v = getChild(i);
                                if (v != null) {
                                        strChilds.append(separator).append(
                                                ((AbstractBTreeNode) v).name);
                                } else {
                                        strChilds.append(separator).append("null");
                                }
                                separator = ", ";
                        }

                        StringBuilder ret = new StringBuilder();
                        ret.append(name).append("\n (").append(strValues.toString());
                        ret.append(") \n(").append(strChilds).append(")\n");
                        for (int i = 0; i < children.size(); i++) {
                                BTreeNode node = getChild(i);
                                if (node != null) {
                                        ret.append(node.toString());
                                }
                        }

                        return ret.toString();
                } catch (IOException e) {
                        throw new IllegalStateException(e);
                }
        }

        @Override
        public Value getSmallestValueNotIn(BTreeNode treeNode) throws IOException {
                for (int i = 0; i < children.size(); i++) {
                        Value ret = getChild(i).getSmallestValueNotIn(treeNode);
                        if (!ret.isNull()) {
                                return ret;
                        }
                }

                return ValueFactory.createNullValue();
        }

        @Override
        public BTreeLeaf getFirstLeaf() throws IOException {
                return getChild(0).getFirstLeaf();
        }

        /**
         * Merges this node with one of its neighbors.
         *
         * @param node
         * @return true if we can merge. False otherwise
         * @throws IOException
         */
        public boolean mergeWithNeighbour(BTreeNode node) throws IOException {
                int index = getIndexOf(node);
                AbstractBTreeNode smallest;
                AbstractBTreeNode rightNeighbour = getRightNeighbour(index);
                AbstractBTreeNode leftNeighbour = getLeftNeighbour(index);

                if ((rightNeighbour != null) && (leftNeighbour != null)) {
                        smallest = rightNeighbour;
                        if (leftNeighbour.values.size() < rightNeighbour.values.size()) {
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
                        children.remove(index - 1);
                        values.remove(index - 1);
                } else {
                        node.mergeWithRight(rightNeighbour);
                        children.remove(index + 1);
                        values.remove(index);
                }

                // update the node index
                if (smallest == leftNeighbour) {
                        index--;
                }

                if (index > 0) {
                        values.set(index - 1, node.getSmallestValueNotIn(getChild(index - 1)));
                }

                return true;
        }

        @Override
        public void mergeWithRight(BTreeNode rightNode) throws IOException {
                BTreeInteriorNode node = checkTreeNode(rightNode);
                // values.add(values.size(), rightNode.getSmallestValueNotIn(this));
                int first = values.size();
                for (int i = 0; i < node.values.size(); i++) {
                        values.add(node.values.get(i));
                        ChildReference childRef = node.children.get(i);
                        childRef.resolve();
                        childRef.getReference().setParentAddress(this.address);
                        children.add(childRef);
                }
                ChildReference childRef = node.children.get(node.children.size() - 1);
                childRef.resolve();
                childRef.getReference().setParentAddress(this.address);
                children.add(childRef);
                values.add(first, getChild(first + 1).getSmallestValueNotIn(
                        getChild(first)));

                tree.removeNode(node.address);
        }

        private BTreeInteriorNode checkTreeNode(BTreeNode node) {
                if (node instanceof BTreeInteriorNode) {
                        return (BTreeInteriorNode) node;
                }
                throw new IllegalArgumentException("The given node is not of type BTreeInteriorNode.");
        }

        @Override
        public void mergeWithLeft(BTreeNode leftNode) throws IOException {
                BTreeInteriorNode node = checkTreeNode(leftNode);
                for (int i = 0; i < node.values.size(); i++) {
                        values.add(i, node.values.get(i));
                        ChildReference childRef = node.children.get(i);
                        childRef.resolve();
                        childRef.getReference().setParentAddress(this.address);
                        children.add(i, childRef);
                }
                int middleIndex = node.values.size();
                ChildReference childRef = node.children.get(node.children.size() - 1);
                childRef.resolve();
                childRef.getReference().setParentAddress(this.address);
                children.add(middleIndex, childRef);
                values.add(middleIndex, getChild(middleIndex + 1).getSmallestValueNotIn(getChild(middleIndex)));

                tree.removeNode(node.address);
        }

        /**
         * Selects the neighbor and moves the nearest element into this node if it
         * is possible
         *
         * @param node
         * @return true if it is possible to move from a neighbor and false
         * otherwise
         * @throws IOException
         */
        public boolean moveFromNeighbour(BTreeNode node) throws IOException {
                int index = getIndexOf(node);
                BTreeNode rightNeighbour = getRightNeighbour(index);
                BTreeNode leftNeighbour = getLeftNeighbour(index);
                if ((rightNeighbour != null) && (rightNeighbour.canGiveElement())) {
                        rightNeighbour.moveFirstTo(node);
                        values.set(index, rightNeighbour.getSmallestValueNotIn(node));
                        if (index > 0) {
                                values.set(index - 1, node.getSmallestValueNotIn(leftNeighbour));
                        }
                        return true;
                } else if ((leftNeighbour != null) && (leftNeighbour.canGiveElement())) {
                        leftNeighbour.moveLastTo(node);
                        values.set(index - 1, node.getSmallestValueNotIn(leftNeighbour));
                        if (index > 1) {
                                values.set(index - 2, leftNeighbour.getSmallestValueNotIn(getLeftNeighbour(index - 1)));
                        }
                        return true;
                } else {
                        return false;
                }
        }

        @Override
        public void moveFirstTo(BTreeNode node) throws IOException {
                BTreeInteriorNode n = checkTreeNode(node);
                ChildReference childRef = children.remove(0);
                childRef.resolve();
                childRef.getReference().setParentAddress(node.getAddress());
                n.children.add(childRef);
                values.remove(0);
                n.values.add(n.getChild(n.children.size() - 1).getSmallestValueNotIn(
                        n.getChild(n.children.size() - 2)));
        }

        @Override
        public void moveLastTo(BTreeNode node) throws IOException {
                BTreeInteriorNode n = checkTreeNode(node);
                values.remove(values.size() - 1);
                ChildReference childRef = children.remove(children.size() - 1);
                childRef.resolve();
                childRef.getReference().setParentAddress(node.getAddress());
                n.children.add(0, childRef);
                n.values.add(0, n.getChild(1).getSmallestValueNotIn(n.getChild(0)));
        }

        /**
         * Gets the neighbor on the left of the specified node
         *
         * @param index
         * @return
         * @throws IOException
         */
        private AbstractBTreeNode getLeftNeighbour(int index) throws IOException {
                if (index > 0) {
                        return (AbstractBTreeNode) getChild(index - 1);
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
        private AbstractBTreeNode getRightNeighbour(int index) throws IOException {
                if (index < values.size()) {
                        return (AbstractBTreeNode) getChild(index + 1);
                } else {
                        return null;
                }
        }

        /**
         * Check whether this node is valid or not. It is valid if
         * it is the root, and it has between 1 and n children, or if it is an interior node
         * and it has between (n-1)/2 and n children, where n is the order of the tree.
         *
         * @param valueCount
         * @return
         * @throws IOException
         */
        @Override
        protected boolean isValid(int valueCount) throws IOException {
                if (getParentAddress() == -1) {
                        return (valueCount >= 1) && (valueCount <= tree.getN());
                } else {
                        return (valueCount + 1 >= ((tree.getN() + 1) / 2))
                                && (valueCount <= tree.getN());
                }
        }

        @Override
        public boolean contains(Value v) throws IOException {
                int index = getChildForValue(v);
                if (getChild(index).contains(v)) {
                        return true;
                } else {
                        while ((index < children.size() - 1)
                                && (getChild(index + 1).getSmallestValue().lessEqual(v).getAsBoolean())) {
                                index++;
                                if (getChild(index).contains(v)) {
                                        return true;
                                }
                        }

                        return false;
                }
        }

        @Override
        public void checkTree() throws IOException {
                if (!isValid(values.size())) {
                        throw new IllegalStateException(this + " Not enough childs");
                } else {
                        for (int i = 0; i < children.size(); i++) {
                                if (getChild(i).getParent() != this) {
                                        throw new IllegalStateException(this + " parent is wrong");
                                }
                                getChild(i).checkTree();

                                if (i > 0) {
                                        Value smallestValueNotIn = getChild(i).getSmallestValueNotIn(getChild(i - 1));
                                        if (smallestValueNotIn.isNull()) {
                                                if (!values.get(i - 1).isNull()) {
                                                        throw new IllegalStateException("The " + i
                                                                + "th value is not right");
                                                }
                                        } else {
                                                if (!smallestValueNotIn.equals(values.get(i - 1)).getAsBoolean()) {
                                                        throw new IllegalStateException("The " + i
                                                                + "th value is not right");
                                                }
                                        }
                                }
                        }
                }
        }

        public int getChildForValue(Value v) {
                int index = values.size();
                for (int i = 0; i < values.size(); i++) {
                        // take care of NULL
                        final BooleanValue less = v.less(values.get(i));
                        if (!less.isNull() && less.getAsBoolean()) {
                                index = i;
                                break;
                        }
                }

                while ((index > 0) && (values.get(index - 1).isNull())) {
                        index--;
                }

                return index;
        }

        @Override
        public boolean delete(Value v, int row) throws IOException {
                int index = getChildForValue(v);
                BTreeNode child = getChild(index);

                boolean done = false;
                while (!done) {
                        if (child.delete(v, row)) {
                                // update value
                                if (index > 0) {
                                        Value smaller = child.getSmallestValueNotIn(getChild(index - 1));
                                        values.set(index - 1, smaller);
                                }
                                if (index < values.size()) {
                                        Value smaller = getChild(index + 1).getSmallestValueNotIn(
                                                getChild(index));
                                        values.set(index, smaller);
                                }

                                // Check validity
                                if (!child.isValid()) {
                                        // move from neighbour
                                        if (!moveFromNeighbour(child) && !mergeWithNeighbour(child)) {
                                                // If we cannot merge create new root
                                                tree.removeNode(this.address);
                                                getChild(0).setParentAddress(-1);
                                        } else if ((getParentAddress() == -1) && (!isValid())) {
                                                tree.removeNode(this.address);
                                                getChild(0).setParentAddress(-1);
                                        }
                                }

                                return true;
                        } else {
                                index++;
                        }

                        if (index >= children.size()) {
                                done = true;
                        } else {
                                child = getChild(index);
                                if (child.getSmallestValue().greater(v).getAsBoolean()) {
                                        done = true;
                                }
                        }
                }

                return false;
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

                // Write the children direction
                if (values.size() > 0) {
                        for (int i = 0; i < children.size(); i++) {
                                dos.writeLong(children.get(i).getAddress());
                        }
                }

                dos.close();

                return bos.toByteArray();
        }

        public static BTreeInteriorNode createInteriorNodeFromBytes(DiskBTree tree,
                long address, long parentAddress, int n, byte[] bytes) throws IOException {
                BTreeInteriorNode ret = new BTreeInteriorNode(tree, address, parentAddress);
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

                // Read the children directions
                for (int i = 0; i < valueCount + 1; i++) {
                        ret.children.add(new ChildReference(tree, dis.readLong()));
                }

                dis.close();

                return ret;
        }

        @Override
        public int getValueCount() throws IOException {
                int count = 0;
                for (int i = 0; i < children.size(); i++) {
                        count += getChild(i).getValueCount();
                }
                return count;
        }

        private static class ChildReference {

                private BTreeNode object;
                private long address;
                private DiskBTree tree;

                ChildReference(DiskBTree tree, BTreeNode object) {
                        this.tree = tree;
                        this.object = object;
                        if (object == null) {
                                this.address = -1;
                        } else {
                                this.address = ((AbstractBTreeNode) object).address;
                        }
                }

                ChildReference(DiskBTree tree, long readLong) {
                        this.tree = tree;
                        this.address = readLong;
                        this.object = null;
                }

                public BTreeNode getReference() {
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
        public boolean isValid() throws IOException {
                return isValid(values.size());
        }

        @Override
        public BTreeNode splitNode() throws IOException {
                BTreeInteriorNode m = tree.createInteriorNode(address, getParentAddress());
                m.setParentAddress(this.address);

                // Move half the values to the new node
                for (int i = (tree.getN() + 1) / 2 + 1; i < values.size();) {
                        m.values.add(values.remove(i));
                        ChildReference child = children.remove(i);
                        child.resolve();
                        child.getReference().setParentAddress(m.address);
                        m.addChild(child.getReference());
                }
                values.remove((tree.getN() + 1) / 2);
                ChildReference child = children.remove((tree.getN() + 1) / 2 + 1);
                child.resolve();
                child.getReference().setParentAddress(m.address);
                m.addChild(child.getReference());

                return m;
        }

        @Override
        public BTreeNode getNewRoot() throws IOException {
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
        public int[] query(RangeComparator[] comparators) throws IOException {
                RangeComparator minComparator = comparators[0];
                RangeComparator maxComparator = comparators[1];

                int[] minChildRange = minComparator.getRange(this);
                int[] maxChildRange = maxComparator.getRange(this);

                int minChild = Math.max(minChildRange[0], maxChildRange[0]);
                int maxChild = Math.min(minChildRange[1], maxChildRange[1]);

                int[] childResult = getChild(minChild).query(comparators);
                ArrayList<int[]> childrenResult = new ArrayList<int[]>();
                int index = minChild + 1;
                int numResults = 0;
                while (index <= maxChild) {
                        numResults += childResult.length;
                        childrenResult.add(childResult);
                        childResult = getChild(index).query(comparators);
                        index++;
                }
                numResults += childResult.length;
                childrenResult.add(childResult);
                int[] ret = new int[numResults];
                int acum = 0;
                for (int[] is : childrenResult) {
                        System.arraycopy(is, 0, ret, acum, is.length);
                        acum += is.length;
                }

                return ret;
        }

        @Override
        public void query(RangeComparator[] comparators, IndexVisitor<Value> visitor) throws IOException {
                RangeComparator minComparator = comparators[0];
                RangeComparator maxComparator = comparators[1];

                int[] minChildRange = minComparator.getRange(this);
                int[] maxChildRange = maxComparator.getRange(this);

                int minChild = Math.max(minChildRange[0], maxChildRange[0]);
                int maxChild = Math.min(minChildRange[1], maxChildRange[1]);

                int index = minChild;
                while (index <= maxChild) {
                        getChild(index).query(comparators, visitor);
                        index++;
                }
        }

        @Override
        public Value[] getAllValues() throws IOException {
                ArrayList<Value> ret = new ArrayList<Value>();
                for (int i = 0; i < children.size(); i++) {
                        Value[] temp = getChild(i).getAllValues();
                        ret.addAll(Arrays.asList(temp));
                }

                return ret.toArray(new Value[ret.size()]);
        }

        @Override
        public Value getSmallestValue() throws IOException {
                return getChild(0).getSmallestValue();
        }

        @Override
        public void updateRows(int row, int inc) throws IOException {
                for (int i = 0; i < children.size(); i++) {
                        getChild(i).updateRows(row, inc);
                }
        }
}
