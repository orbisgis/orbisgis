package org.orbisgis.core.ui.configuration;

import javax.swing.JComponent;

public class ConfigurationDecorator implements IConfiguration {
	private IConfiguration config;
	private String id, text, parentId;
	private boolean isComponentCreated;

	/**
	 * Creates a new configuration decorator for the specified class with the
	 * given id
	 * 
	 * @param className
	 *            the name of the decorated class
	 * @param id
	 *            the id of the configuration
	 */
	public ConfigurationDecorator(IConfiguration config, String id,
			String text, String parentId) {
		this.id = id;
		this.text = text;
		this.parentId = parentId;
		this.config = config;
		isComponentCreated = false;
	}

	@Override
	public JComponent getComponent() {
		isComponentCreated = true;
		return config.getComponent();
	}

	@Override
	public void loadAndApply() {
		config.loadAndApply();
	}

	@Override
	public String validateInput() {
		if (isComponentCreated) {
			return config.validateInput();
		} else {
			return null;
		}
	}

	@Override
	public void applyUserInput() {
		if (isComponentCreated) {
			config.applyUserInput();
		}
	}

	@Override
	public void saveApplied() {
		config.saveApplied();
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
}
