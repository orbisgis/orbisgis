package org.gdms.sql.strategies;

import org.gdms.sql.evaluator.Field;

/**
 * Indicates that a field cannot be resolved to only one of the referenced
 * tables in an SQL instruction
 * 
 * @author Fernando Gonzalez Cortes
 */
public class AmbiguousFieldReferenceException extends SemanticException {

	public AmbiguousFieldReferenceException(Field field) {
		super("Ambiguous field reference: " + field);
	}
}
