package org.gdms.sql.strategies;

import java.util.HashSet;
import java.util.Set;

import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;

/**
 * Class that validates all field, table and function references, the types of
 * the expressions, etc
 *
 * @author Fernando Gonzalez Cortes
 *
 */
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
	public void resolveFieldAndTableReferences() throws DriverException,
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
		resolveFieldAndTableReferences();
		validateExpressionTypes();
		validateDuplicatedFields();
	}
}
