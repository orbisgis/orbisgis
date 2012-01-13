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
package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.LineSymbolizerType;
import net.opengis.se._2_0.core.ObjectFactory;
import org.gdms.data.DataSource;

import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.RenderContext;

import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.geometry.GeometryAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;

/**
 * A {@code LineSymbolizer} is used to style a {@code Stroke} along a linear 
 * geometry type (a LineString, for instance). It is dependant upon the same
 * parameters as {@link VectorSymbolizer}, and upon two others :
 * <ul><li>PerpendicularOffset : Used to draw lines in parallel to the original
 * geometry</li>
 * <li>Stroke : defines the way to render the line, as described in {@link Stroke}
 * and its children</li>
 * </ul>
 *
 * @todo add perpendicular offset
 *
 * @author alexis, maxence
 */
public final class LineSymbolizer extends VectorSymbolizer implements StrokeNode {

    private RealParameter perpendicularOffset;
    private Stroke stroke;

    /**
     * Instanciate a new default {@code LineSymbolizer}. It's named {@code 
     * Line Symbolizer"}, is defined in {@link Uom#MM}, and is drawn using a 
     * default {@link PenStroke}
     */
    public LineSymbolizer() {
        super();
        this.name = "Line Symbolizer";
        setStroke(new PenStroke());
    }

    /**
     * Build a new {@code LineSymbolizer} using the {@code JAXBElement} given in
     * argument
     * @param st
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public LineSymbolizer(JAXBElement<LineSymbolizerType> st) throws InvalidStyle {
        super(st);
        LineSymbolizerType ast = st.getValue();


        if (ast.getGeometry() != null) {
            this.setGeometryAttribute(new GeometryAttribute(ast.getGeometry()));
        }

        if (ast.getUom() != null) {
            setUom(Uom.fromOgcURN(ast.getUom()));
        }

        if (ast.getPerpendicularOffset() != null) {
            this.setPerpendicularOffset(SeParameterFactory.createRealParameter(ast.getPerpendicularOffset()));
        }

        /*if (ast.getTranslate() != null) {
            this.setTranslate(new Translate(ast.getTranslate()));
        }*/

        if (ast.getStroke() != null) {
            this.setStroke(Stroke.createFromJAXBElement(ast.getStroke()));
        }
    }

    @Override
    public Stroke getStroke() {
        return stroke;
    }

    @Override
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
        stroke.setParent(this);
    }

    /**
     * Get the current perpendicular offset. If null, considered to be set to 0.
     * @return 
     */
    public RealParameter getPerpendicularOffset() {
        return perpendicularOffset;
    }

    /**
     * Set the perpendicular offset. If a {@code null} value is given, the offset
     * will be considered as equal to 0.
     * @param perpendicularOffset 
     */
    public void setPerpendicularOffset(RealParameter perpendicularOffset) {
        this.perpendicularOffset = perpendicularOffset;
        if (this.perpendicularOffset != null) {
            this.perpendicularOffset.setContext(RealParameterContext.REAL_CONTEXT);
        }
    }

    /**
     *
     * @param g2
     * @param sds
     * @param fid
     * @throws ParameterException
     * @throws IOException
     * @todo make sure the geom is a line or an area; implement p_offset
     */
    @Override
    public void draw(Graphics2D g2, DataSource sds, long fid,
            boolean selected, MapTransform mt, Geometry the_geom, RenderContext perm)
            throws ParameterException, IOException, DriverException {
        if (stroke != null) {
            List<Shape> shapes = this.getLines(sds, fid, mt, the_geom);


            double offset = 0.0;
            if (perpendicularOffset != null) {
                offset = Uom.toPixel(perpendicularOffset.getValue(sds, fid),
                        getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
            }

            if (shapes != null) {
                for (Shape shp : shapes) {
                    if (shp != null) {
                        stroke.draw(g2, sds, fid, shp, selected, mt, offset);
                    }
                }
            }
        }
    }

    @Override
    public JAXBElement<LineSymbolizerType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        LineSymbolizerType s = of.createLineSymbolizerType();

        this.setJAXBProperty(s);

        if (this.getGeometryAttribute() != null){
            s.setGeometry(getGeometryAttribute().getJAXBGeometryType());
        }

        if (this.getUom() != null) {
            s.setUom(this.getUom().toURN());
        }

        /*if (translate != null) {
            s.setTranslate(translate.getJAXBType());
        }*/

        if (this.perpendicularOffset != null) {
            s.setPerpendicularOffset(perpendicularOffset.getJAXBParameterValueType());
        }

        if (stroke != null) {
            s.setStroke(stroke.getJAXBElement());
        }


        return of.createLineSymbolizer(s);
    }

    @Override
    public HashSet<String> dependsOnFeature() {
        HashSet<String> ret = new HashSet<String>();
        if(perpendicularOffset != null){
            ret.addAll(perpendicularOffset.dependsOnFeature());
        }
        if(stroke != null){
            ret.addAll(stroke.dependsOnFeature());
        }
        return ret;
    }
    
}
