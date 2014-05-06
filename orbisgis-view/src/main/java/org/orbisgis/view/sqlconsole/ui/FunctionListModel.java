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
package org.orbisgis.view.sqlconsole.ui;

import org.apache.log4j.Logger;
import org.markdown4j.Markdown4jProcessor;

import javax.sql.DataSource;
import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * A custom list model to load and manage SQL functions.
 * @author Erwan Bocher
 * @author Nicolas Fortin
 */
public class FunctionListModel extends AbstractListModel<FunctionElement> {
    private static final long serialVersionUID = 1L;
    private List<FunctionElement> functionsList;
    private List<Integer> filteredFunctionList;
    private List<FunctionFilter> filters = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(FunctionListModel.class);
    private static final int AVERAGE_FUNCTION_COUNT = 300; //Hint for size of array
    private DataSource dataSource;

    private HashSet<String> uniqueFunctionNames = new HashSet<>();

    public FunctionListModel(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int getSize() {
        if(functionsList == null) {
            readSQLFunctions();
        }
        if(filteredFunctionList!=null) {
            return filteredFunctionList.size();
        } else {
            return functionsList.size();
        }
    }

    @Override
    public FunctionElement getElementAt(int i) {
        if(filteredFunctionList!=null) {
            return functionsList.get(filteredFunctionList.get(i));
        } else {
            return functionsList.get(i);
        }
    }

    /**
     * Set the list filters, and refresh the list of functions
     * through {@code refreshFunctionList}.
     * @param filters Filter list, not null
     */
    public void setFilters(List<FunctionFilter> filters) {
            this.filters = new ArrayList<FunctionFilter>(filters);
            refreshFilter();
    }

    private void refreshFilter() {
        int oldSize = getSize();
        if(filters.isEmpty()) {
            filteredFunctionList = null;
            if(!functionsList.isEmpty()) {
                if(oldSize>0) {
                    fireIntervalRemoved(this,0, oldSize);
                }
                fireIntervalAdded(this,0, functionsList.size());
            }
        }
        filteredFunctionList = null;
        List<Integer> newFilteredFunctionList = new ArrayList<Integer>(functionsList.size());
        if(oldSize>0) {
            fireIntervalRemoved(this,0, oldSize);
        }
        for(int idElement=0;idElement< functionsList.size(); idElement++) {
            FunctionElement element = functionsList.get(idElement);
            boolean rejected = false;
            for(FunctionFilter filter : filters) {
                if(!filter.accepts(element)) {
                    rejected = true;
                    break;
                }
            }
            if(!rejected) {
                newFilteredFunctionList.add(idElement);
            }
        }
        filteredFunctionList = newFilteredFunctionList;
        if(getSize()>0) {
            fireIntervalAdded(this,0,getSize()-1);
        }
    }

    private void readSQLFunctions() {
        functionsList = new ArrayList<FunctionElement>(AVERAGE_FUNCTION_COUNT);
        try {

            try(Connection connection = dataSource.getConnection();
                ResultSet resultSet = connection.getMetaData().getProcedures(null,null,null)) {
                while(resultSet.next()) {
                    final String procedureName = resultSet.getString("PROCEDURE_NAME");
                    if (!uniqueFunctionNames.contains(procedureName)) {
                        uniqueFunctionNames.add(procedureName);
                        final String rawRemarks = resultSet.getString("REMARKS");
                        String remarks;
                        try {
                            remarks = new Markdown4jProcessor().process(rawRemarks);
                        } catch (IOException e) {
                            remarks = rawRemarks;
                        }
                        FunctionElement element = new FunctionElement(procedureName,
                                resultSet.getShort("PROCEDURE_TYPE"),
                                remarks,
                                dataSource);
                        functionsList.add(element);
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Could not read SQL function list");
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
