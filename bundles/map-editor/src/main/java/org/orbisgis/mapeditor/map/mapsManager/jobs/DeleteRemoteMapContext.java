/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
