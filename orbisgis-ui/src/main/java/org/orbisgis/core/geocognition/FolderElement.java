/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.geocognition;

import java.util.ArrayList;

import org.orbisgis.core.OrbisGISPersitenceConfig;
import org.orbisgis.core.geocognition.mapContext.GeocognitionException;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.I18N;

class FolderElement extends AbstractGeocognitionElement implements
		GeocognitionElement {

	private String id;
	private ArrayList<GeocognitionElement> elements = new ArrayList<GeocognitionElement>();
	private DefaultGeocognition geocognition;

	FolderElement(DefaultGeocognition geocognition) {
		this.geocognition = geocognition;
	}

	public int getElementCount() {
		return elements.size();
	}

	public GeocognitionElement getElement(int i) {
		return elements.get(i);
	}

	@Override
	public void addElement(GeocognitionElement element) {
		addElement(element, true);
	}

	public void addElement(GeocognitionElement element, boolean fireEvents) {
		if (getElement(element.getId()) != null) {
			throw new UnsupportedOperationException(I18N.getString("orbisgis.org.orbisgis.folderElement.thereIsAlready") //$NON-NLS-1$
					+ I18N.getString("orbisgis.org.orbisgis.folderElement.elementWithId") + element.getId()); //$NON-NLS-1$
		}
		elements.add(element);
		((AbstractGeocognitionElement) element).setParent(this);
		if (fireEvents) {
			geocognition.fireElementAdded(this, element);
		}
	}

	@Override
	public boolean isFolder() {
		return true;
	}

	@Override
	public Object getObject() {
		throw new UnsupportedOperationException(I18N.getString("orbisgis.org.orbisgis.folderElement.foldesDoNotWrapObjects")); //$NON-NLS-1$
	}

	@Override
	public Object getJAXBObject() {
		throw new RuntimeException(I18N.getString("orbisgis.org.orbisgis.folderElement.bug")); //$NON-NLS-1$
	}

	@Override
	public String getTypeId() {
		return OrbisGISPersitenceConfig.GEOCOGNITION_FOLDER_ELEMENT_ID;
	}

	@Override
	public boolean removeElement(GeocognitionElement element) {
		return removeElement(element, true);
	}

	public boolean removeElement(GeocognitionElement element, boolean fireEvents) {
		boolean allowRemove;
		if (fireEvents) {
			allowRemove = geocognition.fireElementRemoving(element);
		} else {
			allowRemove = true;
		}
		if (allowRemove) {
			((AbstractGeocognitionElement) element).setParent(null);
			elements.remove(element);
			if (fireEvents) {
				geocognition.fireElementRemoved(element);
			}
			((AbstractGeocognitionElement) element).elementRemoved(this);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void elementRemoved(GeocognitionElement oldParent) {
		for (GeocognitionElement child : elements) {
			((AbstractGeocognitionElement) child).elementRemoved(this);
		}
	}

	@Override
	public void close(ProgressMonitor progressMonitor) {
		unsupported();
	}

	@Override
	public void open(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException {
		unsupported();
	}

	@Override
	public void save() {
		unsupported();
	}

	private void unsupported() {
		throw new UnsupportedOperationException(I18N.getString("orbisgis.org.orbisgis.folderElement.cannotBeEdited")); //$NON-NLS-1$
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public GeocognitionElement getElement(String id) {
		for (GeocognitionElement element : elements) {
			if (element.getId().equals(id)) {
				return element;
			}
		}
		return null;
	}

	@Override
	public GeocognitionElementFactory getFactory() {
		return null;
	}

	@Override
	public String toString() {
		return getId();
	}

	@Override
	public String getXMLContent() {
		throw new UnsupportedOperationException(
				I18N.getString("orbisgis.org.orbisgis.folderElement.cannotExtractXMLFromFolder")); //$NON-NLS-1$
	}

	@Override
	public void setXMLContent(String xml) {
		throw new UnsupportedOperationException(
				I18N.getString("orbisgis.org.orbisgis.folderElement.cannotExtractXMLFromFolder")); //$NON-NLS-1$
	}

	@Override
	public boolean removeElement(String elementId) {
		GeocognitionElement elem = getElement(elementId);
		if (elem != null) {
			return removeElement(elem);
		} else {
			return false;
		}
	}

	@Override
	public GeocognitionElement cloneElement() throws GeocognitionException {
		FolderElement ret = new FolderElement(geocognition);
		ret.setId(getId());
		for (int i = 0; i < getElementCount(); i++) {
			ret.addElement(getElement(i).cloneElement());
		}
		return ret;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		fireIdChanged();
	}
}
