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
package org.orbisgis.core.ui.components.listManager;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.utils.I18N;

public class ListManager extends JPanel {

    private static final String MODIFY = I18N.getString("orbisgis.org.orbisgis.geocatalog.createFile.modifyConstraint");
    private static final String REMOVE = I18N.getString("orbisgis.org.orbisgis.geocatalog.createFile.removeConstraint");
    private static final String ADD = I18N.getString("orbisgis.org.orbisgis.geocatalog.createFile.addConstraint");
    private ListManagerListener listener;
    private JTable table;
    private JButton btnModify;
    private JButton btnRemove;
    private JButton btnAdd;
    private TableModel model;

    public ListManager(ListManagerListener listener, TableModel tableModel) {
        JPanel pnlButtons = new JPanel();
        pnlButtons.setLayout(new CRFlowLayout());
        btnAdd = getButton(ADD);
        pnlButtons.add(btnAdd);
        pnlButtons.add(new CarriageReturn());
        btnRemove = getButton(REMOVE);
        pnlButtons.add(btnRemove);
        pnlButtons.add(new CarriageReturn());
        btnModify = getButton(MODIFY);
        pnlButtons.add(btnModify);
        pnlButtons.add(new CarriageReturn());
        this.setLayout(new BorderLayout());
        table = new JTable();
        this.model = tableModel;
        table.setModel(model);
        this.add(new JScrollPane(table), BorderLayout.CENTER);
        this.add(pnlButtons, BorderLayout.EAST);

        RefreshListener refreshListener = new RefreshListener();
        table.addKeyListener(refreshListener);
        table.addMouseListener(refreshListener);

        this.listener = listener;

        updateButtons();
    }

    private JButton getButton(String text) {
        JButton ret = new JButton(text);
        ret.setActionCommand(text);
        ret.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(ADD)) {
                    listener.addNewElement();
                } else if (e.getActionCommand().equals(REMOVE)) {
                    listener.removeElement(table.getSelectedRow());
                } else if (e.getActionCommand().equals(MODIFY)) {
                    listener.modifyElement(table.getSelectedRow());
                }
                updateButtons();
            }
        });

        return ret;
    }

    private void updateButtons() {
        btnRemove.setEnabled(table.getSelectedRow() != -1);
        btnModify.setEnabled(table.getSelectedRow() != -1);
    }

    private class RefreshListener implements KeyListener, MouseListener {

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
            updateButtons();
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            updateButtons();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            updateButtons();
        }
    }
}
