package org.orbisgis.core.ui.plugins.views.geocatalog;

import java.util.Observable;

import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.geocatalog.newSourceWizards.db.ConnectionPanel;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.toc.ExportInDatabaseOperation;
import org.orbisgis.core.ui.plugins.toc.SchemaSelectionPanel;

public class GeocatalogSaveInDataBasePlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().executeGeocatalog();
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame()
				.getGeocatalog();
		context.getFeatureInstaller().addPopupMenuItem(
				frame,
				this,
				new String[] { Names.POPUP_GEOCATALOG_EXPORT,
						Names.POPUP_GEOCATALOG_EXPORT_INDB },
				Names.POPUP_GEOCATALOG_EXPORT_INDB, false, null, wbContext);

	}

	public void execute(SourceManager sourceManager, String currentNode) {
		final ConnectionPanel firstPanel = new ConnectionPanel();
		final SchemaSelectionPanel schemaSelectionPanel = new SchemaSelectionPanel(
				firstPanel, currentNode);

		if (UIFactory.showDialog(new UIPanel[] { firstPanel,
				schemaSelectionPanel })) {
			DataManager dm = Services.getService(DataManager.class);
			String layerName = schemaSelectionPanel.getSourceName();
			String schemaName = schemaSelectionPanel.getSelectedSchema();
			BackgroundManager bm = Services.getService(BackgroundManager.class);
			bm.backgroundOperation(new ExportInDatabaseOperation(dm.getDSF(),
					currentNode, layerName, schemaName, firstPanel
							.getDBSource(), firstPanel.getDBDriver()));

		}
	}

	public boolean isVisible() {
		return getPlugInContext().geocatalogIsVisible();
	}

	public boolean accepts(SourceManager sourceManager, String selectedNode) {
		Source source = sourceManager.getSource(selectedNode);
		return (source.getType() & SourceManager.WMS) == 0;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount == 1;
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
