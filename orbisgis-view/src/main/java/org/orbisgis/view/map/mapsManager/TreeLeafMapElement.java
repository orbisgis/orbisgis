/*
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
package org.orbisgis.view.map.mapsManager;

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.orbisgis.core.Services;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.common.MenuCommonFunctions;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.components.fstree.AbstractTreeNode;
import org.orbisgis.view.components.fstree.PopupTreeNode;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.MapElement;
import org.orbisgis.view.map.jobs.ReadMapContextJob;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Maps manager manage only files that contain a MapElement
 * @author Nicolas Fortin
 */
public abstract class TreeLeafMapElement extends AbstractTreeNode implements PopupTreeNode {
        private static final I18n I18N = I18nFactory.getI18n(TreeLeafMapElement.class);
        
        /**
         * Create the map element associated with this file
         * @param pm 
         * @return 
         */
        abstract public MapElement getMapElement(ProgressMonitor pm);  
               
        /**
         * Open the selected map file (only the first one selected)
         */
        public void onOpenMap() {
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.backgroundOperation(new ReadMapContextJob(getMapElement(new NullProgressMonitor())));
        }

        @Override
        public void feedPopupMenu(JPopupMenu menu) {
                JMenuItem openMapItem = new JMenuItem(I18N.tr("Open the map"), OrbisGISIcon.getIcon("map"));
                openMapItem.addActionListener(EventHandler.create(ActionListener.class,this,"onOpenMap"));
                openMapItem.setActionCommand("TreeNodeMapElement:OpenMap");
                MenuCommonFunctions.updateOrInsertMenuItem(menu, openMapItem);
        }
}
