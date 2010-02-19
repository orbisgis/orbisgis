package org.orbisgis.core.javaManager.autocompletion;

public class MethodOption extends AbstractPrefixedOption implements Option {

	private Class<?>[] parameters;
	protected String methodName;
	protected String completionName;

	public MethodOption(String prefix, String methodName,
			Class<?>[] parameterTypes) {
		super(prefix);
		this.methodName = methodName;
		this.completionName = methodName;
		this.parameters = parameterTypes;
	}

	public String getAsString() {
		String separator = "";
		StringBuffer ret = new StringBuffer("method-").append(methodName)
				.append("(");
		for (int i = 0; i < parameters.length; i++) {
			ret.append(separator);
			Class<?> type = parameters[i];
			if (type.isArray()) {
				ret.append(type.getComponentType().getSimpleName())
						.append("[]");
			} else {
				ret.append(type.getSimpleName());
			}
			ret.append(" ").append("arg").append(i);
			separator = ", ";
		}

		return ret.append(")").toString();
	}

	public String getSortString() {
		return "ba" + toString();
	}

	@Override
	public String toString() {
		String separator = "";
		StringBuffer ret = new StringBuffer();
		ret.append(methodName).append("(");
		for (int i = 0; i < parameters.length; i++) {
			ret.append(separator);
			Class<?> type = parameters[i];
			if (type.isArray()) {
				ret.append(type.getComponentType().getSimpleName())
						.append("[]");
			} else {
				ret.append(type.getSimpleName());
			}
			ret.append(" ").append("arg").append(i);
			separator = ", ";
		}

		return ret.append(")").toString();
	}

	public int getPosition() {
		return cursorPos;
	}

	@Override
	public String getCompletionWord() {
		String separator = "";
		StringBuffer ret = new StringBuffer();
		ret.append(completionName).append("(");
		for (int i = 0; i < parameters.length; i++) {
			ret.append(separator);
			ret.append("arg").append(i);
			separator = ", ";
		}
		return ret.append(")").toString();
	}
}
