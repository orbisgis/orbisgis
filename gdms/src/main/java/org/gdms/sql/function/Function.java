package org.gdms.sql.function;

import org.gdms.data.values.Value;


/**
 * Interface to be implemented to create a function. The name will be
 * the string used in the SQL to refeer the function. A function will be
 * created once for each instruction execution.
 */
public interface Function {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws FunctionException DOCUMENT ME!
     */
    public Value evaluate(Value[] args) throws FunctionException;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getName();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isAggregate();

    /**
     * Create a new instance of this function
     *
     * @return DOCUMENT ME!
     */
    public Function cloneFunction();

	/**
	 * Gets the type of the result this function provides.
	 *
	 * @param paramTypes
	 * @return The type of the function
	 */
	public int getType(int[] paramTypes);
}
