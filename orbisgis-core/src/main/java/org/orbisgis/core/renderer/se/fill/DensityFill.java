package org.orbisgis.core.renderer.se.fill;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;

public class DensityFill extends Fill{

    public void setHatches(PenStroke hatches){
        this.hatches = hatches;
        hatches.setParent(this);
    }

    public PenStroke getHatches(){
        return hatches;
    }

    /**
     *
     * @param orientation angle in deegree 
     */
    public void setHatchesOrientation(RealParameter orientation){
        this.orientation = orientation;
    }

    public RealParameter getHatchesOrientation(){
        return orientation;
    }


    public void setMark(GraphicCollection mark){
        this.mark = mark;
        mark.setParent(this);
    }

    public GraphicCollection getMark(){
        return mark;
    }

    public void useMarks(){
        isHatched = false;
    }

    public boolean useHatches(){
        return isHatched;
    }


    /**
     *
     * @param percent percentage covered by the marks/hatches [0.0;1.0]
     */
    public void setPercentageCovered(RealParameter percent){
        this.percentageCovered = percent;
    }

    public RealParameter getPercentageCovered(){
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
    public void draw(Graphics2D g2, Shape shp, DataSource ds, int fid) throws ParameterException, IOException {
        double percentage = percentageCovered.getValue(ds, fid);

        if (percentage < 0.0)
            percentage = 0.0; // nothing to draw (compare with an epsilon !!)
        if (percentage > 1)
            percentage = 1.0;

        if (isHatched && hatches != null){
            double angle;
            
            if (this.orientation == null)
                angle = -45;
            else
                angle = this.orientation.getValue(ds, fid);

            // TODO IMPLEMENT

        }
        else if(mark  != null){ // Marked
            BufferedImage g = mark.getGraphic(ds, fid);
            // TODO IMPLEMENT
        }
        else{
            // throw something... ("Neither marks or hatches are defined");
        }
    }

    private boolean isHatched;

    private PenStroke hatches;
    private RealParameter orientation;

    private GraphicCollection mark;

    private RealParameter percentageCovered;

}
