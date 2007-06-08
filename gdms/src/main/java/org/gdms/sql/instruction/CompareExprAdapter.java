package org.gdms.sql.instruction;

import org.gdms.data.types.Type;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
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
	 * @see org.gdms.sql.instruction.Expression#evaluate(long)
	 */
	public Value evaluate(long row) throws EvaluationException {
		Adapter[] hijos = getChilds();

		if (hijos[0].getClass() == SelectAdapter.class) {
			return null;
		} else if (hijos[0].getClass() == IsClauseAdapter.class) {
			return ((IsClauseAdapter) hijos[0]).evaluate(row);
		} else if (hijos[0].getClass() == ExistsClauseAdapter.class) {
			return null;
		} else if (hijos[0] instanceof Expression) {
			int nChildren = hijos.length;

			if (nChildren == 1) {
				return ((Expression) hijos[0]).evaluateExpression(row);
			} else {
				CompareExprRigthAdapter right = (CompareExprRigthAdapter) hijos[1];
				Adapter[] hijosRight = right.getChilds();

				if (hijosRight[0].getClass() == CompareOpAdapter.class) {
					CompareOpAdapter comparator = (CompareOpAdapter) hijosRight[0];

					try {
						switch (comparator.getOperator()) {
						case SQLEngineConstants.EQUAL:
							return ((Expression) hijos[0]).evaluateExpression(
									row).equals(
									((Expression) hijosRight[1])
											.evaluateExpression(row));

						case SQLEngineConstants.NOTEQUAL:
							return ((Expression) hijos[0]).evaluateExpression(
									row).notEquals(
									((Expression) hijosRight[1])
											.evaluateExpression(row));

						case SQLEngineConstants.NOTEQUAL2:
							return ((Expression) hijos[0]).evaluateExpression(
									row).notEquals(
									((Expression) hijosRight[1])
											.evaluateExpression(row));

						case SQLEngineConstants.GREATER:
							return ((Expression) hijos[0]).evaluateExpression(
									row).greater(
									((Expression) hijosRight[1])
											.evaluateExpression(row));

						case SQLEngineConstants.GREATEREQUAL:
							return ((Expression) hijos[0]).evaluateExpression(
									row).greaterEqual(
									((Expression) hijosRight[1])
											.evaluateExpression(row));

						case SQLEngineConstants.LESS:
							return ((Expression) hijos[0]).evaluateExpression(
									row).less(
									((Expression) hijosRight[1])
											.evaluateExpression(row));

						case SQLEngineConstants.LESSEQUAL:
							return ((Expression) hijos[0]).evaluateExpression(
									row).lessEqual(
									((Expression) hijosRight[1])
											.evaluateExpression(row));

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
					BooleanValue value;
					try {
						value = (BooleanValue) ((Expression) hijos[0])
								.evaluateExpression(row)
								.like(
										ValueFactory
												.createValue(((LikeClauseAdapter) hijos[1]
														.getChilds()[0])
														.getPattern()));

						if (((LikeClauseAdapter) hijos[1].getChilds()[0])
								.isNegated()) {
							value = (BooleanValue) value.and(ValueFactory
									.createValue(false));
						}

					} catch (IncompatibleTypesException e) {
						throw new EvaluationException(e);
					}

					return value;
				} else if (hijosRight[0].getClass() == InClauseAdapter.class) {
					InClauseAdapter inAdapter = (InClauseAdapter) hijosRight[0];

					Value test = ((Expression) hijos[0])
							.evaluateExpression(row);

					boolean is = false;
					for (int i = 0; i < inAdapter.getListLength(); i++) {
						Value inElement = inAdapter.getLValue(i, row);

						if (inElement instanceof NullValue) {
							if (test instanceof NullValue) {
								is = true;
								break;
							}
						} else {
							try {
								if (((BooleanValue) test.equals(inElement))
										.getValue()) {
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

					Value test = ((Expression) hijos[0])
							.evaluateExpression(row);

					try {
						Value ret = test.less(betweenAdapter.getSupValue(row))
								.and(
										test.greater(betweenAdapter
												.getInfValue(row)));
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
}
