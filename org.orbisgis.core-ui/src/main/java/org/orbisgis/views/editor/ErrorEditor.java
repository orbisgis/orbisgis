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
package org.orbisgis.views.editor;

import java.awt.Component;

import javax.swing.JLabel;

import org.orbisgis.editor.IEditor;
import org.orbisgis.geocognition.AbstractGeocognitionElement;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.progress.IProgressMonitor;

public class ErrorEditor implements IEditor {

	private String message;
	private String documentName;

	public ErrorEditor(String documentName, String message) {
		this.message = message;
		this.documentName = documentName;
	}

	public boolean acceptElement(String typeId) {
		return false;
	}

	public GeocognitionElement getElement() {
		return new DummyElement();
	}

	public String getTitle() {
		return documentName;
	}

	public void setElement(GeocognitionElement obj) {
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

	private class DummyElement extends AbstractGeocognitionElement implements
			GeocognitionElement {

		@Override
		public void addElement(GeocognitionElement element)
				throws UnsupportedOperationException {
			
		}

		@Override
		public void close(IProgressMonitor progressMonitor)
				throws UnsupportedOperationException {
			
		}

		@Override
		public GeocognitionElement getElement(int i)
				throws UnsupportedOperationException {
			return null;
		}

		@Override
		public int getElementCount() throws UnsupportedOperationException {
			return 0;
		}

		@Override
		public Object getJAXBObject() {
			return null;
		}

		@Override
		public Object getObject() throws UnsupportedOperationException {
			return null;
		}

		@Override
		public String getTypeId() {
			return "org.orbisgis.geocognition.Error";
		}

		@Override
		public boolean isFolder() {
			return false;
		}

		@Override
		public void open(IProgressMonitor progressMonitor)
				throws UnsupportedOperationException, GeocognitionException {
			
		}

		@Override
		public boolean removeElement(GeocognitionElement element) {
			return false;
		}

		@Override
		public void save() throws UnsupportedOperationException {
			
		}

		@Override
		public boolean isModified() {
			return false;
		}

		@Override
		public GeocognitionElement getElement(String id) {
			return null;
		}

		@Override
		public GeocognitionElementFactory getFactory() {
			return null;
		}

		@Override
		public String getXMLContent() throws GeocognitionException {
			return null;
		}

		@Override
		public void setXMLContent(String xml) throws GeocognitionException {
			
		}

		@Override
		public boolean removeElement(String elementId) {
			return false;
		}

		@Override
		public GeocognitionElement cloneElement() throws GeocognitionException {
			return null;
		}

		@Override
		public String getId() {
			return null;
		}

		@Override
		public void setId(String id) throws IllegalArgumentException {
			
		}

		@Override
		public void elementRemoved(GeocognitionElement oldParent) {
		}

	}

}
