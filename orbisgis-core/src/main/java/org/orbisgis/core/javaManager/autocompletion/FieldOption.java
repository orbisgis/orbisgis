package org.orbisgis.core.javaManager.autocompletion;

public class FieldOption extends AbstractPrefixedOption implements Option {

	private String fieldName;

	public FieldOption(String prefix, String name) {
		super(prefix);
		this.fieldName = name;
	}

	public String getAsString() {
		return "field-" + fieldName;
	}

	public String getSortString() {
		return "aa" + toString();
	}

	@Override
	public String toString() {
		return fieldName;
	}

	@Override
	protected String getCompletionWord() {
		return fieldName;
	}
}
