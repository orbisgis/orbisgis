package org.orbisgis.core.ui.plugins.toc;

import java.util.Observable;

import org.gdms.driver.DriverException;
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
import org.orbisgis.core.ui.pluginSystem.PlugInContext.LayerSelectionTest;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.LayerTest;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;

public class SaveInDataBasePlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) {
		MapContext mapContext = context.getWorkbenchContext().getWorkbench()
				.getFrame().getToc().getMapContext();
		ILayer[] selectedResources = mapContext.getSelectedLayers();

		if (selectedResources.length == 0) {
			execute(mapContext, null);
		} else {
			for (ILayer resource : selectedResources) {
				execute(mapContext, resource);
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

	public void execute(MapContext mapContext, ILayer layer) {
		final ConnectionPanel firstPanel = new ConnectionPanel();
		final SchemaSelectionPanel schemaSelectionPanel = new SchemaSelectionPanel(
				firstPanel, layer.getName());

		if (UIFactory.showDialog(new UIPanel[] { firstPanel,
				schemaSelectionPanel })) {
			DataManager dm = Services.getService(DataManager.class);
			String layerName = schemaSelectionPanel.getSourceName();
			String schemaName = schemaSelectionPanel.getSelectedSchema();
			BackgroundManager bm = Services.getService(BackgroundManager.class);
			bm.backgroundOperation(new ExportInDatabaseOperation(dm.getDSF(),
					layer.getName(), layerName, schemaName, firstPanel
							.getDBSource(), firstPanel.getDBDriver()));
		}
	}

	public boolean isVisible() {
		return getPlugInContext().checkLayerAvailability(
				new LayerSelectionTest[] {LayerSelectionTest.EQUAL},
				1,
				new LayerTest[] {LayerTest.VECTORIAL}, 
				false);
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

}
