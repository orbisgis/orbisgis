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
 * JPanelSimpleLegend.java
 *
 * Created on 22 de febrero de 2008, 16:33
 */

package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolFactory;
import org.orbisgis.core.renderer.symbol.SymbolManager;
import org.orbisgis.core.ui.components.sif.AskValue;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.Canvas;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.SymbolCollection;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.SymbolSelection;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ISymbolEditor;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.images.IconLoader;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;

/**
 *
 * @author david
 */
public class SymbolBuilder extends JPanel implements UIPanel,
		SymbolEditorListener {

	private final class NoSuitableEditor implements ISymbolEditor {
		private Symbol symbol;

		public void setSymbolEditorListener(SymbolEditorListener listener) {
		}

		public void setSymbol(Symbol symbol) {
			this.symbol = symbol;
		}

		public ISymbolEditor newInstance() {
			return new NoSuitableEditor();
		}

		public Symbol getSymbol() {
			return symbol;
		}

		public Component getComponent() {
			return new JLabel("No suitable editor");
		}

		public boolean accepts(Symbol symbol) {
			return false;
		}
	}

	private Canvas canvas = new Canvas();
	private boolean showCollection = false;
	private LegendContext legendContext;
	private SymbolFilter symbolFilter;
	private JPanel pnlEdition;
	private SymbolListModel model;


	/** Creates new form JPanelSimpleSimbolLegend */
	public SymbolBuilder(boolean showCollection, LegendContext legendContext,
			SymbolFilter symbolFilter) {
		this.legendContext = legendContext;
		this.showCollection = showCollection;
		this.symbolFilter = symbolFilter;
		initComponents();
		model = new SymbolListModel();
		lstSymbols.setModel(model);
	}

	private void refresh() {
		if (!showCollection) {
			btnToCollection.setVisible(false);
		}
		canvas.setSymbol(getSymbolComposite());

		int[] idxs = lstSymbols.getSelectedIndices();
		int maximum = lstSymbols.getModel().getSize() - 1;
		int minimum = 0;

		if (maximum == -1) {
			idxs = new int[0];
		}

		if (idxs.length == 1) {
			int idx = idxs[0];
			if (idx == -1) {
				jButtonSymbolUp.setEnabled(false);
				jButtonSymbolDown.setEnabled(false);
				jButtonSymbolDel.setEnabled(false);
				jButtonSymbolRename.setEnabled(false);
			} else {
				jButtonSymbolDel.setEnabled(true);
				jButtonSymbolRename.setEnabled(true);
				jButtonSymbolDown.setEnabled(idx < maximum);
				jButtonSymbolUp.setEnabled(idx > minimum);
			}
		} else {
			if (idxs.length > 0) {
				jButtonSymbolDel.setEnabled(true);
			} else {
				jButtonSymbolDel.setEnabled(false);
			}
			jButtonSymbolUp.setEnabled(false);
			jButtonSymbolDown.setEnabled(false);
			jButtonSymbolRename.setEnabled(false);
		}

		btnToCollection.setEnabled(lstSymbols.getModel().getSize() > 0);
	}

	public void setSymbol(Symbol symbol) {
		if (symbol == null) {
			symbol = SymbolFactory.createSymbolComposite();
		} else {
			symbol = symbol.cloneSymbol();
		}
		model.removeAll();

		if (!(symbol.acceptsChildren())) {
			Symbol[] syms = new Symbol[1];
			syms[0] = symbol;
			symbol = SymbolFactory.createSymbolComposite(syms);
		}

		int numSymbols = symbol.getSymbolCount();

		for (int i = 0; i < numSymbols; i++) {
			Symbol singleSymbol = symbol.getSymbol(i);
			if (singleSymbol != null) {
				model.addElement(singleSymbol, getEditor(singleSymbol));
			}
		}

		if (model.getSize() > 0) {
			lstSymbols.clearSelection();
			lstSymbols.setSelectedIndex(0);
		}
		refresh();
	}

	/**
	 * returns a symbolcomposite with all the symbols in the list
	 *
	 * @return
	 */
	public Symbol getSymbolComposite() {
		int size = model.getSize();
		Symbol[] syms = new Symbol[size];
		for (int i = 0; i < size; i++) {
			syms[i] = model.getSymbol(i);
		}
		Symbol comp = SymbolFactory.createSymbolComposite(syms);
		return comp;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents() {

		jToolBar1 = new JToolBar();
		jButtonSymbolUp = new JButton();
		jButtonSymbolDown = new JButton();
		jButtonSymbolAdd = new JButton();
		jButtonSymbolDel = new JButton();
		jButtonSymbolRename = new JButton();
		jPanelPreview = new JPanel();
		canvasPreview = new JPanel();
		canvasPreview.add(canvas);
		jPanelButtonsCollection = new JPanel();
		btnToCollection = new JButton();
		btnFromCollection = new JButton();

		JPanel jPanelRight = getRightPanel();

		JPanel all = new JPanel();
		pnlEdition = new JPanel();
		pnlEdition.setLayout(new BorderLayout());
		all.add(pnlEdition);
		all.add(jPanelRight);

		add(all);

	}

	private JPanel getRightPanel() {
		JPanel jPanelRight = new JPanel();
		jPanelRight.setBorder(null);
		jPanelRight.setLayout(new BorderLayout());

		jToolBar1.setFloatable(false);
		jToolBar1.setRollover(true);

		jButtonSymbolUp.setIcon(IconLoader.getIcon("go-up.png"));
		jButtonSymbolUp.setToolTipText("Up");
		jButtonSymbolUp.setFocusable(false);
		jButtonSymbolUp.setHorizontalTextPosition(SwingConstants.CENTER);
		jButtonSymbolUp.setVerticalTextPosition(SwingConstants.BOTTOM);
		jButtonSymbolUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonSymbolUpActionPerformed(evt);
			}
		});
		jToolBar1.add(jButtonSymbolUp);

		jButtonSymbolDown.setIcon(IconLoader.getIcon("go-down.png"));
		jButtonSymbolDown.setToolTipText("Down");
		jButtonSymbolDown.setFocusable(false);
		jButtonSymbolDown.setHorizontalTextPosition(SwingConstants.CENTER);
		jButtonSymbolDown.setVerticalTextPosition(SwingConstants.BOTTOM);
		jButtonSymbolDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonSymbolDownActionPerformed(evt);
			}
		});
		jToolBar1.add(jButtonSymbolDown);

		jButtonSymbolAdd.setIcon(IconLoader.getIcon("add.png"));
		jButtonSymbolAdd.setToolTipText("Add");
		jButtonSymbolAdd.setFocusable(false);
		jButtonSymbolAdd.setHorizontalTextPosition(SwingConstants.CENTER);
		jButtonSymbolAdd.setVerticalTextPosition(SwingConstants.BOTTOM);
		jButtonSymbolAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonSymbolAddActionPerformed(evt);
			}
		});
		jToolBar1.add(jButtonSymbolAdd);

		jButtonSymbolDel.setIcon(IconLoader.getIcon("delete.png"));
		jButtonSymbolDel.setToolTipText("Delete");
		jButtonSymbolDel.setFocusable(false);
		jButtonSymbolDel.setHorizontalTextPosition(SwingConstants.CENTER);
		jButtonSymbolDel.setVerticalTextPosition(SwingConstants.BOTTOM);
		jButtonSymbolDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonSymbolDelActionPerformed(evt);
			}
		});
		jToolBar1.add(jButtonSymbolDel);

		jButtonSymbolRename.setIcon(IconLoader.getIcon("pencil.png"));
		jButtonSymbolRename.setToolTipText("Rename");
		jButtonSymbolRename.setFocusable(false);
		jButtonSymbolRename.setHorizontalTextPosition(SwingConstants.CENTER);
		jButtonSymbolRename.setVerticalTextPosition(SwingConstants.BOTTOM);
		jButtonSymbolRename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonSymbolRenameActionPerformed(evt);
			}
		});
		jToolBar1.add(jButtonSymbolRename);

		jPanelRight.add(jToolBar1, BorderLayout.PAGE_START);

		lstSymbols = new JList();
		lstSymbols.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				jList1ValueChanged();
			}
		});
		JScrollPane scrollPane = new JScrollPane(lstSymbols);
		scrollPane.setPreferredSize(new Dimension(200, 200));

		jPanelRight.add(scrollPane, BorderLayout.CENTER);

		canvasPreview.setBorder(null);
		canvasPreview.setMinimumSize(new Dimension(126, 70));
		canvasPreview.setPreferredSize(new Dimension(126, 70));
		canvasPreview.setLayout(new GridLayout(1, 0));
		jPanelPreview.add(canvasPreview);

		jPanelButtonsCollection.setLayout(new BoxLayout(
				jPanelButtonsCollection, BoxLayout.PAGE_AXIS));

		btnToCollection.setIcon(IconLoader.getIcon("save.png"));
		btnToCollection.setToolTipText("Save");
		btnToCollection.setFocusable(false);
		btnToCollection.setHorizontalTextPosition(SwingConstants.CENTER);
		btnToCollection.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnToCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonToCollectionActionPerformed(evt);
			}
		});
		jPanelButtonsCollection.add(btnToCollection);

		btnFromCollection.setIcon(IconLoader.getIcon("add.png"));
		btnFromCollection.setToolTipText("Add");
		btnFromCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonFromCollectionActionPerformed(evt);
			}
		});
		jPanelButtonsCollection.add(btnFromCollection);

		jPanelPreview.add(jPanelButtonsCollection);

		jPanelRight.add(jPanelPreview, BorderLayout.SOUTH);
		return jPanelRight;
	}

	/**
	 * open the collection window in order to select one symbol.
	 *
	 * @param evt
	 */
	private void jButtonFromCollectionActionPerformed(ActionEvent evt) {
		SymbolCollection coll = new SymbolCollection(legendContext);
		if (UIFactory.showDialog(coll)) {
			Symbol sym = coll.getSelectedSymbol();

			if (sym.acceptsChildren()) {
				int numSymbols = sym.getSymbolCount();

				for (int i = 0; i < numSymbols; i++) {
					Symbol simpSym = sym.getSymbol(i);
					if (simpSym != null) {
						model.addElement(simpSym, getEditor(simpSym));
					}
				}
			} else {
				model.addElement(sym, getEditor(sym));
			}

			if (model.getSize() > 0) {
				lstSymbols.setSelectedIndex(0);
			}
			refresh();
		}
	}

	/**
	 * move up the selected symbol in the list
	 *
	 * @param evt
	 */
	private void jButtonSymbolUpActionPerformed(ActionEvent evt) {
		int idx = lstSymbols.getSelectedIndex();
		if (idx > 0) {
			model.moveUp(idx);
			lstSymbols.setSelectedIndex(idx - 1);
			refresh();
		}
	}

	/**
	 * When the selected value in the list is changed we will call to the
	 * refresh functions with the new symbol.
	 *
	 * @param evt
	 *            ;
	 */
	private void jList1ValueChanged() {
		pnlEdition.removeAll();
		int index = lstSymbols.getSelectedIndex();
		if ((model.getSize() > 0) && (index != -1)) {
			ISymbolEditor selected = model.getEditor(index);

			if (selected != null) {
				pnlEdition.add(selected.getComponent(), BorderLayout.CENTER);
			} else {
				Services.getErrorManager().error(
						"There is no suitable editor for the symbol");
			}

			refresh();
		}
		pnlEdition.invalidate();
		pnlEdition.repaint();
	}

	private ISymbolEditor getEditor(Symbol selectedSymbol) {
		ISymbolEditor[] editors = legendContext.getAvailableSymbolEditors();
		ISymbolEditor selected = null;
		for (int i = 0; i < editors.length; i++) {
			if (editors[i].accepts(selectedSymbol)) {
				selected = editors[i];
			}
		}
		if (selected == null) {
			return new NoSuitableEditor();
		} else {
			ISymbolEditor ret = selected.newInstance();
			ret.setSymbolEditorListener(this);
			return ret;
		}
	}

	/**
	 * moves down the symbol
	 *
	 * @param evt
	 */
	private void jButtonSymbolDownActionPerformed(ActionEvent evt) {
		int idx = lstSymbols.getSelectedIndex();
		if (idx < model.getSize() - 1) {
			model.moveDown(idx);
			lstSymbols.setSelectedIndex(idx + 1);
			refresh();
		}
	}

	/**
	 * adds a symbol in the list
	 *
	 * @param evt
	 */
	private void jButtonSymbolAddActionPerformed(ActionEvent evt) {

		// Filter the available symbols
		SymbolManager sm = (SymbolManager) Services
				.getService(SymbolManager.class);
		ArrayList<Symbol> availableSymbols = sm.getAvailableSymbols();
		ArrayList<Symbol> filtered = new ArrayList<Symbol>();
		for (Symbol availableSymbol : availableSymbols) {
			if (symbolFilter.accept(availableSymbol)) {
				filtered.add(availableSymbol);
			}
		}

		SymbolSelection sel = new SymbolSelection(filtered);
		if (UIFactory.showDialog(sel)) {
			Symbol selectedSymbol = (Symbol) sel.getSelected();
			model.addElement(selectedSymbol, getEditor(selectedSymbol));
			lstSymbols.setSelectedIndex(model.getSize() - 1);
			jList1ValueChanged();
			refresh();
		}
	}

	/**
	 * delete the selected symbols
	 *
	 * @param evt
	 */
	private void jButtonSymbolDelActionPerformed(ActionEvent evt) {
		int[] indexes = lstSymbols.getSelectedIndices();
		Arrays.sort(indexes);
		for (int i = indexes.length - 1; i >= 0; i--) {
			model.removeElementAt(indexes[i]);
		}
		if (model.getSize() > 0) {
			lstSymbols.setSelectedIndex(0);
		}
		jList1ValueChanged();
		refresh();
	}

	/**
	 * rename the selected symbol
	 *
	 * @param evt
	 */
	private void jButtonSymbolRenameActionPerformed(ActionEvent evt) {
		int idx = lstSymbols.getSelectedIndex();
		Symbol selectedSymbol = model.getSymbol(idx);
		AskValue ask = new AskValue("Insert the new name", "txt is not null",
				"A name must be specified", selectedSymbol.getName());
		String new_name = "";
		if (UIFactory.showDialog(ask)) {
			new_name = ask.getValue();

			if (new_name != null && new_name != "") {
				selectedSymbol.setName(new_name);
				model.refresh();
				lstSymbols.setSelectedIndex(idx);

				refresh();
			}
		}
	}

	/**
	 * gets the symbols and adds to the collection as a new Composite
	 *
	 * @param evt
	 */
	private void jButtonToCollectionActionPerformed(ActionEvent evt) {
		Geocognition geocognition = Services.getService(Geocognition.class);
		geocognition.addElement(geocognition.getUniqueId("symbol"),
				getSymbolComposite());
		JOptionPane.showMessageDialog(this, "Symbol saved in geocognition");
	}

	private JPanel canvasPreview;
	private JButton btnFromCollection;
	private JButton jButtonSymbolAdd;
	private JButton jButtonSymbolDel;
	private JButton jButtonSymbolDown;
	private JButton jButtonSymbolRename;
	private JButton jButtonSymbolUp;
	private JButton btnToCollection;
	private JList lstSymbols;
	private JPanel jPanelButtonsCollection;
	private JPanel jPanelPreview;
	private JToolBar jToolBar1;
	private SymbolEditionValidation validation;

	public Component getComponent() {
		return this;
	}

	public URL getIconURL() {
		return UIFactory.getDefaultIcon();
	}

	public String getInfoText() {
		return UIFactory.getDefaultOkMessage();
	}

	public String getTitle() {
		return "Symbol edition";
	}

	public String initialize() {
		return null;
	}

	public String postProcess() {
		return null;
	}

	public String validateInput() {
		if (lstSymbols.getModel().getSize() == 0) {
			return "At least a symbol must be created";
		}

		if (validation != null) {
			String txt = validation.isValid(getSymbolComposite());
			if (txt != null) {
				return txt;
			}
		}

		return null;
	}

	public void setSymbolFilter(SymbolFilter symbolFilter) {
		this.symbolFilter = symbolFilter;
	}

	public void symbolChanged() {
		canvas.setSymbol(getSymbolComposite());
	}

	public void setValidation(SymbolEditionValidation symbolEditionValidation) {
		this.validation = symbolEditionValidation;
	}

}