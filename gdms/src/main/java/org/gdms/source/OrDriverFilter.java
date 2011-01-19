
package org.gdms.source;

import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverFilter;

/**
 *
 * @author Antoine Gourlay
 */
public class OrDriverFilter implements DriverFilter {

        private DriverFilter[] filters;

	public OrDriverFilter(DriverFilter... filters) {
		this.filters = filters;
	}

        @Override
        public boolean acceptDriver(Driver driver) {
                for (DriverFilter filter : filters) {
			if (filter.acceptDriver(driver)) {
				return true;
			}
		}

		return false;
        }

}
