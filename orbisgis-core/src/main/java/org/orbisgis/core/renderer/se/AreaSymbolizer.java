package org.orbisgis.core.renderer.se;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.AreaSymbolizerType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;

import org.gdms.data.feature.Feature;
import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.common.ShapeHelper;


import org.orbisgis.core.renderer.se.common.Uom;

import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.transform.Transform;

public final class AreaSymbolizer extends VectorSymbolizer implements FillNode, StrokeNode {

	private RealParameter perpendicularOffset;
	private Stroke stroke;
	private Fill fill;

	public AreaSymbolizer() {
		super();
		name = "Area symbolizer";
		uom = Uom.MM;
		this.setFill(new SolidFill());
		this.setStroke(new PenStroke());
	}

	public AreaSymbolizer(JAXBElement<AreaSymbolizerType> st) {
		super(st);

		AreaSymbolizerType ast = st.getValue();


		if (ast.getGeometry() != null) {
			// TODO createGeometryFunction from XML
			this.setGeometry(SeParameterFactory.createGeometryParameter(ast.getGeometry()));
		}

		if (ast.getUnitOfMeasure() != null) {
			this.uom = Uom.fromOgcURN(ast.getUnitOfMeasure());
		}

		if (ast.getPerpendicularOffset() != null) {
			this.setPerpendicularOffset(SeParameterFactory.createRealParameter(ast.getPerpendicularOffset()));
		}

		if (ast.getLevel() != null){
			this.setLevel(ast.getLevel());
		}

		if (ast.getTransform() != null) {
			this.setTransform(new Transform(ast.getTransform()));
		}

		if (ast.getFill() != null) {
			this.setFill(Fill.createFromJAXBElement(ast.getFill()));
		}

		if (ast.getStroke() != null) {
			this.setStroke(Stroke.createFromJAXBElement(ast.getStroke()));
		}
	}

	@Override
	public void setStroke(Stroke stroke) {
		if (stroke != null) {
			stroke.setParent(this);
		}
		this.stroke = stroke;
	}

	@Override
	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public void setFill(Fill fill) {
		if (fill != null) {
			fill.setParent(this);
		}
		this.fill = fill;
	}

	@Override
	public Fill getFill() {
		return fill;
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

	/**
	 *
	 * @param g2
	 * @param sds
	 * @param fid
	 * @throws ParameterException
	 * @throws IOException error while accessing external resource
	 * @throws DriverException
	 */
	@Override
	public void draw(Graphics2D g2, Feature feat, boolean selected, MapTransform mt) throws ParameterException, IOException, DriverException {
		ArrayList<Shape> shapes = this.getShape(feat, mt);

		if (shapes != null) {
			for (Shape shp : shapes) {
				if (perpendicularOffset != null) {
					double offset = perpendicularOffset.getValue(feat);
					shp = ShapeHelper.perpendicularOffset(shp, offset);
				}

				if (fill != null) {
					fill.draw(g2, shp, feat, selected, mt);
				}

				if (stroke != null) {
					stroke.draw(g2, shp, feat, selected, mt);
				}
			}
		}
	}

	@Override
	public JAXBElement<AreaSymbolizerType> getJAXBElement() {
		ObjectFactory of = new ObjectFactory();
		AreaSymbolizerType s = of.createAreaSymbolizerType();

		this.setJAXBProperty(s);

		if (uom != null) {
			s.setUnitOfMeasure(this.getUom().toURN());
		}

		if (transform != null) {
			s.setTransform(transform.getJAXBType());
		}

		if (level >= 0){
			s.setLevel(level);
		}

		if (this.perpendicularOffset != null) {
			s.setPerpendicularOffset(perpendicularOffset.getJAXBParameterValueType());
		}

		if (fill != null) {
			s.setFill(fill.getJAXBElement());
		}

		if (stroke != null) {
			s.setStroke(stroke.getJAXBElement());
		}

		return of.createAreaSymbolizer(s);
	}

}
