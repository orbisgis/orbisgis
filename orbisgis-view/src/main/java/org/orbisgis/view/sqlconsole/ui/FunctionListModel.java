/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.sqlconsole.ui;

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.FunctionManagerListener;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;

/**
 * A custom list model to load and manage GDMS functions.
 * @author Erwan Bocher
 */
public class FunctionListModel extends AbstractListModel implements ListModel {
    private static final long serialVersionUID = 1L;
    private List<FunctionElement> functionsList;
    private FunctionManagerListener functionListener = EventHandler.create(FunctionManagerListener.class,this,"");
    private List<FunctionFilter> filters = new ArrayList<FunctionFilter>();
    
    public FunctionListModel() {
        FunctionManager functionManager = Services.getService(DataManager.class).getDataSourceFactory().getFunctionManager();
        readSQLFunctions(functionManager);
        functionManager.addFunctionManagerListener(functionListener);
    }

    @Override
    public int getSize() {
        return functionsList.size();
    }

    @Override
    public Object getElementAt(int i) {
        return functionsList.get(i);
    }

    /**
     * Release listeners created by this list model
     */
    public void dipose() {
            Services.getService(DataManager.class).getDataSourceFactory().getFunctionManager().removeFunctionManagerListener(functionListener);
    }
    /**
     * Set the list filters, and refresh the list of functions
     * through {@code refreshFunctionList}.
     * @param filters 
     */
    public void setFilters(List<FunctionFilter> filters) {
            this.filters = new ArrayList<FunctionFilter>(filters);
            refreshFunctionList();
    }
    
    /**
     * Update the list of functions.
     * Called by the function manager listener 
     */
    public void refreshFunctionList() {
            FunctionManager functionManager = Services.getService(DataManager.class).getDataSourceFactory().getFunctionManager();
            readSQLFunctions(functionManager);
    }

    private void readSQLFunctions(FunctionManager functionManager) {
        functionsList = new ArrayList<FunctionElement>();
        String[] functions = functionManager.getFunctionNames();

        for (String functionName : functions) {
            boolean rejected = false;
            FunctionElement element = new FunctionElement(functionName.toUpperCase(), FunctionElement.BASIC_FUNCTION);
            for(FunctionFilter filter : filters) {
                    if(!filter.accepts(element)) {
                            rejected = true;
                            break;
                    }
            }
            if(!rejected) {
                functionsList.add(element);
            }
        }        
        Collections.sort(functionsList, new NameComparator());
        fireIntervalAdded(this, 0, getSize());
    }

    /**
     * A comparator to order functionElement according to its name.
     */
    private static class NameComparator implements Comparator<FunctionElement> {
        @Override
        public int compare(FunctionElement functionElement1, FunctionElement functionElement2) {
            return functionElement1.getFunctionName().toLowerCase().compareTo(functionElement2.getFunctionName().toLowerCase());
        }
    }
}
