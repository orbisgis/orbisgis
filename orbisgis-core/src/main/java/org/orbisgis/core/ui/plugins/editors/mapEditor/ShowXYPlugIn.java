package org.orbisgis.core.ui.plugins.editors.mapEditor;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.MapEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;

public class ShowXYPlugIn extends AbstractPlugIn {	
	
	public boolean execute(PlugInContext context) throws Exception {
		EditorManager em = Services.getService(EditorManager.class);
		MapEditorPlugIn mapEditor = (MapEditorPlugIn) em.getActiveEditor();
		mapEditor.setShowInfo(!mapEditor.getShowInfo());
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();		
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame()
				.getMapEditor();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_MAP_SHOW_XY },
				Names.POPUP_MAP_EXPORT_PDF_GROUP, true, null, wbContext);
	}	

	public boolean isEnabled() {
		return getPlugInContext().getMapEditor() == null ? false : true;
	}
	
	public boolean isSelected() {		
		if (getPlugInContext().getMapEditor() != null) {			
			return getPlugInContext().getMapEditor().getShowInfo();
		}
		return false;
	}
}
