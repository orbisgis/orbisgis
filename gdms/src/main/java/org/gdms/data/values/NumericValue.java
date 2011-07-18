package org.gdms.data.values;

/**
 *
 * @author nurgle
 */
public interface NumericValue extends Value {

        /**
         * Retrieve the numeric value as a byte. Abstract, must be implemented by clients.
         * @return the value as a byte.
         */
        byte byteValue();

        /**
         * Retrieve the numeric value as a double. Abstract, must be implemented by clients.
         * @return the value as a double.
         */
        double doubleValue();

        /**
         * Retrieve the numeric value as a float. Abstract, must be implemented by clients.
         * @return the value as a float.
         */
        float floatValue();

        /**
         * Retrieve the numeric value as an int. Abstract, must be implemented by clients.
         * @return the value as an int.
         */
        int intValue();

        /**
         * Retrieve the numeric value as a long. Abstract, must be implemented by clients.
         * @return the value as a long.
         */
        long longValue();

        /**
         * Retrieve the numeric value as a short. Abstract, must be implemented by clients.
         * @return the value as a short.
         */
        short shortValue();

        /**
         * Returns the number of digits after the decimal point
         *
         * @return
         */
        int getDecimalDigitsCount();
}
