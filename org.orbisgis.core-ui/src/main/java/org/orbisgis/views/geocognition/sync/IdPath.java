package org.orbisgis.views.geocognition.sync;

import java.util.ArrayList;
import java.util.Collections;

public class IdPath {
	private ArrayList<String> idPath;

	/**
	 * Creates a new IdPath from the given path. The given path separator must
	 * be "/"
	 * 
	 * @param path
	 *            the id path
	 */
	public IdPath(String path) {
		idPath = new ArrayList<String>();
		Collections.addAll(idPath, path.split("/"));
	}

	/**
	 * Creates an empty IdPath
	 */
	public IdPath() {
		idPath = new ArrayList<String>();
	}

	/**
	 *Creates a copy of this IdPath
	 * 
	 * @return a copy of this IdPath
	 */
	public IdPath copy() {
		IdPath copy = new IdPath();
		for (int i = 0; i < size(); i++) {
			copy.addLast(get(i));
		}

		return copy;
	}

	/**
	 * Gets the size of this IdPath
	 * 
	 * @return the size of this IdPath
	 */
	public int size() {
		return idPath.size();
	}

	/**
	 * Gets the <code>i</code> element of this IdPath
	 * 
	 * @param i
	 *            the index of the element
	 * @return the element for the given index
	 */
	public String get(int i) {
		return idPath.get(i);
	}

	/**
	 * Gets the last element of this IdPath
	 * 
	 * @return the last element of this IdPath
	 */
	public String getLast() {
		return idPath.get(idPath.size() - 1);
	}

	/**
	 * Adds the given id at the end of this IdPath
	 * 
	 * @param s
	 *            the id to add
	 */
	public void addLast(String s) {
		idPath.add(s);
	}

	/**
	 * Adds the given id at the beginning of this IdPath
	 * 
	 * @param s
	 *            the id to add
	 */
	public void addFirst(String s) {
		idPath.add(0, s);
	}

	/**
	 * Removes the last element of this IdPath
	 * 
	 * @return the removed element
	 */
	public String removeLast() {
		return idPath.remove(idPath.size() - 1);
	}

	/**
	 * Gets the index of the given id in this IdPath
	 * 
	 * @param id
	 *            the id to get the index
	 * @return the index of the id
	 */
	public int indexOf(String id) {
		return idPath.indexOf(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IdPath) {
			return idPath.equals(((IdPath) obj).idPath);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return idPath.hashCode();
	}

	/**
	 * Determines if this element starts with the same id elements as the given
	 * IdPath
	 * 
	 * @param prefix
	 *            the starting path
	 * @return true if this element starts with the given prefix, false
	 *         otherwise
	 */
	public boolean startsWith(IdPath prefix) {
		boolean matches = true;

		if (prefix.size() <= this.size()) {
			for (int i = 0; matches && i < prefix.size(); i++) {
				if (!get(i).equals(prefix.get(i))) {
					matches = false;
				}
			}
		} else {
			matches = false;
		}

		return matches;
	}
}
