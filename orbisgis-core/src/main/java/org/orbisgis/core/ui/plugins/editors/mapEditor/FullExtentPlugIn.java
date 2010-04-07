package org.orbisgis.core.ui.plugins.editors.mapEditor;

import java.util.Observable;

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

public class FullExtentPlugIn extends AbstractPlugIn {

	private JButton btn;

	public FullExtentPlugIn() {
		btn = new JButton(getIcon(IconNames.MAP_FULL_EXTENT_ICON));
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getNavigationToolBar().addPlugIn(
				this, btn, context);
	}

	public boolean execute(PlugInContext context) throws Exception {
		IEditor editor = Services.getService(EditorManager.class)
				.getActiveEditor();
		MapContext mc = (MapContext) editor.getElement().getObject();
		((MapEditorPlugIn) editor).getMapTransform().setExtent(
				mc.getLayerModel().getEnvelope());
		return true;
	}

	public boolean isEnabled(IEditor editor) {
		MapContext mc = (MapContext) editor.getElement().getObject();
		return mc.getLayerModel().getLayerCount() > 0;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		IEditor editor = Services.getService(EditorManager.class)
				.getActiveEditor();
		if (editor != null && editor instanceof MapEditorPlugIn)
			btn.setEnabled(isEnabled(editor));
		else
			btn.setEnabled(false);
	}

	@Override
	public boolean isVisible() {
		//Never called. It's update method that set plugin context.
		return false;
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}
}
