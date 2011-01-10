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
public class GeocatalogSaveChangesPlugIn extends AbstractPlugIn {

        @Override
        public void initialize(PlugInContext context) throws Exception {
                WorkbenchContext wbContext = context.getWorkbenchContext();
                WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getGeocatalog();
                context.getFeatureInstaller().addPopupMenuItem(frame, this,
                        new String[]{Names.POPUP_TOC_SAVE_PATH1},
                        Names.POPUP_TOC_INACTIVE_GROUP, false,
                        OrbisGISIcon.SAVE, wbContext);
        }

        @Override
        public boolean execute(PlugInContext context) throws Exception {
                String[] res = getPlugInContext().getSelectedSources();
                final Catalog geocatalog = context.getWorkbenchContext().getWorkbench().getFrame().getGeocatalog();
                for (int i = 0; i < res.length; i++) {
                        final String name = res[i];
                        final EditableSource s = geocatalog.getEditingSource(name);
                        if (s.isModified()) {
                                try {
                                        s.getDataSource().commit();
                                } catch (DriverException e) {
                                        Services.getErrorManager().error("Cannot save source", e);
                                } catch (NonEditableDataSourceException e) {
                                        Services.getErrorManager().error("It is not possible to save " + "this source.", e);
                                }
                        }
                        s.setEditing(false);
                        JOptionPane.showMessageDialog(null, "The source has been saved");

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
                String[] res = getPlugInContext().getSelectedSources();
                if (res.length != 1) {
                        return false;
                }
                EditableSource s = getPlugInContext().getWorkbenchContext().getWorkbench().getFrame().getGeocatalog().getEditingSource(res[0]);
                return s != null && s.getDataSource() != null && s.isModified();
        }
}
