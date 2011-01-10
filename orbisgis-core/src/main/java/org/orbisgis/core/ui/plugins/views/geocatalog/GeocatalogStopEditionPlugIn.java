package org.orbisgis.core.ui.plugins.views.geocatalog;

import javax.swing.JOptionPane;
import org.gdms.data.NonEditableDataSourceException;
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
public class GeocatalogStopEditionPlugIn extends AbstractPlugIn {

        @Override
        public void initialize(PlugInContext context) throws Exception {
                WorkbenchContext wbContext = context.getWorkbenchContext();
                WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getGeocatalog();
                context.getFeatureInstaller().addPopupMenuItem(frame, this,
                        new String[]{Names.POPUP_TOC_INACTIVE_PATH1},
                        Names.POPUP_TOC_INACTIVE_GROUP, false, OrbisGISIcon.LAYER_STOPEDITION,
                        wbContext);
        }

        @Override
        public boolean execute(PlugInContext context) throws Exception {
                String[] res = getPlugInContext().getSelectedSources();
                Catalog catalog = context.getWorkbenchContext().getWorkbench().getFrame().getGeocatalog();
                for (int i = 0; i < res.length; i++) {
                        final String name = res[i];
                        EditableSource s = catalog.getEditingSource(name);
                        if (s.getDataSource() != null) {
                                int option = JOptionPane.showConfirmDialog(null,
                                        "Do you want to save your changes", "Stop edition",
                                        JOptionPane.YES_NO_CANCEL_OPTION);
                                if (option == JOptionPane.YES_OPTION) {
                                        try {
                                                s.getDataSource().commit();
                                        } catch (DriverException e) {
                                                Services.getErrorManager().error("Cannot save source", e);
                                        } catch (NonEditableDataSourceException e) {
                                                Services.getErrorManager().error("This source cannot be saved",
                                                        e);
                                        }
                                        catalog.getEditingSource(name).setEditing(false);
                                } else if (option == JOptionPane.NO_OPTION) {
                                        try {
                                                s.getDataSource().syncWithSource();
                                        } catch (DriverException e) {
                                                Services.getErrorManager().error("Cannot revert source", e);
                                        }
                                        catalog.getEditingSource(name).setEditing(false);
                                }
                        } else {
                                catalog.getEditingSource(name).setEditing(false);
                        }
                }
                // DO NOT REMOVE
                // this call is needed to work around a strange Swing painting problem
                // when using for the first time our custom SourceListRender
                // to display a change in the font of a listed source
                catalog.repaint();

                return true;
        }

        @Override
        public boolean isEnabled() {
                String[] res = getPlugInContext().getSelectedSources();
                if (res.length != 1) {
                        return false;
                }
                return getPlugInContext().getWorkbenchContext().getWorkbench().getFrame().getGeocatalog().isEditingSource(res[0]);
        }
}
