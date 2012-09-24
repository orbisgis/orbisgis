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

import java.io.IOException;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.layerModel.mapcatalog.Workspace;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.map.mapsManager.TreeNodeWorkspace;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Nicolas Fortin
 */
public class UploadMapContext implements BackgroundJob {
        private static final I18n I18N = I18nFactory.getI18n(UploadMapContext.class);
        private static final Logger LOGGER = Logger.getLogger(UploadMapContext.class);
        private MapContext mapContext;
        private TreeNodeWorkspace workspaceNode;
        private Integer mapContextid;

        /**
         * Upload a new Map Context
         * @param mapContext
         * @param workspaceNode
         * @param mapContextid 
         */
        public UploadMapContext(MapContext mapContext, TreeNodeWorkspace workspaceNode, int mapContextid) {
                this.mapContext = mapContext;
                this.workspaceNode = workspaceNode;
                this.mapContextid = mapContextid;
        }
        
        /**
         * Update an existing remote map context
         * @param mapContext
         * @param workspaceNode 
         */
        public UploadMapContext(MapContext mapContext, TreeNodeWorkspace workspaceNode) {
                this.mapContext = mapContext;
                this.workspaceNode = workspaceNode;
                this.mapContextid = null;
        }
        
        
        @Override
        public void run(ProgressMonitor pm) {
                Workspace workspace = workspaceNode.getWorkspace();
                try {
                        workspace.publishMapContext(mapContext,mapContextid);
                        SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                        workspaceNode.update();
                                }
                        });
                        
                } catch(IOException ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                }
        }

        @Override
        public String getTaskName() {
                return I18N.tr("Uploading the map context..");
        }
        
}
