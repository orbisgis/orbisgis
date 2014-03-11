/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.h2gis.utilities.SFSUtilities;
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
public class SourceListModel extends AbstractListModel<ContainerItemProperties> {
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
    private DataSource dataSource;
    private CatalogComparator catalogComparator = new CatalogComparator();

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
    public SourceListModel(DataSource dataSource) {
        this.dataSource = dataSource;
        //Install listeners
        //Call readDatabase when a SourceManager fire an event
        onDataManagerChange();
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
        //The content will be soonly refreshed by another ReadDataManagerOnSwingThread
        if(!awaitingRefresh.getAndSet(true)) {
            SwingUtilities.invokeLater(new ReadDataManagerOnSwingThread(this));
        }
    }
    /**
     * Refresh the JList on the swing thread
     */
    private static class ReadDataManagerOnSwingThread implements Runnable {
        private SourceListModel model;

        private ReadDataManagerOnSwingThread(SourceListModel model) {
            this.model = model;
        }

        /**
         * Refresh the JList on the swing thread
         */
        @Override
        public void run(){
            model.awaitingRefresh.set(false);
            model.readDatabase();
            model.doFilter();
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
            TableLocation location = TableLocation.parse(tableAttr.get(IFilter.ATTRIBUTES.LOCATION));
            for(IFilter filter : filters) {
                if(!filter.accepts(location,tableAttr)) {
                    accepts = false;
                    break;
                }
            }
            if(accepts && (!checkForDefaultFilter || defaultFilter.accepts(location, tableAttr))) {
                newModel.add(new CatalogSourceItem(location.toString(), tableAttr.get(IFilter.ATTRIBUTES.LABEL), getIconName(location, tableAttr)));
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
            // Fetch Geometry tables
            Map<String,String> tableGeometry = new HashMap<>();
            try(Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM geometry_columns")) {
                    while(rs.next()) {
                        tableGeometry.put(new TableLocation(rs.getString("F_TABLE_CATALOG"),
                                rs.getString("F_TABLE_SCHEMA"), rs.getString("F_TABLE_NAME")).toString(), rs.getString("TYPE"));
                    }
            }
            // Fetch all tables
            try(ResultSet rs = connection.getMetaData().getTables(null, null, null, SHOWN_TABLE_TYPES)) {
                final String defaultCatalog = connection.getCatalog();
                final String defaultSchema = "PUBLIC";
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
                    tableAttr.put(IFilter.ATTRIBUTES.LOCATION, location.toString());
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

    private static void putAttribute(Map<IFilter.ATTRIBUTES, String> tableAttr, IFilter.ATTRIBUTES attribute, ResultSet rs) {
        try {
            tableAttr.put(attribute, rs.getString(attribute.toString().toLowerCase()));
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
                if(locationLeft.getSchema().equalsIgnoreCase("PUBLIC")) {
                    return -1;
                } else if(locationRight.getSchema().equalsIgnoreCase("PUBLIC")) {
                    return 1;
                } else {
                    return tmpCompare;
                }
            }
            // if schema the same, sort by table
            return NATURAL_COMPARATOR.compare(locationLeft.getTable(), locationRight.getTable());
        }
    }
}
