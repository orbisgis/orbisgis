/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * or contact directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.workspace;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.PopupMenu;
import java.awt.TextField;
import java.awt.Window;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import org.orbisgis.sif.components.CustomButton;
import org.orbisgis.sif.multiInputPanel.ComboBoxChoice;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author ebocher
 */
public class DatabaseSettingsPanel extends JDialog {

    protected static final I18n I18N = I18nFactory.getI18n(DatabaseSettingsPanel.class);
    private JPanel mainPanel;
    private AtomicBoolean initialised = new AtomicBoolean(false);

    public DatabaseSettingsPanel() {
        super();
        init();
    }
    
    public DatabaseSettingsPanel(Dialog owner) {
        super(owner);
        init();
    }
    
    public DatabaseSettingsPanel(Frame owner) {
        super(owner);
        init();
    }
    
    public DatabaseSettingsPanel(Window owner) {
        super(owner);
        init();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        init();
    }

    private void init() {
        if (!initialised.getAndSet(true)) {
            mainPanel = new JPanel(new MigLayout());
            JLabel cbLabel = new JLabel(I18N.tr("Saved settings"));
            ComboBoxChoice cbc = new ComboBoxChoice("Vilaine");
            mainPanel.add(cbLabel);
            mainPanel.add(cbc.getComboBox(), "span, grow");
            JLabel labelName = new JLabel(I18N.tr("Setting name"));
            TextField connectionName = new TextField();
            mainPanel.add(labelName);
            mainPanel.add(connectionName, "width 200!");
            CustomButton saveBt = new CustomButton(OrbisGISIcon.getIcon("save"));
            CustomButton removeBt = new CustomButton(OrbisGISIcon.getIcon("remove"));
            mainPanel.add(saveBt, "width 16!");
            mainPanel.add(removeBt, "width 16!, wrap");
            JLabel labelURL = new JLabel("URL");
            TextField urlValue = new TextField();
            mainPanel.add(labelURL);
            mainPanel.add(urlValue, "span, grow, wrap");
            JLabel userLabel = new JLabel(I18N.tr("User name"));
            TextField userValue = new TextField();
            mainPanel.add(userLabel);
            mainPanel.add(userValue, "span, grow, wrap");
            JLabel pswLabel = new JLabel(I18N.tr("Password"));
            TextField pswValue = new TextField();
            mainPanel.add(pswLabel);
            mainPanel.add(pswValue, "span, grow");            
            getContentPane().add(mainPanel);
            setTitle(I18N.tr("Database parameters"));
            pack();
            setResizable(false);
        }
    }

  
    
}
