
package org.gdms.data.values;

import java.sql.Time;

/**
 *
 * @author Antoine Gourlay
 */
public interface TimeValue extends Value {

    /**
     * Sets the value of the TimeValue
     *
     * @param d
     * valor
     */
    void setValue(Time d);

}