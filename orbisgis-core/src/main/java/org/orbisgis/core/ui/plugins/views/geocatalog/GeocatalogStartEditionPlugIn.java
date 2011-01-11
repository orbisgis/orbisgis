package org.orbisgis.core.ui.plugins.views.geocatalog;

import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SelectionAvailability;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SourceAvailability;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

/**
 *
 * @author Antoine Gourlay
 */
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
