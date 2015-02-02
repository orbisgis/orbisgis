package org.orbisgis.sif.edition;

import java.util.Collection;

/**
 * @author Nicolas Fortin
 */
public interface EditorManager {
    /**
    * Add a new editor factory, if the factory is a SingleEditorFactory then
    * the panels are immediately shown
    * @param editorFactory
    */
    void addEditorFactory(EditorFactory editorFactory);

    /**
     * Remove an editor factory.
     * @param editorFactory
     */
    void removeEditorFactory(EditorFactory editorFactory);

    /**
     * Add single instance editor.Only one frame.The user cannot close it. just hidden by user.
     * @param editor EditorDockable instance
     */
    void addEditor(EditorDockable editor);

    /**
     * Remove single instance edito
     * @param editor EditorDockable instance
     */
    void removeEditor(EditorDockable editor);

    /**
     * Return all editor's editable
     * @return Collection of EditableElement returned by editor.getEditableElement()
     */
    Collection<EditableElement> getEditableElements();

    /**
     *
     * @return All shown editors
     */
    Collection<EditorDockable> getEditors();

    /**
    * Open this editable with all compatible factories.
    * @param editableElement
    */
    void openEditable(EditableElement editableElement);
}
