package org.orbisgis.core.ui.views.information;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;

/**
 * Service to show data information to the user
 * 
 * @author Fernando Gonzalez Cortes
 */
public interface InformationManager {

	/**
	 * Set the contents shown to the user by the information manager
	 * 
	 * @param ds
	 * @throws DriverException
	 *             If the data source cannot be opened
	 */
	void setContents(DataSource ds) throws DriverException;

}
