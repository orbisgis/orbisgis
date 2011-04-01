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

import java.awt.Font;

/**
 *
 * @author maxence
 */
public class RealParameterContext {

	protected Double min;
	protected Double max;

	public RealParameterContext(Double min, Double max){
		this.min = min;
		this.max = max;
	}

	public Double getMin(){
		return min;
	}
	public Double getMax(){
		return max;
	}

    @Override
	public String toString(){
		return " [" + min + ";" + max + "]";
	}

	static {
		percentageContext = new RealParameterContext(0.0, 1.0);
		nonNegativeContext = new RealParameterContext(0.0, null);
		realContext = new RealParameterContext(null, null);
	};

	public static RealParameterContext percentageContext;
	public static RealParameterContext nonNegativeContext;
	public static RealParameterContext realContext;

	public class MarkIndexContext extends RealParameterContext {

		private Font font;
		public MarkIndexContext(Font font){
			super(0.0, 0.0);
			this.font = font;
			this.max = (double)(font.getNumGlyphs() - 1);
		}

		public Font getFont(){
			return font;
		}
	}
}
