/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.plugins.views.editor;

import org.apache.log4j.Logger;
import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.edition.EditableElementException;
import org.orbisgis.core.ui.editor.EditorDecorator;
import org.orbisgis.core.ui.editor.EditorListener;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.I18N;

public class DefaultEditorManager implements EditorManager {

	private static final Logger logger = Logger
			.getLogger(DefaultEditorManager.class);

	private EditorPanel editorPanel;

	public EditorPanel getEditorPanel() {
		return editorPanel;
	}

	public DefaultEditorManager(EditorPanel editor) {
		this.editorPanel = editor;
	}

	public EditableElement getActiveElement() {
		return editorPanel.getCurrentDocument();
	}

	public IEditor getActiveEditor() {
		if (editorPanel.getCurrentEditor() == null) {
			return null;
		} else {
			return editorPanel.getCurrentEditor().getEditor();
		}
	}

	public boolean closeEditor(IEditor editor) {
		return this.editorPanel.closeEditor(editor);
	}

	public IEditor[] getEditors() {
		return editorPanel.getEditors();
	}

	@Override
	public boolean hasEditor(EditableElement element) {
		return EditorPanel.hasEditor(element);
	}

	@Override
	public void open(EditableElement element, ProgressMonitor pm)
			throws UnsupportedOperationException {
		EditorDecorator editor = EditorPanel.getFirstEditor(element);

		if (editor == null) {
			throw new UnsupportedOperationException(
					I18N.getString("orbisgis.org.orbisgis.ui.defaultEditorManager.noSuitableEditorForElement")); //$NON-NLS-1$
		} else {
			if (!this.editorPanel.isBeingEdited(element, editor.getEditor()
					.getClass())) {
				try {
					element.open(pm);
					editor.setElement(element);
				} catch (EditableElementException e) {
                                        throw new UnsupportedOperationException(I18N.getString("orbisgis.org.orbisgis.ui.defaultEditorManager.errorOpeningElement"),e); //$NON-NLS-1$
				}
				this.editorPanel.addEditor(editor);
			} else {
				this.editorPanel.showEditor(element, editor.getEditor()
						.getClass());
			}
		}
	}

	@Override
	public IEditor[] getEditor(EditableElement element) {
		return this.editorPanel.getEditor(element);
	}

	@Override
	public String getEditorId(IEditor editor) {
		return editorPanel.getEditorId(editor);
	}

	@Override
	public void addEditorListener(EditorListener listener) {
		editorPanel.addEditorListener(listener);
	}

	@Override
	public void removeEditorListener(EditorListener listener) {
		editorPanel.removeEditorListener(listener);
	}

	@Override
	public IEditor[] getEditors(String editorId, Object object) {
		return editorPanel.getEditors(editorId, object);
	}

	@Override
	public IEditor[] getEditors(String editorId) {
		return editorPanel.getEditors(editorId);
	}

	
}
