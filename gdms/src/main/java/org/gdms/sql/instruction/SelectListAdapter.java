/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import java.util.ArrayList;

import org.gdms.sql.parser.SQLEngineConstants;
import org.gdms.sql.parser.SimpleNode;
import org.gdms.sql.parser.Token;

/**
 * Adaptador
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class SelectListAdapter extends Adapter {
	private Expression[] fieldExpressions;

	private String[] fieldAliases;

	/**
	 * Obtiene las expresiones de los campos y los alias
	 */
	private void selectList() {
		Adapter[] hijos = getChilds();

		ArrayList<Expression> exprs = new ArrayList<Expression>();

		ArrayList<String> aliases = new ArrayList<String>();
		for (int i = 0; i < hijos.length; i++) {
			exprs.add((Expression) hijos[i]);
			SimpleNode node = hijos[i].getEntity();
			Token token = node.first_token;
			while (token != node.last_token) {
				token = token.next;
			}
			token = token.next;
			String alias = null;
			if (token.kind == SQLEngineConstants.AS) {
				token = token.next;
				alias = token.image;
			}
			aliases.add(alias);
		}

		fieldAliases = (String[]) aliases.toArray(new String[0]);
		fieldExpressions = (Expression[]) exprs.toArray(new Expression[0]);
	}

	/**
	 * Obtiene las expresiones de los campos
	 *
	 * @return
	 */
	public Expression[] getFieldsExpression() {
		if (fieldExpressions == null) {
			selectList();
		}

		return fieldExpressions;
	}

	/**
	 * Obtiene los alias de los campos
	 *
	 * @return
	 */
	public String[] getFieldsAlias() {
		if (fieldAliases == null) {
			selectList();
		}

		return fieldAliases;
	}
}
