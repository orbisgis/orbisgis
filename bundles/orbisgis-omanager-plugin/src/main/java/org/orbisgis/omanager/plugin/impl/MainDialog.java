/*
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

package org.orbisgis.omanager.plugin.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import org.apache.felix.shell.gui.Plugin;
import org.apache.log4j.Logger;
import org.orbisgis.omanager.plugin.api.CustomPlugin;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @author Nicolas Fortin
 */
public class MainDialog extends JDialog {
    private static final Dimension DEFAULT_DIMENSION = new Dimension(980,480);
    private static final I18n I18N = I18nFactory.getI18n(MainDialog.class);
    private static final Logger LOGGER = Logger.getLogger("gui." + MainDialog.class);
    private JList<ItemPlugin> shellPlugins = new JList<>();
    private List<Plugin> loadedPlugins = new ArrayList<Plugin>();
    private JPanel centerComponent = new JPanel(new BorderLayout());
    private ListSelectionListener modelListener = EventHandler.create(ListSelectionListener.class,this,"onShellSelectionChange");

    /**
     * @param frame MainFrame, in order to place this dialog and release resource automatically.
     */
    public MainDialog(Frame frame) {
        super(frame);
        JPanel contentPane = new JPanel(new BorderLayout());
        shellPlugins.setVisible(false);
        shellPlugins.setCellRenderer(new ItemPluginListRenderer(shellPlugins));
        contentPane.add(shellPlugins,BorderLayout.WEST);
        contentPane.add(centerComponent,BorderLayout.CENTER);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setContentPane(contentPane);
        setSize(DEFAULT_DIMENSION);
        setTitle(I18N.tr("Plugin Manager"));
        shellPlugins.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Called when the user select another shell on the left list.
     */
    public void onShellSelectionChange() {
        if(shellPlugins.isVisible()) {
            centerComponent.removeAll();
            int selected = shellPlugins.getSelectedIndex();
            if(selected >= 0 && selected < loadedPlugins.size()) {
                centerComponent.add(loadedPlugins.get(shellPlugins.getSelectedIndex()).getGUI(),BorderLayout.CENTER);
            }
            //By default display the first element in the list
            else if(selected==-1){
                centerComponent.add(loadedPlugins.get(0).getGUI(),BorderLayout.CENTER);
                shellPlugins.setSelectedIndex(0);
            }
            centerComponent.updateUI();
        }
    }

    public void addPanel(Plugin plugin) {
        loadedPlugins.add(plugin);
        updateLoadedPluginsList();
        if(!shellPlugins.isVisible()) {
            centerComponent.add(plugin.getGUI(),BorderLayout.CENTER);
            centerComponent.updateUI();
            shellPlugins.setSelectedValue(plugin.getName(),true);
        }
    }

    public void removePanel(Plugin plugin) {
        loadedPlugins.remove(plugin);
        Object selected = shellPlugins.getSelectedValue();
        if((selected!=null && selected.equals(plugin.getName())) || loadedPlugins.isEmpty()) {
            centerComponent.removeAll();
            centerComponent.updateUI();
        }
        updateLoadedPluginsList();
        if(loadedPlugins.size()==1) {
            centerComponent.add(loadedPlugins.get(0).getGUI(),BorderLayout.CENTER);
        }
        centerComponent.updateUI();
    }

    private void updateLoadedPluginsList() {
        DefaultListModel<ItemPlugin> pluginNames = new DefaultListModel<>();
        for(Plugin plugin : loadedPlugins) {
            try {
                pluginNames.addElement(new ItemPlugin(plugin.getName(), ((CustomPlugin)plugin).getIcon()));
            } catch (ClassCastException ex) {
                pluginNames.addElement(new ItemPlugin(plugin.getName(),
                        new ImageIcon(ManagerMenuFactory.class.getResource("panel_icon.png"))));
            }
        }
        shellPlugins.setModel(pluginNames);
        // Set selection
        shellPlugins.addListSelectionListener(modelListener);
        shellPlugins.setVisible(pluginNames.size()>1);
    }
}
