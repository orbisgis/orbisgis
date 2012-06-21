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

import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.edition.EditableElementException;
import org.orbisgis.core.geocognition.mapContext.GeocognitionException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.map.TransformListener;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public final class LeafElement extends AbstractGeocognitionElement implements
		GeocognitionElement, TransformListener{

	private String fixedId;
	private String id;
	private Boolean modified = null;
	private GeocognitionExtensionElement element;
	private ContentListener contentListener;

	public LeafElement(GeocognitionExtensionElement element) {
		this.element = element;
		fixedId = element.getFixedId();
		if (fixedId != null) {
			this.id = fixedId;
		}
		contentListener = new ContentListener();
	}

	@Override
	public void setId(String id) throws IllegalArgumentException {
		if (fixedId != null) {
			if (!id.toLowerCase().equals(fixedId.toLowerCase())) {
				throw new IllegalArgumentException(I18N.getString("orbisgis.org.orbigis.leafElement.thisElementCannot") //$NON-NLS-1$
						+ I18N.getString("orbisgis.org.orbigis.leafElement.haveAnIdDifferentThan") + fixedId); //$NON-NLS-1$
			} else {
				try {
					element.idChanged(id);
					this.id = id;
					fireIdChanged();
				} catch (GeocognitionException e) {
					throw new IllegalArgumentException(I18N.getString("orbisgis.org.orbigis.leafElement.cannotChangeId"), e); //$NON-NLS-1$
				}
			}
		} else {
			try {
				GeocognitionElement parent = getParent();
				if (parent != null) {
					for (int i = 0; i < parent.getElementCount(); i++) {
						GeocognitionElement child = parent.getElement(i);
						if (child != this) {
							if (child.getId().toLowerCase().equals(
									id.toLowerCase())) {
								throw new GeocognitionException(
										I18N.getString("orbisgis.org.orbigis.leafElement.thereIsAlready") //$NON-NLS-1$
												+ I18N.getString("orbisgis.org.orbigis.leafElement.anElementWithTheId") //$NON-NLS-1$
												+ id);
							}
						}
					}
				}
				element.idChanged(id);
				this.id = id;
				fireIdChanged();
			} catch (GeocognitionException e) {
				throw new IllegalArgumentException(I18N.getString("orbisgis.org.orbigis.leafElement.cannotChangeId"), e); //$NON-NLS-1$
			}
		}
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void addElement(GeocognitionElement element) {
		throw new UnsupportedOperationException(
				I18N.getString("orbisgis.org.orbigis.leafElement.cannotHaveChildren")); //$NON-NLS-1$
	}

	@Override
	public boolean removeElement(GeocognitionElement element) {
		throw new UnsupportedOperationException(
				I18N.getString("orbisgis.org.orbigis.leafElement.cannotHaveChildren")); //$NON-NLS-1$
	}

	@Override
	public void elementRemoved(GeocognitionElement oldPatent) {
		element.elementRemoved();
	}

	@Override
	public GeocognitionElement getElement(int i) {
		throw new UnsupportedOperationException(
				I18N.getString("orbisgis.org.orbigis.leafElement.cannotHaveChildren")); //$NON-NLS-1$
	}

	@Override
	public GeocognitionElement getElement(String id) {
		throw new UnsupportedOperationException(
				I18N.getString("orbisgis.org.orbigis.leafElement.cannotHaveChildren")); //$NON-NLS-1$
	}

	@Override
	public int getElementCount() {
		throw new UnsupportedOperationException(
				I18N.getString("orbisgis.org.orbigis.leafElement.cannotHaveChildren")); //$NON-NLS-1$
	}

	@Override
	public boolean removeElement(String elementId) {
		throw new UnsupportedOperationException(
				I18N.getString("orbisgis.org.orbigis.leafElement.cannotHaveChildren")); //$NON-NLS-1$
	}

	@Override
	public boolean isFolder() {
		return false;
	}

	@Override
	public boolean isModified() {
		if (modified == null) {
			if (element.isModified()) {
				modified = true;
			} else {
				try {
					Object obj = getJAXBObject();
					String contextPath = getFactory().getJAXBContextPath();
					if (contextPath == null) {
						contextPath = DefaultGeocognition
								.getCognitionContextPath();
					}
					JAXBContext context = JAXBContext.newInstance(contextPath,
							this.getClass().getClassLoader());
					Marshaller marshaller = context.createMarshaller();
					ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
					marshaller.marshal(obj, bos1);
					ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
					marshaller.marshal(element.getRevertJAXBObject(), bos2);
					modified = !new String(bos1.toByteArray())
							.equals(new String(bos2.toByteArray()));
				} catch (JAXBException e) {
					modified = true;
				}
			}
		}

		return modified;
	}

	@Override
	public void save() throws UnsupportedOperationException,
			EditableElementException {
		modified = null;
		EditableElementException problem = null;
		try {
			element.save();
		} catch (EditableElementException e) {
			problem = e;
		}
		fireSave();
		if (problem != null) {
			throw problem;
		}
	}

	@Override
	public void open(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		modified = null;
		element.setElementListener(contentListener);
		element.open(progressMonitor);
	}

	@Override
	public void close(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		element.close(progressMonitor);
		element.setElementListener(null);
	}

	private final class ContentListener implements
			GeocognitionElementContentListener{
		@Override
		public void contentChanged() {
			modified = null;
			fireContentChanged();
		}
	}

	@Override
	public GeocognitionElementFactory getFactory() {
		return element.getFactory();
	}

	@Override
	public Object getJAXBObject() {
		return element.getJAXBObject();
	}

	@Override
	public Object getObject() throws UnsupportedOperationException {
		return element.getObject();
	}

	@Override
	public String getTypeId() {
		return element.getTypeId();
	}

	@Override
	public String toString() {
		return getId();
	}

	@Override
	public String getXMLContent() throws GeocognitionException {
		try {
			return DefaultGeocognition.getXML(getJAXBObject(), getTypeId());
		} catch (JAXBException e) {
			throw new GeocognitionException(
					I18N.getString("orbisgis.org.orbigis.leafElement.cannotGetTheXML"), e); //$NON-NLS-1$
		}
	}

	@Override
	public void setXMLContent(String xml) throws GeocognitionException {
		try {
			Object xmlObject = DefaultGeocognition.getValidXMLObject(this, xml);
			element.setJAXBObject(xmlObject);
		} catch (IllegalArgumentException e) {
			throw new GeocognitionException(
					I18N.getString("orbisgis.org.orbigis.leafElement.cannotSetTheXML"), e); //$NON-NLS-1$
		} catch (JAXBException e) {
			throw new GeocognitionException(
					I18N.getString("orbisgis.org.orbigis.leafElement.cannotSetTheXML"), e); //$NON-NLS-1$
		} catch (GeocognitionException e) {
			throw new GeocognitionException(
					I18N.getString("orbisgis.org.orbigis.leafElement.cannotSetTheXML"), e); //$NON-NLS-1$
		}
	}

	@Override
	public GeocognitionElement cloneElement() throws GeocognitionException {
		LeafElement cloned;
		try {
			cloned = new LeafElement(getFactory().createElementFromXML(
					element.getJAXBObject(), element.getTypeId()));
			cloned.setId(getId());
		} catch (PersistenceException e) {
			throw new GeocognitionException(e.getMessage(), e);
		}
		return cloned;
	}

	@Override
	public Map<String, String> getProperties() {
		Map<String, String> ret = element.getProperties();
		if (ret == null) {
			return super.getProperties();
		} else {
			return ret;
		}
	}

	@Override
	public void extentChanged(Envelope oldExtent, MapTransform mapTransform) {		
		GeometryFactory geomFactory = new GeometryFactory();
		if(oldExtent!=null) {
			Geometry oldGeom = geomFactory.toGeometry(oldExtent);
			Geometry newGeom = geomFactory.toGeometry(mapTransform.getExtent());
			if(!newGeom.toText().equals(oldGeom.toText())){		
				//if extented value changes enable save button
				modified = null;
			}
		}
	}

	@Override
	public void imageSizeChanged(int oldWidth, int oldHeight,
			MapTransform mapTransform) {
		
		
	}
}
