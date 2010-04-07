package org.orbisgis.core.ui.plugins.toc;

import java.util.Observable;

import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.MapEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;

public class ZoomToLayerPlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		
		final MapContext mapContext = context.getWorkbenchContext().getWorkbench()
												.getFrame().getToc().getMapContext();
		
		BackgroundManager bm = Services.getService(BackgroundManager.class);
		bm.backgroundOperation(new BackgroundJob() {

			@Override
			public void run(IProgressMonitor pm) {
		
				
				ILayer[] layers = mapContext.getSelectedLayers();
		
				Envelope env = new Envelope(layers[0].getEnvelope());
				for (ILayer layer : layers) {
					env.expandToInclude(layer.getEnvelope());
				}
				EditorManager em = (EditorManager) Services
						.getService(EditorManager.class);
				IEditor[] editors = em.getEditors(Names.EDITOR_MAP_ID, mapContext);
				for (IEditor editor : editors) {
					((MapEditorPlugIn) editor).getMapTransform().setExtent(env);
				}
			}

			@Override
			public String getTaskName() {				
				return "Zoom to layer";
			}
		});
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_ZOOM_PATH1 },
				Names.POPUP_TOC_ZOOM_GROUP, false,
				getIcon(IconNames.POPUP_TOC_ZOOM_ICON), wbContext);
	}

	public boolean isVisible() {
		return getPlugInContext().IsMultipleLayer();
	}

	public boolean acceptsAll(ILayer[] layer) {
		return layer.length > 0;
	}
	
	public String getName() {
		return "ZoomToLayer";
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
