package org.gdms.data.values;

import java.util.Date;

/**
 *
 * @author nurgle
 */
public interface DateValue extends Value {

    /**
     * Set the stored datvalue.
     *
     * @param d
     * The new date
     */
    void setValue(Date d);

}
