package org.orbisgis.core.javaManager.autocompletion;

public abstract class AbstractPrefixedOption extends AbstractOption {

	private String prefix;

	public AbstractPrefixedOption(String prefix) {
		this.prefix = prefix;
	}

	public int getCursorPosition() {
		return cursorPos + getCompletionWord().length() - prefix.length();
	}

	public String getTransformedText() {
		StringBuffer buffer = new StringBuffer(text);
		buffer.delete(cursorPos - prefix.length(), cursorPos);
		buffer.insert(cursorPos - prefix.length(), getCompletionWord());
		return buffer.toString();
	}

	protected abstract String getCompletionWord();

	@Override
	public boolean setPrefix(String prefix) {
		this.prefix = prefix;
		return getCompletionWord().toLowerCase().startsWith(
				prefix.toLowerCase());
	}
	
	@Override
	public String getPrefix() {
		return prefix;
	}
}
