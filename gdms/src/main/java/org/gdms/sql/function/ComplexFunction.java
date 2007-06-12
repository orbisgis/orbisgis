package org.gdms.sql.function;

public interface ComplexFunction extends Function {

	/**
	 * Gets the relationships between the parameters this function
	 * receives in order to give the system the possibility of
	 * increase the performance using indexes
	 *
	 * @return
	 */
	public ParamRelationship getRelations();

}
