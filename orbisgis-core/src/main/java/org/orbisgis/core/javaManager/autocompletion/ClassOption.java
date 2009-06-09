package org.orbisgis.core.javaManager.autocompletion;

public class ClassOption extends AbstractPrefixedOption implements Option {

	private String completionWord;
	private ImportOption[] imports;
	private String className;
	private String qName;
	private boolean isInterface;

	public ClassOption(String prefix, String qName, boolean isInterface) {
		super(prefix);
		this.isInterface = isInterface;
		ImportsVisitor iv = CompletionUtils.getImportsVisitor();
		this.qName = qName;
		className = CompletionUtils.getClassSimpleName(qName);
		if (iv.isImported(qName)) {
			completionWord = className;
			imports = new ImportOption[0];
		} else if (iv.getImportedClassNames().contains(className)) {
			completionWord = qName;
			imports = new ImportOption[0];
		} else {
			completionWord = className;
			imports = new ImportOption[] { new ImportOption(qName) };
		}
	}

	public String getAsString() {
		return "class-" + getCompletionWord();
	}

	public String getSortString() {
		return "bb" + toString();
	}

	@Override
	public String toString() {
		return className + " - " + CompletionUtils.getClassPackage(qName);
	}

	@Override
	protected String getCompletionWord() {
		return completionWord;
	}

	@Override
	public ImportOption[] getImports() {
		return imports;
	}

	public boolean isInterface() {
		return isInterface;
	}
}
