/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

/**
 * Adaptador
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class WhereAdapter extends Adapter {
	/**
	 * Obtiene la expresi�n del where
	 * 
	 * @return Expression
	 */
	public Expression getExpression() {
		// Ha de ser un OrExprAdapter
		return (Expression) getChilds()[0];
	}
}
