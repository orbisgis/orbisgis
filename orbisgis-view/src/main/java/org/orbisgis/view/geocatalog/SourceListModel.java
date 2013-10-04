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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.log4j.Logger;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.sputilities.SFSUtilities;
import org.orbisgis.sputilities.TableLocation;
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
        readDatabase();
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
            SwingUtilities.invokeLater(new ReadDataManagerOnSwingThread());
        }
    }
    /**
     * Refresh the JList on the swing thread
     */
    private class ReadDataManagerOnSwingThread implements Runnable {
        /**
         * Refresh the JList on the swing thread
         */
        @Override
        public void run(){
            awaitingRefresh.set(false);
            readDatabase();
        }
    }
    /**
     *
     * @return True if at least one of filter is an instance of TableSystemFilter
     */
    private boolean isSystemTableFilterInFilters() {
        for(IFilter filter : filters) {
            if(filter instanceof TableSystemFilter) {
                return true;
            }
        }
        return false;
    }
    /**
     * TODO stop timers
     */
    public void dispose() {

    }
    /**
     * Find the icon corresponding to a table reference
     * @param rs result set obtained through {@link java.sql.DatabaseMetaData#getTables(String, String, String, String[])}
     * @return The source item icon name, in org.orbisgis.view.icons package
     */
    private String getIconName(Connection connection, ResultSet rs) throws SQLException {
        if (rs == null) {
            return "information_geo"; //Unknown source type
        }
        if(!SFSUtilities.getGeometryFields(connection, new TableLocation(rs)).isEmpty()) {
            return "geofile";
        }
        switch(rs.getString("TABLE_TYPE")) {
            case "SYSTEM_TABLE":
                return "drive";
            case "LINKED TABLE":
                return "database";
            default:
                return "flatfile";
        }
        //"remove";
        //"image";
        //"server_connect";
        // information_geo // Unknown
    }

    private static String addQuotesIfNecessary(String tableLocationPart) {
        if(tableLocationPart.contains(".")) {
            return "`"+tableLocationPart+"`";
        } else {
            return tableLocationPart;
        }
    }

    /**
     * Read the table list in the database
     */
    private void readDatabase() {
        List<CatalogSourceItem> newModel = new LinkedList<>();
        try (Connection connection = dataSource.getConnection();
                ResultSet rs = connection.getMetaData().getTables(null, null, null, null)) {
            final String defaultCatalog = connection.getCatalog();
            final String defaultSchema = "PUBLIC";
            boolean checkForDefaultFilter = true;
            for(IFilter filter : filters) {
                if(filter instanceof TableSystemFilter) {
                    checkForDefaultFilter = false;
                }
            }
            while(rs.next()) {
                TableLocation location = new TableLocation(rs);
                boolean accepts = true;
                for(IFilter filter : filters) {
                    if(!filter.accepts(connection, location.toString(), rs)) {
                        accepts = false;
                        break;
                    }
                }
                if(accepts && (!checkForDefaultFilter || defaultFilter.accepts(connection, location.toString(), rs))) {
                    // Make Label
                    StringBuilder label = new StringBuilder(addQuotesIfNecessary(location.getTable()));
                    if(!location.getSchema().equalsIgnoreCase(defaultSchema)) {
                        label.insert(0, ".");
                        label.insert(0, addQuotesIfNecessary(location.getSchema()));
                    }
                    if(!location.getCatalog().equalsIgnoreCase(defaultCatalog)) {
                        label.insert(0, ".");
                        label.insert(0, addQuotesIfNecessary(location.getCatalog()));
                    }
                    newModel.add(new CatalogSourceItem(location.toString(), label.toString(), getIconName(connection, rs)));
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(I18N.tr("Cannot read the table list"), ex);
        }
        Collections.sort(newModel, catalogComparator);
        int oldLength = sourceList.length;
        sourceList = new ContainerItemProperties[0];
        fireIntervalRemoved(this, 0, oldLength);
        sourceList = newModel.toArray(new ContainerItemProperties[newModel.size()]);
        fireIntervalAdded(this, 0, this.sourceList.length);
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
     * This method clear all source in the SourceManager except source Table
     */
    public void clearAllSourceExceptSystemTables() throws SQLException {
        try (Connection connection = dataSource.getConnection() ;
                ResultSet rs = connection.getMetaData().getTables(null, "PUBLIC", null, new String[]{"TABLE", "VIEW"})) {
            List<TableLocation> tableToDrop = new LinkedList<>();
            while(rs.next()) {
                String tableCatalog = rs.getString("TABLE_CAT");
                String tableSchema = rs.getString("TABLE_SCHEM");
                String tableName = rs.getString("TABLE_NAME");
                tableToDrop.add(new TableLocation(tableCatalog, tableSchema, tableName));
            }
            for(TableLocation table : tableToDrop) {
                connection.createStatement().execute("DROP TABLE "+ table);
            }
        }
    }

    /**
     * Set the filter and refresh the Source list
     * according to the new filter
     * @param filters A collection of filters
     */
    public void setFilters(List<IFilter> filters) {
        this.filters = filters;
        readDatabase();
    }

    /**
     * Remove all filters and refresh the Source list
     */
    public void clearFilters() {
        this.filters.clear();
        readDatabase();
    }

    /**
     * This filter is always applied, to hide system table
     */
    private static final class DefaultFilter implements IFilter {
        @Override
        public boolean accepts(Connection connection, String sourceName, ResultSet tableProperties) throws SQLException {
            return !tableProperties.getString("TABLE_TYPE").equalsIgnoreCase("SYSTEM TABLE");
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
