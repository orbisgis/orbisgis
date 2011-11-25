/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.renderer.classification;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.utils.I18N;

/**
 * Adapted from SCAP3 : http://w3.geoprdc.univ-tlse2.fr/scap/java/
 * 
 */
public class ProportionalMethod {

    int LINEAR = 1;

	private DataSource ds;
	private double maxValue;
	private RealParameter value;

    int LOGARITHMIC = 2;


    private final static int MIN_SURFACE = 10;

    // The surface reference must be greater or equals than 10.

    private double minSymbolArea;


    private double minValue;


    private int method;


    public ProportionalMethod(DataSource ds, RealParameter value) {
        this.ds = ds;
        this.value = value;
    }

    // TODO what the surfRef parameter is used to

    public void build(double minSymbolArea) throws DriverException,
            ClassificationMethodException,
            ParameterException {

        double[] valeurs = ClassificationUtils.getSortedValues(ds, value);

        maxValue = valeurs[valeurs.length - 1];
        minValue = valeurs[0];

        if ((method == LOGARITHMIC) && (minValue <= 0)) {
            throw new ClassificationMethodException(
                    I18N.getString("orbisgis.org.orbisgis.core.renderer.legend.carto.defaultProportionalLegend.symbolSize.logarithmic"));

        }
        if (minSymbolArea >= 10) {
            this.minSymbolArea = minSymbolArea;
        } else {
            this.minSymbolArea = MIN_SURFACE;
        }

    }


    public double getSymbolCoef() {
        return minSymbolArea / maxValue;
    }


    public double getMaxValue() {
        return maxValue;
    }


    /**
     * Compute the symbol size using a linear method Adpated from SCAP3 :
     * http://w3.geoprdc.univ-tlse2.fr/scap/java/
     * 
     * @param value
     * @param coefType
     * @return
     */
    public double getLinearSize(double value, int coefType) {
        double coefSymb = Math.abs(getSymbolCoef());

        double surface = Math.abs(value) * coefSymb;

        return Math.sqrt(surface / coefType);
    }


    /**
     * Compute the symbol size using a squareroot method Adpated from SCAP3 :
     * http://w3.geoprdc.univ-tlse2.fr/scap/java/
     * 
     * @param value
     * @param sqrtFactor
     * @param coefType
     * @return
     */
    public double getSquareSize(double value, double sqrtFactor, int coefType) {
        double coefSymb = Math.abs(minSymbolArea
                / (Math.pow(getMaxValue(), (1 / sqrtFactor))));
        double surface = Math.pow(Math.abs(value), (1 / sqrtFactor)) * coefSymb;

        return Math.sqrt(surface / coefType);
    }


    /**
     * Compute the symbol size using a logarithm method Adpated from SCAP3 :
     * http://w3.geoprdc.univ-tlse2.fr/scap/java/
     * 
     * @param value
     * @param coefType
     * @return
     */
    public double getLogarithmicSize(double value, int coefType) {
        double coefSymb = Math.abs(minSymbolArea
                / Math.log(Math.abs(getMaxValue())));

        double surface = Math.abs(Math.log(Math.abs(value))) * coefSymb;

        return Math.sqrt(surface / coefType);
    }


    public double getMinValue() {
        return minValue;
    }


    public void setMethod(int method) {
        this.method = method;
    }


    public int getMethod() {
        return method;
    }


}
