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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.OwsMapContext;
import org.orbisgis.core.renderer.se.common.Description;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.common.MenuCommonFunctions;
import org.orbisgis.view.components.fstree.TreeNodeCustomIcon;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.MapElement;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Nicolas Fortin
 */
public final class TreeLeafMapContextFile extends TreeLeafMapElement implements TreeNodeCustomIcon  {
        private static final Logger LOGGER = Logger.getLogger(TreeLeafMapContextFile.class);
        private static final I18n I18N = I18nFactory.getI18n(TreeLeafMapContextFile.class);
                
        public TreeLeafMapContextFile(File mapContextFilePath) {
                // For fast loading, take the filename as the ows title
                super(mapContextFilePath);
                setLabel(FilenameUtils.getBaseName(mapContextFilePath.getName()));
                setEditable(false);
        }
        
        /**
         * Create and save a minimal ows Map context file
         * @param fileName
         * @return
         */
        public static boolean createEmptyMapContext(File fileName) {
                
                //Create an empty map context
                OwsMapContext emptyMapContext = new OwsMapContext();                
                try {
                        //Set minimal informations
                        emptyMapContext.open(null);
                        Description mapDescription = new Description();
                        mapDescription.addTitle(Locale.getDefault(),
                                FilenameUtils.getBaseName(fileName.getName()));
                        emptyMapContext.setDescription(mapDescription);
                        emptyMapContext.close(null);
                        emptyMapContext.write(new FileOutputStream(fileName));
                } catch (FileNotFoundException ex) {
                        LOGGER.error(I18N.tr("Map creation failed"),ex);
                        return false;
                } catch (LayerException ex) {
                        LOGGER.error(I18N.tr("Map creation failed"),ex);
                        return false;
                }
                return true;
        }
        
        public void onDeleteFile() {
                if(getFilePath().exists()) {            
                        int result = JOptionPane.showConfirmDialog(UIFactory.getMainFrame(),
                                I18N.tr("Are you sure you want to delete permanently the selected files ?"),
                                I18N.tr("Remove the files"),
                                JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                        if(result == JOptionPane.YES_OPTION) {
                                try {
                                        if(!getFilePath().delete()) {   
                                                LOGGER.error(I18N.tr("Cannot remove the file"));
                                        }
                                } catch(SecurityException ex) {
                                        LOGGER.error(I18N.tr("Cannot remove the file {0}",getFilePath().getName()),ex);
                                }                        
                        } else {
                                return;
                        }
                }
                model.removeNodeFromParent(this);
        }
        @Override
        public void feedPopupMenu(JPopupMenu menu) {
                super.feedPopupMenu(menu);
                if(!isLoaded()) {
                        JMenuItem folderRemove = new JMenuItem(I18N.tr("Delete"),
                                OrbisGISIcon.getIcon("remove"));
                        folderRemove.setToolTipText(I18N.tr("Remove permanently the map"));
                        folderRemove.setActionCommand("delete");
                        folderRemove.addActionListener(
                        EventHandler.create(ActionListener.class,
                        this, "onDeleteFile"));
                        MenuCommonFunctions.updateOrInsertMenuItem(menu,folderRemove);
                }
        }       

        @Override
        public void setUserObject(Object o) {
                // The map context need to be open and close to edit the label,
                // this can be quite long and register some data sources..
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
        
        private OwsMapContext getMapContext() throws FileNotFoundException {
                OwsMapContext mapContext = new OwsMapContext();
                mapContext.read(new FileInputStream(getFilePath()));
                return mapContext;
        }

        @Override
        public MapElement getMapElement(ProgressMonitor pm) {
                try {
                        MapElement mapElement = new MapElement(getMapContext(), getFilePath());
                        return mapElement;
                } catch(FileNotFoundException ex) {
                        throw new IllegalStateException(ex);
                }
        }
}
