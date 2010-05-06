/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.transform;

import com.vividsolutions.jts.geom.util.AffineTransformation;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealBinaryOperator;
import org.orbisgis.core.renderer.se.parameter.real.RealBinaryOperatorType;
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
    public AffineTransform getAffineTransform(DataSource ds, int fid, Uom uom) throws ParameterException {
        return new AffineTransform( // TODO DPI !
                Uom.toPixel(a.getValue(ds, fid), uom, 96, 25000),
                Uom.toPixel(b.getValue(ds, fid), uom, 96, 25000),
                Uom.toPixel(c.getValue(ds, fid), uom, 96, 25000),
                Uom.toPixel(d.getValue(ds, fid), uom, 96, 25000),
                Uom.toPixel(e.getValue(ds, fid), uom, 96, 25000),
                Uom.toPixel(f.getValue(ds, fid), uom, 96, 25000)
               );
    }

    @Override
    public boolean allowedForGeometries() {
        return false;
    }

    /**
     * Return a new matrix which is the product of this x b
     * Product is done with the help of RealBinaryOperator
     * @param b
     * @return
     */
    public Matrix product(Matrix b) {
        Matrix product = new Matrix();

        product.a = new RealBinaryOperator(
                new RealBinaryOperator(this.a, b.a, RealBinaryOperatorType.MUL),
                new RealBinaryOperator(this.c, b.b, RealBinaryOperatorType.MUL),
                RealBinaryOperatorType.ADD);

        product.b = new RealBinaryOperator(
                new RealBinaryOperator(this.b, b.a, RealBinaryOperatorType.MUL),
                new RealBinaryOperator(this.d, b.b, RealBinaryOperatorType.MUL),
                RealBinaryOperatorType.ADD);

        product.c = new RealBinaryOperator(
                new RealBinaryOperator(this.a, b.c, RealBinaryOperatorType.MUL),
                new RealBinaryOperator(this.c, b.d, RealBinaryOperatorType.MUL),
                RealBinaryOperatorType.ADD);

        product.d = new RealBinaryOperator(
                new RealBinaryOperator(this.b, b.c, RealBinaryOperatorType.MUL),
                new RealBinaryOperator(this.d, b.d, RealBinaryOperatorType.MUL),
                RealBinaryOperatorType.ADD);

        product.e = new RealBinaryOperator(
                new RealBinaryOperator(
                new RealBinaryOperator(this.a, b.e, RealBinaryOperatorType.MUL),
                new RealBinaryOperator(this.c, b.f, RealBinaryOperatorType.MUL),
                RealBinaryOperatorType.ADD),
                this.e,
                RealBinaryOperatorType.ADD);

        product.f = new RealBinaryOperator(
                new RealBinaryOperator(
                new RealBinaryOperator(this.b, b.e, RealBinaryOperatorType.MUL),
                new RealBinaryOperator(this.d, b.f, RealBinaryOperatorType.MUL),
                RealBinaryOperatorType.ADD),
                this.f,
                RealBinaryOperatorType.ADD);

        return product;
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



    public boolean equals(Matrix matrix, DataSource ds, int fid){
        try {
            if (Math.abs(this.a.getValue(ds, fid) - matrix.a.getValue(ds, fid)) > 1e-10) {
                return false;
            }
            if (Math.abs(this.b.getValue(ds, fid) - matrix.b.getValue(ds, fid)) > 1e-10) {
                return false;
            }
            if (Math.abs(this.c.getValue(ds, fid) - matrix.c.getValue(ds, fid)) > 1e-10) {
                return false;
            }
            if (Math.abs(this.d.getValue(ds, fid) - matrix.d.getValue(ds, fid)) > 1e-10) {
                return false;
            }
            if (Math.abs(this.e.getValue(ds, fid) - matrix.e.getValue(ds, fid)) > 1e-10) {
                return false;
            }
            if (Math.abs(this.f.getValue(ds, fid) - matrix.f.getValue(ds, fid)) > 1e-10) {
                return false;
            }
            return true;

        } catch (ParameterException ex) {
            return false;
        }

    }


    /**
     * For testing purpose...
     * @param ds
     * @param fid
     */

    public void print(DataSource ds, int fid){
        DecimalFormat df = new DecimalFormat("##.###");
        try {
            System.out.println(df.format(this.a.getValue(ds, fid)) + "\t"
                             + df.format(this.c.getValue(ds, fid)) + "\t"
                             + df.format(this.e.getValue(ds, fid)));

            System.out.println(df.format(this.b.getValue(ds, fid)) + "\t"
                             + df.format(this.d.getValue(ds, fid)) + "\t"
                             + df.format(this.f.getValue(ds, fid)));

            System.out.println("0.0\t0.0\t1.0");
            System.out.println("");

        } catch (ParameterException ex) {
            Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private RealParameter a;
    private RealParameter b;
    private RealParameter c;
    private RealParameter d;
    private RealParameter e;
    private RealParameter f;
}
