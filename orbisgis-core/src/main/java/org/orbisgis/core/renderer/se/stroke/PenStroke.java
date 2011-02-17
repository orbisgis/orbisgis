/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */



package org.orbisgis.core.renderer.se.stroke;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.persistance.se.PenStrokeType;

import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

/**
 * Basic stroke for linear features
 * @author maxence
 */
public final class PenStroke extends Stroke implements FillNode {

    private static LineCap DEFAULT_CAP = LineCap.BUTT;
    private static LineJoin DEFAULT_JOIN = LineJoin.ROUND;

    private Fill fill;
    //private RealParameter opacity;
    private RealParameter width;
    private LineJoin lineJoin;
    private LineCap lineCap;
    private StringParameter dashArray;
    private RealParameter dashOffset;

    //private BasicStroke bStroke;
    public enum LineCap {

        BUTT, ROUND, SQUARE;

        public ParameterValueType getParameterValueType() {
            return SeParameterFactory.createParameterValueType(this.name().toLowerCase());
        }
    }

    public enum LineJoin {

        MITRE, ROUND, BEVEL;

        public ParameterValueType getParameterValueType() {
            return SeParameterFactory.createParameterValueType(this.name().toLowerCase());
        }
    }

    /**
     * Create a standard undashed 0.1mm-wide opaque black stroke
     */
    public PenStroke() {
        setFill(new SolidFill(Color.BLACK, 100.0));
        setWidth(new RealLiteral(0.1));
        //setOpacity(new RealLiteral(100.0));

        setUom(null);

        setDashArray(null);
        setDashOffset(null);

        setLineCap(null);
        setLineJoin(null);

        //updateBasicStroke();
    }

    /**
     * @param t
     */
    public PenStroke(PenStrokeType t) throws InvalidStyle {
        this();

        if (t.getFill() != null) {
            this.setFill(Fill.createFromJAXBElement(t.getFill()));
        }

        if (t.getDashArray() != null) {
            this.setDashArray(SeParameterFactory.createStringParameter(t.getDashArray()));
        }

        if (t.getDashOffset() != null) {
            this.setDashOffset(SeParameterFactory.createRealParameter(t.getDashOffset()));
        }

        if (t.getWidth() != null) {
            this.setWidth(SeParameterFactory.createRealParameter(t.getWidth()));
        }

        if (t.getLineCap() != null) {
            try {
                StringParameter lCap = SeParameterFactory.createStringParameter(t.getLineCap());
                this.setLineCap(LineCap.valueOf(lCap.getValue(null, -1).toUpperCase()));
            } catch (Exception ex) {
                Logger.getLogger(PenStroke.class.getName()).log(Level.SEVERE, "Could not convert line cap", ex);
            }
        }

        if (t.getLineJoin() != null) {
            try {
                StringParameter lJoin = SeParameterFactory.createStringParameter(t.getLineJoin());
                this.setLineJoin(LineJoin.valueOf(lJoin.getValue(null, -1).toUpperCase()));
            } catch (Exception ex) {
                Logger.getLogger(PenStroke.class.getName()).log(Level.SEVERE, "Could not convert line join", ex);
            }
        }

        /*if (t.getOpacity() != null) {
        this.setOpacity(SeParameterFactory.createRealParameter(t.getOpacity()));
        }*/

        if (t.getUnitOfMeasure() != null) {
            this.setUom(Uom.fromOgcURN(t.getUnitOfMeasure()));
        } else {
            this.uom = null;
        }

        //this.updateBasicStroke();
    }

    public PenStroke(JAXBElement<PenStrokeType> s) throws InvalidStyle {
        this(s.getValue());
    }

    @Override
    public String dependsOnFeature() {
        String result = "";

        if (fill != null) {
            result = fill.dependsOnFeature();
        }
        if (dashOffset != null) {
            result +=  " " + dashOffset.dependsOnFeature();
        }
        if (dashArray != null) {
            result += " " + dashArray.dependsOnFeature();
        }
        if (width != null){
            result  +=  " " + width.dependsOnFeature();
        }

        return result.trim();
    }

    @Override
    public Fill getFill() {
        return fill;
    }

    @Override
    public void setFill(Fill fill) {
        if (fill != null) {
            fill.setParent(this);
        }
        this.fill = fill;
    }

    public void setLineCap(LineCap cap) {
        lineCap = cap;
        //updateBasicStroke();
    }

    public LineCap getLineCap() {
        if (lineCap != null){
            return lineCap;
        } else {
            return DEFAULT_CAP;
        }
    }

    public void setLineJoin(LineJoin join) {
        lineJoin = join;
        //updateBasicStroke();
    }

    public LineJoin getLineJoin() {
        if (lineJoin != null)
            return lineJoin;
        else 
            return DEFAULT_JOIN;

    }

    /*public void setOpacity(RealParameter opacity) {
    this.opacity = opacity;

    if (opacity != null) {
    this.opacity.setContext(RealParameterContext.percentageContext);
    }
    //updateBasicStroke();
    }

    public RealParameter getOpacity() {
    return this.opacity;
    }*/
    public void setWidth(RealParameter width) {
        this.width = width;

        if (width != null) {
            width.setContext(RealParameterContext.nonNegativeContext);
        }
        //updateBasicStroke();
    }

    public RealParameter getWidth() {
        return this.width;
    }

    public RealParameter getDashOffset() {
        return dashOffset;
    }

    public void setDashOffset(RealParameter dashOffset) {
        this.dashOffset = dashOffset;
        if (dashOffset != null) {
            dashOffset.setContext(RealParameterContext.realContext);
        }
        //updateBasicStroke();
    }

    public StringParameter getDashArray() {
        return dashArray;
    }

    public void setDashArray(StringParameter dashArray) {
        this.dashArray = dashArray;
    }

    /*
    private void updateBasicStroke() {
    try {
    bStroke = createBasicStroke(null, null);
    } catch (Exception e) {
    // thrown if the stroke depends on the feature
    this.bStroke = null;
    }
    }*/
    private BasicStroke createBasicStroke(SpatialDataSourceDecorator sds, long fid, MapTransform mt, Double v100p) throws ParameterException {

        int cap;
        if (this.lineCap == null) {
            cap = BasicStroke.CAP_BUTT;
        } else {
            switch (this.lineCap) {
                case ROUND:
                    cap = BasicStroke.CAP_ROUND;
                    break;
                case SQUARE:
                    cap = BasicStroke.CAP_SQUARE;
                    break;
                default:
                case BUTT:
                    cap = BasicStroke.CAP_BUTT;
                    break;
            }
        }

        int join;
        if (this.lineJoin == null) {
            join = BasicStroke.JOIN_ROUND;
        } else {
            switch (this.lineJoin) {
                case BEVEL:
                    join = BasicStroke.JOIN_BEVEL;
                    break;
                case MITRE:
                    join = BasicStroke.JOIN_MITER;
                    break;
                case ROUND:
                default:
                    join = BasicStroke.JOIN_ROUND;
                    break;
            }
        }

        double w = 1.0;

        if (width != null) {
            w = width.getValue(sds, fid);
            w = Uom.toPixel(w, getUom(), mt.getDpi(), mt.getScaleDenominator(), null); // 100% based on view box height or width ? TODO
        }


        if (this.dashArray != null && !this.dashArray.getValue(sds, fid).isEmpty()) {

            float dashO = 0.0f;
            float[] dashA;

            String sDash = this.dashArray.getValue(sds, fid);
            String[] splitedDash = sDash.split(" ");

            dashA = new float[splitedDash.length];
            for (int i = 0; i < splitedDash.length; i++) {
                dashA[i] = (float) Uom.toPixel(Double.parseDouble(splitedDash[i]), getUom(), mt.getDpi(), mt.getScaleDenominator(), v100p);
            }

            if (this.dashOffset != null) {
                dashO = (float) Uom.toPixel(this.dashOffset.getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), v100p);
            }
            return new BasicStroke((float) w, cap, join, 10.0f, dashA, dashO);
        } else {
            return new BasicStroke((float) w, cap, join);
        }
    }

    public BasicStroke getBasicStroke(SpatialDataSourceDecorator sds, long fid, MapTransform mt, Double v100p) throws ParameterException {
        //if (bStroke != null) {
        //    return bStroke;
        //} else {
        return this.createBasicStroke(sds, fid, mt, v100p);
        //}

    }

    /**
     * Draw a pen stroke
     *
     * @todo DashOffset
     */
    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, Shape shp, boolean selected, MapTransform mt) throws ParameterException, IOException {

        //Paint paint = null;

        //Shape shape = shp;

        BasicStroke stroke = null;

        //if (this.bStroke == null) {
        stroke = this.createBasicStroke(sds, fid, mt, ShapeHelper.getAreaPerimeterLength(shp));
        //} else {
        //    stroke = this.bStroke;
        //}

        //g2.setStroke(stroke);

        //paint = fill.getPaint(fid, sds, selected, mt);

        if (fill != null) {
            Shape outline = stroke.createStrokedShape(shp);
            fill.draw(g2, sds, fid, outline, selected, mt);
            //g2.setPaint(paint);
            //g2.draw(shape);
        }
    }

    @Override
    public double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException {
        if (this.width != null) {
            return Uom.toPixel(width.getValue(sds, fid), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        } else {
            return 0.0;
        }
    }

    @Override
    public double getMinLength(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        return 0;
    }

    @Override
    public JAXBElement<PenStrokeType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createPenStroke(this.getJAXBType());
    }

    public PenStrokeType getJAXBType() {
        PenStrokeType s = new PenStrokeType();

        this.setJAXBProperties(s);

        if (this.fill != null) {
            s.setFill(fill.getJAXBElement());
        }

        if (this.dashArray != null) {
            //s.setDashArray(null);
            s.setDashArray(dashArray.getJAXBParameterValueType());
        }

        if (this.dashOffset != null) {
            s.setDashOffset(this.dashOffset.getJAXBParameterValueType());
        }

        if (this.lineCap != null) {
            s.setLineCap(this.lineCap.getParameterValueType());
        }

        if (this.lineJoin != null) {
            s.setLineJoin(this.lineJoin.getParameterValueType());
        }

        /*if (this.opacity != null) {
        s.setOpacity(this.opacity.getJAXBParameterValueType());
        }*/

        if (this.width != null) {
            s.setWidth(this.width.getJAXBParameterValueType());
        }

        return s;
    }
}
