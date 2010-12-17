package org.orbisgis.core.renderer.se.fill;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;

import java.io.IOException;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.DotMapFillType;

import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.GraphicNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

public final class DotMapFill extends Fill implements GraphicNode {

	DotMapFill(JAXBElement<DotMapFillType> f) throws InvalidStyle {
		DotMapFillType dmf = f.getValue();

		if (dmf.getGraphic() != null) {
			this.setGraphicCollection(new GraphicCollection(dmf.getGraphic(), this));
		}

		if (dmf.getValuePerMark() != null) {
			this.setQuantityPerMark(SeParameterFactory.createRealParameter(dmf.getValuePerMark()));
		}

		if (dmf.getValueToRepresent() != null) {
			this.setTotalQuantity(SeParameterFactory.createRealParameter(dmf.getValueToRepresent()));
		}
	}

	@Override
	public void setGraphicCollection(GraphicCollection mark) {
		this.mark = mark;
		mark.setParent(this);
	}

	@Override
	public GraphicCollection getGraphicCollection() {
		return mark;
	}

	public void setQuantityPerMark(RealParameter quantityPerMark) {
		this.quantityPerMark = quantityPerMark;
		if (this.quantityPerMark != null) {
			this.quantityPerMark.setContext(RealParameterContext.realContext);
		}
	}

	public RealParameter getQantityPerMark() {
		return quantityPerMark;
	}

	public void setTotalQuantity(RealParameter totalQuantity) {
		this.totalQuantity = totalQuantity;
		if (this.totalQuantity != null) {
			this.totalQuantity.setContext(RealParameterContext.realContext);
		}
	}

	public RealParameter getTotalQantity() {
		return totalQuantity;
	}

	@Override
	public Paint getPaint(long fid, SpatialDataSourceDecorator sds, boolean selected, MapTransform mt) throws ParameterException {
		return null;
	}

	@Override
	public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, Shape shp, boolean selected, MapTransform mt) throws ParameterException, IOException {
	}

	@Override
	public boolean dependsOnFeature() {
		if (mark != null && this.mark.dependsOnFeature()) {
			return true;
		}
		if (this.quantityPerMark != null && quantityPerMark.dependsOnFeature()) {
			return true;
		}
		if (this.totalQuantity != null && totalQuantity.dependsOnFeature()) {
			return true;
		}
		return false;
	}

	@Override
	public DotMapFillType getJAXBType() {
		DotMapFillType f = new DotMapFillType();

		if (mark != null) {
			f.setGraphic(mark.getJAXBElement());
		}

		if (quantityPerMark != null) {
			f.setValuePerMark(quantityPerMark.getJAXBParameterValueType());
		}

		if (totalQuantity != null) {
			f.setValuePerMark(totalQuantity.getJAXBParameterValueType());
		}

		return f;
	}

	@Override
	public JAXBElement<DotMapFillType> getJAXBElement() {
		ObjectFactory of = new ObjectFactory();
		return of.createDotMapFill(this.getJAXBType());
	}
	private GraphicCollection mark;
	private RealParameter quantityPerMark;
	private RealParameter totalQuantity;
}
