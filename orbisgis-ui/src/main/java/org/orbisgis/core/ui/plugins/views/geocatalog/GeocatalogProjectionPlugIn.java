/**
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
package org.orbisgis.core.ui.plugins.views.geocatalog;

import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.crs.ProjectionConfigPanel;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SelectionAvailability;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SourceAvailability;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class GeocatalogProjectionPlugIn extends AbstractPlugIn {

        ProjectionConfigPanel projectionConfigPanel = null;

        @Override
        public boolean execute(PlugInContext context) throws Exception {
                String[] res = getPlugInContext().getSelectedSources();
                DataManager dm = Services.getService(DataManager.class);
                if (projectionConfigPanel == null) {
                        projectionConfigPanel = new ProjectionConfigPanel(dm.getDataSourceFactory(), null, true);
                }
                projectionConfigPanel.setVisible(true);

                return true;
        }

        @Override
        public void initialize(PlugInContext context) throws Exception {
                WorkbenchContext wbContext = context.getWorkbenchContext();
                WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getGeocatalog();
                context.getFeatureInstaller().addPopupMenuItem(frame, this,
                        new String[]{"Projection"},
                        Names.POPUP_GEOCATALOG_TABLE_GROUP, false,
                        OrbisGISIcon.SHOW_ATTRIBUTES, wbContext);

        }

        @Override
        public boolean isEnabled() {
                return getPlugInContext().checkLayerAvailability(
                        new SelectionAvailability[]{SelectionAvailability.EQUAL},
                        1, new SourceAvailability[]{SourceAvailability.RASTER});
        }
}
