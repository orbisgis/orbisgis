package org.orbisgis.core;
import com.sun.rowset.JdbcRowSetImpl;
import org.orbisgis.core.api.DataManager;
import org.orbisgis.utils.FileUtils;


import javax.sql.DataSource;
import javax.sql.RowSet;
import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Implementation of the DataManager service.
 * @author Nicolas Fortin
 */
public class DataManagerImpl implements DataManager {
    DataSource dataSource;

    /**
     * @param dataSource Active DataSource
     */
    public DataManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void dispose() {

    }

    @Override
    public RowSet getDataSource(String sourceName) throws SQLException {
        JdbcRowSetImpl rowSet = new JdbcRowSetImpl(dataSource.getConnection());
        rowSet.setCommand("SELECT * FROM "+sourceName);
        return rowSet;
    }

    @Override
    public RowSet getDataSource(PreparedStatement statement) throws SQLException {
        return new JdbcRowSetImpl(statement.executeQuery());
    }

    @Override
    public String registerDataSource(URI uri) throws SQLException {
        Connection connection = dataSource.getConnection();
        String tableName = FileUtils.getNameFromURI(uri);
        if("file".equals(uri.getScheme())) {
            File path = new File(uri);
            try {
                connection.createStatement().execute("CALL FILE_TABLE('"+path.getAbsolutePath()+"','"+tableName+"')");
            } finally {
                connection.close();
            }
            return tableName;
        } else {
            throw new SQLException("URI not supported by DataManager:\n"+uri);
        }
    }

    @Override
    public URI getDataSourceUri(String tableReference) throws SQLException {
        // TODO Compute DataSource and table URI
        return null;
    }
}
