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
import java.sql.ResultSet;
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

    private String findUniqueTableName(String originalTableName) throws SQLException{
        String tableName = originalTableName;
        int offset = 0;
        while(isTableExists(tableName)) {
            tableName = originalTableName + "_" + ++offset;
        }
        return tableName;
    }

    @Override
    public String registerDataSource(URI uri) throws SQLException {
        if(!uri.isAbsolute()) {
            // Uri is incomplete, resolve it by using working directory
            uri = new File("./").toURI().resolve(uri);
        }
        String tableName = findUniqueTableName(FileUtils.getNameFromURI(uri));
        if("file".equals(uri.getScheme())) {
            File path = new File(uri);
            if(!path.exists()) {
                throw new SQLException("Specified source does not exists");
            }
            Connection connection = dataSource.getConnection();
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

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public boolean isTableExists(String tableName) throws SQLException {
        Connection connection = dataSource.getConnection();
        boolean exists = false;
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE UPPER(TABLE_NAME) = ?");
            st.setString(1, tableName.toUpperCase());
            ResultSet rs = st.executeQuery();
            exists = rs.next();
            rs.close();
        } finally {
            connection.close();
        }
        return exists;
    }
}
