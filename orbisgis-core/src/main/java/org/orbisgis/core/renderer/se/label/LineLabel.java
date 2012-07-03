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
import java.util.HashSet;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.LineLabelType;
import net.opengis.se._2_0.core.ObjectFactory;
import org.gdms.data.DataSource;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.RenderContext;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.RelativeOrientation;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 * A {@code LineLabel} is a text of some kinf associated to a Line (polygon or not).
 * @author alexis, maxence
 * @todo implements
 */
public class LineLabel extends Label {

    private RelativeOrientation orientation;

    /**
         * Build a new default {@code LineLabel}, using the defaults in 
         * {@link org.orbisgis.core.renderer.se.label.StyledText#StyledText()  StyledText}.
         * The label will be centered (horizontally), and in the middle (vertically)
         * of the graphic.
         */
    public LineLabel() {
        super();
        setVerticalAlign(VerticalAlignment.MIDDLE);
        setHorizontalAlign(HorizontalAlignment.CENTER);
    }

    /**
     * Build a {@code LineLabel} from a {@code LineLabelType}
     * @param t
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public LineLabel(LineLabelType t) throws InvalidStyle {
        super(t);
        if(t.getRelativeOrientation() != null){
            setOrientation(RelativeOrientation.readFromToken(t.getRelativeOrientation()));
        }
    }

    /**
     * Build a {@code LineLabel} from a JAXBElement.
     * @param l
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public LineLabel(JAXBElement<LineLabelType> l) throws InvalidStyle {
        this(l.getValue());
    }

    /**
     * Gets the orientation of the characters along the line.
     */
    public final RelativeOrientation getOrientation() {
        return orientation;
    }

    /**
     * Sets the orientation of the characters along the line.
     * @param orientation
     */
    public final void setOrientation(RelativeOrientation orientation) {
        this.orientation = orientation;
    }

    /**
     *
     */
    @Override
    public void draw(Graphics2D g2, DataSource sds, long fid, 
            Shape shp, boolean selected, MapTransform mt, RenderContext perm)
            throws ParameterException, IOException {

        Rectangle2D bounds = getLabel().getBounds(g2, sds, fid, mt);
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

        VerticalAlignment vA = getVerticalAlign();
        HorizontalAlignment hA = getHorizontalAlign();
        RelativeOrientation ra = getOrientation();

        if (vA == null) {
            vA = VerticalAlignment.TOP;
            //The four important lines, here, according to the SE norm, are the
            //middle line, the baseline, the ascent line and the descent line.
        }
        if (hA == null) {
            hA = HorizontalAlignment.CENTER;
        }
        if(ra == null) {
            ra= RelativeOrientation.NORMAL_UP;
        }
        double lineLength = ShapeHelper.getLineLength(shp);
        double startAt;
        double stopAt;
        switch (hA) {
            case RIGHT:
                startAt = lineLength - totalWidth;
                stopAt = lineLength;
                break;
            case LEFT:
                startAt = 0.0;
                stopAt = totalWidth;
                break;
            default:
            case CENTER:
                startAt = (lineLength - totalWidth) / 2.0;
                stopAt = (lineLength + totalWidth) / 2.0;
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
        // Do not laid out the label upside-down !
                if (ptStart.x > ptStop.x){
                // invert line way
                way = -1;
                startAt = stopAt;
                }

        double currentPos = startAt;
        double glyphWidth;

        String text = getLabel().getText().getValue(sds, fid);
        String[] glyphs = text.split("");

        ArrayList<Shape> outlines = new ArrayList<Shape>();

        for (String glyph : glyphs) {
            if (glyph != null && !glyph.isEmpty()) {
                Rectangle2D gBounds = getLabel().getBounds(g2, glyph, sds, fid, mt);

                glyphWidth = gBounds.getWidth()*way;
                Point2D.Double pAt = ShapeHelper.getPointAt(shp, currentPos);
                Point2D.Double pAfter = ShapeHelper.getPointAt(shp, currentPos + glyphWidth);
                //We compute the angle we must use to rotate our glyph.
                double theta = Math.atan2(pAfter.y - pAt.y, pAfter.x - pAt.x);
                //We compute the place where we will draw the chatacter, and
                //the orientation it must have.
                AffineTransform at = AffineTransform.getTranslateInstance(pAt.x, pAt.y);
                at.concatenate(AffineTransform.getRotateInstance(theta));
                currentPos += glyphWidth;
                outlines.add(getLabel().getOutline(g2, glyph, sds, fid, mt, at, perm, vA));
            } else {
                //System.out.println ("Space...");
                //currentPos += emWidth*way;
            }
        }
        getLabel().drawOutlines(g2, outlines, sds, fid, selected, mt);
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
    public HashSet<String> dependsOnFeature() {
        if (getLabel() != null) {
            return getLabel().dependsOnFeature();
        }
        return new HashSet<String>();
    }
}