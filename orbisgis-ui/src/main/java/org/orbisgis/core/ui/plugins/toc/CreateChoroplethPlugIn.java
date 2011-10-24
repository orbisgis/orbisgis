/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.toc;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.ChoroplethWizardPanel;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

/**
 *
 * @author maxence
 */
public class CreateChoroplethPlugIn extends AbstractPlugIn {

	@Override
	public boolean execute(PlugInContext context) {
		MapContext mapContext = getPlugInContext().getMapContext();
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
				new String[]{Names.POPUP_TOC_FEATURETYPE_STYLE_WIZARDS, Names.POPUP_TOC_FEATURETYPESTYLE_CREATE_CHOROPLETH},
				Names.POPUP_TOC_FEATURETYPE_STYLE_WIZARDS, false,
				OrbisGISIcon.EDIT_LEGEND, wbContext);
	}

	public void execute(MapContext mapContext, ILayer layer) {
		try {
			Type typ = layer.getSpatialDataSource().getMetadata().getFieldType(layer.getSpatialDataSource().getSpatialFieldIndex());
			GeometryConstraint cons = (GeometryConstraint) typ.getConstraint(Constraint.GEOMETRY_TYPE);
			// 1) Create a panel
			ChoroplethWizardPanel pan = new ChoroplethWizardPanel(layer);


			// 2) Show the panel:
			// This method will return true only if the user click on OK
			// and pan.validateInput() return null (i.e. content is valid)
			if (UIFactory.showDialog(pan)) {
				// Fetch the new Rule
				Rule r = pan.getRule();
				if (r != null) {
					// Add the rule in the current featureTypeStyle
					layer.getStyle().clear();
					layer.getStyle().addRule(r);
					// And finally redraw the map
					layer.fireStyleChangedPublic();
				}
			}
		} catch (DriverException ex) {
			Logger.getLogger(CreateChoroplethPlugIn.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public boolean isEnabled() {

		MapContext mapContext = getPlugInContext().getMapContext();
		if (mapContext != null) {
			ILayer[] selectedResources = mapContext.getSelectedLayers();
			try {
				// Only 1 layer is selected, ans it's a layer of polygons
				if (selectedResources.length == 1){
					SpatialDataSourceDecorator sds = selectedResources[0].getSpatialDataSource();
					return sds.getRowCount() > 0 && sds.getGeometry(0).getGeometryType().contains("Polygon");
				}
			} catch (DriverException ex) {
				return false;
			}
		}
		return false;
	}
}
