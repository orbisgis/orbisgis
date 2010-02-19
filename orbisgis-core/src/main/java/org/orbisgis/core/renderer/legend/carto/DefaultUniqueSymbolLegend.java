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
package org.orbisgis.core.renderer.legend.carto;

import java.awt.Graphics2D;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.RenderException;
import org.orbisgis.core.renderer.legend.carto.persistence.LegendContainer;
import org.orbisgis.core.renderer.legend.carto.persistence.UniqueSymbolLegendType;
import org.orbisgis.core.renderer.symbol.RenderUtils;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolManager;
import org.orbisgis.core.renderer.symbol.collection.persistence.SymbolType;

public class DefaultUniqueSymbolLegend extends AbstractCartoLegend implements
		UniqueSymbolLegend {

	private Symbol symbol;

	public Symbol getSymbol() {
		return symbol;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
		fireLegendInvalid();
	}

	public Symbol getSymbol(SpatialDataSourceDecorator sds, long row)
			throws RenderException {
		try {
			return RenderUtils.buildSymbolToDraw(getSymbol(), sds
					.getGeometry(row));
		} catch (DriverException e) {
			throw new RenderException("Cannot access layer contents" + e);
		}
	}

	public Object getJAXBObject() {
		UniqueSymbolLegendType xmlLegend = new UniqueSymbolLegendType();
		save(xmlLegend);
		SymbolManager sm = (SymbolManager) Services
				.getService(SymbolManager.class);
		if (symbol != null) {
			xmlLegend.setSymbol(sm.getJAXBSymbol(symbol));
		}
		LegendContainer xml = new LegendContainer();
		xml.setLegendDescription(xmlLegend);

		return xml;
	}

	public void setJAXBObject(Object jaxbObject) {
		LegendContainer xml = (LegendContainer) jaxbObject;
		UniqueSymbolLegendType xmlLegend = (UniqueSymbolLegendType) xml
				.getLegendDescription();
		load(xmlLegend);
		SymbolManager sm = (SymbolManager) Services
				.getService(SymbolManager.class);
		SymbolType xmlSymbol = xmlLegend.getSymbol();
		if (xmlSymbol != null) {
			setSymbol(sm.getSymbolFromJAXB(xmlSymbol));
		} else {
			setSymbol(null);
		}
	}

	public String getLegendTypeId() {
		return "org.orbisgis.legend.UniqueSymbol";
	}

	public Legend newInstance() {
		return new DefaultUniqueSymbolLegend();
	}

	public void drawImage(Graphics2D g) {
		new LegendLine(symbol, "Unique symbol").drawImage(g);
	}

	public int[] getImageSize(Graphics2D g) {
		return new LegendLine(symbol, "Unique symbol").getImageSize(g);
	}

	@Override
	public String getLegendTypeName() {
		return "Unique symbol";
	}

	@Override
	public int getSymbolsToUpdateOnRowModification() {
		return ONLY_AFFECTED;
	}

}
