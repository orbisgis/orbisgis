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

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.orbisgis.omanager.plugin.api.CustomPlugin;
import org.orbisgis.viewapi.components.actions.DefaultAction;
import org.orbisgis.viewapi.main.frames.ext.MainFrameAction;
import org.orbisgis.viewapi.main.frames.ext.MainWindow;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.RepositoryAdmin;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Create menu item and create the main menu when the user click on the menu item.
 * @author Nicolas Fortin
 */
@Component
public class ManagerMenuFactory implements MainFrameAction {
    private static final Logger LOGGER = Logger.getLogger("gui."+ManagerMenuFactory.class);
    public static final String MENU_MANAGE_PLUGINS = "A_MANAGE_PLUGINS";
    private static final String OSGI_REPOSITORY_FILENAME = "repositories.properties";
    private final URI ORBISGIS_OSGI_REPOSITORY = URI.create("http://plugins.orbisgis.org/.meta/obr.xml");
    private static final I18n I18N = I18nFactory.getI18n(ManagerMenuFactory.class);
    private MainDialog mainPanel;
    private MainWindow target; // There is only one main window in the application, it can be stored here.
    private List<CustomPlugin> panels = new ArrayList<>();
    private AtomicBoolean addingRepositories = new AtomicBoolean(false);
    private RepositoryAdmin repositoryAdmin;

    public List<Action> createActions(MainWindow target) {
        this.target = target;
        List<Action> actions = new ArrayList<>();
        actions.add(new DefaultAction(MENU_MANAGE_PLUGINS,I18N.tr("&Manage plugins"),
                new ImageIcon(ManagerMenuFactory.class.getResource("panel_icon.png")),
                EventHandler.create(ActionListener.class,this,"showManager")).setParent(MENU_TOOLS).setInsertFirst(true));
        return actions;
    }

    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, service = CustomPlugin.class, policy = ReferencePolicy.DYNAMIC)
    public void addPlugin(CustomPlugin plugin) {
        if(!panels.contains(plugin)) {
            panels.add(plugin);
            if (mainPanel != null) {
                mainPanel.addPanel(plugin);
            }
        }
    }

    public void removePlugin(CustomPlugin plugin) {
        if(panels.remove(plugin)) {
            if (mainPanel != null) {
                mainPanel.removePanel(plugin);
            }
        }
    }

    @Reference
    public void setRepositoryAdmin(RepositoryAdmin repositoryAdmin) {
        this.repositoryAdmin = repositoryAdmin;
    }

    public void unsetRepositoryAdmin(RepositoryAdmin repositoryAdmin) {
        this.repositoryAdmin = null;
    }

    /**
     * Make and show the plug-ins manager
     */
    public void showManager() {
        if(mainPanel==null) {
            mainPanel = new MainDialog(target.getMainFrame());
            mainPanel.setModal(false);
            for(CustomPlugin plugin : panels) {
                mainPanel.addPanel(plugin);
            }
        }
        mainPanel.setVisible(true);
    }

    public void disposeActions(MainWindow target, List<Action> actions) {
        // Close the Dialog if created
        if(mainPanel != null) {
            mainPanel.dispose();
        }
    }

    @Activate
    public void activate(BundleContext bc) {
        if(!addingRepositories.getAndSet(true)) {
            // Read repositories
            Set<URI> obrRepositories = new HashSet<>();
            obrRepositories.add(ORBISGIS_OSGI_REPOSITORY);
            readRepositoryListFile(obrRepositories, bc.getDataFile(OSGI_REPOSITORY_FILENAME));
            RegisterSavedRepositories process = new RegisterSavedRepositories(obrRepositories,repositoryAdmin,addingRepositories);
            process.execute();
        }
    }

    @Deactivate
    public void deactivate(BundleContext bc) {
        // Save repository list
        File repoListFile = bc.getDataFile(OSGI_REPOSITORY_FILENAME);
        final String LINE_SEP = System.getProperty("line.separator");
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(repoListFile))) {
                for (Repository repository : repositoryAdmin.listRepositories()) {
                    writer.write(repository.getURL().toURI().toString() + LINE_SEP);
                }
            }
        }catch (Exception ex) {
            LOGGER.error("Could not save the plugins repository list",ex);
        }
    }

    private void readRepositoryListFile(Set<URI> obrRepositories,File repoListFile) {
        if(repoListFile.exists()) {
            try {
                try (BufferedReader reader = new BufferedReader(new FileReader((repoListFile)))) {
                    while (reader.ready()) {
                        String line = reader.readLine();
                        if (!line.trim().isEmpty()) {
                            try {
                                URI obrURI = URI.create(line);
                                obrRepositories.add(obrURI);
                            } catch (IllegalArgumentException ex) {
                                LOGGER.error(ex.getLocalizedMessage(), ex);
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                //Invalid property file, remove it
                LOGGER.error(ex.getLocalizedMessage(), ex);
                if(!repoListFile.delete()) {
                    LOGGER.error("Could not remove repository file "+repoListFile.getAbsolutePath());
                }
            }
        }
    }
}
