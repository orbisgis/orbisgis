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
package org.orbisgis.layerModel;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.gdms.driver.DriverException;
import org.orbisgis.PersistenceException;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.LegendListener;
import org.orbisgis.renderer.legend.RenderException;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.SymbolFactory;

public class LegendDecorator implements Legend, EditionListener {

	private final static Logger logger = Logger
			.getLogger(LegendDecorator.class);

	private Legend legend;
	private ArrayList<Symbol> symbols = new ArrayList<Symbol>();
	private SpatialDataSourceDecorator sds;

	public LegendDecorator(Legend legend) {
		this.legend = legend;
	}

	public String getLegendTypeName() {
		return legend.getLegendTypeName();
	}

	public String getName() {
		return legend.getName();
	}

	public Symbol getSymbol(SpatialDataSourceDecorator sds, long row)
			throws RenderException {
		return symbols.get((int) row);
	}

	public void initialize(SpatialDataSourceDecorator ds)
			throws RenderException {
		this.sds = ds;
		symbols.clear();
		try {
			for (int i = 0; i < ds.getRowCount(); i++) {
				Symbol symbol = legend.getSymbol(sds, i);
				if (symbol != null) {
					symbols.add(symbol);
				} else {
					symbols.add(SymbolFactory.createNullSymbol());
				}
			}
		} catch (DriverException e) {
			throw new RenderException("Cannot cache the symbols", e);
		}
	}

	public void setName(String name) {
		legend.setName(name);
	}

	public void multipleModification(MultipleEditionEvent e) {
		EditionEvent[] events = e.getEvents();
		for (EditionEvent editionEvent : events) {
			singleModification(editionEvent);
		}
	}

	public void singleModification(EditionEvent e) {
		switch (e.getType()) {
		case EditionEvent.DELETE:
			symbols.remove(e.getRowIndex());
			break;
		case EditionEvent.INSERT:
			try {
				symbols.add((int) e.getRowIndex(), legend.getSymbol(sds, e
						.getRowIndex()));
			} catch (RenderException e1) {
				symbols.add(SymbolFactory.createNullSymbol());
				logger.error("Cannot update symbol", e1);
			}
			break;
		case EditionEvent.MODIFY:
			try {
				symbols.set((int) e.getRowIndex(), legend.getSymbol(sds, e
						.getRowIndex()));
			} catch (RenderException e1) {
				symbols.add(SymbolFactory.createNullSymbol());
				logger.error("Cannot update symbol", e1);
			}
			break;
		case EditionEvent.RESYNC:
			try {
				initialize(sds);
			} catch (RenderException e1) {
				symbols.add(SymbolFactory.createNullSymbol());
				logger.error("Cannot update symbol", e1);
			}
		}
	}

	public void addLegendListener(LegendListener listener) {
		legend.addLegendListener(listener);
	}

	public void removeLegendListener(LegendListener listener) {
		legend.removeLegendListener(listener);
	}

	public Legend getLegend() {
		return legend;
	}

	public String getLegendTypeId() {
		return legend.getLegendTypeId();
	}

	public String getVersion() {
		return legend.getVersion();
	}

	public void load(File file, String version) throws PersistenceException {
		legend.load(file, version);
	}

	public Legend newInstance() {
		return legend.newInstance();
	}

	public void save(File file) throws PersistenceException {
		legend.save(file);
	}
}
