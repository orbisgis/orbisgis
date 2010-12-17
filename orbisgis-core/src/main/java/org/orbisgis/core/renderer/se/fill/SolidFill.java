package org.orbisgis.core.renderer.se.fill;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;

import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.SolidFillType;

import org.gdms.data.feature.Feature;

import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.color.ColorHelper;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * A solid fill fills a shape with a solid color (+opacity)
 *
 * @author maxence
 */
public final class SolidFill extends Fill {

	private ColorParameter color;
	private RealParameter opacity;


	/**
	 * fill with random color 60% opaque
	 */
	public SolidFill() {
		this(new ColorLiteral(), new RealLiteral(60.0));
	}

	/**
	 * fill with specified color 60% opaque
	 * @param c
	 */
	public SolidFill(Color c) {
		this(new ColorLiteral(c), new RealLiteral(60.0));
	}

	/**
	 * fill with specified color and opacity
	 * @param c
	 * @param opacity
	 */
	public SolidFill(Color c, double opacity) {
		this(new ColorLiteral(c), new RealLiteral(opacity));
	}

	/**
	 * fill with specified color and opacity
	 * @param c
	 * @param opacity
	 */
	public SolidFill(ColorParameter c, RealParameter opacity) {
		this.setColor(c);
		this.setOpacity(opacity);
	}

	public SolidFill(JAXBElement<SolidFillType> sf) throws InvalidStyle {
		if (sf.getValue().getColor() != null) {
			setColor(SeParameterFactory.createColorParameter(sf.getValue().getColor()));
		}

		if (sf.getValue().getOpacity() != null) {
			setOpacity(SeParameterFactory.createRealParameter(sf.getValue().getOpacity()));
		}
	}

	public void setColor(ColorParameter color) {
		this.color = color;
	}

	public ColorParameter getColor() {
		return color;
	}

	public void setOpacity(RealParameter opacity) {
		this.opacity = opacity;

		if (opacity != null) {
			this.opacity.setContext(RealParameterContext.percentageContext);
		}
	}

	public RealParameter getOpacity() {
		return opacity;
	}

	@Override
	public Paint getPaint(long fid, SpatialDataSourceDecorator sds, boolean selected, MapTransform mt) throws ParameterException {
		Color c = new Color(128, 128, 128);

		if (color != null){
			c = color.getColor(sds, fid);
		}
		Double op = 100.0;

		if (this.opacity != null) {
			op = this.opacity.getValue(sds, fid);
		}

		// Add opacity to the color
		Color ac = ColorHelper.getColorWithAlpha(c, op);


		if (selected) {
			ac = ColorHelper.invert(ac);
		}

		return ac;
	}

	@Override
	public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, Shape shp, boolean selected, MapTransform mt) throws ParameterException {
		g2.setPaint(getPaint(fid, sds, selected, mt));
		g2.fill(shp);
	}

	@Override
	public String toString() {
		return "Color: " + color + " alpha: " + opacity;
	}

	@Override
	public boolean dependsOnFeature() {
		if (color != null && this.color.dependsOnFeature()) {
			return true;
		}
		if (opacity != null && this.opacity.dependsOnFeature()) {
			return true;
		}
		return false;
	}

	@Override
	public SolidFillType getJAXBType() {
		SolidFillType f = new SolidFillType();

		if (color != null) {
			f.setColor(color.getJAXBParameterValueType());
		}
		if (opacity != null) {
			f.setOpacity(opacity.getJAXBParameterValueType());
		}

		return f;
	}

	@Override
	public JAXBElement<SolidFillType> getJAXBElement() {
		ObjectFactory of = new ObjectFactory();
		return of.createSolidFill(this.getJAXBType());
	}
}
