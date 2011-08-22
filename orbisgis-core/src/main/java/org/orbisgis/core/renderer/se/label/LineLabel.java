/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.label;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;

import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.RenderContext;

import net.opengis.se._2_0.core.LineLabelType;
import net.opengis.se._2_0.core.ObjectFactory;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 * A {@code LineLabel} is a text of some kinf associated to a Line (polygon or not).
 * @author alexis, maxence
 * @todo implements
 */
public class LineLabel extends Label {

    /**
         * Build a new default {@code LineLabel}, using the defaults in 
         * {@link org.orbisgis.core.renderer.se.label.StyledText#StyledText()  StyledText}.
         * The label will be centered (horizontally), and in the middle (vertically)
         * of the graphic.
         */
    public LineLabel() {
        super();
        setvAlign(VerticalAlignment.MIDDLE);
        sethAlign(HorizontalAlignment.CENTER);
    }

    /**
     * Build a {@code LineLabel} from a {@code LineLabelType}
     * @param t
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public LineLabel(LineLabelType t) throws InvalidStyle {
        super(t);
    }

    /**
     * Build a {@code LineLabel} from a JABElement.
     * @param l
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public LineLabel(JAXBElement<LineLabelType> l) throws InvalidStyle {
        this(l.getValue());
    }

    /**
     *
     */
    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, 
            Shape shp, boolean selected, MapTransform mt, RenderContext perm)
            throws ParameterException, IOException {

        Rectangle2D bounds = label.getBounds(g2, sds, fid, mt);
        double totalWidth = bounds.getWidth();

        // TODO, is shp a polygon ? Yes so create a line like:

        /**
         *         ___________
         *   _____/           \
         *   \                 \
         *   /   - - - - - -    \
         *  /                    \
         * |_____________________/
         *
         * And plot label as:
         *         ___________
         *   _____/           \
         *   \                 \
         *   /   A  L  P  E  S  \
         *  /                    \
         * |_____________________/
         *
         * Rather than:
         *         ___________
         *   _____/           \
         *   \                 \
         *   /       ALPES      \
         *  /                    \
         * |_____________________/
         *

         */

        VerticalAlignment vA = this.vAlign;
        HorizontalAlignment hA = this.hAlign;

        if (vA == null) {
            vA = VerticalAlignment.TOP;
            // TODO implement !
        }

        if (hA == null) {
            hA = HorizontalAlignment.CENTER;
        }

        System.out.println ("hA: " + hA);
        
        double lineLength = ShapeHelper.getLineLength(shp);

        System.out.println ("Label/Line : "+ totalWidth + "/" + lineLength);
        
        
        double startAt;
        double stopAt;

        switch (hA) {
            case RIGHT:
                startAt = lineLength - totalWidth;
                stopAt = lineLength;
        
                System.out.println ("RIGHT: " + startAt + ";" + stopAt);
                break;
            case LEFT:
                startAt = 0.0;
                stopAt = totalWidth;
                System.out.println ("LEFT: " + startAt + ";" + stopAt);
                break;
            default:
            case CENTER:
                startAt = (lineLength - totalWidth) / 2.0;
                stopAt = (lineLength + totalWidth) / 2.0;
                System.out.println ("CENTER: " + startAt + ";" + stopAt);
                break;

        }

        if (startAt < 0.0) {
            startAt = 0.0;
        }
        if (stopAt > lineLength){
            stopAt = lineLength;
        }



        Point2D.Double ptStart = ShapeHelper.getPointAt(shp, startAt);
        Point2D.Double ptStop = ShapeHelper.getPointAt(shp, stopAt);

        int way = 1;

        // Do not laid out le label upside-down !
        if (ptStart.x > ptStop.x){
            // invert line way
            way = -1;
            //double tmp = startAt;
            startAt = stopAt;
            //stopAt = tmp;
        }

        double currentPos = startAt;
        double glyphWidth;

        String text = label.getText().getValue(sds, fid);
        String[] glyphs = text.split("");

        ArrayList<Shape> outlines = new ArrayList<Shape>();

        for (String glyph : glyphs) {
            if (glyph != null && !glyph.isEmpty()) {
                Rectangle2D gBounds = label.getBounds(g2, glyph, sds, fid, mt);

                glyphWidth = gBounds.getWidth()*way;

                //System.out.println("Glyph : curPos:" + currentPos + " w: " + ri.getWidth());
                Point2D.Double pAt = ShapeHelper.getPointAt(shp, currentPos);
                Point2D.Double pAfter = ShapeHelper.getPointAt(shp, currentPos + glyphWidth);

                double theta = Math.atan2(pAfter.y - pAt.y, pAfter.x - pAt.x);

                AffineTransform at = AffineTransform.getTranslateInstance(pAt.x, pAt.y);
                at.concatenate(AffineTransform.getRotateInstance(theta));

                currentPos += glyphWidth;
                outlines.add(label.getOutline(g2, glyph, sds, fid, selected, mt, at, perm));

                //g2.drawRenderedImage(ri , at);
            } else {
                //System.out.println ("Space...");
                //currentPos += emWidth*way;
            }
        }
        label.drawOutlines(g2, outlines, sds, fid, selected, mt);
    }

    @Override
    public JAXBElement<LineLabelType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createLineLabel(this.getJAXBType());
    }

    /**
     * Get a JAXB representation of this {@code LineLabelType}.
     * @return 
     */
    public LineLabelType getJAXBType() {
        LineLabelType ll = new LineLabelType();

        setJAXBProperties(ll);
        return ll;
    }

    @Override
    public String dependsOnFeature() {
        if (label != null) {
            return label.dependsOnFeature();
        }
        return "";
    }
}
