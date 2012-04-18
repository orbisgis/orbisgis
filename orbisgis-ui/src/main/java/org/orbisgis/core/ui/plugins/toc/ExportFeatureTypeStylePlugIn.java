package org.orbisgis.core.ui.plugins.toc;

import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.LayerAvailability;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SelectionAvailability;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class ExportFeatureTypeStylePlugIn extends AbstractPlugIn {

	@Override
	public boolean execute(PlugInContext context) {
		MapContext mapContext = getPlugInContext().getMapContext();
		Style[] selectedResources = mapContext.getSelectedStyles();

		if (selectedResources.length == 0) {
			execute(mapContext, null);
		} else {
			for (Style resource : selectedResources) {
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
				new String[] { Names.POPUP_TOC_FEATURETYPESTYLE_EXPORT },
				Names.POPUP_TOC_LEGEND_GROUP, false,
				OrbisGISIcon.EDIT_LEGEND, wbContext);
	}

	public void execute(MapContext mapContext, Style style) {

                final SaveFilePanel outputXMLPanel = new SaveFilePanel(
                                "org.orbisgis.core.ui.editorViews.toc.actions.ImportStyle",
                                "Choose a location");

                outputXMLPanel.addFilter("se", "Symbology Encoding FeatureTypeStyle");

                if (UIFactory.showDialog(outputXMLPanel)) {
                        String seFile = outputXMLPanel.getSelectedFile().getAbsolutePath();
                        style.export(seFile);
                }
	}

	@Override
	public boolean isEnabled() {
		return getPlugInContext().checkLayerAvailability(
				new SelectionAvailability[] {SelectionAvailability.EQUAL},
				1,
				new LayerAvailability[] {LayerAvailability.VECTORIAL});
	}
}
