/**
 * OrbisGIS is a GIS application dedicated to scientific spatial analysis.
 * This cross-platform GIS is developed at the Lab-STICC laboratory by the DECIDE 
 * team located in University of South Brittany, Vannes.
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 * Copyright (C) 2015-2016 CNRS (UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
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
     * @param editor Editor instance
     */
    void addEditor(Editor editor);

    /**
     * Remove single instance editor
     * @param editor Editor instance
     */
    void removeEditor(Editor editor);

    /**
     * Return all editor's editable
     * @return Collection of EditableElement returned by editor.getEditableElement()
     */
    Collection<EditableElement> getEditableElements();

    /**
     *
     * @return All shown editors
     */
    Collection<Editor> getEditors();

    /**
    * Open this editable with all compatible factories.
    * @param editableElement
    */
    void openEditable(EditableElement editableElement);
}
