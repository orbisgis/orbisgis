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

package org.orbisgis.core.renderer.se;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.Drawer;

import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.TextSymbolizerType;

import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.label.Label;
import org.orbisgis.core.renderer.se.label.PointLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.transform.Transform;

public final class TextSymbolizer extends VectorSymbolizer {

	private RealParameter perpendicularOffset;
	private Label label;

	public TextSymbolizer() {
		super();
		this.name = "Label";
		setLabel(new PointLabel());
		uom = Uom.MM;
	}

	public void setLabel(Label label) {
		label.setParent(this);
		this.label = label;
	}

	public Label getLabel() {
		return label;
	}

	public RealParameter getPerpendicularOffset() {
		return perpendicularOffset;
	}

	public void setPerpendicularOffset(RealParameter perpendicularOffset) {
		this.perpendicularOffset = perpendicularOffset;
		if (this.perpendicularOffset != null){
			this.perpendicularOffset.setContext(RealParameterContext.realContext);
		}
	}

	@Override
	public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException, DriverException {
		ArrayList<Shape> shapes = this.getShapes(sds, fid, mt);

		if (shapes != null) {
			for (Shape shp : shapes) {
				if (shp != null && label != null) {
					label.draw(g2, sds, fid, shp, selected, mt);
				}
			}
		}
	}

	@Override
	public JAXBElement<TextSymbolizerType> getJAXBElement() {

		ObjectFactory of = new ObjectFactory();
		TextSymbolizerType s = of.createTextSymbolizerType();

		this.setJAXBProperty(s);


		if (this.getUom() != null){
			s.setUnitOfMeasure(this.getUom().toURN());
		}

		if (transform != null) {
			s.setTransform(transform.getJAXBType());
		}

		if (perpendicularOffset != null) {
			s.setPerpendicularOffset(perpendicularOffset.getJAXBParameterValueType());
		}

		if (label != null) {
			s.setLabel(label.getJAXBElement());
		}

		return of.createTextSymbolizer(s);
	}

	public TextSymbolizer(JAXBElement<TextSymbolizerType> st) throws InvalidStyle {
		super(st);
		TextSymbolizerType tst = st.getValue();


		if (tst.getGeometry() != null) {
			// TODO
		}

		if (tst.getUnitOfMeasure() != null) {
			this.uom = Uom.fromOgcURN(tst.getUnitOfMeasure());
		}

		if (tst.getPerpendicularOffset() != null) {
			this.setPerpendicularOffset(SeParameterFactory.createRealParameter(tst.getPerpendicularOffset()));
		}

		if (tst.getTransform() != null) {
			this.setTransform(new Transform(tst.getTransform()));
		}

		if (tst.getLabel() != null) {
			this.setLabel(Label.createLabelFromJAXBElement(tst.getLabel()));
		}
	}

	@Override
	public void draw(Drawer drawer, long fid, boolean selected) {
		drawer.drawTextSymbolizer(fid, selected);
	}
}
