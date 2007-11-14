/**
 *
 */
package org.orbisgis.core.wizards;

public class WizardAndId<T> {
	private T wizard;
	private String id;

	public WizardAndId(T wizard, String id) {
		super();
		this.wizard = wizard;
		this.id = id;
	}

	public T getWizard() {
		return wizard;
	}

	public String getId() {
		return id;
	}
}