
package org.gdms.data.values;

import java.sql.Timestamp;

/**
 *
 * @author Antoine Gourlay
 */
public interface TimestampValue extends Value {

    /**
     * Sets the value of this TimestampValue
     *
     * @param d
     * valor
     */
    void setValue(Timestamp d);

}