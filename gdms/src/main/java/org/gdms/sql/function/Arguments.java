package org.gdms.sql.function;

import org.gdms.data.types.Type;

public class Arguments {

	public static final Arguments STAR_ARGS = new Arguments(new Argument(
			Type.NULL));

	private Argument[] argumentsTypes;

	public Arguments(Argument... argumentsTypes) {
		this.argumentsTypes = argumentsTypes;
	}

	public boolean isValid(Type[] argsTypes) {
		if (this == STAR_ARGS) {
			return argsTypes.length > 0;
		} else {
			if (argsTypes.length != argumentsTypes.length) {
				return false;
			} else {
				for (int i = 0; i < argsTypes.length; i++) {
					if (!argumentsTypes[i].isValid(argsTypes[i])) {
						return false;
					}
				}

				return true;
			}
		}
	}
}
