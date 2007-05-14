package org.gdms.data.db;

import java.sql.SQLException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DataSourceFinalizationException;
import org.gdms.data.driver.DBDriver;
import org.gdms.data.driver.DriverException;
import org.gdms.data.driver.GDBMSDriver;

import com.hardcode.driverManager.Driver;

/**
 * @author Fernando Gonz�lez Cort�s
 */
public class DBQuerySourceDefinition extends DBTableSourceDefinition {
	public String sql;

    private String viewName = null;

	private String driverName;

    /**
     * Creates a new DBQuerySourceDefinition
     *
     * @param name Name of the data source in this system
     * @param sql SQL instruction that defines the data source
     * @param driverName driver to access the data
     */
    public DBQuerySourceDefinition(DBSource def, String sql) {
		super(def);
		this.sql = sql;
	}

	public DataSource createDataSource(String tableName, String tableAlias, String driverName) throws DataSourceCreationException {
		this.driverName = driverName;
        Driver d = getDataSourceFactory().getDriverManager().getDriver(driverName);
        ((GDBMSDriver)d).setDataSourceFactory(getDataSourceFactory());

        try {
			return getDataSourceByQuery(getDataSourceFactory(), tableName, tableAlias);
		} catch (SQLException e) {
			throw new DataSourceCreationException(e);
		} catch (DriverException e) {
            throw new DataSourceCreationException(e);
        }
	}

	@Override
	public void freeResources(String name) throws DataSourceFinalizationException {
		if (viewName != null) {

			try {
				DBTableDataSourceAdapter dbds = getDataSourceByQuery(getDataSourceFactory(), name, name);
				dbds.execute("DROP VIEW " + viewName);
			} catch (SQLException e) {
				throw new DataSourceFinalizationException(e);
			} catch (DriverException e) {
                throw new DataSourceFinalizationException(e);
            }
		}
	}

    /**
     * Gets a DataSource implementation with the sql instruction as the data
     * source by creating a view in the underlaying datasource management
     * system
     *
     * @param sql Instruction definig the data source
     * @param tableName Name of the DataSource
     * @param tableAlias Alias of the DataSource
     *
     * @return DataSource
     *
     * @throws SQLException If cannot create the view in the dbms
     * @throws DriverException
     */
    private DBTableDataSourceAdapter getDataSourceByQuery(DataSourceFactory dsf,
    		String tableName, String tableAlias)
        throws SQLException, DriverException {
    	DBDriver driver = (DBDriver) dsf.getDriverManager().getDriver(driverName);
    	((GDBMSDriver) driver).setDataSourceFactory(dsf);

    	//Create the adapter
        DBTableDataSourceAdapter adapter = new DBTableDataSourceAdapter(tableName, tableAlias, def, driver);

        //Gets the view name
        String viewName = createView(dsf, adapter, sql);

        //Complete the source info with the view name
        adapter.setTableName(viewName);

        //Register the name association
        dsf.getDelegatingStrategy().registerView(tableName, viewName);

        //setup the adapter
        adapter.setDataSourceFactory(dsf);

        return adapter;
    }

    /**
     * Creates a view in the database management system that hosts the data
     * source 'dbds'. The view is defined by the sql parameter
     *
     * @param dsf DataSourceFactory
     * @param dbds DataSource used to execute the query
     * @param sql The SQL query defining the view
     *
     * @return Name of the view
     *
     * @throws DriverException If the view cannot be created
     */
    private String createView(DataSourceFactory dsf, DBTableDataSourceAdapter dbds,
    		String sql) throws SQLException {
        /*
         * Return the view name if it's already created or create the view
         * if it's not created
         */
        if (viewName != null) {
            return viewName;
        } else {
            //create the view
            viewName = dsf.getUID();
            String viewQuery = "CREATE VIEW " + viewName + " AS " + sql;
            dbds.execute(viewQuery);

            return viewName;
        }
    }
}