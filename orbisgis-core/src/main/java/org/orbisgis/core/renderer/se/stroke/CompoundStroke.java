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
package org.orbisgis.core.renderer.se.stroke;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.CompoundStrokeType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.StrokeAnnotationGraphicType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 *
 * @author maxence
 */
public final class CompoundStroke extends Stroke {

	RealParameter preGap;
	RealParameter postGap;
	ArrayList<CompoundStrokeElement> elements;
	ArrayList<StrokeAnnotationGraphic> annotations;

	public CompoundStroke(CompoundStrokeType s) throws InvalidStyle {
		if (s.getPreGap() != null) {
			setPreGap(SeParameterFactory.createRealParameter(s.getPreGap()));
		}

		if (s.getPostGap() != null) {
			setPostGap(SeParameterFactory.createRealParameter(s.getPostGap()));
		}

		elements = new ArrayList<CompoundStrokeElement>();
		annotations = new ArrayList<StrokeAnnotationGraphic>();

		if (s.getStrokeElementOrAlternativeStrokeElements() != null) {
			for (Object o : s.getStrokeElementOrAlternativeStrokeElements()) {
				CompoundStrokeElement cse = CompoundStrokeElement.createCompoundStrokeElement(o);
				addCompoundStrokeElement(cse);
			}
		}

		if (s.getStrokeAnnotationGraphic() != null) {
			for (StrokeAnnotationGraphicType sagt : s.getStrokeAnnotationGraphic()) {
				StrokeAnnotationGraphic sag = new StrokeAnnotationGraphic(sagt);
				addStrokeAnnotationGraphic(sag);
			}
		}

		if (s.getUnitOfMeasure() != null) {
			this.setUom(Uom.fromOgcURN(s.getUnitOfMeasure()));
		} else {
			this.uom = null;
		}
	}

	public CompoundStroke(JAXBElement<CompoundStrokeType> s) throws InvalidStyle {
		this(s.getValue());
	}

	public void setPreGap(RealParameter preGap) {
		this.preGap = preGap;

		if (preGap != null) {
			this.preGap.setContext(RealParameterContext.nonNegativeContext);
		}
	}

	public void setPostGap(RealParameter postGap) {
		this.postGap = postGap;

		if (postGap != null) {
			this.postGap.setContext(RealParameterContext.nonNegativeContext);
		}
	}

	public RealParameter getPreGap() {
		return preGap;
	}

	public RealParameter getPostGap() {
		return postGap;
	}

	@Override
	public double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

    @Override
    public double getMinLength(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	@Override
	public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, Shape shp, boolean selected, MapTransform mt) throws ParameterException, IOException {

		double initGap = 0.0;
		double endGap = 0.0;

		if (preGap != null) {
			initGap = preGap.getValue(sds, fid);
		}

		if (postGap != null) {
			endGap = postGap.getValue(sds, fid);
		}



	}

	@Override
	public boolean dependsOnFeature() {
		for (StrokeAnnotationGraphic sag : annotations) {
			if (sag.dependsOnFeature()) {
				return true;
			}
		}

		for (CompoundStrokeElement elem : elements) {
			if (elem.dependsOnFeature()) {
				return true;
			}
		}

		return false;
	}

	private void addCompoundStrokeElement(CompoundStrokeElement cse) {
		elements.add(cse);
		cse.setParent(this);
	}

	private void addStrokeAnnotationGraphic(StrokeAnnotationGraphic sag) {
		annotations.add(sag);
		sag.setParent(this);
	}

	@Override
	public JAXBElement<CompoundStrokeType> getJAXBElement() {
		ObjectFactory of = new ObjectFactory();
		return of.createCompoundStroke(this.getJAXBType());
	}

	public CompoundStrokeType getJAXBType() {
		CompoundStrokeType s = new CompoundStrokeType();

		this.setJAXBProperties(s);

		if (this.preGap != null) {
			s.setPreGap(preGap.getJAXBParameterValueType());
		}

		if (this.postGap != null) {
			s.setPostGap(postGap.getJAXBParameterValueType());
		}


		List<Object> sElem = s.getStrokeElementOrAlternativeStrokeElements();
		List<StrokeAnnotationGraphicType> sAnnot = s.getStrokeAnnotationGraphic();

		for (CompoundStrokeElement elem : this.elements) {
			sElem.add(elem.getJaxbType());
		}

		for (StrokeAnnotationGraphic sag : annotations) {
			sAnnot.add(sag.getJaxbType());
		}

		return s;
	}
}
