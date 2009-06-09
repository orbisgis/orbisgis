/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui.views.editor;

import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.ui.editor.EditorListener;
import org.orbisgis.core.ui.editor.IEditor;

public interface EditorManager {

	/**
	 * Gets the active document
	 * 
	 * @return
	 */
	EditableElement getActiveElement();

	/**
	 * Gets the active editor
	 * 
	 * @return
	 */
	IEditor getActiveEditor();

	/**
	 * Gets all the opened editors
	 * 
	 * @return
	 */
	IEditor[] getEditors();

	/**
	 * Asks the specified editor to be closed. Note that it may not be closed
	 * because, for example, it can have unsaved changes.
	 * 
	 * @param editor
	 * @return True if the editor was closed, false otherwise
	 * @throws IllegalArgumentException
	 *             If the specified editor is not open
	 */
	boolean closeEditor(IEditor editor) throws IllegalArgumentException;

	/**
	 * Return true if there is an editor able to edit the specified
	 * GeocognitionElement
	 * 
	 * @param element
	 * @return
	 */
	boolean hasEditor(EditableElement element);

	/**
	 * Opens the specified element in the default editor. If the element is been
	 * edited the editor gains focus and no new editor is open. The comparison
	 * between elements is done using the {@link Object#equals(Object)} method.
	 * 
	 * @param element
	 * @throws UnsupportedOperationException
	 *             If there is no valid editor for the specified element
	 */
	void open(EditableElement element) throws UnsupportedOperationException;

	/**
	 * Gets the editors that edit the specified element
	 * 
	 * @param element
	 * @return List with editor. Can be empty. Never null
	 */
	IEditor[] getEditor(EditableElement element);

	/**
	 * Adds an editor listener
	 * 
	 * @param listener
	 */
	void addEditorListener(EditorListener listener);

	/**
	 * Removes an editor listener
	 * 
	 * @param listener
	 */
	void removeEditorListener(EditorListener listener);

	/**
	 * Returns the id of the specified editor
	 * 
	 * @param editor
	 * @return Null if the editor is not open
	 */
	String getEditorId(IEditor editor);

	/**
	 * Get the open editors with the specified id and which object stored in the
	 * element in edition is the same as 'object'
	 * 
	 * @param editorId
	 * @param object
	 * 
	 * @return
	 */
	IEditor[] getEditors(String editorId, Object object);

	/**
	 * Get the open editors with the specified id
	 * 
	 * @param editorId
	 * @return
	 */
	IEditor[] getEditors(String editorId);

}
