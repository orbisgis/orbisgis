package org.gdms.data.edition;

import org.gdms.data.types.Type;

public class Field {
	private int originalIndex;

	private String name;

	private Type type;

	public Field(int originalIndex, String name, Type type) {
		this.originalIndex = originalIndex;
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOriginalIndex() {
		return originalIndex;
	}

	public void setOriginalIndex(int originalIndex) {
		this.originalIndex = originalIndex;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}