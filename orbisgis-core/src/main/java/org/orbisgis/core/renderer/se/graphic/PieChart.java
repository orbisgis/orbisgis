package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.media.jai.RenderableGraphics;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.common.PieChartType;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.Stroke;

public class PieChart extends Graphic{

    public int getNumSlices(){
        return slices.size();
    }

    public Slice getSlice(int i){
        if (i>=0 && i < getNumSlices()){
          return slices.get(i);
        }
        else {
            return null;
        }
    }
    public void addSlice(Slice slice){
        if (slice != null){
            slices.add(slice);
            slice.setParent(this);
        }
    }

    public void moveSliceUp(int i){
        // déplace i vers i-1
        if (slices.size() > 1){
            if (i > 0 && i < slices.size()){
                Slice tmp = slices.get(i);
                slices.set(i, slices.get(i-1));
                slices.set(i-1, tmp);
            }
            else{
                // TODO throw
            }
        }
    }

    public void moveSliceDown(int i){
        // déplace i vers i+1
        if (slices.size() > 1){
            if (i >= 0 && i < slices.size() -1 ){
                Slice tmp = slices.get(i);
                slices.set(i, slices.get(i+1));
                slices.set(i+1, tmp);
            }
            else{
                // TODO throw
            }
        }
    }

    public boolean isDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(boolean displayValue) {
        this.displayValue = displayValue;
    }

    public RealParameter getHoleRadius() {
        return holeRadius;
    }

    public void setHoleRadius(RealParameter holeRadius) {
        this.holeRadius = holeRadius;
    }

    public RealParameter getRadius() {
        return radius;
    }

    public void setRadius(RealParameter radius) {
        this.radius = radius;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
        stroke.setParent(this);
    }

    public PieChartType getType() {
        return type;
    }

    public void setType(PieChartType type) {
        this.type = type;
    }

    
    /**
     *
     * @param ds
     * @param fid
     */
    @Override
    public RenderableGraphics getRenderableGraphics(DataSource ds, int fid) throws ParameterException{
        // TODO IMPLEMENT drawGraphic

        int nSlices = slices.size();

        double total = 0.0;
        double[] values = new double[nSlices];
        double[] stackedValues = new double[nSlices];
        double[] gaps = new double[nSlices];

        double r;

        if (radius != null){
            r = Uom.toPixel(this.getRadius().getValue(ds, fid),
                            this.getUom(),
                            96,
                            25000); // TODO DPI + SCALE
        }
        else{
            r = 10;
        }

        double maxGap = 0.0;

        for (int i=0;i<nSlices;i++){
            Slice slc = slices.get(i);
            values[i] = slc.getValue().getValue(ds, fid);
            total += values[i];
            stackedValues[i] = total;
            RealParameter gap = slc.getGap();
            if (gap != null){
                gaps[i] = Uom.toPixel(slc.getGap().getValue(ds, fid), this.getUom(), 96, 25000);
            }
            else{
                gaps[i] = 0.0;
            }

            if (gaps[i] > maxGap){
                maxGap = gaps[i];
            }
        }

        double imageWidth = r + maxGap;

        double[] px = new double[nSlices];
        double[] py = new double[nSlices];

        // Now, the total is defines, we can compute percentages and slices begin and end
        double[] percentages = new double[nSlices];

        for (int i=0;i<nSlices;i++){
            if (i==0)
                percentages[i] = 0.0;
            else
                percentages[i] = stackedValues[(i-1 + nSlices) % nSlices] / total;

            px[i] = Math.cos(percentages[i]*2*Math.PI);
            py[i] = Math.sin(percentages[i]*2*Math.PI);
        }

        // Create BufferedImage imageWidth x imageWidth
        
        // Create slices
        for (int i=0;i<nSlices;i++){
            /*
            Shape gSlc = new Arc2D.Double(...,
                                          percentages[i]*360.0,
                                          percentages[(i+1)%nSlices]*360.0,
                                          Arc2D.PIE);
             */

            // Create AT for the gap
            // double gap_x = Math.cos((pStart + pEnd)/2.0)*gaps[i];
            // double gap_y = Math.sin((pStart + pEnd)/2.0)*gaps[i];

        }
        
        /* compute each angle, create shape for slices (with AT for gaps)
         * draw each slice
         */

        return null;
    }


    @Override
    public double getMaxWidth(DataSource ds, int fid) throws ParameterException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }



    private PieChartType type;
    private RealParameter radius;
    private RealParameter holeRadius;
    private boolean displayValue;
    private Stroke stroke;

    private ArrayList<Slice> slices;
}
