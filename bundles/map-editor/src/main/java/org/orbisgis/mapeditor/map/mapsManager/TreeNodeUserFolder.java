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
package org.orbisgis.mapeditor.map.mapsManager;

import org.orbisgis.sif.components.fstree.TreeNodeFileFactoryManager;
import org.orbisgis.sif.common.MenuCommonFunctions;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;

/**
 * A folder that can be unlinked with map manager.
 * @author Nicolas Fortin
 */
public class TreeNodeUserFolder extends TreeNodeDiskFolder {
    private MapsManagerPersistenceImpl mapsManagerPersistence;

    /**
     * Constructor
     * @param folderPath Path of this folder
     * @param factoryManager File factory
     * @param mapsManagerPersistence Container of other TreeNodeUserFolder references
     * @throws IllegalArgumentException If the provided path represent a file
     */
    public TreeNodeUserFolder(File folderPath, TreeNodeFileFactoryManager factoryManager, MapsManagerPersistenceImpl mapsManagerPersistence) {
        super(folderPath, factoryManager);
        this.mapsManagerPersistence = mapsManagerPersistence;
    }

    /**
     * Unlink this folder
     */
    public void onUnlinkFolder() {
        model.removeNodeFromParent(this);
        List<String> current = new ArrayList<String>(mapsManagerPersistence.getMapCatalogFolderList());
        if(current.remove(getFilePath().getAbsolutePath())) {
            mapsManagerPersistence.setMapCatalogFolderList(current);
        }
    }

    @Override
    public void onDeleteFolder() {
        // Do not delete, should never be called
        throw new IllegalStateException("TreeNodeUserFolder#onDeleteFolder should never be called");
    }

    @Override
    public void feedPopupMenu(JPopupMenu menu) {
        super.feedPopupMenu(menu);
        // Add unlink menu
        JMenuItem folderRemove = new JMenuItem(I18N.tr("Unlink"),
                MapEditorIcons.getIcon("disconnect"));
        folderRemove.setToolTipText(I18N.tr("Unlink the folder, without deleting it"));
        folderRemove.setActionCommand("unlink");
        folderRemove.addActionListener(
                EventHandler.create(ActionListener.class,
                        this, "onUnlinkFolder"));
        MenuCommonFunctions.updateOrInsertMenuItem(menu, folderRemove, false);
        }       
}
