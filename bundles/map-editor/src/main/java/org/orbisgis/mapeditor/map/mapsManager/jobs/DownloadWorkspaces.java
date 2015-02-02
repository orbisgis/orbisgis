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
package org.orbisgis.mapeditor.map.mapsManager.jobs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.coremap.layerModel.mapcatalog.ConnectionProperties;
import org.orbisgis.coremap.layerModel.mapcatalog.RemoteMapCatalog;
import org.orbisgis.coremap.layerModel.mapcatalog.Workspace;
import org.orbisgis.mapeditor.map.mapsManager.TreeNodeBusy;
import org.orbisgis.mapeditor.map.mapsManager.TreeNodeMapCatalogServer;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Download the Workspaces and feed the server tree node.
 *
 * @author Nicolas Fortin
 */
public class DownloadWorkspaces extends SwingWorker<List<Workspace>, List<Workspace>> {
    private static final I18n I18N = I18nFactory.getI18n(ReadStoredMap.class);
    private TreeNodeMapCatalogServer server;
    private TreeNodeBusy treeNodeBusyHint;
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadWorkspaces.class);
    private DataManager dataManager;
    private File mapsFolder;

    public DownloadWorkspaces(TreeNodeMapCatalogServer server, TreeNodeBusy treeNodeBusyHint,
                              DataManager dataManager, File mapsFolder) {
        this.server = server;
        this.treeNodeBusyHint = treeNodeBusyHint;
        this.dataManager = dataManager;
        this.mapsFolder = mapsFolder;
    }

    @Override
    public String toString() {
        return I18N.tr("Download the workspaces of {0}", server.getServerUrl().getHost());
    }

    @Override
    protected List<Workspace> doInBackground() throws Exception {
        //
        try {
            treeNodeBusyHint.setDoAnimation(true);
            ConnectionProperties parameters = new ConnectionProperties(server.getServerUrl(), dataManager, mapsFolder);
            RemoteMapCatalog mapServer = new RemoteMapCatalog(parameters);
            return mapServer.getWorkspaces();
        } catch (IOException ex) {
            // Download fail, inform the user
            // By logging and by the server icon
            return null;
        } finally {
            // Stop animation
            treeNodeBusyHint.setDoAnimation(false);
        }
    }

    @Override
    protected void done() {
        try {
            feedServerNode(get());
        } catch (InterruptedException | ExecutionException ex) {
            feedServerNode(null);
            LOGGER.error(I18N.tr("Cannot download the server's workspaces of {0}", server.getServerUrl().getHost()),
                    ex);
        }
    }

    private void feedServerNode(List<Workspace> workspaces) {
        if (workspaces != null) {
            for (Workspace workspace : workspaces) {
                server.addWorkspace(workspace);
            }
            server.setServerStatus(TreeNodeMapCatalogServer.SERVER_STATUS.CONNECTED);
        } else {
            server.setServerStatus(TreeNodeMapCatalogServer.SERVER_STATUS.UNREACHABLE);
        }
    }
}
