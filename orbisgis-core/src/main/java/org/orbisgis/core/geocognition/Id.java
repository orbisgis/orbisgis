package org.orbisgis.core.geocognition;

import java.util.ArrayList;
import java.util.List;

class Id {

	private List<String> id;

	public Id(String id) {
		if (id.startsWith("/")) {
			id = id.substring(1);
		}
		this.id = new ArrayList<String>();
		if (id.length() != 0) {
			String[] parts = id.split("/");
			for (String part : parts) {
				this.id.add(part);
			}
		}
	}

	Id(List<String> id) {
		this.id = id;
	}

	public String getLast() {
		return id.get(id.size() - 1);
	}

	public int getLength() {
		return id.size();
	}

	public String getPart(int i) {
		return id.get(i);
	}

	public Id getParent() {
		Id ret = new Id(id.subList(0, id.size() - 1));
		return ret;
	}

	@Override
	public String toString() {
		String ret = "/";
		for (String part : id) {
			ret = ret + part + "/";
		}
		return ret;
	}
}
