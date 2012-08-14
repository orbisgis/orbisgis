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

import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SelectionAvailability;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SourceAvailability;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;


public class GeocatalogStartEditionPlugIn extends AbstractPlugIn {

        @Override
        public void initialize(PlugInContext context) throws Exception {
                WorkbenchContext wbContext = context.getWorkbenchContext();
                WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getGeocatalog();
                context.getFeatureInstaller().addPopupMenuItem(frame, this,
                        new String[]{Names.POPUP_TOC_ACTIVE_PATH1},
                        Names.POPUP_TOC_ACTIVE_GROUP, false, OrbisGISIcon.PENCIL,
                        wbContext);
        }

        @Override
        public boolean execute(PlugInContext context) throws Exception {
                String[] res = getPlugInContext().getSelectedSources();
                final Catalog geocatalog = context.getWorkbenchContext().getWorkbench().getFrame().getGeocatalog();
                for (int i = 0; i < res.length; i++) {
                        EditableSource editableSource = geocatalog.getEditingSource(res[i]);
                        if (editableSource == null) {
                                editableSource = new EditableSource(res[i]);
                                editableSource.setEditing(true);
                                geocatalog.addEditingSource(res[i], editableSource);
                        } else {
                                editableSource.setEditing(true);
                        }
                }
                // DO NOT REMOVE
                // this call is needed to work around a strange Swing painting problem
                // when using for the first time our custom SourceListRender
                // to display a change in the font of a listed source
                geocatalog.repaint();

                return true;
        }

        @Override
        public boolean isEnabled() {
                if (!getPlugInContext().checkLayerAvailability(
                        new SelectionAvailability[]{SelectionAvailability.SUPERIOR},
                        0, new SourceAvailability[]{SourceAvailability.RASTER, SourceAvailability.WMS})) {
                        return false;
                }
                String[] res = getPlugInContext().getSelectedSources();
                for (int i = 0; i < res.length; i++) {
                        if (getPlugInContext().getWorkbenchContext().getWorkbench().getFrame().getGeocatalog().isEditingSource(res[i])) {
                                return false;
                        }
                }
                return true;
        }
}
