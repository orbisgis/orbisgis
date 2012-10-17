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
package org.orbisgis.view.map.mapsManager.jobs;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import net.opengis.ows._2.LanguageStringType;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.layerModel.OwsMapContext;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.map.mapsManager.TreeLeafMapElement;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Update the tree node to replace the filename by the title
 * described in the map context files.
 * @author Nicolas Fortin
 */
public class ReadStoredMap implements BackgroundJob {
        private static final I18n I18N = I18nFactory.getI18n(ReadStoredMap.class);
        List<TreeLeafMapElement> mapContextFiles;

        public ReadStoredMap(TreeLeafMapElement mapContextFile) {
                mapContextFiles = new ArrayList<TreeLeafMapElement>();
                mapContextFiles.add(mapContextFile);
        }       
        public ReadStoredMap(List<TreeLeafMapElement> mapContextFiles) {
                this.mapContextFiles = mapContextFiles;
        }       
        
        @Override
        public void run(ProgressMonitor pm) {
                for(int elIndex=0; elIndex<mapContextFiles.size();elIndex++) {
                        TreeLeafMapElement mapEl = mapContextFiles.get(elIndex);
                        MapContext el = mapEl.getMapElement(pm).getMapContext();
                        if(el instanceof OwsMapContext) {
                                OwsMapContext mapContext = (OwsMapContext)el;
                                if(mapContext.getJAXBObject().getGeneral()!=null) {
                                        LanguageStringType title = mapContext.getJAXBObject().getGeneral().getTitle();
                                        if(title!=null && !title.getValue().isEmpty())
                                        SwingUtilities.invokeLater(new DoRename(mapEl,title.getValue()));
                                }
                        }
                        pm.progressTo(elIndex / mapContextFiles.size() * 100);
                        if(pm.isCancelled()) {
                                return;
                        }
                }
        }

        @Override
        public String getTaskName() {
                return I18N.tr("Parse maps title and description");
        }
        
        private static class DoRename implements Runnable {
                TreeLeafMapElement treeNode;
                String newLabel;

                public DoRename(TreeLeafMapElement treeNode, String newLabel) {
                        this.treeNode = treeNode;
                        this.newLabel = newLabel;
                }                
                @Override
                public void run() {
                        treeNode.setLabel(newLabel);
                }                
        }
        
}
