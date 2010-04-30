package org.orbisgis.core.renderer.se.parameter;

import java.util.ArrayList;
import java.util.Collections;

import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 * @param <ToType> One of RealParameter or ColorParameter
 * @param <FallbackType> extends ToType (the LirealOne, please)...
 * @todo find a nice way to compute interpolation for RealParameter and ColorParameter
 *
 */
public abstract class Interpolate<ToType extends SeParameter, FallbackType extends ToType> implements SeParameter {


    public Interpolate(FallbackType fallbackValue){
        this.fallbackValue = fallbackValue;
        this.i_points =  new ArrayList<InterpolationPoint<ToType>>();
    }

        @Override
    public final boolean dependsOnFeature(){
        if (this.getLookupValue().dependsOnFeature())
            return true;

        int i;
        for (i=0;i<this.getNumInterpolationPoint();i++){
           if (this.getInterpolationPoint(i).getValue().dependsOnFeature())
               return true;
        }

        return false;
    }


    public void setFallbackValue(FallbackType fallbackValue){
        this.fallbackValue = fallbackValue;
    }

    public FallbackType getFallbackValue(){
        return fallbackValue;
    }

    public void setLookupValue(RealParameter lookupValue){
        this.lookupValue = lookupValue;
    }

    public RealParameter getLookupValue(){
        return lookupValue;
    }

    /**
     * Return the number of classes defined within the classification. According to this number (n),
     *  available class value ID are [0;n] and ID for threshold are [0;n-1
     *
     *  @return number of defined class
     */
    public int getNumInterpolationPoint(){
        return i_points.size();
    }

    /**
     * Add a new interpolation point. 
     * The new point is inserted at the right place in the interpolation point list, according to its data
     *
     */
    public void addInterpolationPoint(InterpolationPoint<ToType> point){
        i_points.add(point);
        sortInterpolationPoint();
    }

    public InterpolationPoint<ToType> getInterpolationPoint(int i){
        return i_points.get(i);
    }

    public void setInterpolationMode(InterpolationMode mode){
        this.mode = mode;
    }

    public InterpolationMode getInterpolationMode(){
        return mode;
    }


    private void sortInterpolationPoint(){
        Collections.sort(i_points);
    }

    private InterpolationMode mode;
    private RealParameter lookupValue;
    private FallbackType fallbackValue;

    private ArrayList<InterpolationPoint<ToType>> i_points;
    
}
