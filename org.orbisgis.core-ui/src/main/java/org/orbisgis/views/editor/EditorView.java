package org.orbisgis.views.editor;

import java.awt.Component;
import java.util.ArrayList;

import org.orbisgis.Services;
import org.orbisgis.editor.EditorDecorator;
import org.orbisgis.editor.EditorListener;
import org.orbisgis.editor.IEditor;
import org.orbisgis.view.IEditorsView;

public class EditorView implements IEditorsView {

	private EditorPanel editor = new EditorPanel(this);
	private ArrayList<EditorListener> listeners = new ArrayList<EditorListener>();

	public void delete() {
	}

	public Component getComponent() {
		return editor;
	}

	public void initialize() {
		Services.registerService("org.orbisgis.EditorManager",
				EditorManager.class,
				"Gets access to the active editor and its document",
				new DefaultEditorManager(editor));
	}

	public void loadStatus() {

	}

	public void saveStatus() {
		editor.saveAllDocuments();
	}

	public static String getViewId() {
		return "org.orbisgis.views.EditorView";
	}

	public EditorDecorator getActiveEditor() {
		return editor.getCurrentEditor();
	}

	public void addEditorListener(EditorListener listener) {
		listeners.add(listener);
	}

	public void removeEditorListener(EditorListener listener) {
		listeners.remove(listener);
	}

	void fireActiveEditorChanged(IEditor previous, IEditor current) {
		for (EditorListener listener : listeners) {
			listener.activeEditorChanged(previous, current);
		}
	}

	public void fireEditorClosed(IEditor editor) {
		for (EditorListener listener : listeners) {
			listener.activeEditorClosed(editor);
		}
	}

}
