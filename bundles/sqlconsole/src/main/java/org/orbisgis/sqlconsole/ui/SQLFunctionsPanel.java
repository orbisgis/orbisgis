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

import org.orbisgis.view.components.filter.DefaultActiveFilter;
import org.orbisgis.view.components.filter.FilterFactoryManager;
import org.orbisgis.view.sqlconsole.ui.functionFilters.NameFilterFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.beans.EventHandler;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A simple panel to list all SQL functions.
 * @author Erwan Bocher
 * TODO filter with FilterFactoryManager
 */
public class SQLFunctionsPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private final FunctionList list;
        private final JPanel expandedPanel;
        private final FunctionListModel functionListModel;
        private final FilterFactoryManager<FunctionFilter,DefaultActiveFilter> functionFilters = new FilterFactoryManager<FunctionFilter,DefaultActiveFilter>();
        private final JLabel functionLabelCount;
        private final FilterFactoryManager.FilterChangeListener filterEvent = EventHandler.create(FilterFactoryManager.FilterChangeListener.class,this,"doFilter");
        private AtomicBoolean initialised = new AtomicBoolean(false);
        
        protected final static I18n I18N = I18nFactory.getI18n(SQLFunctionsPanel.class);

        private static final String FUNCTION_COUNT = I18N.marktr("Function count = {0}");
        
        public SQLFunctionsPanel(DataSource dataSource) {
                this.setLayout(new BorderLayout());
                expandedPanel = new JPanel(new BorderLayout());
                functionListModel = new FunctionListModel(dataSource);
                functionLabelCount = new JLabel(I18N.tr(FUNCTION_COUNT, 0));

                list = new FunctionList();
                list.setBorder(BorderFactory.createLoweredBevelBorder());
                list.setModel(functionListModel);
                list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                list.addListSelectionListener(EventHandler.create(ListSelectionListener.class,this,"onListChange"));

                expandedPanel.add(new JScrollPane(list), BorderLayout.CENTER);
                functionFilters.setUserCanRemoveFilter(false);
                expandedPanel.add(functionFilters.makeFilterPanel(false), BorderLayout.NORTH);
                                
                list.setCellRenderer(new FunctionListRenderer(list));
                list.setTransferHandler(new FunctionListTransferHandler());
                list.setDragEnabled(true);
                expandedPanel.add(functionLabelCount, BorderLayout.SOUTH);
                add(expandedPanel, BorderLayout.CENTER);
                expandedPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                collapse();
        }
        
        private void initialise() {
                if(!initialised.getAndSet(true)) {
                        functionFilters.getEventFilterChange().addListener(this, filterEvent);
                        NameFilterFactory nameFilter = new NameFilterFactory();
                        functionFilters.registerFilterFactory(nameFilter);
                        functionFilters.addFilter(nameFilter.getDefaultFilterValue());                
                }
        }

        @Override
        public void addNotify() {
                super.addNotify();    
                initialise();
        }

        public String[] getSelectedSources() {
                Object[] selectedValues = list.getSelectedValues();
                String[] sources = new String[selectedValues.length];
                for (int i = 0; i < sources.length; i++) {
                        FunctionElement functionElement = (FunctionElement) selectedValues[i];
                        sources[i] = functionElement.getSQLCommand();
                }
                return sources;
        }

        public void onListChange() {
            functionLabelCount.setText(I18N.tr(FUNCTION_COUNT, functionListModel.getSize()));
        }
        /**
         * Called by the listener filterEvent
         */
        public void doFilter() {
                functionListModel.setFilters(functionFilters.getFilters());
        }
        
        /**
         * Switch the visibility state of the panel
         */
        public void switchPanelVisibilityState() {
                if (expandedPanel.isVisible()) {
                        collapse();
                } else {
                        expand();
                }
        }

        /**
         * Hide the SQL list and show the expand button
         */
        public final void collapse() {
                if (expandedPanel.isVisible()) {
                        expandedPanel.setVisible(false);
                }
        }

        /**
         * Shown the available sql functions
         */
        public final void expand() {
                if (!expandedPanel.isVisible()) {
                        setPreferredSize(null);
                        expandedPanel.setVisible(true);
                }
        }
        
        
}
