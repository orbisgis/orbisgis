package org.gdms.data.values;

/**
 *
 * @author Antoine Gourlay
 */
public interface ValueCollection extends Value {

    /**
     * Sets the values of this ValueCollection
     *
     * @param values
     */
    void setValues(Value[] values);

    /**
     * Gets the ith value of the array
     *
     * @param i
     *
     * @return
     */
    Value get(int i);

    /**
     * Gets the Value objects in this ValueCollection
     *
     * @return an array of Value
     */
    Value[] getValues();

}