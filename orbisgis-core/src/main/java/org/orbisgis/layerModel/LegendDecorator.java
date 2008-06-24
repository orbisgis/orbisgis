package org.orbisgis.layerModel;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.gdms.driver.DriverException;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.LegendListener;
import org.orbisgis.renderer.legend.RenderException;
import org.orbisgis.renderer.legend.Symbol;
import org.orbisgis.renderer.legend.SymbolFactory;

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
				symbols.add(legend.getSymbol(sds, i));
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
}
