/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.tablegui.impl.filters;

import org.orbisgis.sif.components.CustomButton;
import org.orbisgis.sif.components.filter.DefaultActiveFilter;
import org.orbisgis.sif.components.filter.FilterFactory;
import org.orbisgis.tablegui.icons.TableEditorIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.corejdbc.ReadRowSet;
import org.orbisgis.corejdbc.common.LongUnion;
import org.orbisgis.sif.edition.EditableElementException;
import org.orbisgis.tableeditorapi.TableEditableElement;

/**
 * Table extended filter using SQL where request
 * @author Nicolas Fortin
 */
public class WhereSQLFilterFactory implements FilterFactory<TableSelectionFilter,DefaultActiveFilter> {
    public static final String FACTORY_ID  ="WhereSQLFilterFactory";
    private final static I18n I18N = I18nFactory.getI18n(WhereSQLFilterFactory.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(WhereSQLFilterFactory.class);

   

    /** Map Table primary key to the row id of regular select * from mytable */

    @Override
    public DefaultActiveFilter getDefaultFilterValue() {
        return new DefaultActiveFilter(FACTORY_ID, "field > value2 AND field < value1");
    }

    @Override
    public String getFactoryId() {
        return FACTORY_ID;
    }

    @Override
    public String getFilterLabel() {
        return I18N.tr("SQL");
    }

    @Override
    public TableSelectionFilter getFilter(DefaultActiveFilter filterValue) {
        return new SQLFilter(filterValue.getCurrentFilterValue());
    }

    @Override
    public Component makeFilterField(DefaultActiveFilter filterValue) {
        JPanel textAndButton = new JPanel();
        textAndButton.setLayout(new BoxLayout(textAndButton,BoxLayout.X_AXIS));
        JTextField whereText = new JTextField(filterValue.getCurrentFilterValue());
        whereText.setPreferredSize(new Dimension(Short.MAX_VALUE,Short.MIN_VALUE));
        JButton runButton =  new CustomButton(TableEditorIcon.getIcon("execute"));
        runButton.setToolTipText(I18N.tr("Search"));
        textAndButton.add(whereText);
        textAndButton.add(runButton);
        ActionListener listener = EventHandler.create(
                ActionListener.class,filterValue,
                "setCurrentFilterValue","source.text");
        whereText.addActionListener(listener);
        //Click on the button will fire an action event on the text field
        runButton.addActionListener(
                EventHandler.create(ActionListener.class,whereText,
                        "postActionEvent"));
        return textAndButton;
    }   
    
    
    /**
     * A class to build a SQL filter
     */
    public static class SQLFilter implements TableSelectionFilter {

        private Set<Integer> filteredRows;
        String whereText;

        public SQLFilter(String whereText) {
            this.whereText = whereText;
        }

        @Override
        public boolean isSelected(int rowId, TableEditableElement source) {
            return filteredRows.contains(rowId);
        }

        @Override
        public void initialize(ProgressMonitor progress, TableEditableElement source) throws SQLException {
            progress.setTaskName(I18N.tr("Run filter by sql request"));
            // If the table hold a PK then do the find task on the server side
            try (Connection connection = source.getDataManager().getDataSource().getConnection();
                    Statement st = connection.createStatement()) {
                PropertyChangeListener cancelListener = EventHandler.create(PropertyChangeListener.class, st, "cancel");
                progress.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL, cancelListener);
                try {
                    String tablePk = source.getRowSet().getPkName();
                    if (!tablePk.isEmpty()) {
                        final ReadRowSet rowSet = source.getRowSet();
                        StringBuilder request = new StringBuilder(String.format("SELECT %s FROM %s WHERE %s",
                                TableLocation.quoteIdentifier(tablePk),
                                source.getTableReference(), whereText));
                        LOGGER.info(I18N.tr("Find field value with the following request:\n{0}", request.toString()));
                        SortedSet<Long> selectionPk = new LongUnion();
                        try (ResultSet rs = st.executeQuery(request.toString())) {
                            while (rs.next()) {
                                selectionPk.add(rs.getLong(1));
                            }
                        }
                        filteredRows = new HashSet<>();
                        for (int oneBasedRowId : rowSet.getRowNumberFromRowPk(selectionPk)) {
                            filteredRows.add(oneBasedRowId - 1);
                        }
                    }
                } finally {
                    progress.removePropertyChangeListener(cancelListener);
                }
            } catch (EditableElementException ex) {
                throw new SQLException(ex);
            }
        }
    }
    
}
