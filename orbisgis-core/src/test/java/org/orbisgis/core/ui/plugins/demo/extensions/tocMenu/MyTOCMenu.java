package org.orbisgis.core.ui.plugins.demo.extensions.tocMenu;

import javax.swing.JOptionPane;

import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;

public class MyTOCMenu extends AbstractPlugIn {

	@Override
	public boolean execute(PlugInContext context) throws Exception {

		JOptionPane.showMessageDialog(null, "Hello wolrd !");
		return false;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { "Hello the world" }, null, false, null,
				wbContext);
	}

	@Override
	public boolean isEnabled() {
		MapContext mc = getPlugInContext().getMapContext();
		if(mc!=null){
			if (mc.getLayerModel().getLayerCount() > 2) {
				if (mc.getLayerModel().getLayerByName("toto") != null) {
					return true;
				}
			}
		}
		return false;
	}
}
