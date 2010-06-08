package org.orbisgis.core.renderer.se.fill;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.io.IOException;

import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.DensityFillType;

import org.orbisgis.core.renderer.persistance.se.ObjectFactory;

import org.gdms.data.DataSource;

import org.orbisgis.core.renderer.se.common.MapEnv;

import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.color.ColorHelper;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;

public class DensityFill extends Fill {

    public DensityFill(){
    }

    DensityFill(JAXBElement<DensityFillType> f) {

        DensityFillType t = f.getValue();

        if (t.getPenStroke() != null){
            this.setHatches(new PenStroke(t.getPenStroke()));

            if (t.getOrientation() != null){
                this.setHatchesOrientation(SeParameterFactory.createRealParameter(t.getOrientation()));
            }
        }
        else{
            t.getGraphic(); // TODO hangle grain
        }

        if (t.getPercentage() != null){
            this.setPercentageCovered(SeParameterFactory.createRealParameter(t.getPercentage()));
        }
    }

    public void setHatches(PenStroke hatches) {
        this.hatches = hatches;
        this.isHatched = true;
        hatches.setParent(this);
    }

    public PenStroke getHatches() {
        return hatches;
    }

    /**
     *
     * @param orientation angle in deegree 
     */
    public void setHatchesOrientation(RealParameter orientation) {
        this.orientation = orientation;
    }

    public RealParameter getHatchesOrientation() {
        return orientation;
    }

    public void setMark(GraphicCollection mark) {
        this.mark = mark;
        this.isHatched = false;
        mark.setParent(this);
    }

    public GraphicCollection getMark() {
        return mark;
    }

    public void useMarks() {
        isHatched = false;
    }

    public boolean useHatches() {
        return isHatched;
    }

    /**
     *
     * @param percent percentage covered by the marks/hatches [0;100]
     */
    public void setPercentageCovered(RealParameter percent) {
        this.percentageCovered = percent;
    }

    public RealParameter getPercentageCovered() {
        return percentageCovered;
    }

    /**
     *
     * @param g2 draw within this graphics2d
     * @param shp fill this shape
     * @param ds feature came from this datasource
     * @param fid id of the feature to draw
     * @throws IOException
     */
    @Override
    public void draw(Graphics2D g2, Shape shp, DataSource ds, long fid, boolean selected) throws ParameterException, IOException {
        double percentage = 0.0;

        if (percentageCovered != null) {
            percentage = percentageCovered.getValue(ds, fid);
        }

        if (percentage > 100) {
            percentage = 100;
        }

        if (percentage > 0.0) {// nothing to draw (TODO compare with an epsilon !!)
            Paint painter = null;

            if (isHatched && hatches != null) {
                double theta = -45.0;

                if (this.orientation != null) {
                    theta = -this.orientation.getValue(ds, fid) + 90.0;
                }

                theta *= Math.PI / 180.0;

                // Stroke width
                double sWidth = hatches.getMaxWidth(ds, fid);

                // Perpendiculat dist bw two hatches
                double pDist = 100 * sWidth / percentage;


                double cosTheta = Math.cos(theta);
                double sinTheta = Math.sin(theta);

                double dx;
                double dy;

                int ix;
                int iy;

                // avoid div by zero
                if (Math.abs(sinTheta) < 0.001) {
                    dx = 0;
                    ix = (int) pDist;
                } else {
                    dx = pDist / sinTheta;
                    ix = (int) dx;
                }

                if (Math.abs(cosTheta) < 0.001) {
                    dy = 0;
                    iy = (int) pDist;
                } else {
                    dy = pDist / cosTheta;
                    iy = (int) dy;
                }

                // Hatch delta x & y
                int idx = (int) dx;
                int idy = (int) dy;

                // Tile size is always absolute
                ix = Math.abs(ix);
                iy = Math.abs(iy);


                BufferedImage i = new BufferedImage(ix, iy, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D tile = i.createGraphics();

                g2.setRenderingHints(MapEnv.getCurrentRenderContext().getRenderingHints());

                Color c = hatches.getColor().getColor(ds, fid);
                
                if (selected){
                    c = ColorHelper.invert(c);
                }

                tile.setColor(c);

                tile.setStroke(hatches.getBasicStroke(ds, fid));

                // Draw three line in order to ensure mosaic join
                
                int ipDist = (int)pDist;

                if (idx == 0) { // V-Hatches
                    tile.drawLine(0, -idy, 0, idy);
                    tile.drawLine(ipDist, -idy, ipDist, idy);
                } else if (idy == 0) { // H-Hatches
                    tile.drawLine(-idx, 0, idx, 0);
                    tile.drawLine(-idx, ipDist, idx, ipDist);
                } else {
                    tile.drawLine(-idx, -idy, idx, idy);
                    tile.drawLine(0, -idy, 2 * idx, idy);
                    tile.drawLine(-idx, 0, idx, 2 * idy);
                }


                painter = new TexturePaint(i, new Rectangle2D.Double(0, 0, ix, iy));

            } else if (mark != null) { // Marked
                RenderableGraphics g = mark.getGraphic(ds, fid, selected);

                if (g != null){
                    // TODO IMPLEMENT: create TexturePaint, see GraphicFill.getTexturePaint
                    // painter = new TexturePaint(...);
                }
            } else {
                throw new ParameterException("Neither marks or hatches are defined");
            }

            if (painter != null) {
                g2.setPaint(painter);
                g2.fill(shp);
            }
        }
    }


    @Override
    public DensityFillType getJAXBType(){
        DensityFillType f = new DensityFillType();

        if (isHatched){
            if (hatches != null){
                f.setPenStroke(hatches.getJAXBType());
            }
            if (orientation != null){
                f.setOrientation(orientation.getJAXBParameterValueType());
            }
        }
        else{
            if (mark != null){
                f.setGraphic(mark.getJAXBElement());
            }
        }

        if (percentageCovered != null){
            f.setPercentage(percentageCovered.getJAXBParameterValueType());
        }

        return f;
    }


    @Override
    public JAXBElement<DensityFillType> getJAXBElement(){
        ObjectFactory of = new ObjectFactory();
        return of.createDensityFill(this.getJAXBType());
    }


    private boolean isHatched;
    private PenStroke hatches;
    private RealParameter orientation;
    private GraphicCollection mark;
    private RealParameter percentageCovered;
}
