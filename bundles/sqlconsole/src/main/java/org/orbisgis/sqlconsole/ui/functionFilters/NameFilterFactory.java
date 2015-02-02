/*
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
package org.orbisgis.sqlconsole.ui.functionFilters;

import java.awt.Component;
import javax.swing.JTextField;
import org.orbisgis.sif.components.filter.DefaultActiveFilter;
import org.orbisgis.sif.components.filter.FilterFactory;
import org.orbisgis.sif.components.filter.TextFieldDocumentListener;
import org.orbisgis.sqlconsole.ui.FunctionElement;
import org.orbisgis.sqlconsole.ui.FunctionFilter;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Filter functions by their names
 * @author Nicolas Fortin
 */
public class NameFilterFactory implements FilterFactory<FunctionFilter,DefaultActiveFilter> {
        public static final String FACTORY_ID = "NameFilter";
        protected final static I18n I18N = I18nFactory.getI18n(NameFilterFactory.class);
        
        @Override
        public String getFactoryId() {
                return FACTORY_ID;
        }

        @Override
        public String getFilterLabel() {
                return I18N.tr("Contains");
        }

        @Override
        public DefaultActiveFilter getDefaultFilterValue() {
                return new DefaultActiveFilter(getFactoryId(), "");
        }

        @Override
        public FunctionFilter getFilter(DefaultActiveFilter filterValue) {
                return new TextFilter(filterValue.getCurrentFilterValue());
        }

        @Override
        public Component makeFilterField(DefaultActiveFilter filterValue) {
                JTextField filterField = new JTextField(filterValue.getCurrentFilterValue());
                //Update the field at each modification   
                //The lifetime of the listener has the same lifetime than the ActiveFilter,
                //then this is useless to remove the listener.
                filterField.getDocument().addDocumentListener(new TextFieldDocumentListener(filterField,filterValue));
                return filterField;
        }
        /**
         * Inner class text filter
         */
        private class TextFilter implements FunctionFilter {

                private final String nameFilter;
                /**
                 * Constructor
                 * @param nameFilter The filter value
                 */
                public TextFilter(String nameFilter) {
                        this.nameFilter = nameFilter.toLowerCase();
                }

                @Override
                public boolean accepts(FunctionElement functionElement) {
                        return functionElement.getFunctionName().toLowerCase().contains(nameFilter);
                }
        }
        
}
