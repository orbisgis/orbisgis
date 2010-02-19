package org.orbisgis.plugins.core.ui.views;

import java.util.Observable;

import javax.swing.JMenuItem;

import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.workbench.Names;

public class GeomarkViewPlugIn extends ViewPlugIn {

	private GeomarkPanel panel;
	private String editors[];
	private JMenuItem menuItem;

	public void initialize(PlugInContext context) throws Exception {
		panel = new GeomarkPanel();
		editors = new String[1];
		editors[0] = "Map";
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.GEOMARK, true,
				getIcon(Names.GEOMARK_ICON), editors, panel, null, null,
				context.getWorkbenchContext());
		context.getFeatureInstaller().addRegisterCustomQuery(Geomark.class);
	}

	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().loadView(getId());
		return true;
	}

	public boolean setEditor(IEditor editor) {
		panel.setEditor(editor);
		return true;
	}

	public void update(Observable o, Object arg) {
		setSelected();
	}

	public void setSelected() {
		menuItem.setSelected(isVisible());
	}

	public boolean isVisible() {
		return getUpdateFactory().viewIsOpen(getId());
	}

}
