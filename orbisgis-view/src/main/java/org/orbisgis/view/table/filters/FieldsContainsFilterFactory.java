/*
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
package org.orbisgis.view.table.filters;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.corejdbc.MetaData;
import org.orbisgis.coreapi.api.ReadRowSet;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.components.CustomButton;
import org.orbisgis.view.components.filter.DefaultActiveFilter;
import org.orbisgis.view.components.filter.FilterFactory;
import org.orbisgis.viewapi.edition.EditableElementException;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.viewapi.table.TableEditableElement;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Filter Search [JTextField] in [JComboBox(fields)] [Button(search)].
 * @author Nicolas Fortin
 */
public class FieldsContainsFilterFactory implements FilterFactory<TableSelectionFilter,DefaultActiveFilter> {
        public static final String FACTORY_ID  ="FieldsContainsFilter";
        protected final static I18n I18N = I18nFactory.getI18n(FieldsContainsFilterFactory.class);
        private JTable table;
        private static final Logger LOGGER = Logger.getLogger(FieldsContainsFilterFactory.class);

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
        public DefaultActiveFilter getDefaultFilterValue() {
                return new FilterParameters();
        }

        @Override
        public TableSelectionFilter getFilter(DefaultActiveFilter filterValue) {
                return new FieldsContainsFilter((FilterParameters)filterValue);
        }

        @Override
        public Component makeFilterField(DefaultActiveFilter filterValue) {
                FilterParameters params = (FilterParameters) filterValue;
                JPanel filterFields = new JPanel();
                filterFields.setLayout(new BoxLayout(filterFields, BoxLayout.X_AXIS));
                // Searched Text
                JTextField searchedTextBox = new JTextField(params.getSearchedChars());
                filterFields.add(searchedTextBox,BorderLayout.CENTER);
                searchedTextBox.setPreferredSize(new Dimension(Short.MAX_VALUE,Short.MIN_VALUE));
                
                //Field selection
                JComboBox<String> fieldSelection = new JComboBox<>();
                fieldSelection.addItem(I18N.tr("All"));
                for(int i=0; i< table.getColumnCount(); i++) {
                        fieldSelection.addItem(table.getColumnName(i));
                }      
                if(params.getColumnId()!=-1) {
                        fieldSelection.setSelectedIndex(params.getColumnId()+1);
                }
                filterFields.add(fieldSelection);
                // Match Case
                JCheckBox matchCase = new JCheckBox(I18N.tr("Match case"),params.isMatchCase());
                filterFields.add(matchCase);
                //Whole word
                JCheckBox wholeWords = new JCheckBox(I18N.tr("Whole words"),params.isWholeWord());
                filterFields.add(wholeWords);
                //Run filter, will update filterValue
                JButton runButton = new CustomButton(OrbisGISIcon.getIcon("execute"));
                runButton.setToolTipText(I18N.tr("Search"));
                FilterActionListener listener = new FilterActionListener(
                        params,
                        searchedTextBox,
                        fieldSelection,
                        matchCase,
                        wholeWords);
                runButton.addActionListener(listener);
                searchedTextBox.addActionListener(listener); //VK_ENTER validate
                filterFields.add(runButton);
                return filterFields;
        }
        
        private static class FieldsContainsFilter implements TableSelectionFilter {

                private final FilterParameters params;
                private final String searchChars;
                private final Set<Integer> filteredRows = new IntegerUnion();
                /** Not null if work is done on Where SQL Filter */
                private TableSelectionFilter  externalFilter = null;

                public FieldsContainsFilter(FilterParameters params) {
                        this.params = params;
                        if(!params.isMatchCase()) {
                                searchChars = params.getSearchedChars().toLowerCase();
                        }else {
                                searchChars = params.getSearchedChars();
                        }
                }

                private boolean isFieldContains(String fieldValue) {
                        if(!params.isWholeWord()) {
                                if(!params.isMatchCase()) {
                                        fieldValue=fieldValue.toLowerCase();
                                }
                                return fieldValue.contains(searchChars);
                        } else {
                                if(!params.isMatchCase()) {
                                        return fieldValue.equalsIgnoreCase(searchChars);
                                } else {
                                        return fieldValue.equals(searchChars);                                        
                                }
                        }
                }

                @Override
                public boolean isSelected(int rowId, TableEditableElement source) {
                        if(externalFilter != null) {
                            return externalFilter.isSelected(rowId, source);
                        } else {
                            return filteredRows.contains(rowId);
                        }
                }

                private void addFieldWhere(StringBuilder request, String fieldName) {
                    if(!params.isMatchCase()) {
                        request.append(" lower(");
                    } else {
                        request.append(" ");
                    }
                    request.append(TableLocation.quoteIdentifier(fieldName));
                    if(!params.isMatchCase()) {
                        request.append(")");
                    }
                    if(params.isWholeWord()) {
                        request.append(" = ");
                    } else {
                        request.append(" LIKE ");
                    }
                    request.append("'");
                    if(!params.isWholeWord()) {
                        request.append("%");
                    }
                    request.append(params.getSearchedChars());
                    if(!params.isWholeWord()) {
                        request.append("%");
                    }
                    request.append("'");
                }

                @Override
                public void initialize(ProgressMonitor pm, TableEditableElement source) throws SQLException {
                        try(Connection connection = source.getDataManager().getDataSource().getConnection();
                            Statement st = connection.createStatement()) {
                            PropertyChangeListener cancelListener = EventHandler.create(PropertyChangeListener.class, st, "cancel");
                            pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL, cancelListener);
                            try{
                                // If the table hold a PK then do the find task on the server side
                                String tablePk = source.getRowSet().getPkName();
                                if(!tablePk.isEmpty()) {
                                    final ReadRowSet rowSet = source.getRowSet();
                                    StringBuilder request = new StringBuilder();
                                    if (params.getColumnId() != -1) {
                                        // A specific field
                                        String fieldName = rowSet.getMetaData().getColumnName(params.getColumnId() + 1);
                                        addFieldWhere(request, fieldName);
                                    } else {
                                        // All field
                                        int colCount = rowSet.getMetaData().getColumnCount();
                                        int conditionsCount = 0;
                                        for(int idcol = 1; idcol <= colCount; idcol++) {
                                            if(MetaData.isAlphaNumeric(rowSet.getMetaData().getColumnType(idcol))) {
                                                if(conditionsCount > 0) {
                                                    request.append(" OR");
                                                }
                                                String fieldName = source.getRowSet().getMetaData().getColumnName(idcol);
                                                addFieldWhere(request, fieldName);
                                                conditionsCount++;
                                            }
                                        }
                                    }
                                    WhereSQLFilterFactory whereSQLFilterFactory = new WhereSQLFilterFactory();
                                    DefaultActiveFilter whereFilterValue = whereSQLFilterFactory.getDefaultFilterValue();
                                    whereFilterValue.setCurrentFilterValue(request.toString());
                                    externalFilter = whereSQLFilterFactory.getFilter(whereFilterValue);
                                    LOGGER.info(I18N.tr("Find field value with the following request:\n{0}", request.toString()));
                                    externalFilter.initialize(pm, source);
                                } else {
                                    // If the table does not hold any PK, loop through rows
                                    final ReadRowSet rowSet = source.getRowSet();
                                    rowSet.getReadLock().tryLock(1, TimeUnit.SECONDS);
                                    try {
                                        rowSet.beforeFirst();
                                        int rowId = 0;
                                        ProgressMonitor progressMonitor = pm.startTask(rowSet.getRowCount());
                                        if (params.getColumnId() != -1) {
                                            while(rowSet.next()) {
                                                if(isFieldContains(rowSet.getString(params.getColumnId() + 1))) {
                                                    filteredRows.add(rowId);
                                                }
                                                progressMonitor.endTask();
                                                rowId++;
                                            }
                                        } else {
                                            while(rowSet.next()) {
                                                int columnCount = rowSet.getMetaData().getColumnCount();
                                                for(int col = 1; col < columnCount; col++) {
                                                    if(isFieldContains(rowSet.getString(col))) {
                                                        filteredRows.add(rowId);
                                                        break;
                                                    }
                                                }
                                                progressMonitor.endTask();
                                                rowId++;
                                            }
                                        }
                                    } finally {
                                        rowSet.getReadLock().unlock();
                                    }
                                }
                            } finally {
                                pm.removePropertyChangeListener(cancelListener);
                            }
                        } catch (EditableElementException | InterruptedException ex) {
                            throw new SQLException(ex);
                        }
                }
        }
        /**
        * Add some parameters to ActiveFilter
        */
        public static class FilterParameters extends DefaultActiveFilter {
                private static final long serialVersionUID = 2L;
                private int columnId;
                private String searchedChars;
                private boolean matchCase;
                private boolean wholeWord;
                
                public FilterParameters() {
                        super(FieldsContainsFilterFactory.FACTORY_ID,"");
                        this.columnId = -1;
                        this.searchedChars = "";
                        this.matchCase = false;
                        this.wholeWord = false;                        
                }

                public FilterParameters(int columnId, String searchedChars, boolean matchCase, boolean wholeWord) {
                        super(FieldsContainsFilterFactory.FACTORY_ID,searchedChars);
                        this.columnId = columnId;
                        this.searchedChars = searchedChars;
                        this.matchCase = matchCase;
                        this.wholeWord = wholeWord;
                }

                @Override
                public void readStream(DataInputStream in) throws IOException {
                        super.readStream(in);
                        columnId = in.readInt();
                        searchedChars = in .readUTF();
                        matchCase = in.readBoolean();
                        wholeWord = in.readBoolean();
                }

                @Override
                public void writeStream(DataOutputStream out) throws IOException {
                        super.writeStream(out);
                        out.writeInt(columnId);
                        out.writeUTF(searchedChars);
                        out.writeBoolean(matchCase);
                        out.writeBoolean(wholeWord);
                }
                /**
                * 
                * @return Data model Column id
                */
                public int getColumnId() {
                        return columnId;
                }

                public boolean isMatchCase() {
                        return matchCase;
                }

                public boolean isWholeWord() {
                        return wholeWord;
                }

                public void setValue(int columnId, String searchedChars, boolean matchCase, boolean wholeWord) {
                        this.columnId = columnId;
                        this.searchedChars = searchedChars;
                        this.matchCase = matchCase;
                        this.wholeWord = wholeWord;
                        super.setCurrentFilterValue(searchedChars);
                }

                @Override
                public boolean equals(Object o) {
                        if(!(o instanceof FilterParameters)) {
                                return false;
                        }
                        FilterParameters other = (FilterParameters)o;
                        if(other.columnId!=columnId ||
                                !other.searchedChars.equals(searchedChars) ||
                                other.matchCase!=this.matchCase ||
                                other.wholeWord!=this.wholeWord) {
                                return false;
                        }
                        return super.equals(o);
                }

                @Override
                public int hashCode() {
                        int hash = 3 + super.hashCode();
                        hash = 83 * hash + this.columnId;
                        hash = 83 * hash + (this.searchedChars != null ? this.searchedChars.hashCode() : 0);
                        hash = 83 * hash + (this.matchCase ? 1 : 0);
                        hash = 83 * hash + (this.wholeWord ? 1 : 0);
                        return hash;
                }
                
                public String getSearchedChars() {
                        return searchedChars;
                }
        }
        /**
         * This listener update the filter parameter with the values
         * provided by the controls
         */
        private static class FilterActionListener implements ActionListener {
                private FilterParameters filter;
                private JTextField searchedTextBox;
                private JComboBox fieldSelection;
                private JCheckBox matchCase;
                private JCheckBox wholeWord;

                public FilterActionListener(FilterParameters filter, JTextField searchedTextBox, JComboBox fieldSelection, JCheckBox matchCase, JCheckBox wholeWord) {
                        this.filter = filter;
                        this.searchedTextBox = searchedTextBox;
                        this.fieldSelection = fieldSelection;
                        this.matchCase = matchCase;
                        this.wholeWord = wholeWord;
                }
                
                
                @Override
                public void actionPerformed(ActionEvent ae) {
                        filter.setValue(fieldSelection.getSelectedIndex()-1,
                                searchedTextBox.getText(),matchCase.isSelected(),wholeWord.isSelected());
                }
                
        }
}
