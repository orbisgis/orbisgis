/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */

package org.orbisgis.view.geocatalog;

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.gdms.source.Source;
import org.gdms.source.SourceListener;
import org.gdms.source.SourceManager;
import org.orbisgis.view.geocatalog.filters.IFilter;
import org.orbisgis.view.geocatalog.filters.TableSystemFilter;

/**
 * @brief Manage entries of GeoCatalog according to a GDMS SourceManager
 * SourceListModel is a swing component that update the content of the geocatalog
 * according to the SourceManager content and the filter loaded.
 */
public class SourceListModel extends AbstractListModel {

	private static final Logger LOGGER = Logger.getLogger(SourceListModel.class);
        private SourceManager sourceManager; /*!< The SourceManager instance*/
        private SourceListener sourceListener=null; /*!< The listener put in the sourceManager*/
	private String[] names;/*!< Sources */
	private List<IFilter> filters = new ArrayList<IFilter>(); /*!< Active filters */
        private AtomicBoolean awaitingRefresh=new AtomicBoolean(false); /*!< If true a swing runnable
         * is pending to refresh the content of SourceListModel*/
      
        /**
         * Read filters components and generate filter instances
         * @return A list of filters
         */
	public List<IFilter> getFilters() {
		return filters;
	}
        /**
         * Constructor
         * @param sourceManager The sourceManager to listen
         * @note Do not forget to call dispose()
         */
	public SourceListModel(SourceManager sourceManager) {
                this.sourceManager=sourceManager;
                //Install listeners
                //Call readDataManager when a SourceManager fire an event         
		readDataManager();
	}
        /**
         * Install listener(s) on SourceManager
         */
        public void setListeners() {
            sourceListener=EventHandler.create(SourceListener.class,
                                                    this,
                                                    "onDataManagerChange"
                                                    );
            this.sourceManager.addSourceListener(sourceListener);
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
            public void run(){
                awaitingRefresh.set(false);
                readDataManager();
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
         * Remove listeners created by the instance
         */
        public void dispose() {
            this.sourceManager.removeSourceListener(sourceListener);
        }
	private void readDataManager() {
            String[] tempSourceNames = sourceManager.getSourceNames(); //Retrieve all sources names

            if (!filters.isEmpty()) {
                //Apply filter with the Or
                tempSourceNames = filter(sourceManager, tempSourceNames, new AndFilter());
                //Undo system table only if a SystemTable filter is not activated
                if(!isSystemTableFilterInFilters()) {
                    tempSourceNames = filter(sourceManager, tempSourceNames, new DefaultFilter());
                }
            } else {
                //System table are not shown, except if the user want to see them (through or filter)
                tempSourceNames = filter(sourceManager, tempSourceNames, new DefaultFilter());
            }
            Arrays.sort(tempSourceNames, new Comparator<String>() {

                @Override
                public int compare(String o1, String o2) {
                        return o1.toLowerCase().compareTo(o2.toLowerCase());
                }
            });
            this.names = tempSourceNames;
            fireIntervalRemoved(this, 0, this.names.length);
            fireIntervalAdded(this, 0, this.names.length);
	}
        /**
         * Apply the filter sourceFilter on the provided data source names
         * @param sourceManager The source manager instance
         * @param names The names of data source
         * @param sourceFilter The IFilter instance
         * @return 
         */   
	private String[] filter(SourceManager sourceManager, String[] names,
                    IFilter sourceFilter) {
            ArrayList<String> filteredNames = new ArrayList<String>(names.length);
            for (String name : names) {
                if (sourceFilter.accepts(sourceManager, name)) {
                    filteredNames.add(name);
                }
            }
            return filteredNames.toArray(new String[0]);
	}
        /**
         * 
         * @param index The item index @see getSize()
         * @return The item
         */
	public Object getElementAt(int index) {
		return names[index];
	}
        /**
         * 
         * @return The number of source shown
         */
	public int getSize() {
		return names.length;
	}
        /**
         * Set the filter and refresh the Source list
         * according to the new filter
         * @param filters A collection of filters
         */
	public void setFilters(List<IFilter> filters) {
		this.filters = filters;
		readDataManager();
	}
        /**
         * Remove all filters and refresh the Source list
         */
        public void clearFilters() {
            this.filters.clear();
	    readDataManager();
        }
        /**
         * @brief Apply all filters with the logical connective And 
         */
	private final class AndFilter implements IFilter {
            /**
            * Does this filter reject or accept this Source
            * @param sm Source Manager instance
            * @param sourceName Source name
            * @return True if the Source should be shown
            */

            public boolean accepts(SourceManager sm, String sourceName) {
                for (int i = 0; i < filters.size(); i++) {
                    if (!filters.get(i).accepts(sm, sourceName)) {
                            return false;
                    }
                }
                return true;
            }
	}
	/**
         * This filter is always applied, to hide system table
         */
	private final class DefaultFilter implements IFilter {
            /**
            * Does this filter reject or accept this Source
            * @param sm Source Manager instance
            * @param sourceName Source name
            * @return True if the Source should be shown
            */
            public boolean accepts(SourceManager sm, String sourceName) {
                Source source = sm.getSource(sourceName);
                return (source != null) && !source.isSystemTableSource();			
            }
	}
}
