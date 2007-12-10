/**
 *
 */
package org.sif.multiInputPanel;

class Input {
	private String text;
	private String initialValue;
	private InputType type;
	private String name;
	private String group;

	public Input(String name, String text, String initialValue, InputType type) {
		super();
		this.name = name;
		this.text = text;
		this.initialValue = initialValue;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public String getInitialValue() {
		return initialValue;
	}

	public InputType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

}