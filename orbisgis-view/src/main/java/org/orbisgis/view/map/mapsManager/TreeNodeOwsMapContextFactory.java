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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.File;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.MutableTreeNode;
import org.orbisgis.core.Services;
import org.orbisgis.view.workspace.ViewWorkspace;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * OWS Context Map (extension OWS).
 * @author Nicolas Fortin
 */
public class TreeNodeOwsMapContextFactory implements TreeNodeMapFactory {
        private static final I18n I18N = I18nFactory.getI18n(TreeNodeOwsMapContextFactory.class);
        private TreeNodeFolder folderRoot;
        /**
         * Constructor
         * @param folderRoot The root of the Map folder, used to update file system on map insertion
         */
        public TreeNodeOwsMapContextFactory(TreeNodeFolder folderRoot) {
                this.folderRoot = folderRoot;
        }
        
        @Override
        public TreeNodeMapElement create(File filePath) {
                return new TreeNodeMapContextFile(filePath);
        }

        /**
         * Create an empty OWS map at the specified path
         * @param evt 
         */
        public void onCreateEmptyMap(ActionEvent evt) {
                String path = evt.getActionCommand();
                TreeNodeMapContextFile.createEmptyMapContext(new File(path));
                folderRoot.updateTree();
        }

        @Override
        public void feedTreeNodePopupMenu(MutableTreeNode node, JPopupMenu menu) {
                if(node instanceof TreeNodeFolder) {
                        ViewWorkspace viewWorkspace = Services.getService(ViewWorkspace.class);                        
                        File folderPath = ((TreeNodeFolder)node).getFilePath();
                        File mapContextFile = new File(folderPath, viewWorkspace.getMapContextDefaultFileName());
                        JMenuItem createEmptyMap = new JMenuItem(I18N.tr("New empty map"));
                        createEmptyMap.setToolTipText(I18N.tr("Create a new OWS Map in this folder"));
                        createEmptyMap.addActionListener(EventHandler.create(ActionListener.class,this,"onCreateEmptyMap",""));
                        createEmptyMap.setActionCommand(mapContextFile.getAbsolutePath());
                        menu.add(createEmptyMap);
                }
        }
}
