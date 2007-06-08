/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import org.gdms.sql.parser.Node;
import org.gdms.sql.parser.SQLEngineConstants;
import org.gdms.sql.parser.SimpleNode;

/**
 * Adaptador
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class TableRefAdapter extends Adapter {
	private String name;

	private String alias;

	/**
	 * @see org.gdms.sql.instruction.Adapter#setEntity(org.gdms.sql.parser.Node)
	 */
	public void setEntity(Node o) {
		super.setEntity(o);

		SimpleNode sn = (SimpleNode) o;

		if (sn.first_token.kind == SQLEngineConstants.STRING_LITERAL) {
			name = sn.first_token.image.substring(1);
			name = name.substring(0, name.length() - 1);
		} else {
			name = sn.first_token.image;
		}

		if (sn.last_token != sn.first_token) {
			alias = sn.last_token.image;
		}
	}

	/**
	 * Obtiene el alias de la tabla
	 * 
	 * @return Returns the alias.
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Obtiene el nombre de la tabla
	 * 
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
}
