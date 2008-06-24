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
package org.orbisgis.renderer.legend;

import java.awt.Color;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.orbisgis.renderer.classification.ProportionalMethod;

public class DefaultProportionalLegend extends AbstractClassifiedLegend
		implements ProportionalLegend {
	private static final int LINEAR = 1;
	private static final int SQUARE = 2;
	private static final int LOGARITHMIC = 3;

	private Color fill = Color.red;
	private Color outline = Color.black;

	private int minSymbolArea = 3000;
	private int method = LINEAR;
	private double sqrtFactor;

	public void setOutlineColor(Color outline) {
		this.outline = outline;
		fireLegendInvalid();
	}

	public void setFillColor(Color fill) {
		this.fill = fill;
		fireLegendInvalid();
	}

	public Color getOutlineColor() {
		return outline;
	}

	public Color getFillColor() {
		return fill;
	}

	public void setMinSymbolArea(int minSymbolArea) {
		this.minSymbolArea = minSymbolArea;
		fireLegendInvalid();
	}

	public void setLinearMethod() throws DriverException {
		method = LINEAR;
		fireLegendInvalid();
	}

	public void setSquareMethod(double sqrtFactor) throws DriverException {
		method = SQUARE;
		this.sqrtFactor = sqrtFactor;
		fireLegendInvalid();
	}

	public void setLogarithmicMethod() throws DriverException {
		method = LOGARITHMIC;
		fireLegendInvalid();
	}

	public String getLegendTypeName() {
		return "Proportional Legend";
	}

	public Symbol getSymbol(SpatialDataSourceDecorator sds, long row)
			throws RenderException {
		try {
			ProportionalMethod proportionnalMethod = new ProportionalMethod(
					sds, getClassificationField());
			proportionnalMethod.build(minSymbolArea);

			// TODO what's the use of this variable
			int coefType = 1;

			double symbolSize = 0;
			int fieldIndex = sds.getSpatialFieldIndex();
			double value = sds.getFieldValue(row, fieldIndex).getAsDouble();

			switch (method) {

			case LINEAR:
				symbolSize = proportionnalMethod.getLinearSize(value, coefType);

				break;

			case SQUARE:
				symbolSize = proportionnalMethod.getSquareSize(value,
						sqrtFactor, coefType);

				break;

			case LOGARITHMIC:

				symbolSize = proportionnalMethod.getLogarithmicSize(value,
						coefType);

				break;
			}
			return SymbolFactory.createCirclePolygonSymbol(outline, fill,
					(int) Math.round(symbolSize));
		} catch (IncompatibleTypesException e) {
			throw new RenderException("Cannot calculate proportionalities" + e);
		} catch (DriverException e) {
			throw new RenderException("Cannot access layer contents" + e);
		}
	}
}
