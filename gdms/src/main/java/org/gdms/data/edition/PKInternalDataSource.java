package org.gdms.data.edition;

import java.sql.Connection;
import java.sql.SQLException;

import org.gdms.data.DataSource;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DriverException;


public interface PKInternalDataSource extends DataSource {

    public String[] getPKNames() throws DriverException;

    public String[] getFieldNames() throws DriverException;
    
    public long getOriginalRowCount() throws DriverException;

    public Connection getConnection() throws SQLException;

    public void execute(String sql) throws SQLException;

    public ValueCollection getPKValue(long rowIndex) throws DriverException;
}
