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
import org.gdms.data.feature.Feature;

import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.Drawer;

import org.orbisgis.core.renderer.persistance.se.LineSymbolizerType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.ShapeHelper;

import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.transform.Transform;

/**
 * Define a style for line features
 * Only contains a stroke
 *
 * @todo add perpendicular offset
 *
 * @author maxence
 */
public final class LineSymbolizer extends VectorSymbolizer implements StrokeNode {

	private RealParameter perpendicularOffset;
	private Stroke stroke;

	public LineSymbolizer() {
		super();
		this.name = "Line Symbolizer";
		setUom(Uom.MM);
		setStroke(new PenStroke());
	}

	public LineSymbolizer(JAXBElement<LineSymbolizerType> st) throws InvalidStyle {
		super(st);
		LineSymbolizerType ast = st.getValue();


		if (ast.getGeometry() != null) {
			// TODO
		}

		if (ast.getUnitOfMeasure() != null) {
			this.uom = Uom.fromOgcURN(ast.getUnitOfMeasure());
		}

		if (ast.getPerpendicularOffset() != null) {
			this.setPerpendicularOffset(SeParameterFactory.createRealParameter(ast.getPerpendicularOffset()));
		}

		if (ast.getLevel() != null) {
			this.setLevel(ast.getLevel());
		}

		if (ast.getTransform() != null) {
			this.setTransform(new Transform(ast.getTransform()));
		}

		if (ast.getStroke() != null) {
			this.setStroke(Stroke.createFromJAXBElement(ast.getStroke()));
		}
	}

	@Override
	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
		stroke.setParent(this);
	}

	public RealParameter getPerpendicularOffset() {
		return perpendicularOffset;
	}

	public void setPerpendicularOffset(RealParameter perpendicularOffset) {
		this.perpendicularOffset = perpendicularOffset;
		if (this.perpendicularOffset != null) {
			this.perpendicularOffset.setContext(RealParameterContext.realContext);
		}
	}

	/**
	 *
	 * @param g2
	 * @param sds
	 * @param fid
	 * @throws ParameterException
	 * @throws IOException
	 * @todo make sure the geom is a line or an area; implement p_offset
	 */
	@Override
	public void draw(Graphics2D g2, Feature feat, boolean selected, MapTransform mt) throws ParameterException, IOException, DriverException {
		if (stroke != null) {
			ArrayList<Shape> shapes = this.getLines(feat, mt);

			if (shapes != null) {
				for (Shape shp : shapes) {
					if (shp != null) {
						if (perpendicularOffset != null) {
							double offset = Uom.toPixel(perpendicularOffset.getValue(feat),
									getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
							shp = ShapeHelper.perpendicularOffset(shp, offset);
						}
						stroke.draw(g2, shp, feat, selected, mt);
					}
				}
			}
		}
	}

	@Override
	public JAXBElement<LineSymbolizerType> getJAXBElement() {
		ObjectFactory of = new ObjectFactory();
		LineSymbolizerType s = of.createLineSymbolizerType();

		this.setJAXBProperty(s);


		s.setUnitOfMeasure(this.getUom().toURN());

		if (transform != null) {
			s.setTransform(transform.getJAXBType());
		}

		if (level >= 0) {
			s.setLevel(level);
		}

		if (this.perpendicularOffset != null) {
			s.setPerpendicularOffset(perpendicularOffset.getJAXBParameterValueType());
		}

		if (stroke != null) {
			s.setStroke(stroke.getJAXBElement());
		}


		return of.createLineSymbolizer(s);
	}

	@Override
	public void draw(Drawer drawer, long fid, boolean selected) {
		drawer.drawLineSymbolizer(fid, selected);
	}
}
