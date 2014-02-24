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
package org.orbisgis.sif;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;

/**
 * This dialog can host a single SimplePanel. It has a OK
 * and a Cancel buttons as well as SIFDialog. It provides also a
 * Apply button that can be customized through an ActionListener.
 * @author Alexis Guéganno
 */
public class ApplyDialog extends AbstractOutsideFrame {

    protected JButton btnOk;
    protected JButton btnCancel;
    private SimplePanel simplePanel;
    protected JPanel pnlButtons;
    private JButton btnApply;
    private ActionListener applyListener;

    /**
     * Builds a new ApplyDialog
     * @param owner The parent window of this dialog.
     * @param applyListener The ActionListener that must be associated to the
     *                      apply button.
     */
    public ApplyDialog(Window owner, ActionListener applyListener) {
        super(owner);
        init(applyListener);
    }

    /**
     * Initializes the main panel and the buttons. {@code applyListener}
     * will be associated to the Apply button.
     * @param applyListener The listener associated to the apply button.
     */
    private void init(ActionListener applyListener) {
        this.setLayout(new BorderLayout());
        this.applyListener = applyListener;
        btnOk = new JButton(I18N.tr("OK"));
        btnOk.setBorderPainted(false);
        btnOk.addActionListener(EventHandler.create(ActionListener.class, this , "onOk", ""));
        getRootPane().setDefaultButton(btnOk);
        btnCancel = new JButton(I18N.tr("Cancel"));
        btnCancel.setBorderPainted(false);
        btnCancel.addActionListener(EventHandler.create(ActionListener.class, this , "onCancel"));
        btnApply = new JButton(I18N.tr("Apply"));
        btnApply.setBorderPainted(false);
        btnApply.addActionListener(applyListener);
        pnlButtons = new JPanel();
        pnlButtons.add(btnOk);
        pnlButtons.add(btnApply);
        pnlButtons.add(btnCancel);
        this.add(pnlButtons, BorderLayout.SOUTH);

        add(errorLabel, BorderLayout.NORTH);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void onOk(ActionEvent e) {
        applyListener.actionPerformed(e);
        exit(false);
    }

    public void onCancel() {
        exit(false);
    }
    /**
     * Sets the center component og this dialog.
     * @param simplePanel The main component of the panel.
     */
    public void setComponent(SimplePanel simplePanel) {
        this.simplePanel = simplePanel;
        this.add(simplePanel, BorderLayout.CENTER);
        this.setIconImage(getSimplePanel().getIconImage());
    }

    @Override
    protected SimplePanel getSimplePanel() {
        return simplePanel;
    }

}
