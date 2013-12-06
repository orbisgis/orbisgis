package org.orbisgis.core;

import org.h2gis.utilities.TableLocation;
import org.orbisgis.core.api.DataManager;
import org.orbisgis.utils.FileUtils;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.JoinRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import javax.sql.DataSource;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.WebRowSet;
import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of the DataManager service.
 * @author Nicolas Fortin
 */
public class DataManagerImpl implements DataManager {
    private DataSource dataSource;

    @Override
    public CachedRowSet createCachedRowSet() throws SQLException {
        RowSetFactory factory = RowSetProvider.newFactory();
        return factory.createCachedRowSet();
    }

    @Override
    public FilteredRowSet createFilteredRowSet() throws SQLException {
        RowSetFactory factory = RowSetProvider.newFactory();
        return factory.createFilteredRowSet();
    }

    @Override
    public JdbcRowSet createJdbcRowSet() throws SQLException {
        return getRowSet();
    }

    @Override
    public JoinRowSet createJoinRowSet() throws SQLException {
        RowSetFactory factory = RowSetProvider.newFactory();
        return factory.createJoinRowSet();
    }

    @Override
    public WebRowSet createWebRowSet() throws SQLException {
        RowSetFactory factory = RowSetProvider.newFactory();
        return factory.createWebRowSet();
    }

    /**
     * @param dataSource Active DataSource
     */
    public DataManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void dispose() {

    }

    private JdbcRowSet getRowSet() throws SQLException {
        RowSetFactory factory = RowSetProvider.newFactory();
        JdbcRowSet rowSet = factory.createJdbcRowSet();
        rowSet.setDataSourceName(dataSource.toString());
        return rowSet;
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
            try (Connection connection = dataSource.getConnection()) {
                // Find if a linked table use this file path
                DatabaseMetaData meta = connection.getMetaData();
                try(ResultSet tablesRs = meta.getTables(null,null,null,null)) {
                    while(tablesRs.next()) {
                        if(tablesRs.getString("REMARKS").equals(path.getAbsolutePath())) {
                            return new TableLocation(tablesRs.getString("TABLE_CAT"), tablesRs.getString("TABLE_SCHEM"), tablesRs.getString("TABLE_NAME")).toString();
                        }
                    }
                }
                // Table not found, use table link
                // TODO if tcp, use DriverManager
                PreparedStatement st = connection.prepareStatement("CALL FILE_TABLE(?,?)");
                st.setString(1, path.getAbsolutePath());
                st.setString(2, tableName);
                st.execute();
            }
            return tableName;
        } else {
            throw new SQLException("URI not supported by DataManager:\n"+uri);
        }
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public boolean isTableExists(String tableName) throws SQLException {
        boolean exists;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE UPPER(TABLE_NAME) = ?");
            st.setString(1, tableName.toUpperCase());
            ResultSet rs = st.executeQuery();
            exists = rs.next();
            rs.close();
        }
        return exists;
    }
}
