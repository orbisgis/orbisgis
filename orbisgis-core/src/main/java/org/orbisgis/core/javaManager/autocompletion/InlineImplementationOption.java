package org.orbisgis.core.javaManager.autocompletion;

import java.lang.reflect.Method;
import java.util.HashSet;

public class InlineImplementationOption extends AbstractPrefixedOption
		implements Option {

	private String interfaceName;
	private StringBuffer str;
	private String interfaceFullName;
	private HashSet<ImportOption> imports = new HashSet<ImportOption>();

	public InlineImplementationOption(String prefix, String interfaceName,
			String fullName) throws ClassNotFoundException {
		super(prefix);
		this.interfaceName = interfaceName;
		this.interfaceFullName = fullName;
		str = new StringBuffer(getClassReference(Class
				.forName(interfaceFullName))
				+ "() {\n");
		Class<?> cl = Class.forName(fullName);
		Method[] methods = cl.getMethods();
		for (Method method : methods) {
			str.append("public ").append(
					getClassReference(method.getReturnType())).append(" ")
					.append(method.getName()).append(" (");
			Class<?>[] params = method.getParameterTypes();
			String separator = "";
			for (int i = 0; i < params.length; i++) {
				Class<?> paramClass = params[i];
				str.append(separator).append(getClassReference(paramClass))
						.append(" arg").append(i);
				separator = ", ";
			}
			str.append(") {\n}\n");
		}
		str.append("}");
	}

	private String getClassReference(Class<?> cl) throws ClassNotFoundException {
		if (cl == Void.TYPE) {
			return "void";
		} else {
			String arraySuffix = "";
			if (cl.isArray()) {
				arraySuffix = "[]";
				cl = cl.getComponentType();
			}
			ImportsVisitor iv = CompletionUtils.getImportsVisitor();
			String qName = cl.getName();
			String simpleName = CompletionUtils.getClassSimpleName(qName);
			if (cl.isPrimitive()) {
				return simpleName + arraySuffix;
			} else if (iv.isImported(qName)) {
				return simpleName + arraySuffix;
			} else if (iv.getImportedClassNames().contains(simpleName)) {
				return qName + arraySuffix;
			} else {
				imports.add(new ImportOption(qName));
				return simpleName + arraySuffix;
			}
		}
	}

	@Override
	public String getAsString() {
		return "inlineImplementation-" + interfaceName;
	}

	@Override
	public String getSortString() {
		return "c" + toString();
	}

	@Override
	protected String getCompletionWord() {
		return str.toString();
	}

	@Override
	public String toString() {
		return interfaceFullName + "- Inline implementation";
	}

	@Override
	public ImportOption[] getImports() {
		return imports.toArray(new ImportOption[0]);
	}
}
