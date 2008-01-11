/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * Adaptador
 */
public class LValueTermAdapter extends AbstractExpression {

	private String fieldName;

	private String tableName;

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		if (fieldName == null) {
			fillFieldAndTableName();
		}

		return fieldName;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldName()
	 */
	public String getTableName() {
		if (tableName == null) {
			fillFieldAndTableName();
		}

		return tableName;
	}

	private void fillFieldAndTableName() {
		String ss = Utilities.getText(getEntity());
		String[] ret = ss.split("\\Q.\\E");
		if (ret.length == 1) {
			fieldName = ret[0];
		} else {
			tableName = ret[0].trim();
			fieldName = ret[1].trim();
		}
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#simplify()
	 */
	public void simplify() {
	}

	/**
	 * @see org.gdms.sql.instruction.CachedExpression#evaluate()
	 */
	public Value evaluate() throws EvaluationException {
		try {
			return getInstructionContext().getFieldValue(getTableName(), getFieldName());
		} catch (DriverException e) {
			throw new EvaluationException(e);
		} catch (AmbiguousFieldNameException e) {
			throw new EvaluationException(e);
		} catch (FieldNotFoundException e) {
			throw new EvaluationException(e);
		}
	}

	/**
	 * @see org.gdms.sql.instruction.CachedExpression#isLiteral()
	 */
	public boolean isLiteral() {
		return false;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#calculateLiteralCondition()
	 */
	public void calculateLiteralCondition() {
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getType()
	 */
	public int getType() throws DriverException {
		DataSource ds = getInstructionContext().getDs();
		return ds.getFieldType(ds.getFieldIndexByName(getFieldName())).getTypeCode();
	}

	public String getFieldTable() throws DriverException {
		try {
			return getInstructionContext().getTableName(getTableName(), getFieldName());
		} catch (AmbiguousFieldNameException e) {
			throw new DriverException(e);
		} catch (FieldNotFoundException e) {
			throw new DriverException(e);
		}
	}

}
