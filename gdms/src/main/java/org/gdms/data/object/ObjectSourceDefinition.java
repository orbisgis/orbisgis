package org.gdms.data.object;

import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.driver.ObjectDriver;



/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class ObjectSourceDefinition extends AbstractDataSourceDefinition {

	public ObjectDriver driver;

	public ObjectSourceDefinition(ObjectDriver driver) {
		this.driver = driver;
	}

	public DataSource createDataSource(String tableName, String tableAlias, String driverName) throws DataSourceCreationException {
		ObjectDataSourceAdapter ds;
        ds = new ObjectDataSourceAdapter(tableName, tableAlias, driver);
        return ds;
	}
}
