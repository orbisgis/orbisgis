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
import java.util.ArrayList;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.Drawer;
import org.orbisgis.core.renderer.persistance.se.AreaSymbolizerType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;

import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.RenderContext;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;


import org.orbisgis.core.renderer.se.common.Uom;

import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.geometry.GeometryAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.transform.Transform;

public final class AreaSymbolizer extends VectorSymbolizer implements FillNode, StrokeNode {

    private RealParameter perpendicularOffset;
    private Stroke stroke;
    private Fill fill;

    public AreaSymbolizer() {
        super();
        name = "Area symbolizer";
        uom = Uom.MM;
        this.setFill(new SolidFill());
        this.setStroke(new PenStroke());
    }

    public AreaSymbolizer(JAXBElement<AreaSymbolizerType> st) throws InvalidStyle {
        super(st);

        AreaSymbolizerType ast = st.getValue();


        if (ast.getGeometry() != null) {
            this.setGeometry(new GeometryAttribute(ast.getGeometry().getPropertyName()));
        }

        if (ast.getUnitOfMeasure() != null) {
            this.uom = Uom.fromOgcURN(ast.getUnitOfMeasure());
        }

        if (ast.getPerpendicularOffset() != null) {
            this.setPerpendicularOffset(SeParameterFactory.createRealParameter(ast.getPerpendicularOffset()));
        }

        if (ast.getTransform() != null) {
            this.setTransform(new Transform(ast.getTransform()));
        }

        if (ast.getFill() != null) {
            this.setFill(Fill.createFromJAXBElement(ast.getFill()));
        }

        if (ast.getStroke() != null) {
            this.setStroke(Stroke.createFromJAXBElement(ast.getStroke()));
        }
    }

    @Override
    public void setStroke(Stroke stroke) {
        if (stroke != null) {
            stroke.setParent(this);
        }
        this.stroke = stroke;
    }

    @Override
    public Stroke getStroke() {
        return stroke;
    }

    @Override
    public void setFill(Fill fill) {
        if (fill != null) {
            fill.setParent(this);
        }
        this.fill = fill;
    }

    @Override
    public Fill getFill() {
        return fill;
    }

    public RealParameter getPerpendicularOffset() {
        return perpendicularOffset;
    }

    public void setPerpendicularOffset(RealParameter perpendicularOffset) {
        this.perpendicularOffset = perpendicularOffset;
        if (this.perpendicularOffset != null) {
            this.perpendicularOffset.setContext(RealParameterContext.realContext);
        }
    }

    /**
     *
     * @param g2
     * @param sds
     * @param fid
     * @throws ParameterException
     * @throws IOException error while accessing external resource
     * @throws DriverException
     */
    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, 
            boolean selected, MapTransform mt, Geometry the_geom, RenderContext perm)
            throws ParameterException, IOException, DriverException {

        ArrayList<Shape> shapes = this.getShapes(sds, fid, mt, the_geom);

        if (shapes != null) {
            for (Shape shp : shapes) {
                if (fill != null) {
                    fill.draw(g2, sds, fid, shp, selected, mt);
                }

                if (stroke != null && shp != null) {
                    double offset = 0.0;
                    if (perpendicularOffset != null) {
                        offset = Uom.toPixel(perpendicularOffset.getValue(sds, fid),
                                getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                    }
                    stroke.draw(g2, sds, fid, shp, selected, mt, offset);
                }
            }
        }
    }

    @Override
    public JAXBElement<AreaSymbolizerType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        AreaSymbolizerType s = of.createAreaSymbolizerType();

        this.setJAXBProperty(s);

        if (uom != null) {
            s.setUnitOfMeasure(this.getUom().toURN());
        }

        if (transform != null) {
            s.setTransform(transform.getJAXBType());
        }

        if (this.perpendicularOffset != null) {
            s.setPerpendicularOffset(perpendicularOffset.getJAXBParameterValueType());
        }

        if (fill != null) {
            s.setFill(fill.getJAXBElement());
        }

        if (stroke != null) {
            s.setStroke(stroke.getJAXBElement());
        }

        return of.createAreaSymbolizer(s);
    }

    @Override
    public void draw(Drawer drawer, long fid, boolean selected) {
        drawer.drawAreaSymbolizer(0, selected);
    }
}
