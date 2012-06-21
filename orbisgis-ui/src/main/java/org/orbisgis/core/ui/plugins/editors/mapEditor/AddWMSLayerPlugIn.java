/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.editors.mapEditor;

import java.util.Vector;
import javax.swing.JButton;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;

import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.utils.I18N;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.wms.WMSSource;
import org.gvsig.remoteClient.wms.WMSClient;
import org.gvsig.remoteClient.wms.WMSLayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.plugins.views.geocatalog.newSourceWizards.wms.LayerConfigurationPanel;
import org.orbisgis.core.ui.plugins.views.geocatalog.newSourceWizards.wms.SRSPanel;
import org.orbisgis.core.ui.plugins.views.geocatalog.newSourceWizards.wms.WMSConnectionPanel;

public class AddWMSLayerPlugIn extends AbstractPlugIn {

        private JButton btn;

        public AddWMSLayerPlugIn() {
                btn = new JButton(OrbisGISIcon.GEOCATALOG_WMS);
                btn.setToolTipText(I18N.getString("orbisgis.org.orbisgis.ui.addWMSLayerPlugIn"));
        }

        @Override
        public void initialize(PlugInContext context) throws Exception {
                WorkbenchContext wbcontext = context.getWorkbenchContext();
                wbcontext.getWorkbench().getFrame().getMainToolBar().addPlugIn(
                        this, btn, context);
        }

        @Override
        public boolean execute(PlugInContext context) throws Exception {
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.backgroundOperation(new AddWMSLayerJob());
                return true;
        }

        @Override
        public boolean isEnabled() {
                btn.setEnabled(true);
                return true;
        }

        /**
         * A background job to add a WMS layer in the TOC
         * without freezing the renderer
         */
        private final class AddWMSLayerJob implements BackgroundJob {

                @Override
                public void run(ProgressMonitor pm) {
                        IEditor editor = Services.getService(EditorManager.class).getActiveEditor();
                        MapContext mc = null;
                        if (editor != null && editor instanceof MapEditorPlugIn) {
                                mc = (MapContext) editor.getElement().getObject();
                        }
                        LayerConfigurationPanel layerConfiguration = new LayerConfigurationPanel();
                        WMSConnectionPanel wmsConnection = new WMSConnectionPanel(layerConfiguration);
                        SRSPanel srsPanel = new SRSPanel(wmsConnection);
                        if (UIFactory.showDialog(new UIPanel[]{wmsConnection,
                                        layerConfiguration, srsPanel})) {
                                WMSClient client = wmsConnection.getWMSClient();
                                String validImageFormat = getFirstImageFormat(client.getFormats());
                                if (validImageFormat == null) {
                                        ErrorMessages.error(I18N.getString("orbisgis.errorMessages.wms.CannotFindImageFormat"));
                                } else {
                                        Object[] layers = layerConfiguration.getSelectedLayers();
                                        for (Object layer : layers) {
                                                String layerName = ((WMSLayer) layer).getName();
                                                WMSSource source = new WMSSource(client.getHost(),
                                                        layerName, srsPanel.getSRS(), validImageFormat);
                                                DataManager dm = (DataManager) Services.getService(DataManager.class);
                                                SourceManager sourceManager = dm.getSourceManager();
                                                String uniqueName = sourceManager.getUniqueName(layerName);
                                                sourceManager.register(uniqueName, source);
                                                try {
                                                        if (mc != null) {
                                                                mc.getLayerModel().addLayer(dm.createLayer(uniqueName));
                                                        }
                                                } catch (LayerException e) {
                                                        Services.getErrorManager().warning(I18N.getString("orbisgis.org.orbisgis.ui.toc.cannotAddSource"));
                                                }
                                        }
                                }

                        }

                }

                @Override
                public String getTaskName() {
                        return I18N.getString("orbisgis.org.orbisgis.ui.addWMSLayerPlugIn.addingLayer");
                }
        }

        private String getFirstImageFormat(Vector<?> formats) {
                String[] preferredFormats = new String[]{"image/png", "image/jpeg",
                        "image/gif", "image/tiff"};
                for (int i = 0; i < preferredFormats.length; i++) {
                        if (formats.contains(preferredFormats[i])) {
                                return preferredFormats[i];
                        }
                }

                for (Object object : formats) {
                        String format = object.toString();
                        if (format.startsWith("image/")) {
                                return format;
                        }
                }

                return null;
        }
}
