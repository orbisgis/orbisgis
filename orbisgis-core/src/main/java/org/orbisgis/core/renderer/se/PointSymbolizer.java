package org.orbisgis.core.renderer.se;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.PointSymbolizerType;

import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;

import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.transform.Transform;

public final class PointSymbolizer extends VectorSymbolizer implements GraphicNode {

	/*
	 * Create a default pointSymbolizer: Square 10mm
	 *
	 *
	 */
	public PointSymbolizer() {
		super();
		this.name = "Point symbolizer";
		setGraphicCollection(new GraphicCollection());
		uom = Uom.MM;

		MarkGraphic mark = new MarkGraphic();
		mark.setTo3mmCircle();
		graphic.addGraphic(mark);
	}

	public PointSymbolizer(JAXBElement<PointSymbolizerType> st) {
		super(st);
		PointSymbolizerType ast = st.getValue();

		if (ast.getGeometry() != null) {
			// TODO load GeometryFunction !
		}

		if (ast.getUnitOfMeasure() != null) {
			Uom u = Uom.fromOgcURN(ast.getUnitOfMeasure());
			System.out.println("This is the UOM: " + u);
			this.setUom(u);
		}

		if (ast.getTransform() != null) {
			this.setTransform(new Transform(ast.getTransform()));
		}

		if (ast.getLevel() != null){
			this.setLevel(ast.getLevel());
		}

		if (ast.getGraphic() != null) {
			this.setGraphicCollection(new GraphicCollection(ast.getGraphic(), this));

		}
	}

	@Override
	public GraphicCollection getGraphicCollection() {
		return graphic;
	}

	@Override
	public void setGraphicCollection(GraphicCollection graphic) {
		this.graphic = graphic;
		graphic.setParent(this);
	}

	@Override
	public void draw(Graphics2D g2, Feature feat, boolean selected, MapTransform mt) throws IOException, DriverException {
		if (graphic != null && graphic.getNumGraphics() > 0) {

			try {
				Point2D pt = this.getPointShape(feat, mt);
				// This is to emulate ExtractFirstPoint geom function !!!
				//Point2D pt = this.getFirstPointShape(sds, fid);
				RenderableGraphics rg = graphic.getGraphic(feat, selected, mt);

				//RenderedImage cache = graphic.getCache(sds, fid, selected);
				//if (cache != null) {

				if (rg != null) {
					double x = 0, y = 0;

					x = pt.getX();
					y = pt.getY();

					// Draw the graphic right over the point !
					g2.drawRenderedImage(rg.createRendering(mt.getCurrentRenderContext()), AffineTransform.getTranslateInstance(x, y));
					//g2.drawRenderedImage(cache, AffineTransform.getTranslateInstance(x, y));
				}
			} catch (ParameterException ex) {
				Services.getErrorManager().error("Could not render feature ", ex);
			}

		}
	}

	@Override
	public JAXBElement<PointSymbolizerType> getJAXBElement() {
		ObjectFactory of = new ObjectFactory();
		PointSymbolizerType s = of.createPointSymbolizerType();

		this.setJAXBProperty(s);


		if (this.uom != null) {
			s.setUnitOfMeasure(this.getUom().toURN());
		}

		if (transform != null) {
			s.setTransform(transform.getJAXBType());
		}

		if (this.level >= 0){
			s.setLevel(level);
		}


		if (graphic != null) {
			s.setGraphic(graphic.getJAXBElement());
		}

		return of.createPointSymbolizer(s);
	}

	private GraphicCollection graphic;
}
