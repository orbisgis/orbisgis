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



package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import net.opengis.se._2_0.core.InterpolateType;
import net.opengis.se._2_0.core.InterpolationPointType;
import net.opengis.se._2_0.core.ModeType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

import org.orbisgis.core.renderer.se.parameter.Interpolate;
import org.orbisgis.core.renderer.se.parameter.InterpolationPoint;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;


public class Interpolate2Color extends Interpolate<ColorParameter, ColorLiteral> implements ColorParameter {

    public Interpolate2Color(ColorLiteral fallback){
        super(fallback);
    }
    
    public Interpolate2Color(JAXBElement<InterpolateType> expr) throws InvalidStyle {
        InterpolateType t = expr.getValue();
        
        this.setFallbackValue(new ColorLiteral(t.getFallbackValue()));

        this.setLookupValue(SeParameterFactory.createRealParameter(t.getLookupValue()));

		if (t.getMode() == ModeType.COSINE) {
            this.setInterpolationMode(InterpolationMode.COSINE);
        } else if (t.getMode() == ModeType.CUBIC) {
            this.setInterpolationMode(InterpolationMode.CUBIC);
        } else {
			System.out.println ("Fallback to linear mode !");
            this.setInterpolationMode(InterpolationMode.LINEAR);
        }

        for (InterpolationPointType ipt : t.getInterpolationPoint()){
            InterpolationPoint<ColorParameter> ip = new InterpolationPoint<ColorParameter>();

            ip.setData(ipt.getData());
            ip.setValue(SeParameterFactory.createColorParameter(ipt.getValue()));

            this.addInterpolationPoint(ip);
        }

    }
    /**
     *
     * @param ds
     * @param fid
     * @return
     */
    @Override
    public Color getColor(SpatialDataSourceDecorator sds, long fid) throws ParameterException{
		double value = this.getLookupValue().getValue(sds, fid);

        int numPt = getNumInterpolationPoint();
        
		if (getInterpolationPoint(0).getData() >= value){
			return getInterpolationPoint(0).getValue().getColor(sds, fid);
		}

		if (getInterpolationPoint(numPt-1).getData() <= value){
			return getInterpolationPoint(numPt-1 ).getValue().getColor(sds, fid);
		}


		int k = getFirstIP(value);

		InterpolationPoint<ColorParameter> ip1 = getInterpolationPoint(k);
		InterpolationPoint<ColorParameter> ip2 = getInterpolationPoint(k+1);

		double d1 = ip1.getData();
		double d2 = ip2.getData();

		Color c1 = ip1.getValue().getColor(sds, fid);
		Color c2 = ip2.getValue().getColor(sds, fid);


		switch(this.getMode()){
		case CUBIC:
			return new Color((int)cubicInterpolation(d1, d2, value, c1.getRed(), c2.getRed(), -1.0, -1.0),
					(int)cubicInterpolation(d1, d2, value, c1.getGreen(), c2.getGreen(), -1.0, -1.0),
					(int)cubicInterpolation(d1, d2, value, c1.getBlue(), c2.getBlue(), -1.0, 1.0));
		case COSINE:
			return new Color((int)cosineInterpolation(d1, d2, value, c1.getRed(), c2.getRed()),
					(int)cosineInterpolation(d1, d2, value, c1.getGreen(), c2.getGreen()),
					(int)cosineInterpolation(d1, d2, value, c1.getBlue(), c2.getBlue()));
		case LINEAR:
			return new Color((int)linearInterpolation(d1, d2, value, c1.getRed(), c2.getRed()),
					(int)linearInterpolation(d1, d2, value, c1.getGreen(), c2.getGreen()),
					(int)linearInterpolation(d1, d2, value, c1.getBlue(), c2.getBlue()));
		}
        return Color.pink; // TODO compute interpolation
    }
}
