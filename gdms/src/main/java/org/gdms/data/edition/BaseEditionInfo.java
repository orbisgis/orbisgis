package org.gdms.data.edition;

import org.gdms.driver.DBDriver;
import org.gdms.driver.DBReadWriteDriver;

public abstract class BaseEditionInfo implements EditionInfo {

	public String[] getReferenceExpression(DBDriver driver, String[] references) {
		String[] referenceExpressions = new String[references.length];
		for (int i = 0; i < referenceExpressions.length; i++) {
			referenceExpressions[i] = getReferenceExpression(driver, references[i]);
		}

		return referenceExpressions;
	}


	public String getReferenceExpression(DBDriver driver, String reference) {
		return ((DBReadWriteDriver) driver).getReferenceInSQL(reference);
	}

}