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
import java.io.File;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.MutableTreeNode;
import org.apache.log4j.Logger;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.common.MenuCommonFunctions;
import org.orbisgis.view.components.fstree.TreeNodeFileFactory;
import org.orbisgis.view.components.fstree.TreeNodeFolder;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * OWS Context Map (extension OWS).
 * @author Nicolas Fortin
 */
public class TreeNodeOwsMapContextFactory implements TreeNodeFileFactory {
        private static final I18n I18N = I18nFactory.getI18n(TreeNodeOwsMapContextFactory.class);
        private static final String ACTION_ADD_OWS_MAP = "TreeNodeOwsMapContextFactory:NewEmptyMap";
        private static final Logger LOGGER = Logger.getLogger(TreeNodeOwsMapContextFactory.class);
        
        /**
         * Constructor
         */
        public TreeNodeOwsMapContextFactory() {
        }
        
        @Override
        public TreeLeafMapElement create(File filePath) {
                return new TreeLeafMapContextFile(filePath);
        }

        /**
         * Create an empty OWS map at the specified path
         *
         * @param folderNode Where to create an empty map
         */
        private static void onCreateEmptyMap(TreeNodeFolder folderNode) {
                File folderPath = folderNode.getFilePath();                
                String folderName = JOptionPane.showInputDialog(UIFactory.getMainFrame(), I18N.tr("Enter the Map file name"), I18N.tr("MyMap"));
                if(folderName!=null) {
                        if (!folderName.endsWith(".ows")) {
                                folderName = folderName + ".ows";
                        }
                        File mapContextFile = new File(folderPath, folderName);
                        try {
                                if(mapContextFile.exists()) {
                                        LOGGER.error(I18N.tr("The specified file name already exist"));
                                        return;
                                }
                                if(TreeLeafMapContextFile.createEmptyMapContext(mapContextFile)) {
                                        folderNode.updateTree();
                                }
                        } catch (Throwable ex) {
                                LOGGER.error(I18N.tr("The map creation has failed"), ex);
                        }
                }
        }

        @Override
        public void feedTreeNodePopupMenu(MutableTreeNode node, JPopupMenu menu) {
                if(node instanceof TreeNodeFolder) {
                        TreeNodeFolder folderNode = (TreeNodeFolder)node;
                        JMenuItem createEmptyMap = new JMenuItem(I18N.tr("New empty map"));
                        createEmptyMap.setActionCommand(ACTION_ADD_OWS_MAP);
                        createEmptyMap.setToolTipText(I18N.tr("Create a new OWS Map in this folder"));
                        createEmptyMap.addActionListener(new CreateEmptyMap(folderNode));
                        MenuCommonFunctions.updateOrInsertMenuItem(menu,createEmptyMap);
                }
        }
        
        /**
         * A listener that store a folderNode instance
         */
        private static class CreateEmptyMap implements ActionListener {
                TreeNodeFolder folderNode;

                public CreateEmptyMap(TreeNodeFolder folderNode) {
                        this.folderNode = folderNode;
                }
                
                @Override
                public void actionPerformed(ActionEvent ae) {
                        onCreateEmptyMap(folderNode);
                }
                
        }
}
