/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;

import net.opengis.se._2_0.core.MatrixType;
import net.opengis.se._2_0.core.ObjectFactory;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * Affine Transformation based on RealParameters
 * Warning: conversion to pixel unit will give strange behavior !
 * @author maxence
 */
public final class Matrix implements Transformation {

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

        public RealParameter getA() {
                return a;
        }

        public void setA(RealParameter a) {
                if (a == null) {
                        this.a = new RealLiteral(0.0);
                } else {
                        this.a = a;
                }
                this.a.setContext(RealParameterContext.REAL_CONTEXT);
        }

        public RealParameter getB() {
                return b;
        }

        public void setB(RealParameter b) {
                if (b == null) {
                        this.b = new RealLiteral(0.0);
                } else {
                        this.b = b;
                }
                this.b.setContext(RealParameterContext.REAL_CONTEXT);
        }

        public RealParameter getC() {
                return c;
        }

        public void setC(RealParameter c) {
                if (c == null) {
                        this.c = new RealLiteral(0.0);
                } else {
                        this.c = c;
                }
                this.c.setContext(RealParameterContext.REAL_CONTEXT);
        }

        public RealParameter getD() {
                return d;
        }

        public void setD(RealParameter d) {
                if (d == null) {
                        this.d = new RealLiteral(0.0);
                } else {
                        this.d = d;
                }
                this.d.setContext(RealParameterContext.REAL_CONTEXT);
        }

        public RealParameter getE() {
                return e;
        }

        public void setE(RealParameter e) {
                if (e == null) {
                        this.e = new RealLiteral(0.0);
                } else {
                        this.e = e;
                }
                this.e.setContext(RealParameterContext.REAL_CONTEXT);
        }

        public RealParameter getF() {
                return f;
        }

        public void setF(RealParameter f) {
                if (f == null) {
                        this.f = new RealLiteral(0.0);
                } else {
                        this.f = f;
                }
                this.f.setContext(RealParameterContext.REAL_CONTEXT);
        }

        @Override
        public String dependsOnFeature() {
                return (a.dependsOnFeature() + " "
                        + b.dependsOnFeature() + " "
                        + c.dependsOnFeature() + " "
                        + d.dependsOnFeature() + " "
                        + e.dependsOnFeature() + " "
                        + f.dependsOnFeature()).trim();
        }

        @Override
        public AffineTransform getAffineTransform(SpatialDataSourceDecorator sds, long fid, Uom uom, MapTransform mt, Double width, Double height) throws ParameterException {
                return new AffineTransform(
                        //Uom.toPixel(a.getValue(feat), uom, mt.getDpi(), mt.getScaleDenominator(), null),
                        a.getValue(sds, fid),
                        b.getValue(sds, fid),
                        c.getValue(sds, fid),
                        //Uom.toPixel(b.getValue(feat), uom, mt.getDpi(), mt.getScaleDenominator(), null),
                        //Uom.toPixel(c.getValue(feat), uom, mt.getDpi(), mt.getScaleDenominator(), null),
                        //Uom.toPixel(d.getValue(feat), uom, mt.getDpi(), mt.getScaleDenominator(), null),
                        d.getValue(sds, fid),
                        Uom.toPixel(e.getValue(sds, fid), uom, mt.getDpi(), mt.getScaleDenominator(), width),
                        Uom.toPixel(f.getValue(sds, fid), uom, mt.getDpi(), mt.getScaleDenominator(), height));
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
                String sa = a.dependsOnFeature();
                String sb = b.dependsOnFeature();
                String sc = c.dependsOnFeature();
                String sd = d.dependsOnFeature();
                String se = e.dependsOnFeature();
                String sf = f.dependsOnFeature();

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
