package org.orbisgis.core.ui.plugins.views;

import java.awt.Component;
import java.util.Observable;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.orbisgis.core.Services;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.plugins.views.editor.DefaultEditorManager;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.editor.EditorPanel;

public class EditorViewPlugIn extends ViewPlugIn {

	private EditorPanel panel;
	private JMenuItem menuItem;

	public EditorViewPlugIn() {

	}

	public Component getComponent() {
		return panel;
	}

	public void initialize(PlugInContext context) throws Exception {
		panel = new EditorPanel();
		Services.registerService(EditorManager.class,
				"Gets access to the active editor and its document",
				new DefaultEditorManager(panel));
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.EDITORS, true,
				getIcon(IconNames.EDITORS_ICON), null, new JPanel(), context);
	}

	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().loadView(getId());
		return true;
	}

	public void update(Observable o, Object arg) {
		setSelected();
	}

	public void saveStatus() {
		panel.saveAllDocuments();
	}

	public void setSelected() {
		menuItem.setSelected(isVisible());
	}

	public boolean isVisible() {
		return getPlugInContext().viewIsOpen(getId());
	}
}
