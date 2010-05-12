/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import javax.xml.bind.JAXBElement;

import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.persistance.se.MatrixType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.common.MapEnv;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
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
    public AffineTransform getAffineTransform(DataSource ds, long fid, Uom uom) throws ParameterException {
        return new AffineTransform( // TODO DPI !
                Uom.toPixel(a.getValue(ds, fid), uom, MapEnv.getScaleDenominator()),
                Uom.toPixel(b.getValue(ds, fid), uom, MapEnv.getScaleDenominator()),
                Uom.toPixel(c.getValue(ds, fid), uom, MapEnv.getScaleDenominator()),
                Uom.toPixel(d.getValue(ds, fid), uom, MapEnv.getScaleDenominator()),
                Uom.toPixel(e.getValue(ds, fid), uom, MapEnv.getScaleDenominator()),
                Uom.toPixel(f.getValue(ds, fid), uom, MapEnv.getScaleDenominator()));
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
            a = new RealLiteral(a.getValue(null, 0));
        }
        if (!b.dependsOnFeature()) {
            b = new RealLiteral(b.getValue(null, 0));
        }
        if (!c.dependsOnFeature()) {
            c = new RealLiteral(c.getValue(null, 0));
        }
        if (!d.dependsOnFeature()) {
            d = new RealLiteral(d.getValue(null, 0));
        }
        if (!e.dependsOnFeature()) {
            e = new RealLiteral(e.getValue(null, 0));
        }
        if (!f.dependsOnFeature()) {
            f = new RealLiteral(f.getValue(null, 0));
        }
    }

    @Override
    public JAXBElement<?> getJAXBElement(){
        MatrixType r = new MatrixType();
        r.setA(a.getJAXBParameterValueType());
        r.setB(b.getJAXBParameterValueType());
        r.setC(c.getJAXBParameterValueType());
        r.setD(d.getJAXBParameterValueType());
        r.setE(e.getJAXBParameterValueType());
        r.setF(f.getJAXBParameterValueType());

        ObjectFactory of = new ObjectFactory();
        return of.createMatrix(r);
    }
    private RealParameter a;
    private RealParameter b;
    private RealParameter c;
    private RealParameter d;
    private RealParameter e;
    private RealParameter f;
}
