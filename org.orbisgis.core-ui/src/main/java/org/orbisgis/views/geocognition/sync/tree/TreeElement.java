package org.orbisgis.views.geocognition.sync.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.views.geocognition.sync.IdPath;

public class TreeElement {
	private String id;
	private boolean isFolder;
	private ArrayList<TreeElement> children;
	private String typeId;
	private TreeElement parent;

	/**
	 * Create a new TreeElement from a GeocognitionElement
	 * 
	 * @param e
	 *            the GeocognitionElement to represent
	 */
	public TreeElement(GeocognitionElement e) {
		id = e.getId();
		isFolder = e.isFolder();
		typeId = e.getTypeId();
		children = new ArrayList<TreeElement>();
		parent = null;
	}

	/**
	 * Adds an element to this tree node
	 * 
	 * @param e
	 *            the element to add
	 */
	public void addElement(TreeElement e) {
		String id = e.getId();
		if (getElement(id) == null) {
			children.add(e);
			e.parent = this;
			Collections.sort(children, new Comparator<TreeElement>() {

				@Override
				public int compare(TreeElement o1, TreeElement o2) {
					return o1.id.compareTo(o2.id);
				}
			});
		}
	}

	/**
	 * Removes the specified child from this element
	 * 
	 * @param id
	 *            the id of the element to remove
	 * @return true if the element has been removed, false otherwise
	 */
	public boolean removeElement(String id) {
		for (TreeElement child : children) {
			if (child.id.equals(id)) {
				child.parent = null;
				return children.remove(child);
			}
		}
		return false;
	}

	/**
	 * Gets the id
	 * 
	 * @return the id of the element
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the child with the specified id
	 * 
	 * @param id
	 *            the id of the child to get
	 * @return the element with the given id
	 */
	public TreeElement getElement(String id) {
		for (TreeElement child : children) {
			if (child.id.equals(id)) {
				return child;
			}
		}
		return null;
	}

	/**
	 * Gets the <code>i</code> child of this element
	 * 
	 * @param i
	 *            the index of the child
	 * @return the <code>i</code> child of this element
	 */
	public TreeElement getElement(int i) {
		return children.get(i);
	}

	/**
	 * Gets the number of children of this element
	 * 
	 * @return the number of children of this element
	 */
	public int getElementCount() {
		return children.size();
	}

	/**
	 * Gets the descendant with the specified id path
	 * 
	 * @param path
	 *            the path to the element
	 * @return the descendant represented by the path
	 */
	public TreeElement find(IdPath path) {
		return find(path, path.size());
	}

	/**
	 * Gets the descendant with the specified id path using the first
	 * <code>n</code> elements of the path. <b><code>n</code> is an exclusive
	 * index</b>
	 * 
	 * @param path
	 *            the path to the element
	 * @param the
	 *            number of elements of the path to use <b><code>n</code> is an
	 *            exclusive index</b>
	 * @return the descendant represented by the path
	 */
	public TreeElement find(IdPath path, int n) {
		if (!path.get(0).equalsIgnoreCase(id)) {
			return null;
		}

		TreeElement parent = this;
		TreeElement child = this;
		for (int i = 1; i < n; i++) {
			child = parent.getElement(path.get(i));
			if (child == null) {
				return null;
			} else {
				parent = child;
			}
		}
		return child;
	}

	/**
	 * Determines if this element is a folder or not
	 * 
	 * @return true if is a folder, false otherwise
	 */
	public boolean isFolder() {
		return isFolder;
	}

	@Override
	public String toString() {
		return id;
	}

	/**
	 * Removes all the children of the element
	 */
	public void removeAllChildren() {
		children.clear();
	}

	public String getTypeId() {
		return typeId;
	}

	public IdPath getIdPath() {
		IdPath path = new IdPath();
		path.add(getId());
		TreeElement p = parent;
		while (p != null) {
			path.addFirst(p.getId());
			p = p.parent;
		}

		return path;
	}
}
