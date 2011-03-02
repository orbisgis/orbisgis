package org.orbisgis.core.renderer.se.graphic;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.TextGraphicType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.label.StyledLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.transform.Transform;

public class TextGraphic extends Graphic {

    public TextGraphic() {
    }

    TextGraphic(JAXBElement<TextGraphicType> tge) throws InvalidStyle {
        TextGraphicType tgt = tge.getValue();

        if (tgt.getUnitOfMeasure() != null) {
            this.setUom(Uom.fromOgcURN(tgt.getUnitOfMeasure()));
        }

        if (tgt.getTransform() != null) {
            this.setTransform(new Transform(tgt.getTransform()));
        }


        if (tgt.getStyledLabel() != null) {
            this.setStyledLabel(new StyledLabel(tgt.getStyledLabel()));
        }
    }

    public StyledLabel getStyledLabel() {
        return styledLabel;
    }

    public void setStyledLabel(StyledLabel styledLabel) {
        this.styledLabel = styledLabel;
        styledLabel.setParent(this);
    }

    /**
     * @param ds
     * @param fid
     * @todo implements !
     */
    @Override
    public RenderableGraphics getRenderableGraphics(SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException {

        RenderableGraphics label = styledLabel.getImage(sds, fid, selected, mt);

        if (label != null) {
            Rectangle2D bounds = new Rectangle2D.Double(label.getMinX(), label.getMinY(), label.getWidth(), label.getHeight());

            //System.out.println("Bounds: " + bounds);


            if (transform != null) {
                AffineTransform at = this.transform.getGraphicalAffineTransform(false, sds, fid, mt, (double) label.getWidth(), (double) label.getHeight());

                Shape atShp = at.createTransformedShape(bounds);

                //System.out.println("Bounds: " + atShp.getBounds2D());

                RenderableGraphics rg = Graphic.getNewRenderableGraphics(atShp.getBounds2D(), 0, mt);

                rg.drawRenderedImage(label.createRendering(mt.getCurrentRenderContext()), at);
                return rg;
            } else {
                return label;
            }
        }
        return null;
    }

    @Override
    public double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JAXBElement<TextGraphicType> getJAXBElement() {
        TextGraphicType t = new TextGraphicType();

        if (styledLabel != null) {
            t.setStyledLabel(styledLabel.getJAXBType());
        }

        if (transform != null) {
            t.setTransform(transform.getJAXBType());
        }

        if (uom != null) {
            t.setUnitOfMeasure(uom.toString());
        }

        ObjectFactory of = new ObjectFactory();
        return of.createTextGraphic(t);
    }

    @Override
    public String dependsOnFeature() {
        return styledLabel.dependsOnFeature();
    }

    @Override
    public void updateGraphic() {
    }
    private StyledLabel styledLabel;
}
