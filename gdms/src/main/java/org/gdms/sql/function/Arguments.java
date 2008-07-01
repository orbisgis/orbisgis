package org.gdms.sql.function;

import org.gdms.data.types.Type;

public class Arguments {

	public static final Arguments STAR_ARGS = new Arguments(new Argument(
			Type.NULL));

	private Argument[] argumentsTypes;

	public Arguments(Argument... argumentsTypes) {
		this.argumentsTypes = argumentsTypes;
	}

	public String isValid(Type[] argsTypes) {
		if (this == STAR_ARGS) {
			if (argsTypes.length == 0) {
				return "At least one argument is mandatory";
			} else {
				return null;
			}
		} else {
			if (argsTypes.length != argumentsTypes.length) {
				return "Bad number of arguments";
			} else {
				for (int i = 0; i < argsTypes.length; i++) {
					if (!argumentsTypes[i].isValid(argsTypes[i])) {
						return "Bad argument: " + (i + 1);
					}
				}

				return null;
			}
		}
	}

	public int getArgumentCount() {
		return argumentsTypes.length;
	}
}
