package org.orbisgis.plugins.core.ui;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.orbisgis.plugins.core.PersistenceException;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.editors.map.tool.Automaton;
import org.orbisgis.plugins.core.ui.views.ViewDecorator;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.images.IconLoader;

public abstract class ViewPlugIn implements PlugIn {

	private String id;
	private Component component;

	private UpdateViewPlugInFactory updatePlugInFactory;

	public UpdateViewPlugInFactory getUpdateFactory() {
		return updatePlugInFactory;
	}
	
	public void i18nConfigure(String path, String langcountry) {}

	public void createUpdatePlugInFactory(JComponent c, String id,
			ImageIcon icon, String[] editors, WorkbenchContext context) {
		this.component = c;
		this.id = id;
		context.getWorkbench().getFrame().getViews().add(
				new ViewDecorator(this, id, icon,
						(editors == null) ? new String[0] : editors));
		updatePlugInFactory = new UpdateViewPlugInFactory(context, this);
	}

	// View PlugIn Icon
	public static ImageIcon getIcon(String nameIcone) {
		return IconLoader.getIcon(nameIcone);
	}

	// Get View PlugIn Id
	public String getId() {
		return id;
	}

	// get panel in the View PlugIn for load his popup
	public JPanel getPanel() {
		return null;
	}

	// PlugIn implementation
	// Editor in View (used by DW to place MapEditor & TableEditor Views in
	// EditorPanel)
	public void editorViewDisabled() {
	}

	public boolean setEditor(IEditor editor) {
		return false;
	}

	// ViewPlugIn persistence
	public Component getComponent() {
		return component;
	}

	public void delete() {
	}

	public void loadStatus() throws PersistenceException {
	}

	public void saveStatus() throws PersistenceException {
	}

	public void initialize(WorkbenchContext wbContext) {
	}

	public void initialize(PlugInContext wbContext, Automaton automaton) {
	}
}
