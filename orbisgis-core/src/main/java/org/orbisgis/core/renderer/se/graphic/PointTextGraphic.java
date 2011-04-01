package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.PointPositionType;
import net.opengis.se._2_0.core.PointTextGraphicType;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.label.PointLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

public final class PointTextGraphic extends Graphic implements UomNode {

    private Uom uom;
    private PointLabel pointLabel;
    private RealParameter x;
    private RealParameter y;

    public PointTextGraphic() {
        setPointLabel(new PointLabel());
    }

    PointTextGraphic(JAXBElement<PointTextGraphicType> tge) throws InvalidStyle {
        PointTextGraphicType tgt = tge.getValue();

        if (tgt.getUom() != null) {
            this.setUom(Uom.fromOgcURN(tgt.getUom()));
        }

        if (tgt.getPointLabel() != null) {
            this.setPointLabel(new PointLabel(tgt.getPointLabel()));
        }

        if (tgt.getPointPosition() != null) {
            PointPositionType pp = tgt.getPointPosition();
            if (pp.getX() != null) {
                setX(SeParameterFactory.createRealParameter(pp.getX()));
            }

            if (pp.getY() != null) {
                setY(SeParameterFactory.createRealParameter(pp.getY()));
            }
        }
    }


    @Override
    public Uom getUom() {
        if (uom != null) {
            return this.uom;
        } else {
            return parent.getUom();
        }
    }

	@Override
	public Uom getOwnUom(){
		return uom;
	}

	@Override
    public void setUom(Uom uom) {
        this.uom = uom;
    }



    public PointLabel getPointLabel() {
        return pointLabel;
    }

    public void setPointLabel(PointLabel pointLabel) {
        this.pointLabel = pointLabel;
        if (pointLabel != null) {
            pointLabel.setParent(this);
        }
    }

    /**
     * @param ds
     * @param fid
     * @todo implements !
     */
    /*@Override
    public RenderableGraphics getRenderableGraphics(SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException {
        double px = 0;
        double py = 0;

        if (this.x != null) {
            px = Uom.toPixel(x.getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        }
        if (this.y != null) {
            py = Uom.toPixel(y.getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        }

        RenderableGraphics image = pointLabel.getLabel().getImage(sds, fid, selected, mt);

        double height = 0.0;
        height = Math.abs(py);
        height += image.getHeight();

        //height += pointLabel.getLabel().getStroke().getMaxWidth(sds, fid, mt);

        double width = 0.0;
        width = Math.abs(px);
        width += image.getWidth();
        //width += pointLabel.getLabel().getStroke().getMaxWidth(sds, fid, mt);

        Rectangle2D.Double bounds = new Rectangle2D.Double(-width, -height, 2 * width, 2 * height);
        RenderableGraphics g2 = Graphic.getNewRenderableGraphics(bounds, 10, mt);

        pointLabel.draw(g2, sds, fid, bounds, selected, mt, null);
        return g2;
    }*/


    @Override
    public Rectangle2D getBounds(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, 
            boolean selected, MapTransform mt, AffineTransform fat) throws ParameterException, IOException {

        AffineTransform at = new AffineTransform(fat);
        double px = 0;
        double py = 0;

        if (getX() != null) {
            px = Uom.toPixel(getX().getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        }
        if (getY() != null) {
            py = Uom.toPixel(getY().getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        }

        Rectangle2D.Double bounds = new Rectangle2D.Double(px-5, py-5, 10, 10);
        Shape atShp = at.createTransformedShape(bounds);

        pointLabel.draw(g2, sds, fid, atShp, selected, mt, null);
    }


    /*@Override
    public double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }*/

    @Override
    public JAXBElement<PointTextGraphicType> getJAXBElement() {
        PointTextGraphicType t = new PointTextGraphicType();

        if (pointLabel != null) {
            t.setPointLabel(pointLabel.getPointLabelType());
        }

        if (x != null || y != null) {
            PointPositionType ppt = new PointPositionType();
            if (x != null) {
                ppt.setX(x.getJAXBParameterValueType());
            }
            if (y != null) {
                ppt.setY(y.getJAXBParameterValueType());
            }

            t.setPointPosition(ppt);
        }
        if (getOwnUom() != null) {
            t.setUom(getOwnUom().toURN());
        }
        ObjectFactory of = new ObjectFactory();
        return of.createPointTextGraphic(t);
    }

    @Override
    public String dependsOnFeature() {
        String result = "";
        if (pointLabel != null){
            result += pointLabel.dependsOnFeature();
        }
        if (x != null){
            result += " " + x.dependsOnFeature();
        }
        if (y != null){
            result += " " + y.dependsOnFeature();
        }

        return result.trim();
    }

    public RealParameter getX() {
        return x;
    }

    public void setX(RealParameter x) {
        this.x = x;
        if (this.x != null){
            this.x.setContext(RealParameterContext.realContext);
        }
    }

    public RealParameter getY() {
        return y;
    }

    public void setY(RealParameter y) {
        this.y = y;
        if (this.y != null){
            this.y.setContext(RealParameterContext.realContext);
        }
    }

    @Override
    public void updateGraphic() {
    }
}
