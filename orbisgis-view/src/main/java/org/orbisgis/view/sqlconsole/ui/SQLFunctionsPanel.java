/**
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
/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
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
package org.orbisgis.view.sqlconsole.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.view.components.filter.FilterFactoryManager;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A simple panel to list all GDMS functions.
 * @author ebocher
 * TODO filter with FilterFactoryManager
 * TODO swing drag&drop with TransferHandler
 */
public class SQLFunctionsPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private final JList list;
        private final FunctionListModel functionListModel;
        private final FilterFactoryManager<FunctionFilter> functionFilters = new FilterFactoryManager<FunctionFilter>();
        private final JLabel functionLabelCount;
        private final JLabel collapsed;
        //private final JToolBar east;
        private final FunctionManager functionManager;
        private final ActionListener collapseListener = EventHandler.create(ActionListener.class,this,"collapse");
        private final MouseListener expandListener = EventHandler.create(MouseListener.class,this,"expand",null,"mouseClicked");
        protected final static I18n I18N = I18nFactory.getI18n(SQLFunctionsPanel.class);
        
        public SQLFunctionsPanel() {
                this.setLayout(new BorderLayout());
                functionManager = Services.getService(DataManager.class).getDataSourceFactory().getFunctionManager();
                functionListModel = new FunctionListModel();

                list = new JList();
                list.setBorder(BorderFactory.createLoweredBevelBorder());
                list.setModel(functionListModel);
                list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                JButton btnCollapse = new JButton();
                btnCollapse.setIcon(OrbisGISIcon.getIcon("go-next"));
                btnCollapse.setToolTipText(I18N.tr("Collapse"));
                btnCollapse.addActionListener(collapseListener);
                btnCollapse.setBorderPainted(false);

                /*
                east = new JToolBar();
                east.setFloatable(false);
                east.add(txtFilter);
                east.add(btnCollapse);
                east.setOpaque(false);
                this.add(east, BorderLayout.NORTH);
                * 
                */
                this.add(new JScrollPane(list), BorderLayout.CENTER);
                FunctionListRenderer functionListRenderer = new FunctionListRenderer();
                list.setCellRenderer(functionListRenderer);

                functionLabelCount = new JLabel(I18N.tr("Functions count = {0}",functionListModel.getSize()));
                this.add(functionLabelCount, BorderLayout.SOUTH);

                collapsed = new JLabel(OrbisGISIcon.getIcon("go-previous"), JLabel.CENTER);
                collapsed.setIconTextGap(20);
                collapsed.setVerticalTextPosition(JLabel.BOTTOM);
                collapsed.setHorizontalTextPosition(JLabel.CENTER);
                collapsed.setToolTipText(I18N.tr("Expand"));

                this.add(collapsed, BorderLayout.WEST);
                collapsed.addMouseListener(expandListener);

                this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                this.setMinimumSize(new Dimension(100, 40));

                collapse();

        }

        public String[] getSelectedSources() {
                Object[] selectedValues = list.getSelectedValues();
                String[] sources = new String[selectedValues.length];
                for (int i = 0; i < sources.length; i++) {
                        FunctionElement functionElement = (FunctionElement) selectedValues[i];
                        sources[i] = functionManager.getFunction(functionElement.getFunctionName()).getSqlOrder();
                }
                return sources;
        }

        private void doFilter() {
                //functionListModel.filter(txtFilter.getText());
        }

        private void collapse() {
                SQLFunctionsPanel.this.setPreferredSize(new Dimension(20, 0));
                list.setVisible(false);
                //east.setVisible(false);
                collapsed.setVisible(true);
        }

        /**
         * Shown the available sql functions
         */
        public void expand() {
                if (!list.isVisible()) {
                        SQLFunctionsPanel.this.setPreferredSize(null);
                        list.setVisible(true);
                        //east.setVisible(true);
                        collapsed.setVisible(false);
                }
        }
}
