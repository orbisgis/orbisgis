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
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.AreaSymbolizerType;
import net.opengis.se._2_0.core.ObjectFactory;
import org.gdms.data.DataSource;
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
import org.orbisgis.core.renderer.se.transform.Translate;

/**
 * A "AreaSymbolizer" specifies the rendering of a polygon or other area/surface geometry, 
 * including its interior fill and border stroke.</p>
 * <p>In addition of the properties inherited from <code>VectorSymbolizer</code> an <code>
 * AreaSymbolizer</code> is defined with a perpendicular offset, a <code>Stroke</code> (to draw its limit, 
 * and as a <code>StrokeNode</code>) and a <code>Fill</code> (to paint its interior, and 
 * as a <code>FillNode</code>).
 * @author maxence, alexis
 */
public final class AreaSymbolizer extends VectorSymbolizer implements FillNode, StrokeNode {

        private Translate translate;
        private RealParameter perpendicularOffset;
        private Stroke stroke;
        private Fill fill;

        /**
         * Build a new AreaSymbolizer, named "Area Symbolizer". It is defined with a 
         * <code>SolidFill</code> and a <code>PenStroke</code>
         */
        public AreaSymbolizer() {
                super();
                name = "Area symbolizer";
                this.setFill(new SolidFill());
                this.setStroke(new PenStroke());
        }

        /**
         * Build a new <code>AreaSymbolizer</code>, using a JAXB element to fill its properties.
         * @param st
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        public AreaSymbolizer(JAXBElement<AreaSymbolizerType> st) throws InvalidStyle {
                super(st);

                AreaSymbolizerType ast = st.getValue();


                if (ast.getGeometry() != null) {
                        this.setGeometryAttribute(new GeometryAttribute(ast.getGeometry()));
                }

                if (ast.getUom() != null) {
                        setUom(Uom.fromOgcURN(ast.getUom()));
                }

                if (ast.getPerpendicularOffset() != null) {
                        this.setPerpendicularOffset(SeParameterFactory.createRealParameter(ast.getPerpendicularOffset()));
                }

                if (ast.getDisplacement() != null) {
                        this.setTranslate(new Translate(ast.getDisplacement()));
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

        /**
         * Retrieve the geometric transformation that must be applied to the geometries.
         * @return 
         *  The transformation associated to this Symbolizer.
         */
        public Translate getTranslate() {
                return translate;
        }

        /**
         * Get the geometric transformation that must be applied to the geometries.
         * @param transform 
         */
        public void setTranslate(Translate translate) {
                this.translate = translate;
                //translate.setParent(this);
        }

        /**
         * Get the current perpendicular offset associated to this Symbolizer. It allows to
         * draw polygons larger or smaller than their actual geometry. The meaning of the 
         * value is dependant of the <code>Uom</code> instance associated to this <code>Symbolizer</code>.
         * 
         * @return 
         *          The offset as a <code>RealParameter</code>. A positive value will cause the
         *          polygons to be drawn larger than their original size, while a negative value
         *          will cause the drawing of smaller polygons.
         */
        public RealParameter getPerpendicularOffset() {
                return perpendicularOffset;
        }

        /**
         * Set the current perpendicular offset associated to this Symbolizer. It allows to
         * draw polygons larger or smaller than their actual geometry. The meaning of the 
         * value is dependant of the <code>Uom</code> instance associated to this <code>Symbolizer</code>.
         * @param perpendicularOffset 
         *          The offset as a <code>RealParameter</code>. A positive value will cause the
         *          polygons to be drawn larger than their original size, while a negative value
         *          will cause the drawing of smaller polygons.
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
         * @throws IOException error while accessing external resource
         * @throws DriverException
         */
        @Override
        public void draw(Graphics2D g2, DataSource sds, long fid,
                boolean selected, MapTransform mt, Geometry the_geom, RenderContext perm)
                throws ParameterException, IOException, DriverException {

                List<Shape> shapes = new LinkedList<Shape>();
                shapes.add(mt.getShape(the_geom, true));

                if (shapes != null) {
                        for (Shape shp : shapes) {
                                if (this.getTranslate() != null) {
                                        shp = getTranslate().getAffineTransform(sds, fid, getUom(), mt,
                                                (double) mt.getWidth(), (double) mt.getHeight()).createTransformedShape(shp);
                                }
                                if (shp != null) {
                                        if (fill != null) {
                                                fill.draw(g2, sds, fid, shp, selected, mt);
                                        }

                                        if (stroke != null) {
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
        }

        @Override
        public JAXBElement<AreaSymbolizerType> getJAXBElement() {
                ObjectFactory of = new ObjectFactory();
                AreaSymbolizerType s = of.createAreaSymbolizerType();

                this.setJAXBProperty(s);

                if (this.getGeometryAttribute() != null) {
                        s.setGeometry(getGeometryAttribute().getJAXBGeometryType());
                }

                if (getUom() != null) {
                        s.setUom(this.getUom().toURN());
                }

                if (getTranslate() != null) {
                        s.setDisplacement(getTranslate().getJAXBType());
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
        public HashSet<String> dependsOnFeature() {
                HashSet<String> ret = new HashSet<String>();
                if (translate != null) {
                        ret.addAll(translate.dependsOnFeature());
                }
                if (fill != null) {
                        ret.addAll(fill.dependsOnFeature());
                }
                if (stroke != null) {
                        ret.addAll(stroke.dependsOnFeature());
                }
                if (perpendicularOffset != null) {
                        ret.addAll(perpendicularOffset.dependsOnFeature());
                }
                return ret;
        }
}
