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
package org.orbisgis.view.main.geocatalog.filters;

import java.awt.Component;
import javax.swing.JTextField;
import org.gdms.source.SourceManager;
import org.orbisgis.utils.I18N;
import org.orbisgis.view.geocatalog.filters.ActiveFilter;
import org.orbisgis.view.geocatalog.filters.DataSourceFilterFactory;
import org.orbisgis.view.geocatalog.filters.IFilter;
import org.orbisgis.view.geocatalog.filters.TextFieldDocumentListener;

/**
 * @brief Name contains DataSourceFilterFactory
 * This is the data source name contains x filter
 */

public class UnitTestFilterFactory implements DataSourceFilterFactory {
    /**
     * The factory ID
     *
     * @return Internal name of the filter type
     */
    public String getFactoryId() {
        return "unittest_factory";
    }

    /**
     * The user see this label when choosing a filter from a list
     *
     * @return
     */
    public String getFilterLabel() {
        return "unittest_factory";
    }

    /**
     * Make the filter corresponding to the filter value
     *
     * @param filterValue The new value fired by PropertyChangeEvent
     * @return
     */
    public IFilter getFilter(String filterValue) {
        return new UnitTestFilter();
    }


    /**
     * The DataSourceFilterFactory build the component that let the user to
     * define the filter parameters. 
     * @param filterValue When the control change the ActiveFilter value must be updated
     * @return The swing component.
     */
    public Component makeFilterField(ActiveFilter filterValue) {
        JTextField filterField = new JTextField(filterValue.getCurrentFilterValue());
        //Update the field at each modification        
        filterField.getDocument().addDocumentListener(new TextFieldDocumentListener(filterField,filterValue));
        return filterField;
    }
    
    /**
     * Inner class text filter
     */
    public class UnitTestFilter implements IFilter {

        public boolean accepts(SourceManager sm, String sourceName) {
            return false;
        }     
    }
}
