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


package org.orbisgis.core.renderer.se.parameter.real;

import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.persistance.ogc.LiteralType;
import org.orbisgis.core.renderer.se.parameter.Literal;

public class RealLiteral extends Literal implements RealParameter {

	public static final RealLiteral ZERO = new RealLiteral(0.0);
	private double v;
	private RealParameterContext ctx;

	public RealLiteral() {
		v = 1.0;
		ctx = RealParameterContext.realContext;
	}

	public RealLiteral(double literal) {
		v = literal;
		ctx = RealParameterContext.realContext;
	}

	public RealLiteral(String d) {
		this.v = new Double(d);
		ctx = RealParameterContext.realContext;
	}

	public RealLiteral(JAXBElement<LiteralType> l) {
		this(l.getValue().getContent().get(0).toString());
		ctx = RealParameterContext.realContext;
	}

	@Override
	public double getValue(SpatialDataSourceDecorator sds, long fid) {
		return v;
	}

	public void setValue(double value) {
		v = value;
		checkContext();
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

	private void checkContext(){
		if (ctx != null && ! Double.isNaN(v)) {
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
        if (o instanceof RealLiteral){
           RealLiteral ol = (RealLiteral) o;
           double v1 = this.getValue(null, -1);
           double v2 = ol.getValue(null, -1);

           if (v1 < v2)
               return -1;
           else if (v1 > v2)
               return 1;
           else
              return 0;
        }

        return 0;
    }
}
