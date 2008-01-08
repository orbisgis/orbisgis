package org.gdms.sql.strategies.algebraic.preprocessor;

import java.util.ArrayList;
import java.util.HashSet;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.instruction.SemanticException;
import org.gdms.sql.strategies.algebraic.Operator;
import org.gdms.sql.strategies.algebraic.OperatorWithFieldReferences;
import org.gdms.sql.strategies.algebraic.ScalarProductOp;

public class Preprocessor {

	private Operator op;

	public Preprocessor(Operator op) {
		this.op = op;
	}

	/**
	 * Checks that the tables exist, and their aliases doesn't collide
	 *
	 * @throws NoSuchTableException
	 *             if a table in the product does not exist
	 * @throws SemanticException
	 *             if there is a conflict in the table aliases
	 */
	public void validateTableReferences() throws SemanticException,
			NoSuchTableException {
		Operator[] products = getOperators(op, new OperatorFilter() {

			public boolean accept(Operator op) {
				return op instanceof ScalarProductOp;
			}

		});

		for (Operator operator : products) {
			ScalarProductOp prod = (ScalarProductOp) operator;
			prod.validateTableReferences();
		}
	}

	/**
	 * Resolves the field and table references inside the instruction.
	 *
	 * @throws DriverException
	 *             Error accessing tables metadata
	 * @throws SemanticException
	 *             Some semantic error described by the message of the exception
	 * @throws DriverLoadException
	 *             There is not a suitable driver to access one of the sources
	 *             involved in the query
	 * @throws NoSuchTableException
	 *             A table reference does not point to an existing table
	 * @throws DataSourceCreationException
	 *             Cannot retrieve the table
	 */
	public void resolveFieldAndTableReferences() throws DriverException,
			SemanticException, DriverLoadException, NoSuchTableException,
			DataSourceCreationException {
		processTreeInDepth(op, new ResolveFieldReferencesAction());
	}

	private Operator[] getOperators(Operator op, OperatorFilter operatorFilter) {
		ArrayList<Operator> ret = new ArrayList<Operator>();

		if (operatorFilter.accept(op)) {
			ret.add(op);
		}

		for (int i = 0; i < op.getOperatorCount(); i++) {
			Operator childOperator = op.getOperator(i);
			Operator[] ops = getOperators(childOperator, operatorFilter);
			for (Operator operator : ops) {
				ret.add(operator);
			}
		}

		return ret.toArray(new Operator[0]);
	}

	private void processTreeInDepth(Operator op, TreeAction action)
			throws DriverException, SemanticException, DriverLoadException,
			NoSuchTableException, DataSourceCreationException {
		action.action(op);
		for (int i = 0; i < op.getOperatorCount(); i++) {
			processTreeInDepth(op.getOperator(i), action);
		}
	}

	private final class ResolveFieldReferencesAction implements TreeAction {

		public void action(Operator op) throws DriverException,
				SemanticException, AlreadyClosedException, DriverLoadException,
				NoSuchTableException, DataSourceCreationException {
			if (op instanceof OperatorWithFieldReferences) {
				OperatorWithFieldReferences operatorWithReferences = (OperatorWithFieldReferences) op;
				Field[] fields = (operatorWithReferences).getFieldReferences();
				for (Field field : fields) {
					// Look the scalar product operators for the field
					// references
					Operator[] products = getOperators(op,
							new OperatorFilter() {

								public boolean accept(Operator op) {
									return op instanceof ScalarProductOp;
								}

							});
					ScalarProductOp referenced = null;
					int fieldIndex = -1;
					String tableName = field.getTableName();
					String fieldName = field.getFieldName();
					for (Operator operator : products) {
						ScalarProductOp prod = (ScalarProductOp) operator;
						// If the field contains a table reference we look just
						// it
						if (tableName != null) {
							Metadata metadata = prod.getMetadata(tableName);
							if (metadata != null) {
								if (referenced != null) {
									throw new SemanticException(
											"Ambiguous field reference: "
													+ field.getTableName()
													+ "." + fieldName);
								}
								referenced = prod;
								fieldIndex = prod.getFieldIndexInProduct(
										tableName, fieldName);
							} else {
								continue;
							}
						} else {
							// If the field doesn't contain a table reference
							// iterate over the metadata of the tables
							String[] tables = prod.getTables();
							for (String tableInProduct : tables) {
								Metadata metadata = prod
										.getMetadata(tableInProduct);
								for (int i = 0; i < metadata.getFieldCount(); i++) {
									if (metadata.getFieldName(i).equals(
											fieldName)) {
										if (referenced != null) {
											throw new SemanticException(
													"Ambiguous field reference: "
															+ fieldName);
										}
										referenced = prod;
										fieldIndex = prod
												.getFieldIndexInProduct(
														tableName, fieldName);
									}
								}
							}
						}
					}

					if (referenced == null) {
						throw new SemanticException("Field not found: "
								+ field.toString());
					} else {
						field.setFieldIndex(fieldIndex);
						operatorWithReferences.setDependency(referenced);
					}
				}
			}

		}
	}

	private interface TreeAction {
		void action(Operator op) throws DriverException, SemanticException,
				AlreadyClosedException, DriverLoadException,
				NoSuchTableException, DataSourceCreationException;
	}

	private interface OperatorFilter {
		boolean accept(Operator op);
	}

}
