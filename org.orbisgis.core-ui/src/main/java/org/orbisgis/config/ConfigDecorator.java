package org.orbisgis.config;

import javax.swing.JComponent;

import org.orbisgis.Services;

public class ConfigDecorator implements IConfiguration {
	private IConfiguration config;
	private String id, className, text;

	/**
	 * Creates a new configuration decorator for the specified class with the
	 * given id
	 * 
	 * @param className
	 *            the name of the decorated class
	 * @param id
	 *            the id of the configuration
	 */
	public ConfigDecorator(String className, String id, String text) {
		this.id = id;
		this.className = className;
		this.text = text;
	}

	/**
	 * Gets the IConfiguration of this decorator lazily
	 * 
	 * @return the IConfiguration
	 */
	private IConfiguration getConfig() {
		if (config == null) {
			try {
				config = (IConfiguration) getClass().getClassLoader()
						.loadClass(className).newInstance();
				config.load();
			} catch (InstantiationException e) {
				Services.getErrorManager().error("bug!", e);
			} catch (IllegalAccessException e) {
				Services.getErrorManager().error("bug!", e);
			} catch (ClassNotFoundException e) {
				Services.getErrorManager().error("bug!", e);
			}
		}

		return config;
	}

	@Override
	public JComponent getComponent() {
		return getConfig().getComponent();
	}

	@Override
	public void load() {
		getConfig().load();
	}

	@Override
	public void save() {
		getConfig().save();
	}

	/**
	 * Gets the id of this configuration
	 * 
	 * @return the id of this configuration
	 */
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return text;
	}
}
