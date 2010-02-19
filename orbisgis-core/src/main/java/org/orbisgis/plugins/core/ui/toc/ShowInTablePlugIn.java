package org.orbisgis.plugins.core.ui.toc;

import java.util.Observable;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.background.BackgroundManager;
import org.orbisgis.plugins.core.geocognition.Geocognition;
import org.orbisgis.plugins.core.geocognition.GeocognitionElement;
import org.orbisgis.plugins.core.geocognition.GeocognitionFilter;
import org.orbisgis.plugins.core.layerModel.ILayer;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.editorViews.toc.EditableLayer;
import org.orbisgis.plugins.core.ui.views.geocognition.OpenGeocognitionElementJob;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;

public class ShowInTablePlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {

		final MapContext mapContext = context.getWorkbenchContext()
				.getWorkbench().getFrame().getToc().getMapContext();
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

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_TABLE_PATH1 },
				Names.POPUP_TOC_TABLE_GROUP, false,
				getIcon(Names.POPUP_TOC_TABLE_ICON), wbContext);
	}

	public void update(Observable o, Object arg) {
	}

	public void execute(final MapContext mapContext, ILayer layer) {

		GeocognitionElement[] element = Services.getService(Geocognition.class)
				.getElements(new GeocognitionFilter() {

					@Override
					public boolean accept(GeocognitionElement element) {
						return element.getObject() == mapContext;
					}
				});
		Services.getService(BackgroundManager.class).backgroundOperation(
				new OpenGeocognitionElementJob(new EditableLayer(element[0],
						layer)));
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return getUpdateFactory().checkLayerAvailability();
	}

	public boolean accepts(MapContext mc, ILayer layer) {
		return layer.getDataSource() != null;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount == 1;
	}
}
