package org.gdms.sql.instruction;

import org.gdms.data.values.Value;

public class CustomArgsAdapter extends Adapter {

	private Value[] values;

	public Value[] getValues() throws EvaluationException, SemanticException {
		if (values == null) {
			Adapter[] childs = getChilds();
			values = new Value[childs.length];
			for (int i = 0; i < values.length; i++) {
				values [i] = ((Expression) getChilds()[i]).evaluate();
			}
		}

		return values;
	}
}
