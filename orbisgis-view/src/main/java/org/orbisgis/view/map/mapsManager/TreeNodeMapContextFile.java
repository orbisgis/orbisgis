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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Enumeration;
import javax.swing.ImageIcon;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.layerModel.OwsMapContext;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.MapElement;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Nicolas Fortin
 */
public final class TreeNodeMapContextFile implements TreeNodeMapElement, MutableTreeNode, TreeNodeCustomIcon  {
        private static final Logger LOGGER = Logger.getLogger(TreeNodeMapContextFile.class);
        private static final I18n I18N = I18nFactory.getI18n(TreeNodeMapContextFile.class);
        
        File filePath; // Update if parent change
        String label;
        MutableTreeNode parent;

        public TreeNodeMapContextFile(File mapContextFilePath) {
                // For fast loading, take the filename as the ows title
                filePath = mapContextFilePath;
                setLabel(FilenameUtils.getBaseName(mapContextFilePath.getName()));
        }
        
        /**
         * 
         * @param fileName
         * @return
         */
        public static boolean createEmptyMapContext(File fileName) {
                
                //Create an empty map context
                OwsMapContext emptyMapContext = new OwsMapContext();
                try {
                        emptyMapContext.write(new FileOutputStream(fileName));
                } catch (FileNotFoundException ex) {
                        LOGGER.error(I18N.tr("Map creation failed"),ex);
                        return false;
                }
                return true;
        }

        public void setLabel(String label) {
                this.label = label;
        }
        
        private File getFilePath() {
                if(parent instanceof TreeNodeFolder) {
                        TreeNodeFolder parentFolder = (TreeNodeFolder)parent;
                        return new File(parentFolder.getFolderPath(),FilenameUtils.getName(filePath.getName()));
                } else {
                        return filePath;
                }
        }

        @Override
        public String toString() {
                return label;
        }
        
        @Override
        public void insert(MutableTreeNode mtn, int i) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void remove(int i) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void remove(MutableTreeNode mtn) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setUserObject(Object o) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removeFromParent() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setParent(MutableTreeNode mtn) {
                parent = mtn;
        }

        @Override
        public TreeNode getChildAt(int i) {
                throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getChildCount() {
                return 0;
        }

        @Override
        public TreeNode getParent() {
                return parent;
        }

        @Override
        public int getIndex(TreeNode tn) {
                throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean getAllowsChildren() {
                return false;
        }

        @Override
        public boolean isLeaf() {
                return true;
        }

        @Override
        public Enumeration<TreeNode> children() {
                throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ImageIcon getLeafIcon() {
                return OrbisGISIcon.getIcon("map");
        }

        @Override
        public ImageIcon getClosedIcon() {
                throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ImageIcon getOpenIcon() {
                throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public MapElement getMapElement(ProgressMonitor pm) {
                MapContext mapContext = new OwsMapContext();
                try {
                        mapContext.read(new FileInputStream(getFilePath()));
                        MapElement mapElement = new MapElement(mapContext, getFilePath());
                        return mapElement;
                } catch(FileNotFoundException ex) {
                        throw new IllegalStateException(ex);
                }
        }
        
}
