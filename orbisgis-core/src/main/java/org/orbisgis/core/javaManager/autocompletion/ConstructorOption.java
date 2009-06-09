package org.orbisgis.core.javaManager.autocompletion;

public class ConstructorOption extends MethodOption implements Option {

	private ImportOption[] imports;
	private String qName;

	public ConstructorOption(String prefix, String simpleName, String qName,
			Class<?>[] parameterTypes) {
		super(prefix, simpleName, parameterTypes);
		this.qName = qName;
		ImportsVisitor iv = CompletionUtils.getImportsVisitor();
		if (iv.isImported(qName)) {
			imports = new ImportOption[0];
		} else if (iv.getImportedClassNames().contains(simpleName)) {
			this.completionName = qName;
			imports = new ImportOption[0];
		} else {
			imports = new ImportOption[] { new ImportOption(qName) };
		}
	}

	@Override
	public ImportOption[] getImports() {
		return imports;
	}

	@Override
	public String getSortString() {
		return "a" + super.getSortString();
	}
	
	@Override
	public String toString() {
		return super.toString() + " - "
				+ CompletionUtils.getClassPackage(qName);
	}

}
