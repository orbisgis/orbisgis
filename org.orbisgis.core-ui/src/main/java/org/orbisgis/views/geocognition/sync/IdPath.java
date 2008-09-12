package org.orbisgis.views.geocognition.sync;

import java.util.ArrayList;
import java.util.Collections;

public class IdPath {
	private ArrayList<String> idPath;

	public IdPath(String path) {
		idPath = new ArrayList<String>();
		Collections.addAll(idPath, path.split("/"));
	}

	public IdPath() {
		idPath = new ArrayList<String>();
	}

	public IdPath(IdPath p) {
		idPath = new ArrayList<String>();
		for (int i = 0; i < p.size(); i++) {
			idPath.add(p.get(i));
		}
	}

	public int size() {
		return idPath.size();
	}

	public String get(int i) {
		return idPath.get(i);
	}

	public String getLast() {
		return idPath.get(idPath.size() - 1);
	}

	public boolean add(String s) {
		return idPath.add(s);
	}

	public void addFirst(String s) {
		idPath.add(0, s);
	}

	public String removeLast() {
		return idPath.remove(idPath.size() - 1);
	}

	public int indexOf(String s) {
		return idPath.indexOf(s);
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
	
	public boolean startsWith(IdPath path) {
		boolean matches = true;

		if (path.size() <= this.size()) {
			for (int i = 0; matches && i < path.size(); i++) {
				if (!get(i).equals(path.get(i))) {
					matches = false;
				}
			}
		} else {
			matches = false;
		}

		return matches;
	}
}
