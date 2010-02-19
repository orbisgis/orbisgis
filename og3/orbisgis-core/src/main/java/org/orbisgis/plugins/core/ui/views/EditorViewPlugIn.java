package org.orbisgis.plugins.core.ui.views;

import java.awt.Component;
import java.util.Observable;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;
import org.orbisgis.plugins.core.ui.views.editor.DefaultEditorManager;
import org.orbisgis.plugins.core.ui.views.editor.EditorManager;
import org.orbisgis.plugins.core.ui.views.editor.EditorPanel;
import org.orbisgis.plugins.core.ui.workbench.Names;

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
				getIcon(Names.EDITORS_ICON), null, new JPanel(),
				null, null, context.getWorkbenchContext());
	}

	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().loadView(getId());
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
		return getUpdateFactory().viewIsOpen(getId());
	}
}
