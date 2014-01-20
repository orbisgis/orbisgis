package org.orbisgis.core;

import org.apache.log4j.Logger;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.core.api.DataManager;
import org.orbisgis.core.api.ReadRowSet;
import org.orbisgis.core.api.ReversibleRowSet;
import org.orbisgis.core.jdbc.ReadRowSetImpl;
import org.orbisgis.core.jdbc.ReversibleRowSetImpl;
import org.orbisgis.utils.FileUtils;
import javax.sql.rowset.*;
import javax.sql.DataSource;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.io.File;
import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the DataManager service.
 * @author Nicolas Fortin
 */
public class DataManagerImpl implements DataManager {
    private DataSource dataSource;
    /** ReversibleRowSet fire row updates to their DataManager  */
    private Map<String, List<UndoableEditListener>> tableEditionListener = new HashMap<>();
    private static final Logger LOG = Logger.getLogger(DataManagerImpl.class);

    @Override
    public CachedRowSet createCachedRowSet() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public FilteredRowSet createFilteredRowSet() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public ReversibleRowSet createReversibleRowSet() throws SQLException {
        return new ReversibleRowSetImpl(dataSource, this);
    }

    @Override
    public ReadRowSet createReadRowSet() throws SQLException {
        return new ReadRowSetImpl(dataSource);
    }

    @Override
    public JdbcRowSet createJdbcRowSet() throws SQLException {
        return createReversibleRowSet();
    }

    @Override
    public JoinRowSet createJoinRowSet() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public WebRowSet createWebRowSet() throws SQLException {
        throw new SQLFeatureNotSupportedException();
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
                        String remarks = tablesRs.getString("REMARKS");
                        if(remarks!= null && remarks.toLowerCase().startsWith("file:")) {
                            try {
                                URI filePath = URI.create(remarks);
                                if(filePath.equals(path.toURI())) {
                                    return new TableLocation(tablesRs.getString("TABLE_CAT"), tablesRs.getString("TABLE_SCHEM"), tablesRs.getString("TABLE_NAME")).toString();
                                }
                            } catch (Exception ex) {
                                //Ignore, not an URI
                            }
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


    @Override
    public void addUndoableEditListener(String table, UndoableEditListener listener) {
        String parsedTable = TableLocation.parse(table).toString();
        List<UndoableEditListener> listeners = tableEditionListener.get(parsedTable);
        if(listeners == null) {
            listeners = new ArrayList<>();
            tableEditionListener.put(parsedTable, listeners);
        }
        listeners.add(listener);
    }

    @Override
    public void removeUndoableEditListener(String table, UndoableEditListener listener) {
        String parsedTable = TableLocation.parse(table).toString();
        List<UndoableEditListener> listeners = tableEditionListener.get(parsedTable);
        if(listeners != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public void fireUndoableEditHappened(UndoableEditEvent e) {
        if(e.getSource() != null && e.getSource() instanceof ReadRowSet) {
            String table = TableLocation.parse(((ReadRowSet) e.getSource()).getTable()).toString();
            List<UndoableEditListener> listeners = tableEditionListener.get(table);
            if(listeners != null) {
                for(UndoableEditListener listener : listeners) {
                    try {
                        listener.undoableEditHappened(e);
                    } catch (Exception ex) {
                        LOG.error(ex.getLocalizedMessage(), ex);
                    }
                }
            }
        }
    }
}
