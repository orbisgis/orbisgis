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
package org.orbisgis.core.ui;

import junit.framework.TestCase;

import org.gdms.data.DataSourceFactory;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.DefaultDataManager;
import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.ui.editor.EditorListener;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.errorManager.ErrorListener;
import org.orbisgis.errorManager.ErrorManager;

public class AbstractTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory(
				"src/test/resources/sources", "src/test/resources/temp");

		Services.registerService(DataManager.class, "", new DefaultDataManager(
				dsf));
		Services
				.registerService(ErrorManager.class, "", new FailErrorManager());
		Services.registerService(EditorManager.class, "", new EditorManager() {

			@Override
			public void open(EditableElement element)
					throws UnsupportedOperationException {
			}

			@Override
			public boolean hasEditor(EditableElement element) {
				return false;
			}

			@Override
			public IEditor[] getEditors() {
				return new IEditor[0];
			}

			@Override
			public IEditor[] getEditor(EditableElement element) {
				return new IEditor[0];
			}

			@Override
			public EditableElement getActiveElement() {
				return null;
			}

			@Override
			public IEditor getActiveEditor() {
				return null;
			}

			@Override
			public boolean closeEditor(IEditor editor)
					throws IllegalArgumentException {
				return true;
			}

			@Override
			public void addEditorListener(EditorListener listener) {
			}

			@Override
			public String getEditorId(IEditor editor) {
				return null;
			}

			@Override
			public void removeEditorListener(EditorListener listener) {
			}

			@Override
			public IEditor[] getEditors(String editorId, Object object) {
				return getEditors();
			}

			@Override
			public IEditor[] getEditors(String editorId) {
				return getEditors();
			}
		});
		super.setUp();
	}

	protected DataManager getDataManager() {
		return (DataManager) Services.getService(DataManager.class);
	}

	private class FailErrorManager implements ErrorManager {

		public void addErrorListener(ErrorListener listener) {
		}

		public void error(String userMsg) {
			throw new RuntimeException(userMsg);
		}

		public void error(String userMsg, Throwable exception) {
			throw new RuntimeException(userMsg, exception);
		}

		public void removeErrorListener(ErrorListener listener) {
		}

		public void warning(String userMsg, Throwable exception) {
			throw new RuntimeException(userMsg, exception);
		}

		public void warning(String userMsg) {
			throw new RuntimeException(userMsg);
		}

	}
}
