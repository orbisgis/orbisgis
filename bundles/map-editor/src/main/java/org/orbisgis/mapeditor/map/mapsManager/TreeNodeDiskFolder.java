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

import java.io.File;
import javax.swing.ImageIcon;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.sif.components.fstree.TreeNodeCustomIcon;
import org.orbisgis.sif.components.fstree.TreeNodeFileFactoryManager;
import org.orbisgis.sif.components.fstree.TreeNodeFolder;

/**
 * TreeNodeFolder is overloaded in order to define custom folder icons.
 * @author ebocher
 */
public class TreeNodeDiskFolder extends TreeNodeFolder implements TreeNodeCustomIcon {

    public TreeNodeDiskFolder(File folderPath, TreeNodeFileFactoryManager factoryManager) {
        super(folderPath, factoryManager);
    }

    @Override
    protected TreeNodeFolder createInstance(File folderPath, TreeNodeFileFactoryManager factoryManager) {
        return new TreeNodeDiskFolder(folderPath, factoryManager);
    }    
    
    @Override
    public ImageIcon getLeafIcon() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImageIcon getClosedIcon() {
        return MapEditorIcons.getIcon("folder");
    }

    @Override
    public ImageIcon getOpenIcon() {
         return MapEditorIcons.getIcon("folder_open");
    }  
}
