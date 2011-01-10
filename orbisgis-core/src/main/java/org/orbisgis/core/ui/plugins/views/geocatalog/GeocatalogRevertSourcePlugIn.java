package org.orbisgis.core.ui.plugins.views.geocatalog;

import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

/**
 *
 * @author Antoine Gourlay
 */
public class GeocatalogRevertSourcePlugIn extends AbstractPlugIn {

        @Override
        public void initialize(PlugInContext context) throws Exception {
                WorkbenchContext wbContext = context.getWorkbenchContext();
                WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getGeocatalog();
                context.getFeatureInstaller().addPopupMenuItem(frame, this,
                        new String[]{Names.POPUP_TOC_REVERT_PATH1},
                        Names.POPUP_TOC_INACTIVE_GROUP, false,
                        OrbisGISIcon.REVERT, wbContext);
        }

        @Override
        public boolean execute(PlugInContext context) throws Exception {
                String[] res = getPlugInContext().getSelectedSources();
                Catalog catalog = context.getWorkbenchContext().getWorkbench().getFrame().getGeocatalog();
                for (int i = 0; i < res.length; i++) {
                        final String name = res[i];
                        EditableSource s = catalog.getEditingSource(name);
                        try {
                                s.getDataSource().syncWithSource();
                        } catch (DriverException e) {
                                Services.getErrorManager().error("Cannot revert source", e);
                        }
                }

                return true;
        }

        @Override
        public boolean isEnabled() {
                String[] res = getPlugInContext().getSelectedSources();
                if (res.length != 1) {
                        return false;
                }
                EditableSource s = getPlugInContext().getWorkbenchContext().getWorkbench().getFrame().getGeocatalog().getEditingSource(res[0]);
                return s != null && s.getDataSource() != null && s.isModified();
        }
}
