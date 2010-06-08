package org.orbisgis.core.renderer.se.graphic;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.TextGraphicType;
import org.orbisgis.core.renderer.se.common.MapEnv;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.label.StyledLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.transform.Transform;

public class TextGraphic extends Graphic {

    public TextGraphic(){
    }

    TextGraphic(JAXBElement<TextGraphicType> tge) {
        TextGraphicType tgt = tge.getValue();

        if (tgt.getUnitOfMeasure() != null) {
            this.setUom(Uom.fromOgcURN(tgt.getUnitOfMeasure()));
        }

        if (tgt.getTransform() != null) {
            this.setTransform(new Transform(tgt.getTransform()));
        }


        if (tgt.getStyledLabel() != null){
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
    public RenderableGraphics getRenderableGraphics(DataSource ds, long fid, boolean selected) throws ParameterException, IOException {

        RenderableGraphics label = styledLabel.getImage(ds, fid, selected);

        Rectangle2D bounds = new Rectangle2D.Double(label.getMinX(), label.getMinY(), label.getWidth(), label.getHeight());


        if (transform != null) {
            AffineTransform at = this.transform.getGraphicalAffineTransform(ds, fid, false);

            Shape atShp = at.createTransformedShape(bounds);

            RenderableGraphics rg = Graphic.getNewRenderableGraphics(atShp.getBounds2D(), 0);
            rg.drawRenderedImage(label.createRendering(MapEnv.getCurrentRenderContext()), at);
            return rg;
        } else {
            return label;
        }
    }

    @Override
    public double getMaxWidth(DataSource ds, long fid) throws ParameterException, IOException {
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
    public boolean dependsOnFeature() {
        return true;
    }

    @Override
    public void updateGraphic(){
    }

    private StyledLabel styledLabel;
}
