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
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.MatrixType;
import net.opengis.se._2_0.core.ObjectFactory;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.visitors.FeaturesVisitor;

/**
 * Affine Transformation based on RealParameters
 * Warning: conversion to pixel unit will give strange behavior !
 * <p> The matrix as the following form :</p>
 * <p>{@code [A C E]}<br/>
 * {@code|B D F|}<br/>
 * {@code[0 0 1]}<br/></p>
 * <p>Note that the matrix is filled with {@code RealParameter} instances, not 
 * with {@code double} values.
 * 
 * @author Maxence Laurent, Alexis Guéganno
 */
public final class Matrix extends AbstractSymbolizerNode implements Transformation {

        private static final double DEF_A = 1.0;
        private static final double DEF_B = 0.0;
        private static final double DEF_C = 0.0;
        private static final double DEF_D = 1.0;
        private static final double DEF_E = 0.0;
        private static final double DEF_F = 0.0;
        private RealParameter a;
        private RealParameter b;
        private RealParameter c;
        private RealParameter d;
        private RealParameter e;
        private RealParameter f;

        /**
         * Create an identity matrix
         *
         */
        public Matrix() {
                setA(new RealLiteral(DEF_A));
                setB(new RealLiteral(DEF_B));
                setC(new RealLiteral(DEF_C));
                setD(new RealLiteral(DEF_D));
                setE(new RealLiteral(DEF_E));
                setF(new RealLiteral(DEF_F));
        }

        /**
         * Create a new <code>Matrix</code> from <code>double</code> values.
         * @param a
         * @param b
         * @param c
         * @param d
         * @param e
         * @param f 
         */
        public Matrix(double a, double b, double c, double d, double e, double f) {
                setA(new RealLiteral(a));
                setB(new RealLiteral(b));
                setC(new RealLiteral(c));
                setD(new RealLiteral(d));
                setE(new RealLiteral(e));
                setF(new RealLiteral(f));
        }

        /**
         * Create a new <code>Matrix</code> from <code>RealParameter</code> instances. 
         * <code>null</code> values will be transformed to <code>new RealLiteral(0.0)</code>
         * @param a
         * @param b
         * @param c
         * @param d
         * @param e
         * @param f
         */
        public Matrix(RealParameter a, RealParameter b, RealParameter c,
                RealParameter d, RealParameter e, RealParameter f) {
                this();
                if (a != null) {
                        setA(a);
                }
                if (b != null) {
                        setB(b);
                }
                if (c != null) {
                        setC(c);
                }
                if (d != null) {
                        setD(d);
                }
                if (e != null) {
                        setE(e);
                }
                if (f != null) {
                        setF(f);
                }
        }

        /**
         * Creates a hard copy of <code>m</code>
         * @param m
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        Matrix(MatrixType m) throws InvalidStyle {
                this();
                if (m.getA() != null) {
                        this.setA(SeParameterFactory.createRealParameter(m.getA()));
                }
                if (m.getB() != null) {
                        this.setB(SeParameterFactory.createRealParameter(m.getB()));
                }
                if (m.getC() != null) {
                        this.setC(SeParameterFactory.createRealParameter(m.getC()));
                }
                if (m.getD() != null) {
                        this.setD(SeParameterFactory.createRealParameter(m.getD()));
                }
                if (m.getE() != null) {
                        this.setE(SeParameterFactory.createRealParameter(m.getE()));
                }
                if (m.getF() != null) {
                        this.setF(SeParameterFactory.createRealParameter(m.getF()));
                }
        }

        /**
         * Get the A parameter of this {@code Matrix}, as defined in the 
         * description of the class.
         * @return 
         * A {@code RealParameter} that is placed in a 
         * {@link RealParameterContext#REAL_CONTEXT}
         */
        public RealParameter getA() {
                return a;
        }

        /**
         * Set the A parameter of this {@code Matrix}, as defined in the 
         * description of the class.
         * @param a 
         * A {@code RealParameter} that is placed (by this method) in a 
         * {@link RealParameterContext#REAL_CONTEXT}. If null, the value of A is
         * set to 0.
         */
        public void setA(RealParameter a) {
                if (a == null) {
                        this.a = new RealLiteral(0.0);
                } else {
                        this.a = a;
                }
                this.a.setContext(RealParameterContext.REAL_CONTEXT);
                a.setParent(this);
        }

        /**
         * Get the B parameter of this {@code Matrix}, as defined in the 
         * description of the class.
         * @return 
         * A {@code RealParameter} that is placed in a 
         * {@link RealParameterContext#REAL_CONTEXT}
         */
        public RealParameter getB() {
                return b;
        }

        /**
         * Set the B parameter of this {@code Matrix}, as defined in the 
         * description of the class.
         * @param b 
         * A {@code RealParameter} that is placed (by this method) in a 
         * {@link RealParameterContext#REAL_CONTEXT}. If null, the value of B is
         * set to 0.
         */
        public void setB(RealParameter b) {
                if (b == null) {
                        this.b = new RealLiteral(0.0);
                } else {
                        this.b = b;
                }
                this.b.setContext(RealParameterContext.REAL_CONTEXT);
                this.b.setParent(this);
        }

        /**
         * Get the C parameter of this {@code Matrix}, as defined in the 
         * description of the class.
         * @return 
         * A {@code RealParameter} that is placed in a 
         * {@link RealParameterContext#REAL_CONTEXT}
         */
        public RealParameter getC() {
                return c;
        }

        /**
         * Set the C parameter of this {@code Matrix}, as defined in the 
         * description of the class.
         * @param c
         * A {@code RealParameter} that is placed (by this method) in a 
         * {@link RealParameterContext#REAL_CONTEXT}. If null, the value of C is
         * set to 0.
         */
        public void setC(RealParameter c) {
                if (c == null) {
                        this.c = new RealLiteral(0.0);
                } else {
                        this.c = c;
                }
                this.c.setContext(RealParameterContext.REAL_CONTEXT);
                this.c.setParent(this);
        }

        /**
         * Get the D parameter of this {@code Matrix}, as defined in the 
         * description of the class.
         * @return 
         * A {@code RealParameter} that is placed in a 
         * {@link RealParameterContext#REAL_CONTEXT}
         */
        public RealParameter getD() {
                return d;
        }

        /**
         * Set the D parameter of this {@code Matrix}, as defined in the 
         * description of the class.
         * @param d
         * A {@code RealParameter} that is placed (by this method) in a 
         * {@link RealParameterContext#REAL_CONTEXT}. If null, the value of D is
         * set to 0.
         */
        public void setD(RealParameter d) {
                if (d == null) {
                        this.d = new RealLiteral(0.0);
                } else {
                        this.d = d;
                }
                this.d.setContext(RealParameterContext.REAL_CONTEXT);
                this.d.setParent(this);
        }

        /**
         * Get the E parameter of this {@code Matrix}, as defined in the 
         * description of the class.
         * @return 
         * A {@code RealParameter} that is placed in a 
         * {@link RealParameterContext#REAL_CONTEXT}
         */
        public RealParameter getE() {
                return e;
        }

        /**
         * Set the E parameter of this {@code Matrix}, as defined in the 
         * description of the class.
         * @param e 
         * A {@code RealParameter} that is placed (by this method) in a 
         * {@link RealParameterContext#REAL_CONTEXT}. If null, the value of E is
         * set to 0.
         */
        public void setE(RealParameter e) {
                if (e == null) {
                        this.e = new RealLiteral(0.0);
                } else {
                        this.e = e;
                }
                this.e.setContext(RealParameterContext.REAL_CONTEXT);
                this.e.setParent(this);
        }

        /**
         * Get the F parameter of this {@code Matrix}, as defined in the 
         * description of the class.
         * @return 
         * A {@code RealParameter} that is placed in a 
         * {@link RealParameterContext#REAL_CONTEXT}
         */
        public RealParameter getF() {
                return f;
        }

        /**
         * Set the F parameter of this {@code Matrix}, as defined in the 
         * description of the class.
         * @param f 
         * A {@code RealParameter} that is placed (by this method) in a 
         * {@link RealParameterContext#REAL_CONTEXT}. If null, the value of F is
         * set to 0.
         */
        public void setF(RealParameter f) {
                if (f == null) {
                        this.f = new RealLiteral(0.0);
                } else {
                        this.f = f;
                }
                this.f.setContext(RealParameterContext.REAL_CONTEXT);
                this.f.setParent(this);
        }

        @Override
        public UsedAnalysis getUsedAnalysis() {
            UsedAnalysis result = new UsedAnalysis();
            result.merge(a.getUsedAnalysis());
            result.merge(b.getUsedAnalysis());
            result.merge(c.getUsedAnalysis());
            result.merge(d.getUsedAnalysis());
            result.merge(e.getUsedAnalysis());
            result.merge(f.getUsedAnalysis());
            return result;
        }

        @Override
        public List<SymbolizerNode> getChildren() {
                List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
                if (a != null) {
                        ls.add(a);
                }
                if (b != null) {
                        ls.add(b);
                }
                if (c != null) {
                        ls.add(c);
                }
                if (d != null) {
                        ls.add(d);
                }
                if (e != null) {
                        ls.add(e);
                }
                if (f != null) {
                        ls.add(f);
                }
                return ls;
        }

        @Override
        public AffineTransform getAffineTransform(Map<String,Value> map, Uom uom,
            MapTransform mt, Double width, Double height) throws ParameterException {
                return new AffineTransform(
                        //Uom.toPixel(a.getValue(feat), uom, mt.getDpi(), mt.getScaleDenominator(), null),
                        a.getValue(map),
                        b.getValue(map),
                        c.getValue(map),
                        //Uom.toPixel(b.getValue(feat), uom, mt.getDpi(), mt.getScaleDenominator(), null),
                        //Uom.toPixel(c.getValue(feat), uom, mt.getDpi(), mt.getScaleDenominator(), null),
                        //Uom.toPixel(d.getValue(feat), uom, mt.getDpi(), mt.getScaleDenominator(), null),
                        d.getValue(map),
                        Uom.toPixel(e.getValue(map), uom, mt.getDpi(), mt.getScaleDenominator(), width),
                        Uom.toPixel(f.getValue(map), uom, mt.getDpi(), mt.getScaleDenominator(), height));
        }

        @Override
        public boolean allowedForGeometries() {
                return false;
        }

        /**
         * This method simplifiy the matrix.
         * Every matrix element which doesn't depends on a feature is converted to a single RealLiteral
         *
         * @throws ParameterException when something went wrong...
         */
        public void simplify() throws ParameterException {
                FeaturesVisitor vis = new FeaturesVisitor();
                a.acceptVisitor(vis);
                HashSet<String> sa = vis.getResult();
                b.acceptVisitor(vis);
                HashSet<String> sb = vis.getResult();
                c.acceptVisitor(vis);
                HashSet<String> sc = vis.getResult();
                d.acceptVisitor(vis);
                HashSet<String> sd = vis.getResult();
                e.acceptVisitor(vis);
                HashSet<String> se = vis.getResult();
                f.acceptVisitor(vis);
                HashSet<String> sf = vis.getResult();

                if (sa != null && !sa.isEmpty()) {
                        setA(new RealLiteral(a.getValue(null, -1)));
                }
                if (sb != null && !sb.isEmpty()) {
                        setB(new RealLiteral(b.getValue(null, -1)));
                }
                if (sc != null && !sc.isEmpty()) {
                        setC(new RealLiteral(c.getValue(null, -1)));
                }
                if (sd != null && !sd.isEmpty()) {
                        setD(new RealLiteral(d.getValue(null, -1)));
                }
                if (se != null && !se.isEmpty()) {
                        setE(new RealLiteral(e.getValue(null, -1)));
                }
                if (sf != null && !sf.isEmpty()) {
                        setF(new RealLiteral(f.getValue(null, -1)));
                }
        }

        @Override
        public JAXBElement<?> getJAXBElement() {
                MatrixType m = this.getJAXBType();

                ObjectFactory of = new ObjectFactory();
                return of.createMatrix(m);
        }

        @Override
        public MatrixType getJAXBType() {
                MatrixType m = new MatrixType();
                m.setA(a.getJAXBParameterValueType());
                m.setB(b.getJAXBParameterValueType());
                m.setC(c.getJAXBParameterValueType());
                m.setD(d.getJAXBParameterValueType());
                m.setE(e.getJAXBParameterValueType());
                m.setF(f.getJAXBParameterValueType());

                return m;
        }

        @Override
        public String toString() {
                return "Matrix";
        }
}
