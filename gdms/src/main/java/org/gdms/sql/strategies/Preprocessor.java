package org.gdms.sql.strategies;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;
import org.gdms.sql.strategies.joinOptimization.BNB;
import org.gdms.sql.strategies.joinOptimization.BNBNode;

/**
 * Class that validates all field, table and function references, the types of
 * the expressions, etc
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public class Preprocessor {

	private static final Logger logger = Logger.getLogger(Preprocessor.class);

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
	 * @throws DriverException
	 */
	public void validateTableReferences() throws SemanticException,
			NoSuchTableException, DriverException {
		op.validateTableReferences();
	}

	/**
	 * Resolves the field and table references inside the instruction.
	 *
	 * @throws DriverException
	 *             Error accessing tables metadata
	 * @throws SemanticException
	 *             Some semantic error described by the message of the exception
	 */
	public void resolveFieldReferences() throws DriverException,
			SemanticException {
		op.validateFieldReferences();
	}

	/**
	 * Gets the metadata of the result
	 *
	 * @return
	 * @throws DriverException
	 */
	public Metadata getResultMetadata() throws DriverException {
		return op.getResultMetadata();
	}

	/**
	 * Validates the types of the expressions
	 *
	 * @throws SemanticException
	 *             If there is some error in the validation
	 * @throws DriverException
	 *             Error accessing data
	 */
	public void validateExpressionTypes() throws SemanticException,
			DriverException {
		op.validateExpressionTypes();
	}

	public void validateDuplicatedFields() throws DriverException,
			SemanticException {
		Metadata metadata = op.getResultMetadata();
		if (metadata != null) {
			Set<String> fieldNames = new HashSet<String>();
			for (int i = 0; i < metadata.getFieldCount(); i++) {
				String fieldName = metadata.getFieldName(i);
				if (fieldNames.contains(fieldName)) {
					throw new SemanticException("Field " + fieldName
							+ " is duplicated");
				} else {
					fieldNames.add(fieldName);
				}
			}
		}
	}

	public void validateFunctionReferences() throws DriverException,
			SemanticException {
		op.validateFunctionReferences();
	}

	public void validate() throws SemanticException, DriverException,
			NoSuchTableException {
		validateTableReferences();
		validateFunctionReferences();
		op.prepareValidation();
		resolveFieldReferences();
		validateExpressionTypes();
		validateDuplicatedFields();
		op.setValidated(true);
	}

	public void optimize(DataSourceFactory dsf) throws DriverException,
			SemanticException {
		resolveFieldSourceReferences(dsf.getSourceManager());

		// Push selections down
		pushDownSelections();

		// Find best join strategy
		BNB bnb = new BNB(dsf.getIndexManager(), dsf.getSourceManager());
		BNBNode node = bnb.optimize(op);
		if (node != null) {
			node.replaceScalarProduct(dsf.getIndexManager());

			Operator[] selections = op.getOperators(new OperatorFilter() {

				public boolean accept(Operator op) {
					return op instanceof SelectionOp;
				}

			});
			chooseScanStrategy(dsf, selections);
		}

		resolveFieldReferences();

		logger.debug("Optimized Query: " + op.toString());
	}

	private void resolveFieldSourceReferences(SourceManager sourceManager)
			throws DriverException, SemanticException {
		op.resolveFieldSourceReferences(sourceManager);
	}

	private void chooseScanStrategy(DataSourceFactory dsf, Operator[] selections)
			throws DriverException, NoSuchTableException {
		for (Operator operator : selections) {
			SelectionOp selection = (SelectionOp) operator;
			if (selection.getIndexQueries() == null) {
				selection.chooseScanStrategy(dsf.getIndexManager());
			}
			selection.setScanMode(selection.getIndexQueries());
		}
	}

	private void pushDownSelections() throws DriverException, SemanticException {
		Operator[] selections = op.getOperators(new OperatorFilter() {

			public boolean accept(Operator op) {
				return op instanceof SelectionOp;
			}

		});

		for (Operator operator : selections) {
			Operator selectionChild = operator.getOperator(0);
			if (selectionChild instanceof SelectionTransporter) {
				((SelectionTransporter) selectionChild)
						.transportSelection((SelectionOp) operator);
			}
		}
	}
}
