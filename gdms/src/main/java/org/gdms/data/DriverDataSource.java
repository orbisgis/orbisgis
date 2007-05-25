package org.gdms.data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Fernando Gonz�lez Cort�s
 * 
 * Used by the CheckOpenDataSource aspect. Indicates the InternalDataSource accesses
 * directly the driver.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DriverDataSource {
}
