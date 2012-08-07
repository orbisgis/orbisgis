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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.Collection;
import java.util.TreeSet;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.FilterDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.components.filter.ActiveFilter;
import org.orbisgis.view.components.filter.DefaultActiveFilter;
import org.orbisgis.view.components.filter.FilterFactory;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Table extended filter using SQL where request
 * @author Nicolas Fortin
 */
public class WhereSQLFilterFactory implements FilterFactory<TableSelectionFilter> {
        public static final String FACTORY_ID  ="WhereSQLFilterFactory";
        private final static I18n I18N = I18nFactory.getI18n(WhereSQLFilterFactory.class);
        private static final Logger LOGGER = Logger.getLogger(WhereSQLFilterFactory.class);
        
        @Override
        public ActiveFilter getDefaultFilterValue() {
                return new DefaultActiveFilter(FACTORY_ID, "field LIKE '%value%'");
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
        public TableSelectionFilter getFilter(ActiveFilter filterValue) {
                return new SQLFilter(((DefaultActiveFilter)filterValue).getCurrentFilterValue());
        }

        @Override
        public Component makeFilterField(ActiveFilter filterValue) {
                JPanel textAndButton = new JPanel();
                textAndButton.setLayout(new BoxLayout(textAndButton,BoxLayout.X_AXIS));
                JTextField whereText = new JTextField(((DefaultActiveFilter)filterValue).getCurrentFilterValue());
                whereText.setPreferredSize(new Dimension(Short.MAX_VALUE,Short.MIN_VALUE));
                JButton runButton =  new JButton(OrbisGISIcon.getIcon("execute"));
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
        private static class SQLFilter implements TableSelectionFilter {
                Collection<Integer> modelRowsIdResult;
                String whereText;

                public SQLFilter(String whereText) {
                        this.whereText = whereText;
                }
                
                @Override
                public boolean isSelected(int rowId, DataSource source) {
                        return modelRowsIdResult.contains(rowId);
                }

                @Override
                public void initialise(ProgressMonitor pm, DataSource source) {
                        pm.startTask(I18N.tr("Run SQL request"), 100);
                        modelRowsIdResult = new TreeSet<Integer>();
                        try {
                                FilterDataSourceDecorator filterDataSourceDecorator = new FilterDataSourceDecorator(
                                        source);
                                filterDataSourceDecorator.setFilter(whereText);
                                filterDataSourceDecorator.open();
                                modelRowsIdResult.addAll(filterDataSourceDecorator.getIndexMap());
                                filterDataSourceDecorator.close();
                        } catch (DriverException e1) {
                                LOGGER.error(e1.getLocalizedMessage(),e1);
                        } finally {
                                pm.endTask();
                        }
                }
        }
}
