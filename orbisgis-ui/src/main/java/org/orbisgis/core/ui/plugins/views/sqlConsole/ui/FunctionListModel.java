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
/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
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
package org.orbisgis.core.ui.plugins.views.sqlConsole.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.gdms.sql.function.FunctionManager;

/**
 * A custom list model to load and manage GDMS functions.
 * @author ebocher
 */
public class FunctionListModel extends AbstractListModel implements ListModel {

    private ArrayList<FunctionElement> functionsList;

    public FunctionListModel() {
        getSQLFunctions();
    }

    @Override
    public int getSize() {
        return functionsList.size();
    }

    @Override
    public Object getElementAt(int i) {
        return functionsList.get(i);
    }

    private void getSQLFunctions() {
        functionsList = new ArrayList<FunctionElement>();
        String[] functions = Services.getService(DataManager.class).getDataSourceFactory().getFunctionManager().getFunctionNames();

        for (String functionName : functions) {
            functionsList.add(new FunctionElement(functionName.toUpperCase(), FunctionElement.BASIC_FUNCTION));
        }
        
        Collections.sort(functionsList, new NameComparator());


    }

    /**
     * A method to update the list according to a text filter
     * @param text
     */
    public void filter(String text) {
        if (text.length() == 0) {
            getSQLFunctions();
        } else {

            ArrayList<FunctionElement> functionsFiltered = new ArrayList<FunctionElement>();

            for (FunctionElement functionElement : functionsList) {
                if (functionElement.getFunctionName().toLowerCase().contains(text.toLowerCase())) {
                    functionsFiltered.add(functionElement);
                }
            }
            functionsList = functionsFiltered;

        }

        fireIntervalRemoved(this, 0, getSize());
        fireIntervalAdded(this, 0, getSize());
    }

    /**
     * A comparator to order functionElement according to its name.
     */
    private static class NameComparator implements Comparator {

        public NameComparator() {
        }

        @Override
        public int compare(Object t, Object t1) {
            FunctionElement functionElement1 = (FunctionElement) t;
            FunctionElement functionElement2 = (FunctionElement) t1;
            return functionElement1.getFunctionName().toLowerCase().compareTo(functionElement2.getFunctionName().toLowerCase());
        }
    }
}
