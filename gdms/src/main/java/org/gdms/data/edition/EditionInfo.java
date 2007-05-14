package org.gdms.data.edition;

import org.gdms.data.driver.DriverException;

/**
 * 
 */
public interface EditionInfo {
    /**
     * Gets the SQL to modify the data source or null if the data source does not have
     * to be modified
     * 
     * @return SQL statement or null
     * 
     * @throws DriverException if cannot get the SQL statement
     */
    public String getSQL() throws DriverException;
}
