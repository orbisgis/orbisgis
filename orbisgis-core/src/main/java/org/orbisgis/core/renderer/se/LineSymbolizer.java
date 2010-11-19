package org.orbisgis.core.renderer.se;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;

import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.persistance.se.LineSymbolizerType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
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

	public LineSymbolizer(JAXBElement<LineSymbolizerType> st) {
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

		if (ast.getLevel() != null){
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
	 * @throws IOException
	 * @todo make sure the geom is a line or an area; implement p_offset
	 */
	@Override
	public void draw(Graphics2D g2, Feature feat, boolean selected, MapTransform mt) throws ParameterException, IOException, DriverException {
		if (stroke != null) {
			ArrayList<Shape> shapes = this.getShape(feat, mt);

			if (shapes != null) {
				for (Shape shp : shapes) {
					if (shp != null) {
						if (perpendicularOffset != null) {
							double offset = Uom.toPixel(perpendicularOffset.getValue(feat), 
									getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
							stroke.draw(g2, ShapeHelper.perpendicularOffset(shp, offset), feat, selected, mt);
						} else {
							stroke.draw(g2, shp, feat, selected, mt);
						}
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

		if (level >= 0){
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
}
