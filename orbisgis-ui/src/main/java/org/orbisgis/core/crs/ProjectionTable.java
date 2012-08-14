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
package org.orbisgis.core.crs;

import java.awt.BorderLayout;

import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.gdms.data.DataSourceFactory;
import org.jproj.CoordinateReferenceSystem;
import org.jproj.Registry;
import org.orbisgis.core.ui.components.text.JTextFilter;

public class ProjectionTable extends JPanel {

        private JPanel searchPanel;
        private JTextFilter txtFilter;
        private final DataSourceFactory dsf;
        String DEFAULT_REGISTER = "EPSG";
        private JTable crsTable;
        private TableRowSorter<TableModel> sorter;
        private WKTTab wktTab;

        public ProjectionTable(DataSourceFactory dsf) {
                this.dsf = dsf;
                initUI();
        }

        private void initUI() {
                this.setLayout(new BorderLayout());
                JScrollPane scrollPane = new JScrollPane(getTable());
                wktTab = new WKTTab(crsTable);
                this.add(scrollPane, BorderLayout.CENTER);
                this.add(getSearchSRSPanel(), BorderLayout.NORTH);
                this.add(wktTab, BorderLayout.SOUTH);
        }

        public JTable getTable() {
                if (null == crsTable) {
                        crsTable = new JTable();
                        DefaultTableModel model = new DefaultTableModel(
                                new Object[]{"Register", "Projection name"}, 0);
                        Map<String, String> map = Registry.getAvailableCRSNames(DEFAULT_REGISTER);
                        for (Entry<String, String> entry : map.entrySet()) {
                                model.addRow(new Object[]{entry.getKey(), entry.getValue()});
                        }
                        crsTable.setModel(model);
                        sorter = new TableRowSorter<TableModel>(model);
                        crsTable.setRowSorter(sorter);
                        crsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        ListSelectionModel listSelectionModel = crsTable.getSelectionModel();
                        listSelectionModel.addListSelectionListener(new TableSelectionListener());

                }
                return crsTable;
        }

        public JPanel getSearchSRSPanel() {
                if (null == searchPanel) {
                        searchPanel = new JPanel();
                        JLabel label = new JLabel("Search : ");
                        txtFilter = new JTextFilter();
                        txtFilter.addDocumentListener(new DocumentListener() {

                                @Override
                                public void removeUpdate(DocumentEvent e) {
                                        doFilter();
                                }

                                @Override
                                public void insertUpdate(DocumentEvent e) {
                                        doFilter();
                                }

                                @Override
                                public void changedUpdate(DocumentEvent e) {
                                        doFilter();
                                }
                        });
                        searchPanel.add(label);
                        searchPanel.add(txtFilter);
                }
                return searchPanel;

        }

        private void doFilter() {
                String text = txtFilter.getText();
                if (text.length() == 0) {
                        sorter.setRowFilter(null);
                } else {
                        sorter.setRowFilter(RowFilter.regexFilter(text));
                }
        }

        public DataSourceFactory getDsf() {
                return dsf;
        }

        void saveCRSHistory() {
                int selected = crsTable.getSelectedRow();
                if (selected > 0) {
                        String[] crsKeyValue = new String[]{crsTable.getValueAt(selected, 0).toString(),
                                crsTable.getValueAt(selected, 1).toString()};
                        wktTab.saveCRSHistory(crsKeyValue);
                }
        }

        private class TableSelectionListener implements ListSelectionListener {

                public TableSelectionListener() {
                }

                @Override
                public void valueChanged(ListSelectionEvent e) {
                        if (e.getValueIsAdjusting()) {
                                return;
                        }
                        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                        if (!lsm.isSelectionEmpty()) {
                                int selectedRow = lsm.getMinSelectionIndex();
                                String sridASText = crsTable.getValueAt(selectedRow, 0).toString();
                                try {
                                        CoordinateReferenceSystem crs = dsf.getCrsFactory().createFromName(sridASText);
                                        if (crs != null) {
                                                wktTab.setWKT(crs.getParameterString());
                                        }
                                } catch (Exception e1) {
                                        System.out.println(e1);
                                }

                        } else {
                                wktTab.setWKT(WKTTab.NO_SRS_INFO);
                        }
                }
        }
}
