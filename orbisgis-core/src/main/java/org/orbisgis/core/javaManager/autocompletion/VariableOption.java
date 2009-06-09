package org.orbisgis.core.javaManager.autocompletion;

public class VariableOption extends AbstractPrefixedOption implements Option {

	private String varName;

	public VariableOption(String prefix, String varName) {
		super(prefix);
		this.varName = varName.trim();
	}

	public String getAsString() {
		return "variable-" + varName;
	}

	public String getSortString() {
		return "ab" + getAsString();
	}

	@Override
	public String toString() {
		return varName;
	}

	@Override
	protected String getCompletionWord() {
		return varName;
	}

}
