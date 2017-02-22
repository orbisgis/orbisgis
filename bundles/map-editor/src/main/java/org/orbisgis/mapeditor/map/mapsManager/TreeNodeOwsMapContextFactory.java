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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.MutableTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.common.MenuCommonFunctions;
import org.orbisgis.sif.components.fstree.TreeNodeFileFactory;
import org.orbisgis.sif.components.fstree.TreeNodeFolder;
import org.orbisgis.sif.edition.EditorManager;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * OWS Context Map (extension OWS).
 * @author Nicolas Fortin
 */
public class TreeNodeOwsMapContextFactory implements TreeNodeFileFactory {
        private static final I18n I18N = I18nFactory.getI18n(TreeNodeOwsMapContextFactory.class);
        private static final String ACTION_ADD_OWS_MAP = "TreeNodeOwsMapContextFactory:NewEmptyMap";
        private static final Logger LOGGER = LoggerFactory.getLogger(TreeNodeOwsMapContextFactory.class);
        private DataManager dataManager;
        private EditorManager editorManager;
        /**
         * Constructor.
         * @param dataManager DataManager used to initialize map context.
         */
        public TreeNodeOwsMapContextFactory(DataManager dataManager, EditorManager editorManager) {
            this.dataManager = dataManager;
            this.editorManager = editorManager;
        }

        @Override
        public TreeLeafMapElement create(File filePath) {
                return new TreeLeafMapContextFile(filePath, dataManager, editorManager);
        }

        /**
         * Create an empty OWS map at the specified path
         *
         * @param folderNode Where to create an empty map
         */
        private static void onCreateEmptyMap(TreeNodeFolder folderNode, DataManager dataManager) {
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
                                if(TreeLeafMapContextFile.createEmptyMapContext(mapContextFile, dataManager)) {
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
                        createEmptyMap.addActionListener(new CreateEmptyMap(folderNode, dataManager));
                        MenuCommonFunctions.updateOrInsertMenuItem(menu, createEmptyMap);
                }
        }
        
        /**
         * A listener that store a folderNode instance
         */
        private static class CreateEmptyMap implements ActionListener {
                TreeNodeFolder folderNode;
                DataManager dataManager;

                /**
                 * Constructor
                 * @param folderNode Location of folder
                 * @param dataManager DataManager used to initialize map context.
                 */
                public CreateEmptyMap(TreeNodeFolder folderNode, DataManager dataManager) {
                    this.folderNode = folderNode;
                    this.dataManager = dataManager;
                }

                @Override
                public void actionPerformed(ActionEvent ae) {
                        onCreateEmptyMap(folderNode, dataManager);
                }
                
        }
}
