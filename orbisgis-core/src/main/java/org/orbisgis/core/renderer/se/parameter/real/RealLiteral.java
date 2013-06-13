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
package org.orbisgis.core.renderer.se.parameter.real;

import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.LiteralType;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.orbisgis.core.renderer.se.parameter.Literal;

/**
 * The representation of a real literal, bounded in a real context. Boundaries are set
 * using a RealParameterContext.
 * @author Alexis Guéganno
 */
public class RealLiteral extends Literal implements RealParameter {

        public static final RealLiteral ZERO = new RealLiteral(0.0);
        private double v;
        private RealParameterContext ctx;

        /**
         * Create a new RealLiteral, in a <code>REAL_CONTEXT</code> context.
         * Embedded value is <code>1.0</code>
         */
        public RealLiteral() {
                v = 1.0;
                ctx = RealParameterContext.REAL_CONTEXT;
        }

        /**
         * Create a new RealLiteral with value <code>literal</code> in a REAL_CONTEXT.
         * @param literal 
         */
        public RealLiteral(double literal) {
                v = literal;
                ctx = RealParameterContext.REAL_CONTEXT;
        }

        /**
         * Create a new RealLiteral with value <code>d</code>, by transforming d
         * to a double, in a REAL_CONTEXT.
         * @param d
         */
        public RealLiteral(String d) {
                this.v = new Double(d);
                ctx = RealParameterContext.REAL_CONTEXT;
        }

        /**
         * Create a new RealLiteral with value <code>l</code>, by transforming l
         * to a double, in a REAL_CONTEXT.
         * @param l
         */
        public RealLiteral(JAXBElement<LiteralType> l) {
                this(l.getValue().getContent().get(0).toString());
                ctx = RealParameterContext.REAL_CONTEXT;
        }

        public RealLiteral(LiteralType l){
                this(l.getContent().get(0).toString());
                ctx = RealParameterContext.REAL_CONTEXT;
        }

        @Override
        public Double getValue(DataSet sds, long fid) {
                return v;
        }

        @Override
        public Double getValue(Map<String,Value> map) {
                return v;
        }

        /**
         * sets the double value embedded in this RealLiteral
         * @param value 
         */
        public void setValue(double value) {
                v = value;
                checkContext();
                fireChange();
        }

        @Override
        public String toString() {
                return Double.toString(v);
        }

        @Override
        public void setContext(RealParameterContext ctx) {
                this.ctx = ctx;
                checkContext();
        }

        @Override
        public RealParameterContext getContext() {
                return ctx;
        }

        /**
         * Check that the registered double (that determines the double value of this)
         * is valid, according to the inner RealParameterContext. If it is not, the
         * double value is set to one of the extrem authorized values.
         */
        private void checkContext() {
                if (ctx != null && !Double.isNaN(v)) {
                        if (ctx.getMin() != null && this.v < ctx.getMin()) {
                                v = ctx.getMin();
                        }

                        if (ctx.getMax() != null && this.v > ctx.getMax()) {
                                v = ctx.getMax();
                        }
                }
        }

        @Override
        public int compareTo(Object o) {
                if (o instanceof RealLiteral) {
                        RealLiteral ol = (RealLiteral) o;
                        double v1 = this.getValue(null, -1);
                        double v2 = ol.getValue(null, -1);

                        if (v1 < v2) {
                                return -1;
                        } else if (v1 > v2) {
                                return 1;
                        } else {
                                return 0;
                        }
                }

                return 0;
        }

        @Override
        public boolean equals(Object o) {
                if (o instanceof RealLiteral) {
                        RealLiteral rl = (RealLiteral) o;
                        return this.v == rl.v;
                } else {
                        return false;
                }
        }

        @Override
        public int hashCode() {
                int hash = 7;
                hash = 67 * hash + (int) (Double.doubleToLongBits(this.v) ^ (Double.doubleToLongBits(this.v) >>> 32));
                return hash;
        }
}
