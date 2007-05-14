/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

/**
 * Adaptador
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class SelectColsAdapter extends Adapter {
	/**
	 * Devuelve si se utiliz� el modificado DISTINCT
	 *
	 * @return boolean
	 */
	public boolean isDistinct() {
		String text = Utilities.getText(getEntity());

		if (text.trim().startsWith("distinct")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Obtiene las expresiones de los campos
	 *
	 * @return
	 */
	public Expression[] getFieldsExpression() {
		String text = Utilities.getText(getEntity()).trim();

		if (text.endsWith("*")) {
			return null;
		} else {
			return ((SelectListAdapter) getChilds()[0]).getFieldsExpression();
		}
	}

	/**
	 * Obtiene los alias de los campos
	 *
	 * @return
	 */
	public String[] getFieldsAlias() {
		String text = Utilities.getText(getEntity()).trim();

		if (text.endsWith("*")) {
			return null;
		} else {
			return ((SelectListAdapter) getChilds()[0]).getFieldsAlias();
		}
	}
}
