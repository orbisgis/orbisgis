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
/*
 * VentanaFlowLayoutPreview.java
 *
 * Created on 1 de mayo de 2008, 8:50
 */

package org.orbisgis.core.ui.editorViews.toc.actions.cui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendContext;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.GeocognitionFilter;
import org.orbisgis.sif.UIPanel;

/**
 *
 * @author david
 */
public class SymbolCollection extends javax.swing.JPanel implements UIPanel {
	private LegendContext legendContext;
	private JPanel pnlSymbols;
	private ArrayList<SelectableCanvas> selection = new ArrayList<SelectableCanvas>();

	public SymbolCollection(LegendContext legendContext) {
		this.legendContext = legendContext;
		initComponents();

		Geocognition geocognition = Services.getService(Geocognition.class);
		GeocognitionElement[] symbols = geocognition
				.getElements(new GeocognitionFilter() {

					@Override
					public boolean accept(GeocognitionElement element) {
						return element.getTypeId().equals(
								"org.orbisgis.core.geocognition.Symbol");
					}

				});
		for (int i = 0; i < symbols.length; i++) {
			SelectableCanvas can = new SelectableCanvas(symbols[i].getId());

			can.setSymbol(((Symbol) symbols[i].getObject()).cloneSymbol());
			can.setPreferredSize(new Dimension(126, 70));

			can.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent evt) {
					jPanelPreviewSymbolsMouseClicked(evt);
				}
			});

			pnlSymbols.add(can);
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {

		pnlSymbols = new JPanel();

		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
		pnlSymbols.setLayout(flowLayout);

		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);
		final JScrollPane s = new JScrollPane(pnlSymbols);
		s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		s.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				Dimension dimension = new Dimension(s.getWidth(), pnlSymbols
						.getSize().height);
				pnlSymbols.setPreferredSize(dimension);
			}

		});
		this.add(s);
	}

	private void jPanelPreviewSymbolsMouseClicked(java.awt.event.MouseEvent evt) {
		Component c = evt.getComponent();
		if (c instanceof SelectableCanvas) {
			SelectableCanvas can = (SelectableCanvas) c;
			if (evt.isControlDown()) {
				if (selection.contains(can)) {
					selection.remove(can);
					can.setSelected(false);
				} else {
					selection.add(can);
					can.setSelected(true);
				}
			} else {
				for (SelectableCanvas canvas : selection) {
					canvas.setSelected(false);
				}
				selection.clear();
				selection.add(can);
				can.setSelected(true);
			}

		}
		refreshInterface();

	}

	private void refreshInterface() {
		pnlSymbols.repaint();
	}

	public Symbol getSelectedSymbol() {
		return selection.get(0).getSymbol().cloneSymbol();
	}

	private SelectableCanvas getSelectedCanvas() {
		Component[] comps = pnlSymbols.getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof SelectableCanvas) {
				SelectableCanvas can = (SelectableCanvas) comps[i];

				if (selection.contains(can)) {
					return can;
				}
			}
		}
		return null;
	}

	public Component getComponent() {
		return this;
	}

	public URL getIconURL() {
		return null;
	}

	public String getInfoText() {
		return "Select a symbol to add it to the collection";
	}

	public String getTitle() {
		return "Symbol collection";
	}

	public String initialize() {
		return null;
	}

	public String postProcess() {

		return null;
	}

	public String validateInput() {
		if (selection.size() == 0) {
			return "A symbol must be selected";
		} else if (selection.size() > 1) {
			return "Only one symbol should be selected";
		} else {
			Symbol symbol = getSelectedCanvas().getSymbol();
			String ret = acceptsGeometry(symbol);
			if (ret != null) {
				return ret;
			}
		}

		return null;
	}

	private String acceptsGeometry(Symbol symbol) {
		if (!symbol.acceptsChildren()) {
			if (!symbol.acceptGeometryType(legendContext
					.getGeometryConstraint())) {
				return "The symbol type is not applicable";
			} else {
				return null;
			}
		} else {
			for (int i = 0; i < symbol.getSymbolCount(); i++) {
				String ret = acceptsGeometry(symbol.getSymbol(i));
				if (ret != null) {
					return ret;
				}
			}
		}

		return null;
	}
}
