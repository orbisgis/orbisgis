package org.orbisgis.core.javaManager.autocompletion;

public abstract class AbstractOption implements Option {

	protected int cursorPos;
	protected String text;

	@Override
	public ImportOption[] getImports() {
		return new ImportOption[0];
	}

	@Override
	public void setCompletionCase(String text, int cursorPos) {
		this.text = text;
		this.cursorPos = cursorPos;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}
}
