package org.orbisgis.core.crs;

import java.awt.BorderLayout;

import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.gdms.data.SQLDataSourceFactory;
import org.jproj.Registry;
import org.orbisgis.core.ui.components.text.JTextFilter;

public class ProjectionTab extends JPanel {

        private JPanel searchPanel;
        private JTextFilter txtFilter;
        private JList lstSRS;
        private JScrollPane scrollPane;
        private final SQLDataSourceFactory dsf;
        String DEFAULT_REGISTER = "EPSG";
        private JTable crsTable;
        private TableRowSorter<TableModel> sorter;

        public ProjectionTab(SQLDataSourceFactory dsf) {
                this.dsf = dsf;
                initUI();
        }

        private void initUI() {
                this.setLayout(new BorderLayout());
                if (null == scrollPane) {
                        scrollPane = new JScrollPane(getTable());
                }
                this.add(scrollPane, BorderLayout.CENTER);
                this.add(getSearchSRSPanel(), BorderLayout.NORTH);
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

                }
                return crsTable;
        }

        public JPanel getSearchSRSPanel() {
                if (null == searchPanel) {
                        searchPanel = new JPanel();
                        JLabel label = new JLabel("Search a code : ");
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

        public String getSRS() {
                return crsTable.getValueAt(crsTable.getSelectedRow(), 0).toString();
        }

        public SQLDataSourceFactory getDsf() {
                return dsf;
        }
}
