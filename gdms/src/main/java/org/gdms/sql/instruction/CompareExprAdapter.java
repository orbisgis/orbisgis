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
package org.gdms.sql.instruction;

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.parser.SQLEngineConstants;

/**
 * Adaptador sobre los nodos que representan una expresi�n condicional en el
 * arbol sint�ctico
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class CompareExprAdapter extends AbstractExpression implements
		Expression {
	/**
	 * @see org.gdms.sql.instruction.Expression#evaluate()
	 */
	public Value evaluate() throws EvaluationException {
		Adapter[] hijos = getChilds();

		if (hijos[0].getClass() == SelectAdapter.class) {
			return null;
		} else if (hijos[0].getClass() == IsClauseAdapter.class) {
			return ((IsClauseAdapter) hijos[0]).evaluate();
		} else if (hijos[0].getClass() == ExistsClauseAdapter.class) {
			return null;
		} else if (hijos[0] instanceof Expression) {
			int nChildren = hijos.length;

			if (nChildren == 1) {
				return ((Expression) hijos[0]).evaluateExpression();
			} else {
				CompareExprRigthAdapter right = (CompareExprRigthAdapter) hijos[1];
				Adapter[] hijosRight = right.getChilds();

				if (hijosRight[0].getClass() == CompareOpAdapter.class) {
					CompareOpAdapter comparator = (CompareOpAdapter) hijosRight[0];

					try {
						switch (comparator.getOperator()) {
						case SQLEngineConstants.EQUAL:
							return ((Expression) hijos[0]).evaluateExpression()
									.equals(
											((Expression) hijosRight[1])
													.evaluateExpression());

						case SQLEngineConstants.NOTEQUAL:
							return ((Expression) hijos[0]).evaluateExpression()
									.notEquals(
											((Expression) hijosRight[1])
													.evaluateExpression());

						case SQLEngineConstants.NOTEQUAL2:
							return ((Expression) hijos[0]).evaluateExpression()
									.notEquals(
											((Expression) hijosRight[1])
													.evaluateExpression());

						case SQLEngineConstants.GREATER:
							return ((Expression) hijos[0]).evaluateExpression()
									.greater(
											((Expression) hijosRight[1])
													.evaluateExpression());

						case SQLEngineConstants.GREATEREQUAL:
							return ((Expression) hijos[0]).evaluateExpression()
									.greaterEqual(
											((Expression) hijosRight[1])
													.evaluateExpression());

						case SQLEngineConstants.LESS:
							return ((Expression) hijos[0]).evaluateExpression()
									.less(
											((Expression) hijosRight[1])
													.evaluateExpression());

						case SQLEngineConstants.LESSEQUAL:
							return ((Expression) hijos[0]).evaluateExpression()
									.lessEqual(
											((Expression) hijosRight[1])
													.evaluateExpression());

						default:
							throw new RuntimeException(
									"Nunca debi� llegar aqu�");
						}
					} catch (IncompatibleTypesException e) {
						throw new EvaluationException(e);
					} catch (EvaluationException e) {
						throw new EvaluationException(e);
					}

				} else if (hijosRight[0].getClass() == LikeClauseAdapter.class) {
					Value value;
					try {
						value = ((Expression) hijos[0])
								.evaluateExpression()
								.like(
										ValueFactory
												.createValue(((LikeClauseAdapter) hijos[1]
														.getChilds()[0])
														.getPattern()));

						if (((LikeClauseAdapter) hijos[1].getChilds()[0])
								.isNegated()) {
							value = value.and(ValueFactory.createValue(false));
						}

					} catch (IncompatibleTypesException e) {
						throw new EvaluationException(e);
					}

					return value;
				} else if (hijosRight[0].getClass() == InClauseAdapter.class) {
					InClauseAdapter inAdapter = (InClauseAdapter) hijosRight[0];

					Value test = ((Expression) hijos[0]).evaluateExpression();

					boolean is = false;
					for (int i = 0; i < inAdapter.getListLength(); i++) {
						Value inElement = inAdapter.getLValue(i);

						if (inElement.isNull()) {
							if (test.isNull()) {
								is = true;
								break;
							}
						} else {
							try {
								if (test.equals(inElement).getAsBoolean()) {
									is = true;
									break;
								}
							} catch (IncompatibleTypesException e) {
								throw new EvaluationException(e);
							}
						}
					}
					if (inAdapter.isNegated()) {
						is = !is;
					}
					return ValueFactory.createValue(is);

				} else if (hijosRight[0].getClass() == BetweenClauseAdapter.class) {
					BetweenClauseAdapter betweenAdapter = (BetweenClauseAdapter) hijosRight[0];

					Value test = ((Expression) hijos[0]).evaluateExpression();

					try {
						Value ret = test
								.less(betweenAdapter.getSupValue())
								.and(test.greater(betweenAdapter.getInfValue()));
						if (betweenAdapter.isNegated())
							return ret.inversa();
						else
							return ret;

					} catch (IncompatibleTypesException e) {
						throw new EvaluationException(e);
					}
				} else {
					throw new RuntimeException("Nunca debi� llegar aqu�");
				}
			}
		} else {
			// Ha pasado el parsing sintactico, no se produce nunca este caso
			throw new RuntimeException("Nunca debi� llegar aqu�");
		}
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		return null;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#isLiteral()
	 */
	public boolean isLiteral() {
		Adapter[] hijos = getChilds();

		if (hijos[0].getClass() == SelectAdapter.class) {
			return false;
		} else if (hijos[0].getClass() == IsClauseAdapter.class) {
			return false;
		} else if (hijos[0].getClass() == ExistsClauseAdapter.class) {
			return false;
		} else if (hijos[0] instanceof Expression) {
			if (hijos.length == 1) {
				return ((Expression) hijos[0]).isLiteral();
			} else {
				CompareExprRigthAdapter right = (CompareExprRigthAdapter) hijos[1];
				Adapter[] hijosRight = right.getChilds();

				if (hijosRight[0].getClass() == CompareOpAdapter.class) {
					return ((Expression) hijos[0]).isLiteral()
							&& ((Expression) hijosRight[1]).isLiteral();
				} else if (hijosRight[0].getClass() == LikeClauseAdapter.class) {
					return ((Expression) hijos[0]).isLiteral();
				} else if (hijosRight[0].getClass() == InClauseAdapter.class) {
					return false;
				} else if (hijosRight[0].getClass() == BetweenClauseAdapter.class) {
					return false;
				} else {
					throw new RuntimeException("Nunca debi� llegar aqu�");
				}
			}
		} else {
			// Ha pasado el parsing sintactico, no se produce nunca este caso
			throw new RuntimeException("Nunca debi� llegar aqu�");
		}
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#simplify()
	 */
	public void simplify() {
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#calculateLiteralCondition()
	 */
	public void calculateLiteralCondition() {
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getType()
	 */
	public int getType() {
		return Type.BOOLEAN;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldTable()
	 */
	public String getFieldTable() throws DriverException {
		return null;
	}

	public Iterator<PhysicalDirection> filter(DataSource from)
			throws DriverException {
		Adapter[] childs = getChilds();
		if (childs.length == 1) {
			return ((Expression) childs[0]).filter(from);
		} else {
			return null;
		}
	}
}
