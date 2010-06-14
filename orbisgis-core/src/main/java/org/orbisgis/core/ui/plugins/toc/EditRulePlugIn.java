/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.ui.plugins.toc;

import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.renderer.se.Rule;
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
				getIcon(IconNames.POPUP_TOC_LEGEND_ICON), wbContext);

	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {

		MapContext mapContext = getPlugInContext().getMapContext();
		Rule[] rules = mapContext.getSelectedRules();

		System.out.println ("There is " + rules.length + " to edit : " );
		for (Rule r : rules){
			System.out.println ("  -> Rules " + r.getName());
		}

		throw new UnsupportedOperationException("Rule edition ! Not supported yet.");
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
