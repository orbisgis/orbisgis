package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import java.util.ArrayList;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.common.PieChartType;
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
     * @param g2
     * @param ds
     * @param fid
     */
    @Override
    public void drawGraphic(Graphics2D g2, DataSource ds, int fid){
        // TODO IMPLEMENT drawGraphic
        /**
         * double[] values = new double[nSlices];
         * foreach slice  in slices do
         *    values[i] = slices.get(i).getValue(ds, fid);
         *    total += values[i];
         * end
         *
         * compute each angle, create shape for slices (with AT for gaps)
         * draw each slice
         */
    }


    private PieChartType type;
    private RealParameter radius;
    private RealParameter holeRadius;
    private boolean displayValue;
    private Stroke stroke;

    private ArrayList<Slice> slices;
}
