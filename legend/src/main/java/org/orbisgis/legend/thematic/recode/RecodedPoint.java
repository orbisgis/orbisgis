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

import net.opengis.se._2_0.core.ParameterValueType;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.SeExceptions;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.legend.structure.fill.RecodedSolidFillLegend;
import org.orbisgis.legend.structure.recode.*;
import org.orbisgis.legend.structure.recode.type.TypeEvent;
import org.orbisgis.legend.structure.recode.type.TypeListener;
import org.orbisgis.legend.structure.stroke.RecodedPenStroke;
import org.orbisgis.legend.thematic.EnablesStroke;
import org.orbisgis.legend.thematic.OnVertexOnCentroid;
import org.orbisgis.legend.thematic.PointParameters;
import org.orbisgis.legend.thematic.uom.StrokeUom;
import org.orbisgis.legend.thematic.uom.SymbolUom;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Wrapper for unique value classification that are made on PointSymbolizer instances.
 * @author Alexis Guéganno
 */
public class RecodedPoint extends AbstractRecodedLegend<PointParameters>
        implements SymbolUom, EnablesStroke, OnVertexOnCentroid {

    private PointSymbolizer pointSymbolizer;
    private final RecodedSolidFillLegend fill;
    private RecodedPenStroke ps;
    private RecodedString wkn;
    private RecodedReal width;
    private RecodedReal height;
    private static final I18n I18N = I18nFactory.getI18n(RecodedPoint.class);
    public static final String NAME = I18N.tr("Value Classification - Point");

    /**
     * Default constructor : builds an empty classification based on the default {@link org.orbisgis.core.renderer.se.PointSymbolizer}.
     */
    public RecodedPoint(){
        this(new PointSymbolizer());
    }

    /**
     * <p></p>Builds a new {@code RecodedPoint} considering that {@code sym} matches the configuration constraints expected
     * by this class. That means it must be made of a {@link MarkGraphic} containing a {@link PenStroke} and  a
     * {@link SolidFill} and whose shape is configured with a {@code WellKnownName}</p>
     * <p>All of them them must be built with literals or recode instances containing only literals, and that the recode instances are
     * all built with the same analysis field. Use the analyzer dedicated to area to check that automatically.</p>
     * @param symbolizer The base Symbolizer
     */
    public RecodedPoint(PointSymbolizer symbolizer){
        pointSymbolizer = symbolizer;
        MarkGraphic mg = (MarkGraphic) symbolizer.getGraphicCollection().getChildren().get(0);
        Stroke p = mg.getStroke();
        if(p instanceof PenStroke){
            ps=new RecodedPenStroke((PenStroke)p);
        } else if( p == null){
            ps = null;
        } else {
            throw new UnsupportedOperationException("Can't build a RecodedLine with such a Stroke: "+p.getClass().getName());
        }
        Fill originalFill = mg.getFill();
        if(originalFill instanceof SolidFill){
            this.fill = new RecodedSolidFillLegend((SolidFill)originalFill);
        }   else {
            throw new UnsupportedOperationException("Can't build a RecodedLine with such a Fill: "+originalFill.getClass().getName());
        }
        wkn = new RecodedString(mg.getWkn());
        TypeListener tl = new TypeListener() {
            @Override
            public void typeChanged(TypeEvent te) {
                replaceWkn(te.getSource().getParameter());
            }
        };
        wkn.addListener(tl);
        ViewBox vb = mg.getViewBox();
        RealParameter rpw = vb.getWidth();
        RealParameter rph = vb.getHeight();
        try {
            if(rpw == null && rph == null) {
                rpw = new RealLiteral(MarkGraphic.DEFAULT_SIZE);
                rph = new RealLiteral(MarkGraphic.DEFAULT_SIZE);
                vb.setHeight(rph);
                vb.setWidth(rpw);
            } else if(rpw == null){
                ParameterValueType val = rph.getJAXBParameterValueType();
                rpw = SeParameterFactory.createRealParameter(val);
                vb.setWidth(rpw);
            } else if(rph == null){
                ParameterValueType val = rpw.getJAXBParameterValueType();
                rph = SeParameterFactory.createRealParameter(val);
                vb.setHeight(rph);
            }
        } catch (SeExceptions.InvalidStyle invalidStyle) {
            throw new IllegalStateException("We've failed to clone a valid SE Parameter !", invalidStyle);
        }

        width = new RecodedReal(rpw);
        TypeListener tlw = new TypeListener() {
            @Override
            public void typeChanged(TypeEvent te) {
                replaceMarkWidth(te.getSource().getParameter());
            }
        };
        width.addListener(tlw);
        height = new RecodedReal(rph);
        TypeListener tlh = new TypeListener() {
            @Override
            public void typeChanged(TypeEvent te) {
                replaceMarkHeight(te.getSource().getParameter());
            }
        };
        height.addListener(tlh);
        FieldAggregatorVisitor fav = new FieldAggregatorVisitor();
        applyGlobalVisitor(fav);
        String f = getLookupFieldName();
        if(!f.isEmpty()){
            SetFieldVisitor sfv = new SetFieldVisitor(f);
            applyGlobalVisitor(sfv);
        }
    }


    /**
     * Replace the {@code StringParameter} embedded in the inner MarkGraphic with {@code sp}. This method is called
     * when a type change occurs in the associated {@link RecodedString} happens.
     * @param sp The new {@code StringParameter}
     * @throws ClassCastException if sp is not a {@code StringParameter}
     */
    public void replaceWkn(SeParameter sp){
        MarkGraphic mg = (MarkGraphic) pointSymbolizer.getGraphicCollection().getChildren().get(0);
        mg.setWkn((StringParameter) sp);
    }


    /**
     * Replace the {@code RealParameter} embedded in the inner MarkGraphic with {@code sp}. This method is called
     * when a type change occurs in the associated {@link RecodedReal} happens.
     * @param sp The new {@code RealParameter}
     * @throws ClassCastException if sp is not a {@code RealParameter}
     */
    public void replaceMarkWidth(SeParameter sp){
        MarkGraphic mg = (MarkGraphic) pointSymbolizer.getGraphicCollection().getChildren().get(0);
        mg.getViewBox().setWidth((RealParameter) sp);
    }


    /**
     * Replace the {@code RealParameter} embedded in the inner MarkGraphic with {@code sp}. This method is called
     * when a type change occurs in the associated {@link RecodedReal} happens.
     * @param sp The new {@code RealParameter}
     * @throws ClassCastException if sp is not a {@code RealParameter}
     */
    public void replaceMarkHeight(SeParameter sp){
        MarkGraphic mg = (MarkGraphic) pointSymbolizer.getGraphicCollection().getChildren().get(0);
        mg.getViewBox().setHeight((RealParameter) sp);
    }

    @Override
    public Symbolizer getSymbolizer() {
        return pointSymbolizer;
    }

    @Override
    public String getLegendTypeName() {
        return NAME;
    }

    /**
     * Gets the wrapper that manages the width of the line.
     * @return the recoded width
     */
    public RecodedReal getLineWidth(){
        return ps == null ? null :ps.getWidthLegend();
    }

    /**
     * Gets the wrapper that manages the opacity of the line.
     * @return the recoded opacity
     */
    public RecodedReal getLineOpacity(){
        return ps == null ? null :(RecodedReal) ps.getFillLegend().getFillOpacityLegend();
    }

    /**
     * Gets the wrapper that manages the color of the line.
     * @return the recoded color
     */
    public RecodedColor getLineColor(){
        return ps == null ? null :(RecodedColor) ps.getFillLegend().getFillColorLegend();
    }

    /**
     * Gets the wrapper that manages the dash pattern of the line.
     * @return the recoded dash
     */
    public RecodedString getLineDash() {
        return ps == null ? null :ps.getDashLegend();
    }

    /**
     * Gets the wrapper that manages the opacity of the fill.
     * @return the recoded opacity
     */
    public RecodedReal getFillOpacity(){
        return (RecodedReal) fill.getFillOpacityLegend();
    }

    /**
     * Gets the wrapper that manages the color of the fill.
     * @return the recoded color
     */
    public RecodedColor getFillColor(){
        return (RecodedColor) fill.getFillColorLegend();
    }

    /**
     * Gets the wrapper that manages the well-known name that determines the form of the symbol.
     * @return the recoded string
     */
    public RecodedString getWkn(){
        return wkn;
    }

    /**
     * Gets the wrapper that manages width of the symbol.
     * @return the recoded real
     */
    public RecodedReal getSymbolWidth(){
        return width;
    }

    /**
     * Gets the wrapper that manages height of the symbol.
     * @return the recoded real
     */
    public RecodedReal getSymbolHeight(){
        return height;
    }

    @Override
    public Uom getStrokeUom() {
        MarkGraphic mg = (MarkGraphic) pointSymbolizer.getGraphicCollection().getChildren().get(0);
        return ps == null ? Uom.PX : mg.getStroke().getUom();
    }

    @Override
    public void setStrokeUom(Uom u) {
        if (pointSymbolizer == null) {
            System.out.println("Null pointSymbolizer");
        }
        if(ps != null){
            MarkGraphic mg = (MarkGraphic) pointSymbolizer.getGraphicCollection().getChildren().get(0);
            mg.getStroke().setUom(u);
        } else {
            System.out.println("Null ps. Trying another solution.");
            Stroke stroke = ((MarkGraphic) pointSymbolizer.getGraphicCollection().getGraphic(0)).getStroke();
            if (stroke != null){
                stroke.setUom(u);
            } else {
                System.out.println("Null stroke");
            }
        }
    }

    @Override
    public PointParameters get(Object objKey) {
        String key = (String) objKey;
        if(!keySet().contains(key)){
            return null;
        }
        Color sc;
        Double sop;
        Double w;
        String d;
        if(ps !=null){
            sc = getLineColor().getItemValue(key);
            sc = sc==null ? getLineColor().getFallbackValue() : sc;
            sop = getLineOpacity().getItemValue(key);
            sop = sop==null || sop.isNaN() ? getLineOpacity().getFallbackValue() : sop;
            w = getLineWidth().getItemValue(key);
            w = w==null || w.isNaN()? getLineWidth().getFallbackValue() : w;
            d = getLineDash().getItemValue(key);
            d = d==null ? getLineDash().getFallbackValue() : d;
        } else {
            sc=  Color.WHITE;
            sop = 0.0;
            w = 0.0;
            d = "";
        }
        Color fc = getFillColor().getItemValue(key);
        fc = fc==null ? getFillColor().getFallbackValue() : fc;
        Double fop = getFillOpacity().getItemValue(key);
        fop = sop==null || fop.isNaN() ? getFillOpacity().getFallbackValue() : fop;
        String well = wkn.getItemValue(key);
        well = well == null ? wkn.getFallbackValue(): well;
        Double wid = width.getItemValue(key);
        wid = wid == null || wid.isNaN() ? width.getFallbackValue() : wid;
        Double hei = height.getItemValue(key);
        hei = hei == null || hei.isNaN() ? height.getFallbackValue() : hei;
        return new PointParameters(sc,sop,w,d,fc,fop, wid, hei, well);

    }

    @Override
    public PointParameters put(String key, PointParameters value) {
        if(key == null || value == null){
            throw new NullPointerException("We don't manage null as key");
        }
        PointParameters ret = keySet().contains(key) ? get(key) : null;
        if(this.ps != null){
            getLineColor().addItem(key, value.getLineColor());
            getLineOpacity().addItem(key, value.getLineOpacity());
            getLineWidth().addItem(key, value.getLineWidth());
            getLineDash().addItem(key, value.getLineDash());
        }
        getFillColor().addItem(key, value.getFillColor());
        getFillOpacity().addItem(key, value.getFillOpacity());
        wkn.addItem(key, value.getWkn());
        width.addItem(key,value.getWidth());
        height.addItem(key, value.getHeight());
        return ret;
    }

    @Override
    public PointParameters remove(Object objKey) {
        String key = (String) objKey;
        if(key == null){
            throw new NullPointerException("We don't manage null as key");
        }
        PointParameters ret = keySet().contains(key) ? get(key) : null;
        if(this.ps != null){
            getLineColor().removeItem(key);
            getLineDash().removeItem(key);
            getLineOpacity().removeItem(key);
            getLineWidth().removeItem(key);
        }
        getFillColor().removeItem(key);
        getFillOpacity().removeItem(key);
        wkn.removeItem(key);
        width.removeItem(key);
        height.removeItem(key);
        return ret;
    }

    @Override
    public List<RecodedLegend> getRecodedLegends() {
        List<RecodedLegend> psl = ps == null ? new ArrayList<RecodedLegend>() : ps.getRecodedLegends();
        List<RecodedLegend> fsl = fill.getRecodedLegends();
        List<RecodedLegend> ret = new ArrayList<RecodedLegend>(psl.size()+fsl.size()+3);
        ret.addAll(psl);
        ret.addAll(fsl);
        ret.add(wkn);
        ret.add(width);
        ret.add(height);
        return ret;
    }

    @Override
    public PointParameters getFallbackParameters(){
        if(ps == null){
            return new PointParameters(Color.WHITE,
                        0.0,
                        0.0,
                        "",
                        getFillColor().getFallbackValue(),
                        getFillOpacity().getFallbackValue(),
                        width.getFallbackValue(),
                        height.getFallbackValue(),
                        wkn.getFallbackValue());
        }   else {
            return new PointParameters(getLineColor().getFallbackValue(),
                        getLineOpacity().getFallbackValue(),
                        getLineWidth().getFallbackValue(),
                        getLineDash().getFallbackValue(),
                        getFillColor().getFallbackValue(),
                        getFillOpacity().getFallbackValue(),
                        width.getFallbackValue(),
                        height.getFallbackValue(),
                        wkn.getFallbackValue());
        }
    }

    @Override
    public void setFallbackParameters(PointParameters ap){
        if(ps != null){
            getLineColor().setFallbackValue(ap.getLineColor());
            getLineOpacity().setFallbackValue(ap.getLineOpacity());
            getLineWidth().setFallbackValue(ap.getLineWidth());
            getLineDash().setFallbackValue(ap.getLineDash());
        }
        getFillColor().setFallbackValue(ap.getFillColor());
        getFillOpacity().setFallbackValue(ap.getFillOpacity());
        width.setFallbackValue(ap.getWidth());
        height.setFallbackValue(ap.getHeight());
        wkn.setFallbackValue(ap.getWkn());
    }

    @Override
    public int hashCode(){
        int ret = 0;
        Set<Entry<String, PointParameters>> entries = entrySet();
        for(Map.Entry m : entries){
            ret += m.hashCode();
        }
        return ret;
    }

    @Override
    public String getLegendTypeId() {
        return "org.orbisgis.legend.thematic.recode.RecodedPoint";
    }

    @Override
    public boolean isStrokeEnabled(){
        return ps != null;
    }

    @Override
    public void setStrokeEnabled(boolean enable){
        MarkGraphic mg = (MarkGraphic) pointSymbolizer.getGraphicCollection().getChildren().get(0);
        if(enable && ps ==null){
            PenStroke stroke = new PenStroke();
            mg.setStroke(stroke);
            ps = new RecodedPenStroke(stroke);
        } else if(!enable && ps != null){
            mg.setStroke(null);
            ps = null;
        }
    }

    @Override
    public void setOnVertex(){
        pointSymbolizer.setOnVertex(true);
    }

    @Override
    public void setOnCentroid(){
        pointSymbolizer.setOnVertex(false);
    }

    @Override
    public boolean isOnVertex(){
        return pointSymbolizer.isOnVertex();
    }

    @Override
    public Uom getSymbolUom(){
        MarkGraphic mg = (MarkGraphic) pointSymbolizer.getGraphicCollection().getChildren().get(0);
        return mg.getUom();
    }

    @Override
    public void setSymbolUom(Uom u){
        MarkGraphic mg = (MarkGraphic) pointSymbolizer.getGraphicCollection().getChildren().get(0);
        mg.setUom(u);
    }

}
