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
package org.gdms.data.indexes;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

public class DefaultAlphaQuery implements AlphaQuery {

	private Value min;
	private boolean minIncluded;
	private boolean maxIncluded;
	private Value max;
	private String fieldName;

	public DefaultAlphaQuery(String fieldName, Value exactValue) {
		this(fieldName, exactValue, true, exactValue, true);
	}

	public DefaultAlphaQuery(String fieldName, Value min, boolean minIncluded,
			Value max, boolean maxIncluded) {
		this.min = min;
		this.minIncluded = minIncluded;
		this.max = max;
		this.maxIncluded = maxIncluded;
		this.fieldName = fieldName;

		if (this.min == null) {
			this.min = ValueFactory.createNullValue();
		}

		if (this.max == null) {
			this.max = ValueFactory.createNullValue();
		}
	}

	public String getFieldName() {
		return fieldName;
	}

	public boolean isStrict() {
		return true;
	}

	public Value getMin() {
		return min;
	}

	public boolean isMinIncluded() {
		return minIncluded;
	}

	public boolean isMaxIncluded() {
		return maxIncluded;
	}

	public Value getMax() {
		return max;
	}

}
