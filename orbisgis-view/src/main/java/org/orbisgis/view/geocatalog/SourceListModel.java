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
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.gdms.source.*;
import org.orbisgis.view.geocatalog.filters.IFilter;

/**
 * @brief Manage entries of GeoCatalog according to a GDMS SourceManager
 * SourceListModel is a swing component that update the content of the geocatalog
 * according to the SourceManager content and the filter loaded.
 */
public class SourceListModel extends AbstractListModel {

	private static final Logger logger = Logger.getLogger(SourceListModel.class);
        private SourceManager sourceManager; /*!< The SourceManager instance*/
        private SourceListener sourceListener=null; /*!< The listener put in the sourceManager*/
	private String[] names;/*!< Source */
	private List<IFilter> filters = new ArrayList<IFilter>();

	public List<IFilter> getFilters() {
		return filters;
	}
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
         * Remove listeners created by the instance
         */
        public void dispose() {
            this.sourceManager.removeSourceListener(sourceListener);
        }
	private void readDataManager() {
		String[] tempSourceNames = sourceManager.getSourceNames(); //Retrieve all sources names
                
		if (!filters.isEmpty()) {
                    //Apply filter with the Or
                    tempSourceNames = filter(sourceManager, tempSourceNames, new OrFilter());
                } else {	
                    //If the user do not define a filter, SystemTables are hidden
                    tempSourceNames = filter(sourceManager, tempSourceNames, new DefaultFilter());
                }

		Arrays.sort(tempSourceNames, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.toLowerCase().compareTo(o2.toLowerCase());
			}
		});
		this.names = tempSourceNames;
		fireIntervalRemoved(this, 0, getSize());
		fireIntervalAdded(this, 0, getSize());
	}

	private String[] filter(SourceManager sourceManager, String[] names,
			IFilter textFilter) {
		ArrayList<String> filteredNames = new ArrayList<String>();
		for (String name : names) {
			if (textFilter.accepts(sourceManager, name)) {
				filteredNames.add(name);
			}
		}
		String[] array = filteredNames.toArray(new String[filteredNames.size()]);
		return array;
	}

	public Object getElementAt(int index) {
		return names[index];
	}

	public int getSize() {
		return names.length;
	}

	public void filter(ArrayList<IFilter> filters) {
		this.filters = filters;
		readDataManager();
	}

	private final class OrFilter implements IFilter {

		public boolean accepts(SourceManager sm, String sourceName) {
			for (int i = 0; i < filters.size(); i++) {
				if (filters.get(i).accepts(sm, sourceName)) {
					return true;
				}
			}
			return false;
		}
	}
	
	private final class DefaultFilter implements IFilter {
		
		public boolean accepts(SourceManager sm, String sourceName) {
			Source source = sm.getSource(sourceName);
			return (source != null) && source.isWellKnownName();			
		}
	}
}
