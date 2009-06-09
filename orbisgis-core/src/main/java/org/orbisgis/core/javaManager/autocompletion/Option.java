package org.orbisgis.core.javaManager.autocompletion;

public interface Option {

	String getAsString();

	String getSortString();

	/**
	 * Gets the new version of the code resulting of choosing this code
	 * completion option
	 * 
	 * @return
	 */
	String getTransformedText();

	/**
	 * Gets the new cursor position after choosing this code completion option
	 * 
	 * @return
	 */
	int getCursorPosition();

	/**
	 * Return the necessary imports options for this completion option
	 * 
	 * @return
	 */
	ImportOption[] getImports();

	/**
	 * Set the text and the cursor position this option have to autocomplete
	 * 
	 * @param text
	 */
	void setCompletionCase(String text, int cursorPos);

	/**
	 * Sets the new prefix for this option
	 * 
	 * @param prefix
	 * @return true if this option is still valid
	 */
	boolean setPrefix(String prefix);

	/**
	 * Get the prefix used by this option
	 * @return
	 */
	String getPrefix();
}
