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

import java.io.IOException;
import javax.swing.SwingWorker;
import javax.swing.tree.TreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.orbisgis.mapeditor.map.mapsManager.TreeNodeRemoteMap;
import org.orbisgis.mapeditor.map.mapsManager.TreeNodeWorkspace;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Delete the remote map context and update the tree
 *
 * @author Nicolas Fortin
 */
public class DeleteRemoteMapContext extends SwingWorker {
    private static final I18n I18N = I18nFactory.getI18n(DeleteRemoteMapContext.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteRemoteMapContext.class);
    private TreeNodeRemoteMap map;

    /**
     * Constructor
     *
     * @param map The map tree node
     */
    public DeleteRemoteMapContext(TreeNodeRemoteMap map) {
        this.map = map;
    }

    @Override
    protected Object doInBackground() throws Exception {
        try {
            map.getRemoteMapContext().delete();
        } catch (IOException ex) {
            LOGGER.error(I18N.tr("The map cannot be removed"), ex);
        }
        return null;
    }

    @Override
    protected void done() {
        TreeNode parent = map.getParent();
        if (parent instanceof TreeNodeWorkspace) {
            TreeNodeWorkspace workspace = (TreeNodeWorkspace) parent;
            workspace.update();
        }
    }

    @Override
    public String toString() {
        return I18N.tr("Delete the remote map context");
    }

}
