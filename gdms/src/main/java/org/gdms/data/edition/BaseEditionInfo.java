package org.gdms.data.edition;

import org.gdms.driver.DBDriver;

public class BaseEditionInfo {

	protected DBDriver driver;

	public BaseEditionInfo(DBDriver driver) {
		super();
		this.driver = driver;
	}

	public String[] getReferenceExpression(String[] references) {
		String[] referenceExpressions = new String[references.length];
		for (int i = 0; i < referenceExpressions.length; i++) {
			referenceExpressions[i] = getReferenceExpression(references[i]);
		}

		return referenceExpressions;
	}


	public String getReferenceExpression(String reference) {
		return driver.getReferenceInSQL(reference);
	}

}