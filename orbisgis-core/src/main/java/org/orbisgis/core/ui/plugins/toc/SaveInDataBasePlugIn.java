package org.orbisgis.core.ui.plugins.toc;

import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.geocatalog.newSourceWizards.db.ConnectionPanel;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SelectionAvailability;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.LayerAvailability;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;

public class SaveInDataBasePlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) {
		MapContext mapContext = getPlugInContext().getMapContext();
		ILayer[] selectedResources = mapContext.getSelectedLayers();
		
		for (ILayer resource : selectedResources) {
			final ConnectionPanel firstPanel = new ConnectionPanel();
			final SchemaSelectionPanel schemaSelectionPanel = new SchemaSelectionPanel(
					firstPanel, resource.getName());

			if (UIFactory.showDialog(new UIPanel[] { firstPanel,
					schemaSelectionPanel })) {
				DataManager dm = Services.getService(DataManager.class);
				String layerName = schemaSelectionPanel.getSourceName();
				String schemaName = schemaSelectionPanel.getSelectedSchema();
				BackgroundManager bm = Services.getService(BackgroundManager.class);
				bm.backgroundOperation(new ExportInDatabaseOperation(dm.getDSF(),
						resource.getName(), layerName, schemaName, firstPanel
								.getDBSource(), firstPanel.getDBDriver()));
			}
		}		
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(
				frame,
				this,
				new String[] { Names.POPUP_TOC_EXPORT_PATH1,
						Names.TOC_EXPORT_SAVEIN_DB},
				Names.POPUP_TOC_EXPORT_GROUP, false, null, wbContext);
	}



	public boolean isEnabled() {
		return getPlugInContext().checkLayerAvailability(
				new SelectionAvailability[] {SelectionAvailability.EQUAL},
				1,
				new LayerAvailability[] {LayerAvailability.VECTORIAL});
	}
}
