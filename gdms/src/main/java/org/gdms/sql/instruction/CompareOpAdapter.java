/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

/**
 * Adaptador
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class CompareOpAdapter extends Adapter {
	/**
	 * Obtiene el operador
	 *
	 * @return opertador
	 */
	public int getOperator() {
		return getEntity().first_token.kind;
	}
}
