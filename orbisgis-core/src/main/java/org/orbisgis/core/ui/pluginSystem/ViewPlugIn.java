package org.orbisgis.core.ui.pluginSystem;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.images.IconLoader;
import org.orbisgis.core.language.I18N;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.map.tool.Automaton;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.ViewDecorator;

public abstract class ViewPlugIn implements PlugIn {

	private String id;
	private String name;
	private Component component;	
	private PlugInContext plugInContext;
	
	private I18N i18n = I18N.getInstance();	
	private String langAndCountry;

	
	public void i18nConfigure(String langAndCountry) {		
		this.langAndCountry = langAndCountry;
	}
	public I18N getI18n(){	
		return  i18n.configure(langAndCountry, this.getClass());				
	}

	public void createPlugInContext(JComponent c, String id,
			ImageIcon icon, String[] editors, WorkbenchContext context) {
		this.component = c;
		this.id = id;
		context.getWorkbench().getFrame().getViews().add(
				new ViewDecorator(this, id, icon,
						(editors == null) ? new String[0] : editors));
		
		plugInContext = context.createPlugInContext(this);	
	}
	
	protected PlugInContext getPlugInContext() {
		return plugInContext;
	}

	public void setPlugInContext(PlugInContext context) {
		this.plugInContext = context;
	}

	public boolean isSelected(){
		return false;
	}

	// View PlugIn Icon
	public static ImageIcon getIcon(String nameIcone) {
		return IconLoader.getIcon(nameIcone);
	}

	// Get View PlugIn Id
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name == null ? createName(getClass()) : name;
	}
	
	public static String createName(Class plugInClass) {
		return plugInClass.getName();
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
