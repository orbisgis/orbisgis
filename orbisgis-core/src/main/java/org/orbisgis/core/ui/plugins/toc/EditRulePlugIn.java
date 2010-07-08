/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.ui.plugins.toc;

import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.images.OrbisGISIcon;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.renderer.se.FeatureTypeStyle;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.multiInputPanel.DoubleType;
import org.orbisgis.core.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.core.sif.multiInputPanel.StringType;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;

/**
 *
 * @author maxence
 */
public class EditRulePlugIn extends AbstractPlugIn {

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();

		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_FEATURETYPESTYLE_EDIT_RULE },
				Names.POPUP_TOC_LEGEND_GROUP, false,
				OrbisGISIcon.EDIT_LEGEND, wbContext);

	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {

		MapContext mapContext = getPlugInContext().getMapContext();
		Rule[] rules = mapContext.getSelectedRules();


		if (rules.length == 1){
			Rule r = rules[0];

			MultiInputPanel mip = new MultiInputPanel("Edit Rule");

			StringType wValue = new StringType(60);
			wValue.setValue(r.getWhere());
			mip.addInput("where", "Where:", wValue);

			DoubleType minValue = new DoubleType(8);
			//if (r.getMinScaleDenom() > 0){
				minValue.setValue(Double.toString(r.getMaxScaleDenom()));
			//}
			mip.addInput("minScale", "Min Scale Denominator", minValue);
			mip.addValidationExpression(null, null);

			DoubleType maxValue = new DoubleType(8);
			//if (r.getMaxScaleDenom() > 0){
				maxValue.setValue(Double.toString(r.getMaxScaleDenom()));
			//}
			mip.addInput("maxScale", "Max Scale Denominator", maxValue);

			if (UIFactory.showDialog(mip)){

				r.setWhere(mip.getInput("where"));

				if (!mip.getInput("minScale").isEmpty()){
					r.setMinScaleDenom(Double.parseDouble(mip.getInput("minScale")));
				}

				if (!mip.getInput("maxScale").isEmpty()){
					r.setMaxScaleDenom(Double.parseDouble(mip.getInput("maxScale")));
				}

				((FeatureTypeStyle)(r.getParent())).getLayer().fireStyleChangedPublic();


			}
		}

		return true;
	}

	@Override
	public boolean isEnabled() {
		PlugInContext plugInContext = getPlugInContext();
		if (plugInContext != null){
			MapContext mapContext = plugInContext.getMapContext();
			if (mapContext != null){
				return mapContext.getSelectedRules().length == 1;
			}
		}
		return false;
	}


}
