package org.orbisgis.pluginManager;

public class ItemAttributes<T> {

	private String[] attributes;
	private String[] values;
	private Configuration configuration;
	private String xpath;

	public ItemAttributes(Configuration c, String xpath, String[] attributes,
			String[] values) {
		this.attributes = attributes;
		this.values = values;
		this.configuration = c;
		this.xpath = xpath;
	}

	public String getAttribute(String name) {
		int index = getAttrIndex(name);

		if (index != -1) {
			return values[index];
		} else {
			return null;
		}
	}

	public boolean exists(String attributeName) {
		return getAttrIndex(attributeName) != -1;
	}

	@SuppressWarnings("unchecked")
	public T getInstance(String attributeName) {
		return (T) configuration.instantiateFromAttribute(xpath, attributeName);
	}

	private int getAttrIndex(String name) {
		int index = -1;
		for (int i = 0; i < attributes.length; i++) {
			if (attributes[i].equals(name)) {
				index = i;
				break;
			}
		}
		return index;
	}
}
