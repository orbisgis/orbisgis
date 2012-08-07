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
package org.orbisgis.view.geocatalog.filters.factories;

import java.awt.Component;
import javax.swing.JTextField;
import org.gdms.source.SourceManager;
import org.orbisgis.view.components.filter.ActiveFilter;
import org.orbisgis.view.components.filter.FilterFactory;
import org.orbisgis.view.geocatalog.filters.IFilter;
import org.orbisgis.view.components.filter.TextFieldDocumentListener;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @brief Name contains DataSourceFilterFactory
 * This is the data source name contains x filter
 */

public class NameNotContains implements FilterFactory<IFilter> {
    private static final I18n I18N = I18nFactory.getI18n(NameNotContains.class);
    /**
     * The factory ID
     *
     * @return Internal name of the filter type
     */
    @Override
    public String getFactoryId() {
        return "name_not_contains";
    }

    /**
     * The user see this label when choosing a filter from a list
     *
     * @return
     */
    @Override
    public String getFilterLabel() {
        return I18N.tr("Not contains");
    }

    /**
     * Make the filter corresponding to the filter value
     *
     * @param filterValue The new value fired by PropertyChangeEvent
     * @return
     */
    @Override
    public IFilter getFilter(String filterValue) {
        return new TextFilter(filterValue);
    }


    /**
     * The DataSourceFilterFactory build the component that let the user to
     * define the filter parameters. 
     * @param filterValue When the control change the ActiveFilter value must be updated
     * @return The swing component.
     */
    @Override
    public Component makeFilterField(ActiveFilter filterValue) {
        JTextField filterField = new JTextField(filterValue.getCurrentFilterValue());
        //Update the field at each modification        
        filterField.getDocument().addDocumentListener(new TextFieldDocumentListener(filterField,filterValue));
        return filterField;
    }
    
    /**
     * Inner class text filter
     */
    private class TextFilter implements IFilter {
        private final String nameFilter;
        /**
         * Constructor
         * @param nameFilter The filter value
         */
        public TextFilter(String nameFilter) {
            this.nameFilter = nameFilter;
        }
        /**
        * Does this filter reject or accept this Source
        * @param sm Source Manager instance
        * @param sourceName Source name
        * @return True if the Source should be shown
        */
        @Override
        public boolean accepts(SourceManager sm, String sourceName) {
            return !sourceName.toLowerCase().contains(nameFilter.toLowerCase());
        }     
    }
}
