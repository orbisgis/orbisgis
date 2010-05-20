package org.orbisgis.core.ui.plugins.editors.mapEditor;

import javax.swing.JButton;

import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;

public class CoordinateReferenceSystemPlugIn extends AbstractPlugIn{

	private JButton CRSButton;
	
	@Override
	public boolean execute(PlugInContext context) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		CRSButton = new JButton(getIcon(IconNames.MAP_TOOLBAR_PROJECTION));
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		
		wbcontext.getWorkbench().getFrame().getMapEditor()
							.getProjectionToolBar().addSeparator();
		wbcontext.getWorkbench().getFrame().getMapEditor()
							.getProjectionToolBar().addPlugIn(this,CRSButton,context);
		
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

}
