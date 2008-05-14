package org.orbisgis.editorView;

import org.orbisgis.editor.IEditor;
import org.orbisgis.view.IView;

/**
 * Interface to implement for those IView instances that are associated with a
 * concrete editor
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface IEditorView extends IView {

	/**
	 * This method is invoked each time the active editor changes to an editor
	 * which id is equals to the editor id specified in the plugin.xml for this
	 * view
	 *
	 * @param editor
	 */
	void setEditor(IEditor editor);

}
