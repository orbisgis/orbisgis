/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;

import net.opengis.ows._2.LanguageStringType;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.layerModel.OwsMapContext;
import org.orbisgis.mapeditor.map.mapsManager.TreeLeafMapElement;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Update the tree node to replace the filename by the title
 * described in the map context files.
 * @author Nicolas Fortin
 */
public class ReadStoredMap extends SwingWorkerPM<ReadStoredMap.DoRename, ReadStoredMap.DoRename> {
    private static final I18n I18N = I18nFactory.getI18n(ReadStoredMap.class);
    List<TreeLeafMapElement> mapContextFiles;

    /**
     * @param mapContextFile  leaf map element
     */
    public ReadStoredMap(TreeLeafMapElement mapContextFile) {
        mapContextFiles = new ArrayList<>();
        mapContextFiles.add(mapContextFile);
        setTaskName(I18N.tr("Parse maps title and description"));
    }

    /**
     * Constructor using multiple leaf map element
     *
     * @param mapContextFiles Array of map elements.
     */
    public ReadStoredMap(List<TreeLeafMapElement> mapContextFiles) {
        this.mapContextFiles = mapContextFiles;
    }

    @Override
    protected DoRename doInBackground() throws Exception {
        for (int elIndex = 0; elIndex < mapContextFiles.size(); elIndex++) {
            TreeLeafMapElement mapEl = mapContextFiles.get(elIndex);
            MapContext el = mapEl.getMapElement(this, mapEl.getDataManager()).getMapContext();
            if (el instanceof OwsMapContext) {
                OwsMapContext mapContext = (OwsMapContext) el;
                if (mapContext.getJAXBObject().getGeneral() != null) {
                    LanguageStringType title = mapContext.getJAXBObject().getGeneral().getTitle();
                    if (title != null && !title.getValue().isEmpty()) {
                        publish(new DoRename(mapEl, title.getValue()));
                    }
                }
            }
            setProgress(elIndex / mapContextFiles.size() * 100);
            if (isCancelled()) {
                break;
            }
        }
        return null;
    }

    @Override
    protected void process(List<DoRename> chunks) {
        for(DoRename doRename : chunks) {
            if(doRename != null) {
                doRename.getTreeNode().setLabel(doRename.getNewLabel());
            }
        }
    }

    public static class DoRename {
        TreeLeafMapElement treeNode;
        String newLabel;

        public DoRename(TreeLeafMapElement treeNode, String newLabel) {
            this.treeNode = treeNode;
            this.newLabel = newLabel;
        }

        public TreeLeafMapElement getTreeNode() {
            return treeNode;
        }

        public String getNewLabel() {
            return newLabel;
        }
    }
        
}
