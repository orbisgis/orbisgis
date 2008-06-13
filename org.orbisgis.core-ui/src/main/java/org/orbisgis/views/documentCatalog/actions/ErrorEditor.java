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
package org.orbisgis.views.documentCatalog.actions;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JLabel;

import org.orbisgis.editor.IEditor;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.views.documentCatalog.AbstractDocument;
import org.orbisgis.views.documentCatalog.DocumentException;
import org.orbisgis.views.documentCatalog.IDocument;

public class ErrorEditor implements IEditor {

	private String message;
	private String documentName;

	public ErrorEditor(String documentName, String message) {
		this.message = message;
		this.documentName = documentName;
	}

	public boolean acceptDocument(IDocument doc) {
		return false;
	}

	public IDocument getDocument() {
		return new DummyDocument();
	}

	public String getTitle() {
		return documentName;
	}

	public void setDocument(IDocument doc) {
	}

	public void delete() {
	}

	public Component getComponent() {
		return new JLabel(message);
	}

	public void initialize() {

	}

	public void loadStatus() {
	}

	public void saveStatus() {
	}

	private class DummyDocument extends AbstractDocument implements IDocument {

		public void addDocument(IDocument document) {

		}

		public boolean allowsChildren() {
			return false;
		}

		public void closeDocument(IProgressMonitor pm) throws DocumentException {

		}

		public IDocument getDocument(int index) {
			return null;
		}

		public int getDocumentCount() {
			return 0;
		}

		public String getName() {
			return message;
		}

		public HashMap<String, String> getPersistenceProperties()
				throws DocumentException {
			return null;
		}

		public void openDocument(IProgressMonitor pm) throws DocumentException {
		}

		public void saveDocument(IProgressMonitor pm) throws DocumentException {
		}

		public void setPersistenceProperties(HashMap<String, String> properties)
				throws DocumentException {
		}

		public void removeDocument(IDocument document) {

		}

	}

	public void closingEditor() {
	}

}
