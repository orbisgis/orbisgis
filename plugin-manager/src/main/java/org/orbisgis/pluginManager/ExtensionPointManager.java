package org.orbisgis.pluginManager;

import java.util.ArrayList;

public class ExtensionPointManager<T> {

	private String id;

	public ExtensionPointManager(String id) {
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<T> getInstancesFrom(String tag, String attribute) {
		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] exts = reg.getExtensions(id);
		ArrayList<T> instances = new ArrayList<T>();
		for (int i = 0; i < exts.length; i++) {
			Configuration c = exts[i].getConfiguration();
			if (c.getAttribute(tag, attribute) != null) {
				T nr = (T) c.instantiateFromAttribute(tag, attribute);
				instances.add(nr);
			}
		}

		return instances;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<ItemAttributes<T>> getItemAttributes(String xpath) {
		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] exts = reg.getExtensions(id);
		ArrayList<ItemAttributes<T>> instances = new ArrayList<ItemAttributes<T>>();
		for (int i = 0; i < exts.length; i++) {
			Configuration c = exts[i].getConfiguration();
			String[] attributeNames = c.getAttributeNames(xpath);
			if (attributeNames.length > 0) {
				ItemAttributes<T> ia = new ItemAttributes<T>(c, xpath,
						attributeNames, c.getAttributeValues(xpath));
				instances.add(ia);
			}
		}

		return instances;
	}

	@SuppressWarnings("unchecked")
	public T instantiateFrom(String condition, String classAttribute) {
		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] exts = reg.getExtensions(id);
		for (int i = 0; i < exts.length; i++) {
			Configuration c = exts[i].getConfiguration();
			if (c.getAttribute(condition, classAttribute) != null) {
				T action = (T) c.instantiateFromAttribute(condition,
						classAttribute);
				return action;
			}
		}

		return null;
	}
}
