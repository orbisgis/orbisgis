package org.orbisgis.configuration;

import javax.swing.JComponent;

import org.orbisgis.Services;

public class ConfigurationDecorator implements IConfiguration {
	private IConfiguration config;
	private String id, className, text, parentId;

	/**
	 * Creates a new configuration decorator for the specified class with the
	 * given id
	 * 
	 * @param className
	 *            the name of the decorated class
	 * @param id
	 *            the id of the configuration
	 */
	public ConfigurationDecorator(String className, String id, String text,
			String parentId) {
		this.id = id;
		this.className = className;
		this.text = text;
		this.parentId = parentId;
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

	/**
	 * Gets the text to show of this configuration
	 * 
	 * @return the text to show of this configuration
	 */
	public String getText() {
		return text;
	}

	/**
	 * Gets the parent id of this configuration
	 * 
	 * @return the parent id of this configuration
	 */
	public String getParentId() {
		return parentId;
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public String validateInput() {
		return config.validateInput();
	}

}
