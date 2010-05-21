package org.orbisgis.core.ui.plugins.editors.mapEditor;

import javax.swing.JButton;

import org.orbisgis.core.Services;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.MapEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;

public class CoordinateReferenceSystemPlugIn extends AbstractPlugIn{

	private JButton CRSButton;
	
	@Override
	public boolean execute(PlugInContext context) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		CRSButton = new JButton(getIcon(IconNames.MAP_TOOLBAR_PROJECTION));
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		
		wbcontext.getWorkbench().getFrame().getMapEditor()
							.getProjectionToolBar().addSeparator();
		wbcontext.getWorkbench().getFrame().getMapEditor()
							.getProjectionToolBar().addPlugIn(this,CRSButton,context);
		
	}

	@Override
	public boolean isEnabled() {
		boolean isVisible = false;
		IEditor editor = Services.getService(EditorManager.class).getActiveEditor();
		if (editor != null && editor instanceof MapEditorPlugIn && getPlugInContext().getMapEditor()!=null) {
			MapContext mc = (MapContext) editor.getElement().getObject();
			isVisible = mc.getLayerModel().getLayerCount() > 0;			
		}
		CRSButton.setEnabled(isVisible);
		return isVisible;
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

}
