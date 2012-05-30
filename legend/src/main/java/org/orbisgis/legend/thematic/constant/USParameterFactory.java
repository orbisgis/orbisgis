/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.thematic.constant;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

/**
 * This factory provides useful methods to retrieve the {@code USParamter}
 * associated to {@code UniqueSymbol} instances.
 * @author alexis
 */
public final class USParameterFactory {

    private USParameterFactory(){}

    /**
     * Gets all the parameters that can be associated to this
     * {@code UniqueSymbol}.
     * @param us
     * @return
     */
    public static List<USParameter<?>> getParameters(UniqueSymbol us){
        List<USParameter<?>> ret = new LinkedList<USParameter<?>>();
        if(us instanceof IUniqueSymbolLine){
            ret.addAll(getParametersLine((IUniqueSymbolLine)us));
        }
        if(us instanceof IUniqueSymbolArea){
            ret.addAll(getParametersArea((IUniqueSymbolArea)us));
        }
        if(us instanceof UniqueSymbolPoint){
            ret.addAll(getParametersPoint((UniqueSymbolPoint)us));
        }
        return ret;
    }

    /**
     * Gets all the parameters that can be associated to this
     * {@code IUniqueSymbolLine}.
     * @param us
     * @return
     */
    public static List<USParameter<?>> getParametersLine(final IUniqueSymbolLine us){
        LinkedList<USParameter<?>> ll = new LinkedList<USParameter<?>>();
        //The line width
        ll.add(new USNumericParameter<Double>("Line Width") {

            @Override
            public Double getValue() {return us.getLineWidth();}

            @Override
            public void setValue(Double obj) {us.setLineWidth(obj);}

            @Override
            public Double getMinValue(){ return 0.0;}

            @Override
            public Double getMaxValue(){ return Double.POSITIVE_INFINITY;}


        });
        //The line color
        ll.add(new USParameter<Color>("Line Color") {
            @Override
            public Color getValue() {return us.getLineColor();}
            @Override
            public void setValue(Color obj) {us.setLineColor(obj);}
        });
        //The dash array
        ll.add(new USParameter<String>("Dash array") {
            @Override
            public String getValue() {return us.getDashArray();}
            @Override
            public void setValue(String obj) {us.setDashArray(obj);}
        });
        return ll;
    }

    /**
     * Gets all the parameters that can be associated to this
     * {@code IUniqueSymbolArea}.
     * @param us
     * @return
     */
    public static List<USParameter<?>> getParametersArea(final IUniqueSymbolArea us){
        LinkedList<USParameter<?>> ll = new LinkedList<USParameter<?>>();
        //The Fill color
        ll.add(new USParameter<Color>("Fill Color") {
            @Override
            public Color getValue() {return us.getFillColor();}
            @Override
            public void setValue(Color obj) {us.setFillColor(obj);}
        });
        return ll;
    }

    /**
     * Gets all the parameters that can be associated to this
     * {@code UniqueSymbolPoint}.
     * @param us
     * @return
     */
    public static List<USParameter<?>> getParametersPoint(final UniqueSymbolPoint us){
        LinkedList<USParameter<?>> ll = new LinkedList<USParameter<?>>();
        //The symbol width
        ll.add(new USNumericParameter<Double>("Symbol width") {
            @Override
            public Double getValue() {
                return us.getViewBoxWidth() == null ? us.getViewBoxHeight() : us.getViewBoxWidth();
            }
            @Override
            public void setValue(Double obj) {us.setViewBoxWidth(obj);}
            @Override
            public Double getMinValue(){ return Double.NEGATIVE_INFINITY;}
            @Override
            public Double getMaxValue(){ return Double.POSITIVE_INFINITY;}
        });
        //The symbol width
        ll.add(new USNumericParameter<Double>("Symbol height") {
            @Override
            public Double getValue() {
                return us.getViewBoxHeight() == null ? us.getViewBoxWidth() : us.getViewBoxHeight();
            }
            @Override
            public void setValue(Double obj) {us.setViewBoxHeight(obj);}
            @Override
            public Double getMinValue(){ return Double.NEGATIVE_INFINITY;}
            @Override
            public Double getMaxValue(){ return Double.POSITIVE_INFINITY;}
        });
        return ll;
    }

}
