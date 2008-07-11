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

package org.orbisgis.editorViews.toc.actions.cui.components;

import java.awt.Component;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.orbisgis.Services;
import org.orbisgis.SymbolManager;
import org.orbisgis.editorViews.toc.actions.cui.EditableSymbolFilter;
import org.orbisgis.editorViews.toc.actions.cui.SymbolEditor;
import org.orbisgis.editorViews.toc.actions.cui.SymbolFilter;
import org.orbisgis.editorViews.toc.actions.cui.extensions.LegendContext;
import org.orbisgis.images.IconLoader;
import org.orbisgis.renderer.legend.carto.LegendFactory;
import org.orbisgis.renderer.legend.carto.UniqueSymbolLegend;
import org.orbisgis.renderer.symbol.EditableSymbol;
import org.orbisgis.renderer.symbol.Symbol;
import org.sif.UIFactory;
import org.sif.UIPanel;

/**
 *
 * @author david
 */
public class SymbolCollection extends javax.swing.JPanel implements
		UIPanel {
	private LegendContext legendContext;
	private ArrayList<SelectableCanvas> selection = new ArrayList<SelectableCanvas>();

	/**
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public SymbolCollection(LegendContext legendContext) {
		this.legendContext = legendContext;
		initComponents();
		refreshInterface();

		// Add all the symbols as components
		SymbolManager sm = (SymbolManager) Services
				.getService("org.orbisgis.SymbolManager");
		for (int i = 0; i < sm.getSymbolCount(); i++) {
			addSymbolToPanel(sm.getSymbol(i));
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {

		jPanelPreviewSymbols = new javax.swing.JPanel();
		jToolBar1 = new javax.swing.JToolBar();
		jButtonAdd = new javax.swing.JButton();
		jButtonEdit = new javax.swing.JButton();
		jButtonDel = new javax.swing.JButton();

		jPanelPreviewSymbols.setBorder(javax.swing.BorderFactory
				.createEtchedBorder());
		jPanelPreviewSymbols.setLayout(new java.awt.FlowLayout(
				java.awt.FlowLayout.LEFT));

		jToolBar1.setFloatable(false);
		jToolBar1.setRollover(true);

		jButtonAdd.setIcon(IconLoader.getIcon("add.png"));
		jButtonAdd.setToolTipText("Add");
		jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonAddActionPerformed(evt);
			}
		});
		jToolBar1.add(jButtonAdd);

		jButtonEdit.setIcon(IconLoader.getIcon("pencil.png"));
		jButtonEdit.setToolTipText("Edit");
		jButtonEdit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonEditActionPerformed(evt);
			}
		});
		jToolBar1.add(jButtonEdit);

		jButtonDel.setIcon(IconLoader.getIcon("delete.png"));
		jButtonDel.setToolTipText("Delete");
		jButtonDel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonDelActionPerformed(evt);
			}
		});
		jToolBar1.add(jButtonDel);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																jPanelPreviewSymbols,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																453,
																Short.MAX_VALUE)
														.addComponent(
																jToolBar1,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																453,
																Short.MAX_VALUE))
										.addContainerGap()));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												jToolBar1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												25,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jPanelPreviewSymbols,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												367, Short.MAX_VALUE)
										.addContainerGap()));
	}// </editor-fold>//GEN-END:initComponents

	private void jButtonDelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonDelActionPerformed
		Component[] comps = jPanelPreviewSymbols.getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof SelectableCanvas) {
				SelectableCanvas can = (SelectableCanvas) comps[i];

				if (selection.contains(can)) {
					selection.remove(can);
					jPanelPreviewSymbols.remove(can);
				}
			}
		}

		refreshInterface();

	}// GEN-LAST:event_jButtonDelActionPerformed

	private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {
		Component[] comps = jPanelPreviewSymbols.getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof SelectableCanvas) {
				SelectableCanvas can = (SelectableCanvas) comps[i];

				if (selection.contains(can)) {
					Symbol sym = can.getSymbol().cloneSymbol();
					UniqueSymbolLegend leg = LegendFactory
							.createUniqueSymbolLegend();
					leg.setSymbol(sym);
					SymbolEditor usl = new SymbolEditor(false, legendContext,
							getSymbolFilter());

					if (UIFactory.showDialog(usl)) {
						can.setSymbol(usl.getSymbolComposite());
					}

				}

			}
		}
		refreshInterface();
	}

	private SymbolFilter getSymbolFilter() {
		return new EditableSymbolFilter();
	}

	private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {
		SymbolManager sm = (SymbolManager) Services
				.getService("org.orbisgis.SymbolManager");

		SymbolSelection type = new SymbolSelection(sm.getAvailableSymbols());
		if (UIFactory.showDialog(type)) {
			EditableSymbol sym = (EditableSymbol) type.getSelected();

			SymbolEditor se = new SymbolEditor(false, legendContext,
					getSymbolFilter());
			se.setSymbol(sym);

			if (UIFactory.showDialog(se)) {
				Symbol newSymbol = se.getSymbolComposite();
				addSymbolToPanel(newSymbol);
				sm.addSymbol(newSymbol);
				refreshInterface();
			}

		}
	}

	private void jPanelPreviewSymbolsMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jPanelPreviewSymbolsMouseClicked
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

	}// GEN-LAST:event_jPanelPreviewSymbolsMouseClicked

	private void refreshInterface() {
		Component[] comps = jPanelPreviewSymbols.getComponents();
		jButtonDel.setEnabled(false);
		jButtonEdit.setEnabled(false);
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof SelectableCanvas) {
				SelectableCanvas can = (SelectableCanvas) comps[i];

				if (can.isSelected()) {
					jButtonDel.setEnabled(true);
					if (!jButtonEdit.isEnabled()) {
						jButtonEdit.setEnabled(true);
					} else {
						jButtonEdit.setEnabled(false);
					}

				}
			}
		}

		jPanelPreviewSymbols.validate();
		jPanelPreviewSymbols.repaint();
	}

	private void addSymbolToPanel(Symbol sym) {
		SelectableCanvas can = new SelectableCanvas();

		can.setSymbol(sym.cloneSymbol());
		can.setPreferredSize(new Dimension(126, 70));

		can.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jPanelPreviewSymbolsMouseClicked(evt);
			}
		});

		jPanelPreviewSymbols.add(can);
	}

	public Symbol getSelectedSymbol() {
		return selection.get(0).getSymbol().cloneSymbol();
	}

	private SelectableCanvas getSelectedCanvas() {
		Component[] comps = jPanelPreviewSymbols.getComponents();
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

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jButtonAdd;
	private javax.swing.JButton jButtonDel;
	private javax.swing.JButton jButtonEdit;
	private javax.swing.JPanel jPanelPreviewSymbols;
	private javax.swing.JToolBar jToolBar1;

	// End of variables declaration//GEN-END:variables

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
		if (symbol instanceof EditableSymbol) {
			if (!((EditableSymbol) symbol).acceptGeometryType(legendContext
					.getGeometryConstraint())) {
				return "The symbol type is not applicable";
			} else {
				return null;
			}
		} else if (symbol.acceptsChildren()) {
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
