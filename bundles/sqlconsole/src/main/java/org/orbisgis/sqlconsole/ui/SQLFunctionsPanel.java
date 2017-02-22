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
package org.orbisgis.sqlconsole.ui;

import java.awt.*;
import java.beans.EventHandler;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultCaret;
import org.orbisgis.sif.components.filter.DefaultActiveFilter;
import org.orbisgis.sif.components.filter.FilterFactoryManager;
import org.orbisgis.sqlconsole.ui.functionFilters.NameFilterFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A simple panel to list all SQL functions.
 * @author Erwan Bocher
 * @author Nicolas Fortin
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
        private JEditorPane functionDescription;
        private JPanel funcList = new JPanel(new BorderLayout());
        protected final static I18n I18N = I18nFactory.getI18n(SQLFunctionsPanel.class);
        private static final String NO_FUNCTION_MESSAGE = I18n.marktr("<html><body>Select a function to display its description.</body></html>");
        private static final String FUNCTION_COUNT = I18n.marktr("Function count = {0}");
        private JSplitPane splitPane;
        
        public SQLFunctionsPanel(DataSource dataSource) {
                this.setLayout(new BorderLayout());
                expandedPanel = new JPanel(new BorderLayout());
                functionListModel = new FunctionListModel(dataSource);
                functionLabelCount = new JLabel(I18N.tr(FUNCTION_COUNT, 0));

                list = new FunctionList();
                list.setBorder(BorderFactory.createLoweredBevelBorder());
                list.setModel(functionListModel);
                list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                list.addListSelectionListener(EventHandler.create(ListSelectionListener.class, this, "onListChange"));
                list.setCellRenderer(new FunctionListRenderer(list));
                list.setTransferHandler(new FunctionListTransferHandler());
                list.setDragEnabled(true);
                functionFilters.setUserCanRemoveFilter(false);
                funcList.add(functionFilters.makeFilterPanel(false), BorderLayout.NORTH);
                funcList.add(new JScrollPane(list), BorderLayout.CENTER);
                functionDescription = new JEditorPane();
                functionDescription.setContentType("text/html");
                functionDescription.setEditable(false);
                functionDescription.setText(I18N.tr(NO_FUNCTION_MESSAGE));
                DefaultCaret caret = (DefaultCaret)functionDescription.getCaret();
                caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
                splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, funcList, new JScrollPane(functionDescription));
                splitPane.setResizeWeight(0.5);
                expandedPanel.add(splitPane, BorderLayout.CENTER);
                funcList.add(functionLabelCount, BorderLayout.SOUTH);
                add(expandedPanel, BorderLayout.CENTER);
                expandedPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        }
        
        private void initialise() {
                if(!initialised.getAndSet(true)) {
                        functionFilters.getEventFilterChange().addListener(this, filterEvent);
                        NameFilterFactory nameFilter = new NameFilterFactory();
                        functionFilters.registerFilterFactory(nameFilter);
                        functionFilters.addFilter(nameFilter.getDefaultFilterValue());                
                }
        }

        public void repackPanel() {
            splitPane.setDividerLocation(0.5);
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

        /**
         * When the user click on a function in the list the description is showing and
         * the number of corresponding functions.
         */
        public void onListChange() {
            functionLabelCount.setText(I18N.tr(FUNCTION_COUNT, functionListModel.getSize()));
            FunctionElement functElem = list.getSelectedValue();
            if (functElem != null) {
                String description = functElem.getToolTip();
                StringBuilder stringBuilder = new StringBuilder("<html><body><p><b>Description<b></p><br>");
                if (description == null || description.isEmpty()) {
                    stringBuilder.append(I18N.tr("No description available."));
                } else {
                    stringBuilder.append(description);
                }
                stringBuilder.append("</body></html>");
                functionDescription.setText(stringBuilder.toString());
            } else {
                functionDescription.setText(I18N.tr(NO_FUNCTION_MESSAGE));
            }
        }
        
        /**
         * Called by the listener filterEvent
         */
        public void doFilter() {
                functionListModel.setFilters(functionFilters.getFilters());
        }

        
        
}
