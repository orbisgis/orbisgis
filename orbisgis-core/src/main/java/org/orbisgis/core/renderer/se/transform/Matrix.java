/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;

import org.orbisgis.core.renderer.persistance.se.MatrixType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.common.MapEnv;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 * Affine Transformation based on RealParameters
 * Warning: conversion to pixel unit will give strange behavior !
 * @author maxence
 */
public class Matrix implements Transformation {

    /**
     * Create an identity matrix
     *
     */
    public Matrix() {
        a = new RealLiteral(1.0);
        b = new RealLiteral(0.0);
        c = new RealLiteral(0.0);
        d = new RealLiteral(1.0);
        e = new RealLiteral(0.0);
        f = new RealLiteral(0.0);
    }

    public Matrix(double a, double b, double c, double d, double e, double f) {
        this.a = new RealLiteral(a);
        this.b = new RealLiteral(b);
        this.c = new RealLiteral(c);
        this.d = new RealLiteral(d);
        this.e = new RealLiteral(e);
        this.f = new RealLiteral(f);
    }

    /**
     * a null cell means new RealLiteral(0.0)
     * @param a
     * @param b
     * @param c
     * @param d
     * @param e
     * @param f
     */
    public Matrix(RealParameter a, RealParameter b, RealParameter c,
            RealParameter d, RealParameter e, RealParameter f) {
        if (a == null) {
            a = new RealLiteral(0.0);
        }
        if (b == null) {
            b = new RealLiteral(0.0);
        }
        if (c == null) {
            c = new RealLiteral(0.0);
        }
        if (d == null) {
            d = new RealLiteral(0.0);
        }
        if (e == null) {
            e = new RealLiteral(0.0);
        }
        if (f == null) {
            f = new RealLiteral(0.0);
        }

        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
    }

    Matrix(MatrixType m) {
        if (m.getA() != null)
            this.setA(SeParameterFactory.createRealParameter(m.getA()));
        if (m.getB() != null)
            this.setB(SeParameterFactory.createRealParameter(m.getB()));
        if (m.getC() != null)
            this.setC(SeParameterFactory.createRealParameter(m.getC()));
        if (m.getD() != null)
            this.setD(SeParameterFactory.createRealParameter(m.getD()));
        if (m.getE() != null)
            this.setE(SeParameterFactory.createRealParameter(m.getE()));
        if (m.getF() != null)
            this.setF(SeParameterFactory.createRealParameter(m.getF()));
    }

    public RealParameter getA() {
        return a;
    }

    public void setA(RealParameter a) {
        if (a == null) {
            a = new RealLiteral(0.0);
        }
        this.a = a;
    }

    public RealParameter getB() {
        return b;
    }

    public void setB(RealParameter b) {
        if (b == null) {
            b = new RealLiteral(0.0);
        }
        this.b = b;
    }

    public RealParameter getC() {
        return c;
    }

    public void setC(RealParameter c) {
        if (c == null) {
            c = new RealLiteral(0.0);
        }
        this.c = c;
    }

    public RealParameter getD() {
        return d;
    }

    public void setD(RealParameter d) {
        if (d == null) {
            d = new RealLiteral(0.0);
        }
        this.d = d;
    }

    public RealParameter getE() {
        return e;
    }

    public void setE(RealParameter e) {
        if (e == null) {
            e = new RealLiteral(0.0);
        }
        this.e = e;
    }

    public RealParameter getF() {
        return f;
    }

    public void setF(RealParameter f) {
        if (f == null) {
            f = new RealLiteral(0.0);
        }
        this.f = f;
    }

    @Override
    public boolean dependsOnFeature(){
        return (a.dependsOnFeature()
                || b.dependsOnFeature()
                || c.dependsOnFeature()
                || d.dependsOnFeature()
                ||e.dependsOnFeature()
                ||f.dependsOnFeature());
    }

    @Override
    public AffineTransform getAffineTransform(Feature feat, Uom uom) throws ParameterException {
        return new AffineTransform( // TODO DPI !
                Uom.toPixel(a.getValue(feat), uom, MapEnv.getScaleDenominator()),
                Uom.toPixel(b.getValue(feat), uom, MapEnv.getScaleDenominator()),
                Uom.toPixel(c.getValue(feat), uom, MapEnv.getScaleDenominator()),
                Uom.toPixel(d.getValue(feat), uom, MapEnv.getScaleDenominator()),
                Uom.toPixel(e.getValue(feat), uom, MapEnv.getScaleDenominator()),
                Uom.toPixel(f.getValue(feat), uom, MapEnv.getScaleDenominator()));
    }

    @Override
    public boolean allowedForGeometries() {
        return false;
    }

    /**
     * This method simplifiy the matrix.
     * Every matrix element which doesn't depends on a feature is converted to a single RealLiteral
     *
     * @throws ParameterException when somethung wen wrong...
     */
    public void simplify() throws ParameterException {
        if (!a.dependsOnFeature()) {
            a = new RealLiteral(a.getValue(null));
        }
        if (!b.dependsOnFeature()) {
            b = new RealLiteral(b.getValue(null));
        }
        if (!c.dependsOnFeature()) {
            c = new RealLiteral(c.getValue(null));
        }
        if (!d.dependsOnFeature()) {
            d = new RealLiteral(d.getValue(null));
        }
        if (!e.dependsOnFeature()) {
            e = new RealLiteral(e.getValue(null));
        }
        if (!f.dependsOnFeature()) {
            f = new RealLiteral(f.getValue(null));
        }
    }

    @Override
    public JAXBElement<?> getJAXBElement(){
        MatrixType m = this.getJAXBType();
        
        ObjectFactory of = new ObjectFactory();
        return of.createMatrix(m);
    }

    @Override
    public MatrixType getJAXBType(){
        MatrixType m = new MatrixType();
        m.setA(a.getJAXBParameterValueType());
        m.setB(b.getJAXBParameterValueType());
        m.setC(c.getJAXBParameterValueType());
        m.setD(d.getJAXBParameterValueType());
        m.setE(e.getJAXBParameterValueType());
        m.setF(f.getJAXBParameterValueType());

        return m;
    }

    private RealParameter a;
    private RealParameter b;
    private RealParameter c;
    private RealParameter d;
    private RealParameter e;
    private RealParameter f;
}
