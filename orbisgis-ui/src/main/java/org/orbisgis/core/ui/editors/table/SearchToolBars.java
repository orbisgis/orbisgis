/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
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
/**
 *
 * @author ebocher
 */
package org.orbisgis.core.ui.editors.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.gdms.data.FilterDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.components.text.JButtonTextField;
import org.orbisgis.core.ui.editors.table.TableComponent.DataSourceDataModel;
import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.I18N;

public class SearchToolBars extends JPanel implements ActionListener {

        private final TableComponent tc;
        private JComboBox fieldsCmb;
        String fieldAll = I18N.getString("orbisgis.org.orbigis.core.tableComponent.searchPanel.all");
        private JButtonTextField regexTxtFilter;
        private JCheckBox casseCB;
        private JCheckBox wholeWordCB;

        public SearchToolBars(TableComponent tc) {
                this.tc = tc;
                init();
        }

        private void init() {
                this.setLayout(new BorderLayout());
                this.add(getSearchToolBar(), BorderLayout.CENTER);
                this.add(getOptionsToolBar(), BorderLayout.NORTH);
        }

        public JToolBar getOptionsToolBar() {
                JToolBar toolBar = new JToolBar();
                toolBar.setFloatable(false);
                casseCB = new JCheckBox(I18N.getString("orbisgis.org.orbisgis.ui.findReplace.matchCase"));
                wholeWordCB = new JCheckBox(I18N.getString("orbisgis.org.orbisgis.ui.findReplace.wholeWords"));
                final JCheckBox sqlCB = new JCheckBox(I18N.getString("orbisgis.org.orbigis.core.tableComponent.searchPanel.enableSQL"));
                sqlCB.setEnabled(false);
                sqlCB.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                if (sqlCB.isSelected()) {
                                        casseCB.setSelected(false);
                                        wholeWordCB.setSelected(false);
                                        casseCB.setEnabled(false);
                                        wholeWordCB.setEnabled(false);
                                        fieldsCmb.setEnabled(false);
                                } else {
                                        casseCB.setEnabled(true);
                                        wholeWordCB.setEnabled(true);
                                        fieldsCmb.setEnabled(true);
                                }
                        }
                });
                toolBar.add(casseCB);
                toolBar.add(wholeWordCB);
                toolBar.add(sqlCB);
                toolBar.setBorderPainted(false);
                toolBar.setOpaque(false);
                return toolBar;
        }

        public JToolBar getSearchToolBar() {
                JToolBar searchToolBar = new JToolBar();
                searchToolBar.setFloatable(false);
                JLabel label = new JLabel();
                regexTxtFilter = new JButtonTextField(20);
                regexTxtFilter.setBackground(Color.WHITE);
                regexTxtFilter.setToolTipText(I18N.getString("orbisgis.org.orbisgis.core.ui.editors.table.TableComponent.searchEnter")); //$NON-NLS-1$

                regexTxtFilter.addKeyListener(new KeyListener() {

                        @Override
                        public void keyTyped(KeyEvent e) {
                        }

                        @Override
                        public void keyReleased(KeyEvent e) {
                        }

                        @Override
                        public void keyPressed(KeyEvent e) {
                                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                        final String whereText = regexTxtFilter.getText();
                                        if (whereText.length() == 0) {
                                                if (tc.getSelectedRowsCount() > 0) {
                                                        tc.checkSelectionRefresh();
                                                        tc.getSelection().clearSelection();
                                                        tc.setSelectedRowsCount(0);
                                                }

                                        } else {
                                                if (fieldsCmb.isEnabled()) {
                                                        findTextPattern(whereText, (String) fieldsCmb.getSelectedItem(), casseCB.isSelected(), wholeWordCB.isSelected());
                                                } else {
                                                        executeSQLWhere(whereText);
                                                }
                                        }

                                }
                        }
                });


                searchToolBar.add(label);
                searchToolBar.add(regexTxtFilter);
                fieldsCmb = new JComboBox();
                fieldsCmb.addItem(fieldAll);
                searchToolBar.add(fieldsCmb, BorderLayout.EAST);
                JButton searchOptionsButton = new JButton(IconLoader.getIcon("execute.png"));
                searchOptionsButton.setActionCommand("Execute");
                searchOptionsButton.addActionListener(this);
                searchOptionsButton.setBorderPainted(false);
                searchToolBar.add(searchOptionsButton, BorderLayout.EAST);
                searchToolBar.setBorderPainted(false);
                searchToolBar.setOpaque(false);
                return searchToolBar;
        }

        public JComboBox getFieldsCmb() {
                return fieldsCmb;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
                String command = ae.getActionCommand();
                if ("Execute".equals(command)) {
                        final String whereText = regexTxtFilter.getText();
                        if (whereText.length() == 0) {
                                if (tc.getSelectedRowsCount() > 0) {
                                        tc.checkSelectionRefresh();
                                        tc.getSelection().clearSelection();
                                        tc.setSelectedRowsCount(0);
                                }

                        } else {
                                if (fieldsCmb.isEnabled()) {
                                        findTextPattern(whereText, (String) fieldsCmb.getSelectedItem(), casseCB.isSelected(), wholeWordCB.isSelected());
                                } else {
                                        executeSQLWhere(whereText);
                                }
                        }

                }

        }

        private void findTextPattern(String text, String field, boolean isCaseSensitive, boolean wholeWord) {
                if (field.equals(fieldAll)) {
                        findAllTextPattern(text, isCaseSensitive, wholeWord);
                } else {
                        findFieldValueTextPattern(text, field, isCaseSensitive, wholeWord);
                }
        }

        public void findAllTextPattern(final String text, final boolean isCaseSensitive, final boolean wholeWord) {

                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.backgroundOperation(new BackgroundJob() {

                        @Override
                        public void run(ProgressMonitor pm) {
                                String textTemp = text;
                                if (!isCaseSensitive) {
                                        textTemp = text.toLowerCase();
                                }
                                ArrayList<Integer> filtered = new ArrayList<Integer>();
                                pm.startTask(I18N.getString("orbisgis.org.orbisgis.ui.table.tableComponent.searching"), 100); //$NON-NLS-1$
                                //$NON-NLS-1$
                                DataSourceDataModel tableModel = tc.getTableModel();
                                long rowCount = tableModel.getRowCount();
                                for (int i = 0; i < rowCount; i++) {
                                        if (i / 100 == i / 100.0) {
                                                if (pm.isCancelled()) {
                                                        break;
                                                } else {
                                                        pm.progressTo((int) (100 * i / tableModel.getRowCount()));
                                                }
                                        }
                                        Value[] values = tableModel.getRow(i);
                                        boolean select = false;
                                        int valueCount = values.length;
                                        int j = 0;
                                        while (!select && j < valueCount) {

                                                Value value = values[j];
                                                String valueString = value.toString();
                                                if (!value.isNull()) {
                                                        if (isCaseSensitive) {
                                                                if (wholeWord) {
                                                                        select = valueString.equals(textTemp);
                                                                } else {
                                                                        select = valueString.contains(textTemp);
                                                                }
                                                        } else {
                                                                if (wholeWord) {
                                                                        select = valueString.toLowerCase().equals(textTemp);
                                                                } else {
                                                                        select = valueString.toLowerCase().contains(textTemp);
                                                                }
                                                        }

                                                } else {
                                                        if (isCaseSensitive && textTemp.equals("null")) {
                                                                select = true;
                                                        } else {
                                                                select = valueString.toLowerCase().equals(textTemp);
                                                        }
                                                }
                                                j++;
                                        }

                                        if (select) {
                                                filtered.add(i);
                                        }
                                }

                                pm.endTask();

                                int[] sel = new int[filtered.size()];

                                for (int i = 0; i < sel.length; i++) {
                                        sel[i] = filtered.get(i);
                                }

                                MapContext mapC = tc.getElement().getMapContext();
                                if (mapC != null) {
                                        mapC.setSelectionInducedRefresh(true);
                                }

                                tc.checkSelectionRefresh(sel);
                                tc.getSelection().setSelectedRows(sel);
                                tc.updateTableSelection();

                        }

                        @Override
                        public String getTaskName() {
                                return I18N.getString("orbisgis.org.orbisgis.core.ui.editors.table.TableComponent.searching"); //$NON-NLS-1$
                        }
                });

        }

        public void findFieldValueTextPattern(final String text, final String field, final boolean isCaseSensitive, final boolean wholeWord) {

                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.backgroundOperation(new BackgroundJob() {

                        @Override
                        public void run(ProgressMonitor pm) {
                                String textTemp = text;
                                if (!isCaseSensitive) {
                                        textTemp = text.toLowerCase();
                                }
                                ArrayList<Integer> filtered = new ArrayList<Integer>();
                                pm.startTask(I18N.getString("orbisgis.org.orbisgis.ui.table.tableComponent.searching"), 100); //$NON-NLS-1$
                                //$NON-NLS-1$
                                DataSourceDataModel tableModel = tc.getTableModel();
                                int fieldIndex = tableModel.getFieldIndex(field);

                                long rowCount = tableModel.getRowCount();
                                for (int i = 0; i < rowCount; i++) {
                                        if (i / 100 == i / 100.0) {
                                                if (pm.isCancelled()) {
                                                        break;
                                                } else {
                                                        pm.progressTo((int) (100 * i / tableModel.getRowCount()));
                                                }
                                        }
                                        String value = (String) tableModel.getValueAt(i, fieldIndex);
                                        boolean select = false;
                                        if (!value.isEmpty()) {
                                                if (isCaseSensitive) {
                                                        if (wholeWord) {
                                                                select = value.equals(textTemp);
                                                        } else {
                                                                select = value.contains(textTemp);
                                                        }
                                                } else {
                                                        if (wholeWord) {
                                                                select = value.toLowerCase().equals(textTemp);
                                                        } else {
                                                                select = value.toLowerCase().contains(textTemp);
                                                        }
                                                }

                                        } else {
                                                if (isCaseSensitive && textTemp.equals("null")) {
                                                        select = true;
                                                } else {
                                                        select = value.toLowerCase().equals(textTemp);
                                                }
                                        }

                                        if (select) {
                                                filtered.add(i);
                                        }

                                }

                                pm.endTask();

                                int[] sel = new int[filtered.size()];

                                for (int i = 0; i < sel.length; i++) {
                                        sel[i] = filtered.get(i);
                                }

                                MapContext mapC = tc.getElement().getMapContext();
                                if (mapC != null) {
                                        mapC.setSelectionInducedRefresh(true);
                                }

                                tc.checkSelectionRefresh(sel);
                                tc.getSelection().setSelectedRows(sel);
                                tc.updateTableSelection();
                        }

                        @Override
                        public String getTaskName() {
                                return I18N.getString("orbisgis.org.orbisgis.core.ui.editors.table.TableComponent.searching"); //$NON-NLS-1$
                        }
                });

        }

        /**
         * Execute a where condition to select row in the table
         * @param whereText
         */
        public void executeSQLWhere(String whereText) {

                try {
                        FilterDataSourceDecorator filterDataSourceDecorator = new FilterDataSourceDecorator(
                                tc.getDataSource());
                        filterDataSourceDecorator.setFilter(whereText);

                        long dsRowCount = filterDataSourceDecorator.getRowCount();

                        List<Integer> map = filterDataSourceDecorator.getIndexMap();
                        int[] sel = new int[map.size()];
                        for (int i = 0; i < dsRowCount; i++) {
                                sel[i] = (int) filterDataSourceDecorator.getOriginalIndex(i);
                        }

                        MapContext mapC = tc.getElement().getMapContext();
                        if (mapC != null) {
                                mapC.setSelectionInducedRefresh(true);
                        }

                        tc.checkSelectionRefresh(sel);
                        tc.getSelection().setSelectedRows(sel);

                } catch (DriverException e1) {
                        e1.printStackTrace();
                }

        }
}
