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

import java.util.ArrayList;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class DefaultLabelLegend extends AbstractClassifiedLegend implements
		LabelLegend {

	private String labelSizeField;

	int fontSize = 10;

	@Override
	protected ArrayList<Symbol> doClassification(int fieldIndex)
			throws DriverException, RenderException {
		ArrayList<Symbol> ret = new ArrayList<Symbol>();
		SpatialDataSourceDecorator sds = getDataSource();
		for (int i = 0; i < sds.getRowCount(); i++) {
			Value v = sds.getFieldValue(i, fieldIndex);
			Symbol symbol = SymbolFactory.createLabelSymbol(v.getAsString(),
					getSize(sds, i));
			ret.add(symbol);
		}

		return ret;
	}

	private int getSize(SpatialDataSourceDecorator sds, int rowIndex)
			throws RenderException, DriverException {
		if (labelSizeField == null) {
			return fontSize;
		} else {
			int fieldIndex = sds.getFieldIndexByName(labelSizeField);
			if (fieldIndex != -1) {
				throw new RenderException("The label size field '"
						+ labelSizeField + "' does not exist");
			} else {
				return sds.getFieldValue(rowIndex, fieldIndex).getAsInt();
			}
		}
	}

	public void setLabelSizeField(String fieldName) throws DriverException {
		this.labelSizeField = fieldName;
		super.invalidateCache();
	}
	
	public String getLabelSizeField(){
		return this.labelSizeField;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	
	public int getFontSize(){
		return this.fontSize;
	}

	public String getLegendTypeName() {
		return "Label legend";
	}
}
