/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;
import javax.swing.*;

import org.apache.log4j.Logger;
import org.h2gis.utilities.JDBCUtilities;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.DatabaseProgressionListener;
import org.orbisgis.corejdbc.StateEvent;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.view.geocatalog.filters.IFilter;
import org.orbisgis.view.geocatalog.filters.TableSystemFilter;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import static org.apache.commons.collections.ComparatorUtils.NATURAL_COMPARATOR;

/**
 * Manage entries of GeoCatalog according to a GDMS SourceManager
 * SourceListModel is a swing component that update the content of the geocatalog
 * according to the SourceManager content and the filter loaded.
 */
public class SourceListModel extends AbstractListModel<ContainerItemProperties> implements DatabaseProgressionListener {
    private static final I18n I18N = I18nFactory.getI18n(SourceListModel.class);
    private static final Logger LOGGER = Logger.getLogger(SourceListModel.class);
    private static final long serialVersionUID = 1L;
    private static final String[] SHOWN_TABLE_TYPES = new String[]{"TABLE", "SYSTEM TABLE","LINKED TABLE","VIEW", "EXTERNAL"};
    /** Non filtered tables */
    private List<Map<IFilter.ATTRIBUTES, String>> allTables = new ArrayList<>();
    /** Filtered tables */
    private ContainerItemProperties[] sourceList = new ContainerItemProperties[0];/*!< Sources */
    private List<IFilter> filters = new ArrayList<IFilter>(); /*!< Active filters */
    private DefaultFilter defaultFilter = new DefaultFilter();
    private AtomicBoolean awaitingRefresh=new AtomicBoolean(false); /*!< If true a swing runnable
         * is pending to refresh the content of SourceListModel*/
    private boolean updateWhileAwaitingRefresh = false;
    private DataSource dataSource;
    private CatalogComparator catalogComparator = new CatalogComparator();
    private boolean isH2;
    private Map<String, Integer> columnMap = new HashMap<>();
    // Update Geocatalog only if query starts with this command. (lowercase)
    private static final String[] updateSourceListQuery = new String[] {"drop", "create","alter"};
    private static final int MAX_LENGTH_QUERY;
    static {
        int maxLen = 0;
        for(String query : updateSourceListQuery) {
            maxLen = Math.max(maxLen, query.length());
        }
        MAX_LENGTH_QUERY = maxLen;
    }
    /**
     * Read filters components and generate filter instances
     * @return A list of filters
     */
    public List<IFilter> getFilters() {
        return filters;
    }

    /**
     * Constructor
     * @note Do not forget to call dispose()
     */
    public SourceListModel(DataManager dataManager) {
        this.dataSource = dataManager.getDataSource();
        try(Connection connection = dataSource.getConnection()) {
            isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
        //Install database listeners
        dataManager.addDatabaseProgressionListener(this, StateEvent.DB_STATES.STATE_STATEMENT_END);
        //Call readDatabase when a SourceManager fire an event
        onDataManagerChange();
    }

    @Override
    public void progressionUpdate(StateEvent state) {
        // DataBase update
        String name = state.getName();
        if(name != null) {
            name = name.substring(0,MAX_LENGTH_QUERY).trim().toLowerCase();
            for(String query : updateSourceListQuery) {
                if(name.startsWith(query)) {
                    onDataManagerChange();
                    break;
                }
            }
        }
    }

    /**
     * Install listener(s) on SourceManager
     */
    public void setListeners() {
        // TODO, set a timer that hash table list
    }
    /**
     * The DataManager fire a DataSourceEvent
     * Swing will update the list later.
     * This method is called by the EventSource listener
     */
    public void onDataManagerChange() {
        //This is useless to invoke a refresh thread because
        //The content will be refresh is coming soon fired by another ReadDataManagerOnSwingThread
        if(!awaitingRefresh.getAndSet(true)) {
            ReadDataManagerOnSwingThread worker = new ReadDataManagerOnSwingThread(this);
            worker.execute();
        } else {
            updateWhileAwaitingRefresh = true;
        }
    }
    /**
     * Refresh the JList on the swing thread
     */
    private static class ReadDataManagerOnSwingThread extends SwingWorker<Boolean, Boolean> {
        private SourceListModel model;

        private ReadDataManagerOnSwingThread(SourceListModel model) {
            this.model = model;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            model.readDatabase();
            return true;
        }

        @Override
        protected void done() {
            //Refresh the JList on the swing thread
            model.doFilter();
            model.awaitingRefresh.set(false);
            // An update occurs during fetching tables
            if(model.updateWhileAwaitingRefresh) {
                model.updateWhileAwaitingRefresh = false;
                model.onDataManagerChange();
            }
        }
    }

    /**
     * TODO stop timers
     */
    public void dispose() {

    }

    /**
     * Find the icon corresponding to a table reference
     */
    private String getIconName(TableLocation location, Map<IFilter.ATTRIBUTES, String> attr) {
        if(attr.containsKey(IFilter.ATTRIBUTES.GEOMETRY_TYPE)) {
            return "geofile";
        }
        String tableType = attr.get(IFilter.ATTRIBUTES.TABLE_TYPE);
        if(tableType != null) {
            switch(tableType) {
                case "SYSTEM_TABLE":
                    return "drive";
                case "LINKED TABLE":
                    return "database";
                default:
                    return "flatfile";
            }
        } else {
            return "flatfile";
        }
        //"remove";
        //"image";
        //"server_connect";
        // information_geo // Unknown
    }

    private static String addQuotesIfNecessary(String tableLocationPart) {
        if(tableLocationPart.contains(".")) {
            return "\""+tableLocationPart+"\"";
        } else {
            return tableLocationPart;
        }
    }

    protected void doFilter() {
        boolean checkForDefaultFilter = true;
        for(IFilter filter : filters) {
            if(filter instanceof TableSystemFilter) {
                checkForDefaultFilter = false;
            }
        }
        List<CatalogSourceItem> newModel = new LinkedList<>();
        for(Map<IFilter.ATTRIBUTES, String> tableAttr : allTables) {
            boolean accepts = true;
            TableLocation location = TableLocation.parse(tableAttr.get(IFilter.ATTRIBUTES.LOCATION), isH2);
            for(IFilter filter : filters) {
                if(!filter.accepts(location,tableAttr)) {
                    accepts = false;
                    break;
                }
            }
            if(accepts && (!checkForDefaultFilter || defaultFilter.accepts(location, tableAttr))) {
                newModel.add(new CatalogSourceItem(location.toString(isH2), tableAttr.get(IFilter.ATTRIBUTES.LABEL), getIconName(location, tableAttr)));
            }
        }
        Collections.sort(newModel, catalogComparator);
        int oldLength = sourceList.length;
        sourceList = new ContainerItemProperties[0];
        fireIntervalRemoved(this, 0, oldLength);
        sourceList = newModel.toArray(new ContainerItemProperties[newModel.size()]);
        fireIntervalAdded(this, 0, this.sourceList.length);
    }

    /**
     * Read the table list in the database
     */
    protected void readDatabase() {
        List<Map<IFilter.ATTRIBUTES, String>> newTables = new ArrayList<>(allTables.size());
        try (Connection connection = dataSource.getConnection()) {
            final String defaultCatalog = connection.getCatalog();
            String defaultSchema = "PUBLIC";
            try {
                if (connection.getSchema() != null) {
                    defaultSchema = connection.getSchema();
                }
            } catch (AbstractMethodError | Exception ex) {
                // Driver has been compiled with JAVA 6, or is not implemented
            }
            catalogComparator.setDefaultSchema(defaultSchema);
            // Fetch Geometry tables
            Map<String,String> tableGeometry = new HashMap<>();
            try(Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM geometry_columns")) {
                    while(rs.next()) {
                        tableGeometry.put(new TableLocation(rs.getString("F_TABLE_CATALOG"),
                                rs.getString("F_TABLE_SCHEMA"), rs.getString("F_TABLE_NAME")).toString(), rs.getString("TYPE"));
                    }
            } catch (SQLException ex) {
                LOGGER.warn(I18N.tr("Geometry columns information of tables are not available"), ex);
            }
            // Fetch all tables
            try(ResultSet rs = connection.getMetaData().getTables(null, null, null, SHOWN_TABLE_TYPES)) {
                while(rs.next()) {
                    Map<IFilter.ATTRIBUTES, String> tableAttr = new HashMap<>(IFilter.ATTRIBUTES.values().length);
                    TableLocation location = new TableLocation(rs);
                    if(location.getCatalog().isEmpty()) {
                        // PostGIS return empty catalog on metadata
                        location = new TableLocation(defaultCatalog, location.getSchema(), location.getTable());
                    }
                    // Make Label
                    StringBuilder label = new StringBuilder(addQuotesIfNecessary(location.getTable()));
                    if(!location.getSchema().isEmpty() && !location.getSchema().equalsIgnoreCase(defaultSchema)) {
                        label.insert(0, ".");
                        label.insert(0, addQuotesIfNecessary(location.getSchema()));
                    }
                    if(!location.getCatalog().isEmpty() && !location.getCatalog().equalsIgnoreCase(defaultCatalog)) {
                        label.insert(0, ".");
                        label.insert(0, addQuotesIfNecessary(location.getCatalog()));
                    }
                    // Shortcut location for H2 database
                    TableLocation shortLocation;
                    if(isH2) {
                        shortLocation = new TableLocation("",
                                location.getSchema().equals(defaultSchema) ? "" : location.getSchema(),
                                location.getTable());
                    } else {
                        shortLocation = new TableLocation(location.getCatalog().equalsIgnoreCase(defaultCatalog) ?
                                "" : location.getCatalog(),
                                location.getCatalog().equalsIgnoreCase(defaultCatalog) &&
                                        location.getSchema().equalsIgnoreCase(defaultSchema) ? "" : location.getSchema(),
                                location.getTable());
                    }
                    tableAttr.put(IFilter.ATTRIBUTES.LOCATION, shortLocation.toString(isH2));
                    tableAttr.put(IFilter.ATTRIBUTES.LABEL, label.toString());
                    for(IFilter.ATTRIBUTES attribute : IFilter.ATTRIBUTES.values()) {
                        putAttribute(tableAttr, attribute, rs);
                    }
                    String type = tableGeometry.get(location.toString());
                    if(type != null) {
                        tableAttr.put(IFilter.ATTRIBUTES.GEOMETRY_TYPE, type);
                    }
                    newTables.add(tableAttr);
                }
            }
            allTables = newTables;
        } catch (SQLException ex) {
            LOGGER.error(I18N.tr("Cannot read the table list"), ex);
        }
    }

    private void putAttribute(Map<IFilter.ATTRIBUTES, String> tableAttr, IFilter.ATTRIBUTES attribute, ResultSet rs) {
        try {
            String columnName = attribute.toString().toLowerCase();
            Integer columnId = columnMap.get(columnName);
            if(columnId == null) {
                ResultSetMetaData meta = rs.getMetaData();
                columnMap.put(columnName, 0);
                for(int i=1; i<=meta.getColumnCount(); i++) {
                    if(columnName.equals(meta.getCatalogName(i).toLowerCase())) {
                        columnMap.put(columnName, i);
                        columnId = i;
                        break;
                    }
                }
            }
            if(columnId != null && columnId > 0) {
                tableAttr.put(attribute, rs.getString(columnId));
            }
        } catch (SQLException ex) {
            // Ignore
        }
    }

    /**
     *
     * @param index The item index @see getSize()
     * @return The item
     */
    @Override
    public ContainerItemProperties getElementAt(int index) {
        return sourceList[index];
    }

    /**
     *
     * @return The number of source shown
     */
    @Override
    public int getSize() {
        return sourceList.length;
    }

    /**
     * Set the filter and refresh the Source list
     * according to the new filter
     * @param filters A collection of filters
     */
    public void setFilters(List<IFilter> filters) {
        this.filters = filters;
        doFilter();
    }

    /**
     * Remove all filters and refresh the Source list
     */
    public void clearFilters() {
        this.filters.clear();
        doFilter();
    }

    /**
     * This filter is always applied, to hide system tables
     */
    private static final class DefaultFilter implements IFilter {
        private TableSystemFilter filter = new TableSystemFilter();

        @Override
        public boolean accepts(TableLocation table, Map<ATTRIBUTES, String> tableProperties) {
            return !filter.accepts(table, tableProperties);
        }
    }

    private static class CatalogComparator implements Comparator<CatalogSourceItem> {
        private String defaultSchema = "PUBLIC";
        @Override
        public int compare(CatalogSourceItem left, CatalogSourceItem right) {
            TableLocation locationLeft = TableLocation.parse(left.getKey());
            TableLocation locationRight = TableLocation.parse(right.getKey());
            int tmpCompare = 0;
            // Sort by catalog
            tmpCompare = NATURAL_COMPARATOR.compare(locationLeft.getCatalog(), locationRight.getCatalog());
            if(tmpCompare != 0) {
                return tmpCompare;
            }
            // If catalog the same, sort by schema (default first)
            tmpCompare = NATURAL_COMPARATOR.compare(locationLeft.getSchema(), locationRight.getSchema());
            if(tmpCompare != 0) {
                if(locationLeft.getSchema().equals(defaultSchema)) {
                    return -1;
                } else if(locationRight.getSchema().equalsIgnoreCase(defaultSchema)) {
                    return 1;
                } else {
                    return tmpCompare;
                }
            }
            // if schema the same, sort by table
            return NATURAL_COMPARATOR.compare(locationLeft.getTable(), locationRight.getTable());
        }

        public void setDefaultSchema(String defaultSchema) {
            this.defaultSchema = defaultSchema;
        }
    }
}
