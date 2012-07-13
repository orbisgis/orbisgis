package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.PointTextGraphicType;
import net.opengis.se._2_0.core.TranslateType;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.label.PointLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * A {@code PointTextGraphic} is used to paint a text label using a given translation. It is consequently
 * dependant on :
 * <ul><li>A x-coordinate</li>
 * <li>A y-coordinate</li>
 * <li>A {@code PointLabel}</li></ul>
 * @author alexis
 */
public final class PointTextGraphic extends Graphic implements UomNode {

        private Uom uom;
        private PointLabel pointLabel;
        private RealParameter x;
        private RealParameter y;

        /**
         * Build a new {@code PointTextGraphic}, at the position of its container. 
         */
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
                        TranslateType pp = tgt.getPointPosition();
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
        public Uom getOwnUom() {
                return uom;
        }

        @Override
        public void setUom(Uom uom) {
                this.uom = uom;
        }

        /**
         * Get the inner label, contained in this {@code PointTextGraphic}.
         * @return 
         */
        public PointLabel getPointLabel() {
                return pointLabel;
        }

        /**
         * Set the inner label, contained in this {@code PointTextGraphic}.
         * @param pointLabel 
         */
        public void setPointLabel(PointLabel pointLabel) {
                this.pointLabel = pointLabel;
                if (pointLabel != null) {
                        pointLabel.setParent(this);
                }
        }

        @Override
        public Rectangle2D getBounds(Map<String,Value> map, MapTransform mt) throws ParameterException, IOException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void draw(Graphics2D g2, Map<String,Value> map,
                boolean selected, MapTransform mt, AffineTransform fat) throws ParameterException, IOException {

                AffineTransform at = new AffineTransform(fat);
                double px = 0;
                double py = 0;

                if (getX() != null) {
                        px = Uom.toPixel(getX().getValue(map), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                }
                if (getY() != null) {
                        py = Uom.toPixel(getY().getValue(map), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                }

                Rectangle2D.Double bounds = new Rectangle2D.Double(px - 5, py - 5, 10, 10);
                Shape atShp = at.createTransformedShape(bounds);

                pointLabel.draw(g2, map, atShp, selected, mt, null);
        }


        /*@Override
        public double getMaxWidth(DataSource sds, long fid, MapTransform mt) throws ParameterException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
        }*/
        @Override
        public JAXBElement<PointTextGraphicType> getJAXBElement() {
                PointTextGraphicType t = new PointTextGraphicType();

                if (pointLabel != null) {
                        t.setPointLabel(pointLabel.getJAXBType());
                }

                if (x != null || y != null) {
                        TranslateType ppt = new TranslateType();
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
        public HashSet<String> dependsOnFeature() {
                HashSet<String> result = new HashSet<String>();
                if (pointLabel != null) {
                        result.addAll(pointLabel.dependsOnFeature());
                }
                if (x != null) {
                        result.addAll(x.dependsOnFeature());
                }
                if (y != null) {
                        result.addAll(y.dependsOnFeature());
                }

                return result;
        }

        @Override
        public UsedAnalysis getUsedAnalysis() {
            UsedAnalysis ua = new UsedAnalysis();
            if(pointLabel != null){
                ua.merge(pointLabel.getUsedAnalysis());
            }
            ua.include(x);
            ua.include(y);
            return ua;
        }

        /**
         * Get the x-displacement in the associated translation.
         * @return 
         */
        public RealParameter getX() {
                return x;
        }

        /**
         * Set the x-displacement in the associated translation.
         * @param x 
         */
        public void setX(RealParameter x) {
                this.x = x;
                if (this.x != null) {
                        this.x.setContext(RealParameterContext.REAL_CONTEXT);
                }
        }

        /**
         * Get the y-displacement in the associated translation.
         * @return 
         */
        public RealParameter getY() {
                return y;
        }

        /**
         * Set the y-displacement in the associated translation.
         * @param y 
         */
        public void setY(RealParameter y) {
                this.y = y;
                if (this.y != null) {
                        this.y.setContext(RealParameterContext.REAL_CONTEXT);
                }
        }

        @Override
        public void updateGraphic() {
        }
}
