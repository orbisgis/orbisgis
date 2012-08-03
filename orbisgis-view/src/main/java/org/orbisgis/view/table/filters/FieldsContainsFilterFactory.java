/*
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
package org.orbisgis.view.table.filters;

import com.kitfox.svg.Metadata;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.orbisgis.view.components.filter.ActiveFilter;
import org.orbisgis.view.components.filter.FilterFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Filter Search [JTextField] in [JComboBox(fields)] [Button(search)].
 * @author Nicolas Fortin
 */
public class FieldsContainsFilterFactory implements FilterFactory<TableSelectionFilter> {
        public static final String FACTORY_ID  ="FieldsContainsFilter";
        protected final static I18n I18N = I18nFactory.getI18n(FieldsContainsFilterFactory.class);
        private JTable table;

        public FieldsContainsFilterFactory(JTable table) {
                this.table = table;
        }
        
        
        @Override
        public String getFactoryId() {
                return FACTORY_ID;
        }

        @Override
        public String getFilterLabel() {
                return I18N.tr("Find");
        }
        
        private static FilterParameters deserializeValue(String filterValue) {
                if(filterValue.isEmpty()) {
                        return new FilterParameters(-1,"");
                }
                ByteArrayInputStream buf = new ByteArrayInputStream(filterValue.getBytes());
                try {
                        ObjectInputStream objStream = new ObjectInputStream(buf);
                        FilterParameters params = (FilterParameters)objStream.readObject();
                        return params;
                } catch (ClassNotFoundException ex) {
                        throw new IllegalStateException(I18N.tr("Filter error"), ex);
                } catch (IOException ex) {
                        throw new IllegalStateException(I18N.tr("Filter error"), ex);
                }
                
        }

        @Override
        public TableSelectionFilter getFilter(String filterValue) {
                FilterParameters parameters = deserializeValue(filterValue);
                return new FieldsContainsFilter(parameters);
        }

        @Override
        public Component makeFilterField(ActiveFilter filterValue) {
                FilterParameters params = deserializeValue(filterValue.getCurrentFilterValue());                
                JPanel filterFields = new JPanel(new BorderLayout());
                // Searched Text
                JTextField searchedTextBox = new JTextField(params.getSearchedChars());
                filterFields.add(searchedTextBox,BorderLayout.CENTER);
                JPanel rightSide = new JPanel();
                filterFields.add(rightSide,BorderLayout.EAST);
                //Field selection
                JComboBox fieldSelection = new JComboBox();
                fieldSelection.addItem(I18N.tr("All"));
                for(int i=0; i< table.getColumnCount(); i++) {
                        fieldSelection.addItem(table.getColumnName(i));
                }      
                if(params.getRowId()!=-1) {
                        fieldSelection.setSelectedIndex(params.getRowId()+1);
                }
                rightSide.add(fieldSelection);
                //Run filter, will update filterValue
                JButton runButton = new JButton(I18N.tr("Search"));
                FilterActionListener listener = new FilterActionListener(
                        filterValue,
                        searchedTextBox,
                        fieldSelection);
                runButton.addActionListener(listener);
                rightSide.add(runButton);
                return filterFields;
        }
        
        private static class FieldsContainsFilter implements TableSelectionFilter {

                private FilterParameters params;

                public FieldsContainsFilter(FilterParameters params) {
                        this.params = params;
                }

                private boolean isFieldContains(Value field) {
                        return field.toString().toLowerCase().contains(params.getSearchedChars());
                }

                @Override
                public boolean isSelected(int rowId, DataSet source) {
                        Value val;
                        try {

                                if (params.getRowId() != -1) {
                                        val = source.getFieldValue(rowId, params.getRowId());
                                        return isFieldContains(val);

                                } else {
                                        Value[] values = source.getRow(rowId);
                                        for (Value value : values) {
                                                if (isFieldContains(value)) {
                                                        return true;
                                                }
                                        }
                                        return false;
                                }
                        } catch (DriverException ex) {
                                throw new IllegalStateException(I18N.tr("Filter driver error"), ex);
                        }
                }
        }
        /**
        * Produced by the swing fields, read by the filter.
        * Implements Serializable to convert from/to a String
        */
        private static class FilterParameters implements Serializable {
                private static final long serialVersionUID = 2L;
                private int rowId;
                private String searchedChars;

                public FilterParameters(int rowId, String searchedChars) {
                        this.rowId = rowId;
                        this.searchedChars = searchedChars;
                }

                public int getRowId() {
                        return rowId;
                }

                public String getSearchedChars() {
                        return searchedChars;
                }
                
                @Override
                public String toString() {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        try {
                                ObjectOutputStream objOut = new ObjectOutputStream(out);
                                objOut.writeObject(this);
                                objOut.flush();
                                return out.toString();
                        } catch (IOException ex) {
                                throw new IllegalStateException(I18N.tr("Filter error"), ex);
                        }
                }
        }
        
        private static class FilterActionListener implements ActionListener {
                private ActiveFilter filter;
                private JTextField searchedTextBox;
                private JComboBox fieldSelection;

                public FilterActionListener(ActiveFilter filter, JTextField searchedTextBox, JComboBox fieldSelection) {
                        this.filter = filter;
                        this.searchedTextBox = searchedTextBox;
                        this.fieldSelection = fieldSelection;
                }
                
                @Override
                public void actionPerformed(ActionEvent ae) {
                        //Update the filter (apply)
                        filter.setCurrentFilterValue(
                                new FilterParameters(
                                fieldSelection.getSelectedIndex()-1,
                                searchedTextBox.getText()).toString());
                }
                
        }
}
