package org.orbisgis.core.javaManager.autocompletion;

public class ImportOption extends AbstractOption implements Option {

	private String qName;

	ImportOption(String qName) {
		this.qName = qName;
	}

	public String getAsString() {
		return "import " + qName;
	}

	public String getSortString() {
		return "a" + toString();
	}

	@Override
	public String toString() {
		return "import " + qName;
	}

	public int getCursorPosition() {
		return cursorPos + getTransformedText().length() - text.length();
	}

	public String getTransformedText() {
		ImportsVisitor iv = CompletionUtils.getImportsVisitor();
		String newImportSection = iv.getAddImport(qName);

		String ret;
		if (iv.getImportsInitPosition() == Integer.MAX_VALUE) {
			ret = newImportSection + "\n" + text;
		} else {
			String afterImports = "";
			if (text.length() > iv.getImportsEndPosition()) {
				afterImports = text.substring(iv.getImportsEndPosition());
			}
			ret = text.substring(0, iv.getImportsInitPosition())
					+ newImportSection + afterImports;
		}
		return ret;
	}

	@Override
	public ImportOption[] getImports() {
		return new ImportOption[0];
	}

	@Override
	public boolean setPrefix(String prefix) {
		throw new RuntimeException("Bug! This should never be called");
	}

	@Override
	public String getPrefix() {
		throw new RuntimeException("Bug! This should never be called");
	}

}
