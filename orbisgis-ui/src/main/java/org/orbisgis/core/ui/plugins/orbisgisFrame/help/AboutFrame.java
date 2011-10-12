/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.plugins.orbisgisFrame.help;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.log4j.Logger;
import org.gdms.plugins.GdmsPlugIn;
import org.orbisgis.core.ApplicationInfo;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.pluginSystem.Configuration;
import org.orbisgis.core.ui.pluginSystem.Extension;
import org.orbisgis.core.ui.pluginSystem.PlugInManager;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

/**
 * Frame of the about window.
 * @author Antoine Gourlay
 */
public class AboutFrame extends JPanel implements UIPanel {

        private JLabel versionLabel;
        private JPanel bigPanel;
        private JPanel pluginPanel;
        private final JEditorPane viewer = new JEditorPane();
        private Logger log = Logger.getLogger(AboutFrame.class);

        /**
         * Creates the about frame.
         */
        public AboutFrame() {

                setLayout(new BorderLayout());
                final JLabel jLabel = new JLabel(OrbisGISIcon.ORBISGIS_SPLASH);
                jLabel.setBackground(Color.WHITE);
                bigPanel = new JPanel();
                bigPanel.setLayout(new BoxLayout(bigPanel, BoxLayout.Y_AXIS));


                add(jLabel, BorderLayout.NORTH);

                ApplicationInfo ai = Services.getService(ApplicationInfo.class);
                versionLabel = new JLabel(ai.getName() + " " + ai.getVersionNumber() + " ("
                        + ai.getVersionName() + ")" + " - " + ai.getOrganization());
                versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                bigPanel.add(versionLabel);

                pluginPanel = new JPanel();
                pluginPanel.setLayout(new BorderLayout());
                pluginPanel.setMinimumSize(new Dimension(50, 70));
                bigPanel.add(pluginPanel);

                setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

                viewer.setEditable(false);
                try {
                        viewer.setPage(getClass().getResource("about.html"));
                } catch (IOException ex) {
                        log.error("Failed to load about text", ex);
                }
                final JScrollPane scrollPane = new JScrollPane(viewer);
                bigPanel.add(scrollPane);

                setPlugins();

                add(bigPanel, BorderLayout.CENTER);
        }

        private void setPlugins() {
                pluginPanel.add(new JLabel("Loaded plugins:"), BorderLayout.NORTH);

                DefaultTableModel model = new DefaultTableModel();
                model.setColumnIdentifiers(new String[]{"Name", "Version", "Type"});

                DataManager dm = Services.getService(DataManager.class);
                final org.gdms.plugins.PlugInManager plugInManager = dm.getDataSourceFactory().getPlugInManager();
                if (plugInManager != null) {
                        for (GdmsPlugIn p : plugInManager.getPlugIns()) {
                                model.addRow(new String[]{p.getName(), p.getVersion(), "Gdms"});
                        }
                }
                PlugInManager pm = Services.getService(WorkbenchContext.class).getWorkbench().getPlugInManager();
                if (pm != null) {
                        for (Object c : pm.getConfigurations()) {
                                if (c instanceof Extension) {
                                        Extension e = (Extension) c;
                                        model.addRow(new String[]{e.getName(), e.getVersion(), "OrbisGIS"});
                                } else {
                                        Configuration e = (Configuration) c;
                                        model.addRow(new String[]{e.getClass().getName(), "Unknown", "OrbisGIS (legacy)"});
                                }
                        }
                }

                JTable t = new JTable(model);
                t.setEnabled(false);


                pluginPanel.add(new JScrollPane(t), BorderLayout.CENTER);
        }

        @Override
        public URL getIconURL() {
                return null;
        }

        @Override
        public String getTitle() {
                return "About OrbisGIS";
        }

        @Override
        public String initialize() {
                return null;
        }

        @Override
        public String postProcess() {
                return null;
        }

        @Override
        public String validateInput() {
                return null;
        }

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public String getInfoText() {
                return null;
        }
}