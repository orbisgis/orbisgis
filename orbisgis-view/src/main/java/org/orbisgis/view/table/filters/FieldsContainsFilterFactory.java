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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.apache.log4j.Logger;
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
        private static final Logger LOGGER = Logger.getLogger(FieldsContainsFilterFactory.class);
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

        @Override
        public ActiveFilter getDefaultFilterValue() {
                return new FilterParameters(-1,"");
        }

        @Override
        public TableSelectionFilter getFilter(ActiveFilter filterValue) {
                return new FieldsContainsFilter((FilterParameters)filterValue);
        }

        @Override
        public Component makeFilterField(ActiveFilter filterValue) {
                FilterParameters params = (FilterParameters)filterValue;
                JPanel filterFields = new JPanel();
                filterFields.setLayout(new BoxLayout(filterFields, BoxLayout.X_AXIS));
                // Searched Text
                JTextField searchedTextBox = new JTextField(params.getSearchedChars());
                filterFields.add(searchedTextBox,BorderLayout.CENTER);
                searchedTextBox.setPreferredSize(new Dimension(Short.MAX_VALUE,Short.MIN_VALUE));
                //Field selection
                JComboBox fieldSelection = new JComboBox();
                fieldSelection.addItem(I18N.tr("All"));
                for(int i=0; i< table.getColumnCount(); i++) {
                        fieldSelection.addItem(table.getColumnName(i));
                }      
                if(params.getRowId()!=-1) {
                        fieldSelection.setSelectedIndex(params.getRowId()+1);
                }
                filterFields.add(fieldSelection);
                //Run filter, will update filterValue
                JButton runButton = new JButton(I18N.tr("Search"));
                FilterActionListener listener = new FilterActionListener(
                        params,
                        searchedTextBox,
                        fieldSelection);
                runButton.addActionListener(listener);
                filterFields.add(runButton);
                return filterFields;
        }
        
        private static class FieldsContainsFilter implements TableSelectionFilter {

                private FilterParameters params;
                private String searchChars;

                public FieldsContainsFilter(FilterParameters params) {
                        this.params = params;
                        searchChars = params.getSearchedChars().toLowerCase();
                }

                private boolean isFieldContains(Value field) {
                        return field.toString().toLowerCase().contains(searchChars);
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
        * Add some parameters to ActiveFilter
        */
        public static class FilterParameters extends ActiveFilter {
                private static final long serialVersionUID = 2L;
                private String searchedChars;
                private int rowId;

                public FilterParameters(int rowId, String searchedChars) {
                        super(FieldsContainsFilterFactory.FACTORY_ID);
                        this.searchedChars = searchedChars;
                        this.rowId = rowId;
                }

                @Override
                public void readStream(DataInputStream in) throws IOException {
                        super.readStream(in);
                        rowId = in.readInt();
                        searchedChars = in .readUTF();
                }

                @Override
                public void writeStream(DataOutputStream out) throws IOException {
                        super.writeStream(out);
                        out.writeInt(rowId);
                        out.writeUTF(searchedChars);
                }

                public int getRowId() {
                        return rowId;
                }

                public void setValue(int rowId, String searchedChars) {
                        FilterParameters oldParams = new FilterParameters(this.rowId, this.searchedChars);
                        this.rowId = rowId;
                        this.searchedChars = searchedChars;
                        propertySupport.firePropertyChange(PROP_CURRENTFILTERVALUE, oldParams, this);
                }

                @Override
                public boolean equals(Object o) {
                        if(!(o instanceof FilterParameters)) {
                                return false;
                        }
                        FilterParameters other = (FilterParameters)o;
                        if(other.getRowId()!=rowId || !other.searchedChars.equals(searchedChars)) {
                                return false;
                        }
                        return super.equals(o);
                }

                @Override
                public int hashCode() {
                        int hash = 5 + super.hashCode();
                        hash = 47 * hash + this.searchedChars.hashCode();
                        hash = 47 * hash + new Integer(this.rowId).hashCode();
                        return hash;
                }
                
                public String getSearchedChars() {
                        return searchedChars;
                }
        }
        
        private static class FilterActionListener implements ActionListener {
                private FilterParameters filter;
                private JTextField searchedTextBox;
                private JComboBox fieldSelection;

                public FilterActionListener(FilterParameters filter, JTextField searchedTextBox, JComboBox fieldSelection) {
                        this.filter = filter;
                        this.searchedTextBox = searchedTextBox;
                        this.fieldSelection = fieldSelection;
                }
                
                @Override
                public void actionPerformed(ActionEvent ae) {
                        filter.setValue(fieldSelection.getSelectedIndex()-1,
                                searchedTextBox.getText());
                }
                
        }
}
