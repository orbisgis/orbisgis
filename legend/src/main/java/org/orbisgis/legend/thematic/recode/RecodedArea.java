/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.legend.thematic.recode;

import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.legend.structure.fill.RecodedSolidFillLegend;
import org.orbisgis.legend.structure.recode.*;
import org.orbisgis.legend.structure.stroke.RecodedPenStroke;
import org.orbisgis.legend.thematic.AreaParameters;
import sun.swing.StringUIClientPropertyKey;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for unique value classification that are made on AreaSymbolizer instances.
 * @author Alexis Guéganno
 */
public class RecodedArea extends AbstractRecodedLegend<AreaParameters>  {

    private AreaSymbolizer areaSymbolizer;
    private final RecodedSolidFillLegend fill;
    private final RecodedPenStroke ps;

    /**
     * Builds a new {@code RecodedArea} considering that {@code sym} matches the configuration constraints expected
     * by this class. That means it must be made of a {@link PenStroke} and of a {@link SolidFill} and that both of
     * them are built with literals or recode instances containing only literals, and that the recode instances are
     * all built with the same analysis field. Use the analyzer dedicated to area to check that automatically.
     * @param sym The base Symbolizer
     */
    public RecodedArea(AreaSymbolizer sym){
        areaSymbolizer=sym;
        Stroke p = areaSymbolizer.getStroke();
        if(p instanceof PenStroke){
            ps=new RecodedPenStroke((PenStroke)p);
        } else {
            throw new UnsupportedOperationException("Can't build a RecodedLine with such a Stroke: "+p.getClass().getName());
        }
        Fill fill = areaSymbolizer.getFill();
        if(fill instanceof SolidFill){
            this.fill = new RecodedSolidFillLegend((SolidFill)fill);
        }   else {
            throw new UnsupportedOperationException("Can't build a RecodedLine with such a Fill: "+fill.getClass().getName());
        }
        FieldAggregatorVisitor fav = new FieldAggregatorVisitor();
        applyGlobalVisitor(fav);
        String f = getLookupFieldName();
        if(!f.isEmpty()){
            SetFieldVisitor sfv = new SetFieldVisitor(f);
            applyGlobalVisitor(sfv);
        }
    }

    @Override
    public Symbolizer getSymbolizer() {
        return areaSymbolizer;
    }

    @Override
    public String getLegendTypeName() {
        return "Value Classification - Area";
    }

    /**
     * Gets the wrapper that manages the width of the line.
     * @return the recoded width
     */
    public RecodedReal getLineWidth(){
        return ps.getWidthLegend();
    }

    /**
     * Gets the wrapper that manages the opacity of the line.
     * @return the recoded opacity
     */
    public RecodedReal getLineOpacity(){
        return (RecodedReal) ps.getFillLegend().getFillOpacityLegend();
    }

    /**
     * Gets the wrapper that manages the color of the line.
     * @return the recoded color
     */
    public RecodedColor getLineColor(){
        return (RecodedColor) ps.getFillLegend().getFillColorLegend();
    }

    /**
     * Gets the wrapper that manages the dash pattern of the line.
     * @return the recoded dash
     */
    public RecodedString getLineDash() {
        return ps.getDashLegend();
    }

    /**
     * Gets the wrapper that manages the opacity of the line.
     * @return the recoded opacity
     */
    public RecodedReal getFillOpacity(){
        return (RecodedReal) fill.getFillOpacityLegend();
    }

    /**
     * Gets the wrapper that manages the color of the line.
     * @return the recoded color
     */
    public RecodedColor getFillColor(){
        return (RecodedColor) fill.getFillColorLegend();
    }

    @Override
    public AreaParameters get(Object objKey) {
        String key = (String) objKey;
        Color sc = getLineColor().getItemValue(key);
        sc = sc==null ? getLineColor().getFallbackValue() : sc;
        Double sop = getLineOpacity().getItemValue(key);
        sop = sop==null || sop.isNaN() ? getLineOpacity().getFallbackValue() : sop;
        Double w = getLineWidth().getItemValue(key);
        w = w==null || w.isNaN()? getLineWidth().getFallbackValue() : w;
        String d = getLineDash().getItemValue(key);
        d = d==null ? getLineDash().getFallbackValue() : d;
        Color fc = getFillColor().getItemValue(key);
        fc = fc==null ? getFillColor().getFallbackValue() : fc;
        Double fop = getFillOpacity().getItemValue(key);
        fop = sop==null || fop.isNaN() ? getFillOpacity().getFallbackValue() : fop;
        return new AreaParameters(sc,sop,w,d,fc,fop);

    }

    @Override
    public AreaParameters put(String key, AreaParameters value) {
        if(key == null || value == null){
            throw new NullPointerException("We don't manage null as key");
        }
        AreaParameters ret = keySet().contains(key) ? get(key) : null;
        getLineColor().addItem(key, value.getLineColor());
        getLineOpacity().addItem(key, value.getLineOpacity());
        getLineWidth().addItem(key, value.getLineWidth());
        getLineDash().addItem(key, value.getLineDash());
        getFillColor().addItem(key, value.getFillColor());
        getFillOpacity().addItem(key, value.getFillOpacity());
        return ret;
    }

    @Override
    public AreaParameters remove(Object objKey) {
        String key = (String) objKey;
        if(key == null){
            throw new NullPointerException("We don't manage null as key");
        }
        AreaParameters ret = keySet().contains(key) ? get(key) : null;
        getLineColor().removeItem(key);
        getLineDash().removeItem(key);
        getLineOpacity().removeItem(key);
        getLineWidth().removeItem(key);
        getFillColor().removeItem(key);
        getFillOpacity().removeItem(key);
        return ret;
    }

    @Override
    public List<RecodedLegend> getRecodedLegends() {
        List<RecodedLegend> psl = ps.getRecodedLegends();
        List<RecodedLegend> fsl = fill.getRecodedLegends();
        List<RecodedLegend> ret = new ArrayList<RecodedLegend>(psl.size()+fsl.size());
        ret.addAll(psl);
        ret.addAll(fsl);
        return ret;
    }

    /**
     * Gets the configuration used for the line style used to draw features we can't get a value for in the map.
     * @return
     */
    public AreaParameters getFallbackParameters(){
        return new AreaParameters(getLineColor().getFallbackValue(),
                    getLineOpacity().getFallbackValue(),
                    getLineWidth().getFallbackValue(),
                    getLineDash().getFallbackValue(),
                    getFillColor().getFallbackValue(),
                    getFillOpacity().getFallbackValue());
    }
}
