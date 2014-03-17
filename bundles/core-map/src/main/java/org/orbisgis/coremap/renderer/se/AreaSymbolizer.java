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
package org.orbisgis.coremap.renderer.se;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.AreaSymbolizerType;
import net.opengis.se._2_0.core.ObjectFactory;



import org.orbisgis.core.map.MapTransform;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.common.Uom;
import org.orbisgis.coremap.renderer.se.fill.Fill;
import org.orbisgis.coremap.renderer.se.fill.SolidFill;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.coremap.renderer.se.parameter.geometry.GeometryAttribute;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.coremap.renderer.se.stroke.PenStroke;
import org.orbisgis.coremap.renderer.se.stroke.Stroke;
import org.orbisgis.coremap.renderer.se.transform.Translate;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A "AreaSymbolizer" specifies the rendering of a polygon or other area/surface geometry, 
 * including its interior fill and border stroke.</p>
 * <p>In addition of the properties inherited from <code>VectorSymbolizer</code> an <code>
 * AreaSymbolizer</code> is defined with a perpendicular offset, a <code>Stroke</code> (to draw its limit, 
 * and as a <code>StrokeNode</code>) and a <code>Fill</code> (to paint its interior, and 
 * as a <code>FillNode</code>).
 * @author Maxence Laurent, Alexis Guéganno
 */
public final class AreaSymbolizer extends VectorSymbolizer implements FillNode, StrokeNode {

        private Translate translate;
        private RealParameter perpendicularOffset;
        private Stroke stroke;
        private Fill fill;
        private static final I18n I18N = I18nFactory.getI18n(AreaSymbolizer.class);

        /**
         * Build a new AreaSymbolizer, named "Area Symbolizer". It is defined with a
         * <code>SolidFill</code> and a <code>PenStroke</code>
         */
        public AreaSymbolizer() {
                super();
                name = I18N.tr("Area Symbolizer");
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
         * @param translate
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
                        this.perpendicularOffset.setParent(this);
                }
        }

        /**
         *
         * @param g2
         * @param rs
         * @param fid
         * @throws ParameterException
         * @throws IOException error while accessing external resource
         * @throws java.sql.SQLException
         */
        @Override
        public void draw(Graphics2D g2, ResultSet rs, long fid,
                boolean selected, MapTransform mt, Geometry the_geom)
                throws ParameterException, IOException, SQLException {

                List<Shape> shapes = new LinkedList<Shape>();
                shapes.add(mt.getShape(the_geom, true));
                Map<String,Object> map = getFeaturesMap(rs, fid);
                for (Shape shp : shapes) {
                        if (this.getTranslate() != null) {
                                shp = getTranslate().getAffineTransform(map, getUom(), mt,
                                        (double) mt.getWidth(), (double) mt.getHeight()).createTransformedShape(shp);
                        }
                        if (shp != null) {
                                if (fill != null) {
                                        fill.draw(g2, map, shp, selected, mt);
                                }

                                if (stroke != null) {
                                        double offset = 0.0;
                                        if (perpendicularOffset != null) {
                                                offset = Uom.toPixel(perpendicularOffset.getValue(rs, fid),
                                                        getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                                        }
                                        stroke.draw(g2, map, shp, selected, mt, offset);
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
        public List<SymbolizerNode> getChildren() {
                List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>(4);
                if(this.getGeometryAttribute()!=null){
                    ls.add(this.getGeometryAttribute());
                }
                if (translate != null) {
                        ls.add(translate);
                }
                if (fill != null) {
                        ls.add(fill);
                }
                if (perpendicularOffset != null) {
                        ls.add(perpendicularOffset);
                }
                if (stroke != null) {
                        ls.add(stroke);
                }
                return ls;
        }

}
