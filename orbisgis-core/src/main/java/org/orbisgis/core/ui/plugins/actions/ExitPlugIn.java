package org.orbisgis.core.ui.plugins.actions;

import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.orbisgis.core.Services;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.workspace.OrbisGISWorkspace;

public class ExitPlugIn extends AbstractPlugIn {
	
	private JButton btn;
	private JMenuItem menuItem;

	public ExitPlugIn() {
		btn = new JButton(getIcon(IconNames.EXIT_ICON));
		btn.setToolTipText(Names.EXIT);
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getMainToolBar().addPlugIn(this,
				btn, context);
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.FILE }, Names.EXIT, false,
				getIcon(IconNames.EXIT_ICON), null, null, context);
	}

	public boolean execute(PlugInContext context) throws Exception {
		execute();
		return true;
	}

	public static void execute() {
		int answer = JOptionPane.showConfirmDialog(null, "Really quit?",
				"OrbisGIS", JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.YES_OPTION) {
			OrbisGISWorkspace psm = (OrbisGISWorkspace) Services
					.getService(OrbisGISWorkspace.class);
			psm.stopPlugins();
		}
	}

	public void update(Observable o, Object arg) {
		btn.setEnabled(true);
		btn.setVisible(isVisible());

		menuItem.setEnabled(true);
		menuItem.setVisible(isVisible());
	}

	public boolean isVisible() {
		return true;
	}
	
	public boolean isSelected() {
		return false;
	}


}
