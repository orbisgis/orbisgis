package org.orbisgis.core.ui.plugins.editors.mapEditor;

import javax.swing.JButton;

import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.projection.ProjectionConfigPanel;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.plugins.editor.PlugInEditorListener;
import org.orbisgis.core.ui.plugins.views.MapEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;

public class CoordinateReferenceSystemPlugIn extends AbstractPlugIn{

	private JButton CRSButton;	
	
	public boolean execute(PlugInContext context) throws Exception {
		/*final ProjectionConfigPanel projectionPanel = new ProjectionConfigPanel(
				context.getWorkbenchContext().getWorkbench().getFrame(), true);*/
			
		
		return true;
	}
	
	public void initialize(PlugInContext context) throws Exception {
		CRSButton = new JButton(getIcon(IconNames.MAP_TOOLBAR_PROJECTION));		
		EditorManager em = Services.getService(EditorManager.class);
		em.addEditorListener(new PlugInEditorListener(this,CRSButton,Names.MAP_TOOLBAR_PROJECTION,
								null,context,false));	
		
	}
	
	public boolean isEnabled() {
		boolean isVisible = false;
		IEditor editor = Services.getService(EditorManager.class).getActiveEditor();
		if (editor != null && editor instanceof MapEditorPlugIn && getPlugInContext().getMapEditor()!=null) {
			MapContext mc = (MapContext) editor.getElement().getObject();
			isVisible = mc.getLayerModel().getLayerCount() > 0;	
			if(isVisible) {
				try {
					CRSButton.setText( mc.getLayerModel().getLayer(0).getDataSource().getCRS().getName());
				} catch (IllegalStateException e) {
					Services.getErrorManager().error("CRS not found");
				} catch (DriverException e) {
					Services.getErrorManager().error("CRS not found");
				}
			}
		}
		CRSButton.setEnabled(isVisible);
		return isVisible;
	}
}
