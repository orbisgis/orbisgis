package org.orbisgis.corejdbc.internal;

import org.apache.log4j.Logger;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.URIUtility;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.DatabaseProgressionListener;
import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.corejdbc.TableEditListener;
import org.orbisgis.corejdbc.ReadRowSet;
import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.corejdbc.StateEvent;
import org.orbisgis.utils.FileUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.sql.rowset.*;
import javax.sql.DataSource;
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
@Component(service = {DataManager.class, RowSetFactory.class})
public class DataManagerImpl implements DataManager {
    private static Logger LOGGER = Logger.getLogger(DataManagerImpl.class);
    private DataSource dataSource;
    private boolean isH2 = true;
    private boolean isLocalH2Table = true;
    private static final String H2TRIGGER = "org.orbisgis.h2triggers.H2Trigger";

    /** ReversibleRowSet fire row updates to their DataManager  */
    private Map<String, List<TableEditListener>> tableEditionListener = new HashMap<>();
    private static final Logger LOG = Logger.getLogger(DataManagerImpl.class);
    private Map<StateEvent.DB_STATES, ArrayList<DatabaseProgressionListener>> progressionListenerMap = new HashMap<>();

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
        setDataSource(dataSource);
    }

    /**
     * Default constructor for OSGi declarative services. Use {@link #setDataSource(javax.sql.DataSource)}
     */
    public DataManagerImpl() {
    }

    @Override
    public void dispose() {

    }

    @Override
    public String findUniqueTableName(String originalTableName) throws SQLException {
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
        if("file".equalsIgnoreCase(uri.getScheme())) {
            File path = new File(uri);
            if(!path.exists()) {
                throw new SQLException("Specified source does not exists");
            }
            String tableName = findUniqueTableName(FileUtils.getNameFromURI(uri).toUpperCase());
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
        } else if("jdbc".equalsIgnoreCase(uri.getScheme())) {
            // A link to a remote or local database
            try(Connection connection = dataSource.getConnection()) {
                String uriStr = uri.toString();
                if(uriStr.contains("?")) {
                    String withoutQuery = uriStr.substring(0,uriStr.indexOf("?"));
                    if(connection.getMetaData().getURL().startsWith(withoutQuery)) {
                        // Extract catalog, schema and table name
                        Map<String,String> query = URIUtility.getQueryKeyValuePairs(new URI(uri.getSchemeSpecificPart()));
                        return new TableLocation(query.get("catalog"),query.get("schema"),query.get("table")).toString();
                    }
                }
                // External JDBC connection not supported yet
                throw new SQLException("URI not supported by DataManager:\n"+uri);
            } catch (Exception ex) {
                throw new SQLException("URI not supported by DataManager:\n"+uri);
            }
        } else {
            throw new SQLException("URI not supported by DataManager:\n"+uri);
        }
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Reference
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            isH2 = JDBCUtilities.isH2DataBase(meta);
            isLocalH2Table = connection.getMetaData().getURL().startsWith("jdbc:h2:")
                    && !connection.getMetaData().getURL().startsWith("jdbc:h2:tcp:/");
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
    }

    public void unsetDataSource(DataSource dataSource) {
        this.dataSource = null;
        dispose();
    }

    @Override
    public boolean isTableExists(String tableName) throws SQLException {
        TableLocation tableLocation = TableLocation.parse(tableName);
        try (Connection connection = dataSource.getConnection();
            ResultSet rs = connection.getMetaData().getTables(tableLocation.getCatalog(), tableLocation.getSchema(), tableLocation.getTable(), null)) {
            return rs.next();
        }
    }


    @Override
    public void addTableEditListener(String table, TableEditListener listener) {
        String parsedTable = TableLocation.parse(table, isH2).toString(isH2);
        List<TableEditListener> listeners = tableEditionListener.get(parsedTable);
        if(listeners == null) {
            listeners = new ArrayList<>();
            tableEditionListener.put(parsedTable, listeners);
            // Add trigger
            if(isLocalH2Table) {
                try(Connection connection = dataSource.getConnection();
                    Statement st = connection.createStatement()) {
                    String triggerName = getH2TriggerName(table);
                    st.execute("CREATE FORCE TRIGGER "+triggerName+" AFTER INSERT, UPDATE, DELETE ON "+table+" CALL \""+H2TRIGGER+"\"");
                } catch (SQLException ex) {
                    LOGGER.error(ex.getLocalizedMessage(), ex);
                }
            }
        }
        listeners.add(listener);
    }

    @Override
    public void removeTableEditListener(String table, TableEditListener listener) {
        String parsedTable = TableLocation.parse(table).toString();
        List<TableEditListener> listeners = tableEditionListener.get(parsedTable);
        if(listeners != null) {
            listeners.remove(listener);
            if(listeners.isEmpty()) {
                // Remove trigger
                String triggerName = getH2TriggerName(table);
                try(Connection connection = dataSource.getConnection();
                    Statement st = connection.createStatement()) {
                    st.execute("DROP TRIGGER IF EXISTS "+triggerName);
                } catch (SQLException ex) {
                    LOGGER.error(ex.getLocalizedMessage(), ex);
                }

            }
        }
    }
    private static String getH2TriggerName(String table) {
        TableLocation tableIdentifier = TableLocation.parse(table, true);
        return new TableLocation(tableIdentifier.getCatalog(), tableIdentifier.getSchema(),
                "DM_"+tableIdentifier.getTable()).toString(true);
    }
    @Override
    public void fireTableEditHappened(TableEditEvent e) {
        if(e.getSource() != null) {
            String table;
            if(e.getSource() instanceof ReadRowSet) {
                table = TableLocation.parse(((ReadRowSet) e.getSource()).getTable()).toString();
            } else {
                table = e.getSource().toString();
            }
            List<TableEditListener> listeners = tableEditionListener.get(table);
            if(listeners != null) {
                for(TableEditListener listener : listeners) {
                    try {
                        listener.tableChange(e);
                    } catch (Exception ex) {
                        LOG.error(ex.getLocalizedMessage(), ex);
                    }
                }
            }
        }
    }

    @Override
    public void addDatabaseProgressionListener(DatabaseProgressionListener listener, StateEvent.DB_STATES state) {
        ArrayList<DatabaseProgressionListener> listenerList = progressionListenerMap.get(state);
        if(listenerList != null) {
            listenerList = new ArrayList<>(listenerList);
        } else {
            listenerList = new ArrayList<>();
        }
        listenerList.add(listener);
        progressionListenerMap.put(state, listenerList);
    }

    @Override
    public void removeDatabaseProgressionListener(DatabaseProgressionListener listener) {
        for(Map.Entry<StateEvent.DB_STATES,ArrayList<DatabaseProgressionListener>> entry : progressionListenerMap.entrySet()) {
            if(entry.getValue().contains(listener)) {
                ArrayList<DatabaseProgressionListener> newList = new ArrayList<>(entry.getValue());
                newList.remove(listener);
                entry.setValue(newList);
            }
        }
    }

    @Override
    public void fireDatabaseProgression(StateEvent event) {
        ArrayList<DatabaseProgressionListener> listenerList = progressionListenerMap.get(event.getStateIdentifier());
        if(listenerList != null) {
            for(DatabaseProgressionListener listener : listenerList) {
                listener.progressionUpdate(event);
            }
        }
    }
}
