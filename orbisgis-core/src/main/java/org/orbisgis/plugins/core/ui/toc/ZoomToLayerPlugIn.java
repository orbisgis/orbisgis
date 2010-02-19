package org.orbisgis.plugins.core.ui.toc;

import java.util.Observable;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.layerModel.ILayer;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.views.MapEditorPlugIn;
import org.orbisgis.plugins.core.ui.views.editor.EditorManager;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;

import com.vividsolutions.jts.geom.Envelope;

public class ZoomToLayerPlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		MapContext mapContext = context.getWorkbenchContext().getWorkbench()
				.getFrame().getToc().getMapContext();
		ILayer[] layers = mapContext.getSelectedLayers();

		Envelope env = new Envelope(layers[0].getEnvelope());
		for (ILayer layer : layers) {
			env.expandToInclude(layer.getEnvelope());
		}
		EditorManager em = (EditorManager) Services
				.getService(EditorManager.class);
		IEditor[] editors = em.getEditors("Map", mapContext);
		for (IEditor editor : editors) {
			((MapEditorPlugIn) editor).getMapTransform().setExtent(env);
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_ZOOM_PATH1 },
				Names.POPUP_TOC_ZOOM_GROUP, false,
				getIcon(Names.POPUP_TOC_ZOOM_ICON), wbContext);
	}

	public void update(Observable o, Object arg) {
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return getUpdateFactory().IsMultipleLayer();
	}

	public boolean acceptsAll(ILayer[] layer) {
		return layer.length > 0;
	}
}
