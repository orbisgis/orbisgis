package org.orbisgis.core.renderer.se.fill;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;

import java.io.IOException;

import javax.media.jai.RenderableGraphics;

import org.gdms.data.DataSource;

import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;

public class DensityFill extends Fill {

    public void setHatches(PenStroke hatches) {
        this.hatches = hatches;
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
    public void draw(Graphics2D g2, Shape shp, DataSource ds, long fid) throws ParameterException, IOException {
        double percentage = 0.0;

        if (percentageCovered != null) {
            percentageCovered.getValue(ds, fid);
        }

        if (percentage > 100) {
            percentage = 100;
        }

        if (percentage > 0.0) {// nothing to draw (TODO compare with an epsilon !!)
            Paint painter = null;

            if (isHatched && hatches != null) {
                double angle = -45.0;

                if (this.orientation != null) {
                    angle = this.orientation.getValue(ds, fid);
                }

                // TODO IMPLEMENT: create TexturePaint, see GraphicFill.getTexturePaint
                painter = null;

            } else if (mark != null) { // Marked
                RenderableGraphics g = mark.getGraphic(ds, fid);
                painter = null; // TODO IMPLEMENT: create TexturePaint, see GraphicFill.getTexturePaint
            } else {
                throw new ParameterException("Neither marks or hatches are defined");
            }

            if (painter != null) {
                g2.setPaint(painter);
                g2.fill(shp);
            }
        }
    }
    private boolean isHatched;
    private PenStroke hatches;
    private RealParameter orientation;
    private GraphicCollection mark;
    private RealParameter percentageCovered;
}
