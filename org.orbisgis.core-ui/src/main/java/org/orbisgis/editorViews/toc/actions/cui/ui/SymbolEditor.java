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

package org.orbisgis.editorViews.toc.actions.cui.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.orbisgis.Services;
import org.orbisgis.SymbolManager;
import org.orbisgis.editorViews.toc.actions.cui.gui.LegendContext;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.Canvas;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.ColorPicker;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.FlowLayoutPreviewWindow;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.SymbolListDecorator;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.SymbolSelection;
import org.orbisgis.images.IconLoader;
import org.orbisgis.renderer.symbol.EditableLineSymbol;
import org.orbisgis.renderer.symbol.EditablePointSymbol;
import org.orbisgis.renderer.symbol.EditablePolygonSymbol;
import org.orbisgis.renderer.symbol.EditableSymbol;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.SymbolFactory;
import org.orbisgis.ui.sif.AskValue;
import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;
import org.sif.UIFactory;
import org.sif.UIPanel;

/**
 *
 * @author david
 */
public class SymbolEditor extends JPanel implements UIPanel {

	private Canvas canvas = new Canvas();
	private boolean showCollection = false;
	private LegendContext legendContext;
	private boolean syncing = false;

	/** Creates new form JPanelSimpleSimbolLegend */
	public SymbolEditor(boolean showCollection, LegendContext legendContext) {
		this.legendContext = legendContext;
		this.showCollection = showCollection;
		initComponents();
		lstSymbols.setModel(new DefaultListModel());
	}

	/**
	 * if the symbol is not a composite symbol creates a composite and sets in
	 * it the non composite symbol.
	 *
	 * When we have a composite we fill the list.
	 */
	private void addToSymbolList(Symbol sym) {

		DefaultListModel mod = (DefaultListModel) lstSymbols.getModel();

		if (sym.acceptsChildren()) {
			int numSymbols = sym.getSymbolCount();

			for (int i = 0; i < numSymbols; i++) {
				Symbol simpSym = sym.getSymbol(i);
				if (simpSym != null) {
					SymbolListDecorator symbolUni = new SymbolListDecorator(
							(EditableSymbol) sym.getSymbol(i));
					mod.addElement(symbolUni);
				}
			}
		} else {
			SymbolListDecorator symbolUni = new SymbolListDecorator(
					(EditableSymbol) sym);
			mod.addElement(symbolUni);
		}

		if (mod.getSize() > 0) {
			lstSymbols.setSelectedIndex(0);
			syncSymbolControls(((SymbolListDecorator) lstSymbols
					.getSelectedValue()).getSymbol());
		}
		refreshListButtons();
	}

	/**
	 * Change the values of the components in the panel according to the symbol
	 *
	 * @param sym
	 */

	private void syncSymbolControls(Symbol sym) {
		syncing = true;
		chkFill.setSelected(true);
		chkLine.setSelected(true);

		if (sym instanceof EditableLineSymbol) {
			EditableLineSymbol symbol = (EditableLineSymbol) sym;
			Color lineColor = symbol.getOutlineColor();
			if (lineColor != null) {
				lblLine.setBackground(lineColor);
				chkLine.setSelected(true);
				sldTransparency.setValue(255 - lineColor.getAlpha());
			} else {
				chkLine.setSelected(false);
			}
			sldLineWidth.setValue(symbol.getLineWidth());
		}

		if (sym instanceof EditablePolygonSymbol) {
			EditablePolygonSymbol symbol = (EditablePolygonSymbol) sym;

			Color fillColor = symbol.getFillColor();
			if (fillColor != null) {
				lblFill.setBackground(fillColor);
				chkFill.setSelected(true);
				sldTransparency.setValue(255 - fillColor.getAlpha());
			} else {
				chkFill.setSelected(false);
			}
		}

		if (sym instanceof EditablePointSymbol) {
			EditablePointSymbol symbol = (EditablePointSymbol) sym;
			sldVertexSize.setValue(symbol.getSize());
		}

		syncing = false;
	}

	private void refreshListButtons() {
		int[] idxs = lstSymbols.getSelectedIndices();
		int maximo = lstSymbols.getModel().getSize() - 1;
		int minimo = 0;

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
				if (idx == minimo) {
					if (idx == maximo)
						jButtonSymbolDown.setEnabled(false);
					else
						jButtonSymbolDown.setEnabled(true);
					jButtonSymbolUp.setEnabled(false);
				} else {
					if (idx == maximo) {
						jButtonSymbolUp.setEnabled(true);
						jButtonSymbolDown.setEnabled(false);
					} else {
						jButtonSymbolUp.setEnabled(true);
						jButtonSymbolDown.setEnabled(true);
					}
				}
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
	}

	private void refresh() {
		syncListSymbolWithControls();
		boolean enabledFill = false, enabledLine = false, enabledVertex = false;
		if (!showCollection) {
			jButtonToCollection.setVisible(false);
		}

		EditableSymbol sym = ((SymbolListDecorator) lstSymbols
				.getSelectedValue()).getSymbol();
		if (sym instanceof EditablePolygonSymbol) {
			enabledFill = true;
			enabledLine = true;
		}
		if (sym instanceof EditableLineSymbol) {
			enabledLine = true;
		}
		if (sym instanceof EditablePointSymbol) {
			enabledVertex = true;
			enabledFill = true;
			enabledLine = true;
		}

		sldLineWidth.setVisible(enabledLine);
		txtLineWidth.setVisible(enabledLine);
		lblLineWidth.setVisible(enabledLine);

		sldVertexSize.setVisible(enabledVertex);
		txtVertexSize.setVisible(enabledVertex);
		lblSize.setVisible(enabledVertex);

		sldTransparency.setVisible(enabledFill || enabledLine);
		txtTransparency.setVisible(enabledFill || enabledLine);
		lblTransparency.setVisible(enabledFill || enabledLine);

		lblLine.setVisible(enabledLine);
		chkLine.setVisible(enabledLine);

		lblFill.setVisible(enabledFill);
		chkFill.setVisible(enabledFill);

		btnSync.setVisible(enabledFill && enabledLine);

		canvas.setSymbol(getSymbolComposite());

		refreshListButtons();
	}

	public void setSymbol(Symbol symbol) {
		DefaultListModel mod = (DefaultListModel) lstSymbols.getModel();
		mod.removeAllElements();

		if (!(symbol.acceptsChildren())) {
			Symbol[] syms = new Symbol[1];
			syms[0] = symbol;
			symbol = SymbolFactory.createSymbolComposite(syms);
		}

		int numSymbols = symbol.getSymbolCount();

		for (int i = 0; i < numSymbols; i++) {
			Symbol simpSym = symbol.getSymbol(i);
			if (simpSym != null) {
				SymbolListDecorator symbolUni = new SymbolListDecorator(
						(EditableSymbol) symbol.getSymbol(i));
				mod.addElement(symbolUni);
			}
		}

		if (mod.getSize() > 0) {
			lstSymbols.setSelectedIndex(0);
			syncSymbolControls(symbol.getSymbol(0));
		}
		refreshListButtons();
	}

	/**
	 * creates a symbol with the values of the components and returns it.
	 *
	 * @return Symbol
	 */
	private void syncListSymbolWithControls() {
		if (syncing) {
			return;
		}
		SymbolListDecorator symbolDec = (SymbolListDecorator) lstSymbols
				.getSelectedValue();
		EditableSymbol sym = symbolDec.getSymbol();

		if (sym instanceof EditableLineSymbol) {
			EditableLineSymbol symbol = (EditableLineSymbol) sym;
			Color lineColor = lblLine.getBackground();
			lineColor = new Color(lineColor.getRed(), lineColor.getGreen(),
					lineColor.getBlue(), 255 - sldTransparency.getValue());
			if (chkLine.isSelected()) {
				symbol.setOutlineColor(lblLine.getBackground());
			} else {
				symbol.setOutlineColor(null);
			}
			symbol.setLineWidth(sldLineWidth.getValue());
		}

		if (sym instanceof EditablePolygonSymbol) {
			EditablePolygonSymbol symbol = (EditablePolygonSymbol) sym;

			Color fillColor = lblFill.getBackground();
			fillColor = new Color(fillColor.getRed(), fillColor.getGreen(),
					fillColor.getBlue(), 255 - sldTransparency.getValue());
			if (chkFill.isSelected()) {
				symbol.setFillColor(fillColor);
			} else {
				symbol.setFillColor(null);
			}
		}

		if (sym instanceof EditablePointSymbol) {
			EditablePointSymbol symbol = (EditablePointSymbol) sym;
			symbol.setSize(sldVertexSize.getValue());
		}
	}

	/**
	 * returns a symbolcomposite with all the symbols in the list
	 *
	 * @return
	 */
	public Symbol getSymbolComposite() {
		DefaultListModel mod = (DefaultListModel) lstSymbols.getModel();
		int size = mod.getSize();
		Symbol[] syms = new Symbol[size];
		for (int i = 0; i < size; i++) {
			SymbolListDecorator dec = (SymbolListDecorator) mod.getElementAt(i);
			syms[i] = dec.getSymbol();
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

		pnlEdition = new JPanel();
		btnSync = new JButton();
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
		jButtonToCollection = new JButton();
		jButtonFromCollection = new JButton();

		CRFlowLayout flowLayout = new CRFlowLayout();
		pnlEdition.setLayout(flowLayout);

		JPanel pnlTexts = getPnlTexts();
		pnlEdition.add(pnlTexts);

		JPanel pnlColorChoosers = getPnlColorChoosers();
		pnlEdition.add(pnlColorChoosers);
		pnlEdition.add(new CarriageReturn());

		btnSync.setText("Synchronize colors");
		btnSync.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonSyncLineWithFillActionPerformed(evt);
			}
		});

		pnlEdition.add(btnSync);
		pnlEdition.add(new CarriageReturn());

		JPanel pnlSizeTexts = getPnlSizeTexts();
		pnlEdition.add(pnlSizeTexts);

		JPanel pnlSizeControls = getPnlSizeControls();
		pnlEdition.add(pnlSizeControls);
		pnlEdition.add(new CarriageReturn());

		JPanel jPanelRight = getRightPanel();

		JPanel all = new JPanel();
		all.add(pnlEdition);
		all.add(jPanelRight);

		add(all);

	}// </editor-fold>//GEN-END:initComponents

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
				jList1ValueChanged(evt);
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

		jButtonToCollection.setIcon(IconLoader.getIcon("Save.png"));
		jButtonToCollection.setToolTipText("Save");
		jButtonToCollection.setFocusable(false);
		jButtonToCollection.setHorizontalTextPosition(SwingConstants.CENTER);
		jButtonToCollection.setVerticalTextPosition(SwingConstants.BOTTOM);
		jButtonToCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonToCollectionActionPerformed(evt);
			}
		});
		jPanelButtonsCollection.add(jButtonToCollection);

		jButtonFromCollection.setIcon(IconLoader.getIcon("add.png"));
		jButtonFromCollection.setToolTipText("Add");
		jButtonFromCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonFromCollectionActionPerformed(evt);
			}
		});
		jPanelButtonsCollection.add(jButtonFromCollection);

		jPanelPreview.add(jPanelButtonsCollection);

		jPanelRight.add(jPanelPreview, BorderLayout.SOUTH);
		return jPanelRight;
	}

	private JPanel getPnlSizeControls() {
		JPanel pnlSizeControls = new JPanel();
		pnlSizeControls.setLayout(new CRFlowLayout());
		pnlSizeControls.add(new JLabel(""));
		pnlSizeControls.add(new CarriageReturn());
		sldLineWidth = new JSlider();
		sldLineWidth.setMaximum(30);
		sldLineWidth.setMinorTickSpacing(1);
		sldLineWidth.setPaintLabels(true);
		sldLineWidth.setPreferredSize(new Dimension(100, 30));
		sldLineWidth.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				jSliderLineWidthStateChanged(evt);
			}
		});
		pnlSizeControls.add(sldLineWidth);

		txtLineWidth = new JTextField(3);
		txtLineWidth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jTextFieldLineActionPerformed(evt);
			}
		});
		pnlSizeControls.add(txtLineWidth);
		pnlSizeControls.add(new CarriageReturn());

		sldTransparency = new JSlider();
		sldTransparency.setMaximum(255);
		sldTransparency.setMinorTickSpacing(1);
		sldTransparency.setValue(0);
		sldTransparency.setPreferredSize(new Dimension(100, 30));
		sldTransparency.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				jSliderTransparencyStateChanged(evt);
			}
		});
		pnlSizeControls.add(sldTransparency);

		txtTransparency = new JTextField(3);
		txtTransparency.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jTextFieldTransparencyActionPerformed(evt);
			}
		});

		pnlSizeControls.add(txtTransparency);
		pnlSizeControls.add(new CarriageReturn());

		sldVertexSize = new JSlider();
		sldVertexSize.setMaximum(20);
		sldVertexSize.setMinorTickSpacing(1);
		sldVertexSize.setPreferredSize(new Dimension(100, 30));
		sldVertexSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				jSliderVerticesStateChanged(evt);
			}
		});
		pnlSizeControls.add(sldVertexSize);

		txtVertexSize = new JTextField(3);
		txtVertexSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jTextFieldVerticesActionPerformed(evt);
			}
		});

		pnlSizeControls.add(txtVertexSize);
		pnlSizeControls.add(new CarriageReturn());
		return pnlSizeControls;
	}

	private JPanel getPnlSizeTexts() {
		JPanel pnlSizeTexts = new JPanel();
		CRFlowLayout flowLayout2 = new CRFlowLayout();
		flowLayout2.setVgap(18);
		pnlSizeTexts.setLayout(flowLayout2);
		flowLayout2.setAlignment(CRFlowLayout.RIGHT);
		lblLineWidth = new JLabel();
		lblLineWidth.setText("Line width: ");
		pnlSizeTexts.add(lblLineWidth);
		pnlSizeTexts.add(new CarriageReturn());
		lblTransparency = new JLabel();
		lblTransparency.setText("Transparency: ");
		pnlSizeTexts.add(lblTransparency);
		pnlSizeTexts.add(new CarriageReturn());
		lblSize = new JLabel();
		lblSize.setText("Size:");
		pnlSizeTexts.add(lblSize);
		return pnlSizeTexts;
	}

	private JPanel getPnlColorChoosers() {
		JPanel pnlColorChoosers = new JPanel();
		pnlColorChoosers.setLayout(new CRFlowLayout());
		lblFill = new JLabel();
		lblFill.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				chooseFillColor();
			}
		});
		lblFill.setBorder(BorderFactory.createLineBorder(Color.black));
		lblFill.setPreferredSize(new Dimension(40, 20));
		lblFill.setOpaque(true);
		pnlColorChoosers.add(lblFill);
		pnlColorChoosers.add(new CarriageReturn());

		lblLine = new JLabel();
		lblLine.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chooseLineColor();
			}
		});
		lblLine.setBorder(BorderFactory.createLineBorder(Color.black));
		lblLine.setPreferredSize(new Dimension(40, 20));
		lblLine.setOpaque(true);
		pnlColorChoosers.add(lblLine);
		return pnlColorChoosers;
	}

	private JPanel getPnlTexts() {
		JPanel pnlTexts = new JPanel();
		CRFlowLayout flowLayout = new CRFlowLayout();
		pnlTexts.setLayout(flowLayout);
		flowLayout.setAlignment(CRFlowLayout.RIGHT);
		chkFill = new JCheckBox();
		chkFill.setText("Fill:");
		chkFill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jCheckBoxFillActionPerformed(evt);
			}
		});
		pnlTexts.add(chkFill);
		pnlTexts.add(new CarriageReturn());
		chkLine = new JCheckBox();
		chkLine.setText("Line:");
		chkLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jCheckBoxLineActionPerformed(evt);
			}
		});
		pnlTexts.add(chkLine);
		return pnlTexts;
	}

	/**
	 * open the collection window in order to select one symbol.
	 *
	 * @param evt
	 */
	private void jButtonFromCollectionActionPerformed(ActionEvent evt) {
		FlowLayoutPreviewWindow coll = new FlowLayoutPreviewWindow(
				legendContext);
		if (UIFactory.showDialog(coll)) {
			Symbol sym = coll.getSelectedSymbol();
			addToSymbolList(sym);
			refresh();
		}
	}

	/**
	 * move up the selected symbol in the list
	 *
	 * @param evt
	 */
	private void jButtonSymbolUpActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jButtonSymbolUpActionPerformed
		DefaultListModel mod = (DefaultListModel) lstSymbols.getModel();
		int idx = 0;
		idx = lstSymbols.getSelectedIndex();
		if (idx > 0) {
			SymbolListDecorator element = (SymbolListDecorator) mod.get(idx);
			mod.remove(idx);
			mod.add(idx - 1, element);
			lstSymbols.setSelectedIndex(idx - 1);
			refresh();
			refreshListButtons();
		}
	}// GEN-LAST:event_jButtonSymbolUpActionPerformed

	/**
	 * When the selected value in the list is changed we will call to the
	 * refresh functions with the new symbol.
	 *
	 * @param evt
	 */
	private void jList1ValueChanged(ListSelectionEvent evt) {
		DefaultListModel mod = (DefaultListModel) lstSymbols.getModel();
		if ((mod.getSize() > 0) && (lstSymbols.getSelectedIndex() != -1)) {
			SymbolListDecorator dec = (SymbolListDecorator) lstSymbols
					.getSelectedValue();
			syncSymbolControls(dec.getSymbol());
		}
	}

	/**
	 * moves down the symbol
	 *
	 * @param evt
	 */
	private void jButtonSymbolDownActionPerformed(ActionEvent evt) {
		DefaultListModel mod = (DefaultListModel) lstSymbols.getModel();
		int idx = 0;
		idx = lstSymbols.getSelectedIndex();
		if (idx < mod.size() - 1) {
			SymbolListDecorator element = (SymbolListDecorator) mod.get(idx);
			mod.remove(idx);
			mod.add(idx + 1, element);
			lstSymbols.setSelectedIndex(idx + 1);
			refresh();
		}
	}

	/**
	 * adds a symbol in the list
	 *
	 * @param evt
	 */
	private void jButtonSymbolAddActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jButtonSymbolAddActionPerformed
		EditableSymbol sy = null;

		// Filter the available symbols
		SymbolManager sm = (SymbolManager) Services
				.getService("org.orbisgis.SymbolManager");
		ArrayList<Symbol> availableSymbols = sm.getAvailableSymbols();
		ArrayList<Symbol> filtered = new ArrayList<Symbol>();
		for (Symbol symbol : availableSymbols) {
			if (symbol instanceof EditableSymbol) {
				EditableSymbol editableSymbol = (EditableSymbol) symbol;
				if (editableSymbol.acceptGeometryType(legendContext
						.getGeometryConstraint())) {
					filtered.add(editableSymbol);
				}
			}
		}

		SymbolSelection sel = new SymbolSelection(filtered);
		if (UIFactory.showDialog(sel)) {
			EditableSymbol symbol = (EditableSymbol) sel.getSelected();
			sy = (EditableSymbol) symbol.cloneSymbol();
		}

		if (sy != null) {
			SymbolListDecorator deco = new SymbolListDecorator(sy);

			((DefaultListModel) lstSymbols.getModel()).addElement(deco);

			lstSymbols.setSelectedValue(deco, true);

			syncSymbolControls(deco.getSymbol());
			refresh();
		}
	}

	/**
	 * delete the selected symbols
	 *
	 * @param evt
	 */
	private void jButtonSymbolDelActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jButtonSymbolDelActionPerformed
		DefaultListModel mod = (DefaultListModel) lstSymbols.getModel();
		int[] indexes = lstSymbols.getSelectedIndices();
		// for (int i=0; i<indexes.length; i++){
		while (indexes.length > 0) {
			mod.removeElementAt(indexes[0]);
			indexes = lstSymbols.getSelectedIndices();
		}
		if (mod.getSize() > 0) {
			lstSymbols.setSelectedIndex(0);
			syncSymbolControls(((SymbolListDecorator) mod.getElementAt(0))
					.getSymbol());
		}
		refresh();

	}// GEN-LAST:event_jButtonSymbolDelActionPerformed

	/**
	 * rename the selected symbol
	 *
	 * @param evt
	 */
	private void jButtonSymbolRenameActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jButtonSymbolRenameActionPerformed
		DefaultListModel mod = (DefaultListModel) lstSymbols.getModel();
		SymbolListDecorator dec = (SymbolListDecorator) lstSymbols
				.getSelectedValue();
		int idx = lstSymbols.getSelectedIndex();

		AskValue ask = new AskValue("Insert the new name", "txt is not null",
				"A name must be specified", dec.getSymbol().getName());
		String new_name = "";
		if (UIFactory.showDialog(ask)) {
			new_name = ask.getValue();

			if (new_name != null && new_name != "") {
				dec.getSymbol().setName(new_name);
				mod.remove(idx);
				mod.add(idx, dec);
				lstSymbols.setSelectedIndex(idx);

				syncSymbolControls(dec.getSymbol());
				refresh();
			}
		}
	}// GEN-LAST:event_jButtonSymbolRenameActionPerformed

	private void jButtonSyncLineWithFillActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jButtonSyncLineWithFillActionPerformed
		lblLine.setBackground(lblFill.getBackground().darker());
		refresh();
	}// GEN-LAST:event_jButtonSyncLineWithFillActionPerformed

	/**
	 * gets the symbols and adds to the collection as a new Composite
	 *
	 * @param evt
	 */
	private void jButtonToCollectionActionPerformed(ActionEvent evt) {
		SymbolManager sm = (SymbolManager) Services
				.getService("org.orbisgis.SymbolManager");
		sm.addSymbol(getSymbolComposite());
	}

	private void jCheckBoxLineActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jCheckBoxLineActionPerformed

		refresh();
	}// GEN-LAST:event_jCheckBoxLineActionPerformed

	private void jCheckBoxFillActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jCheckBoxFillActionPerformed

		refresh();
	}// GEN-LAST:event_jCheckBoxFillActionPerformed

	private void jTextFieldVerticesActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jTextFieldVerticesActionPerformed
		int value = Integer.parseInt(txtVertexSize.getText());
		sldVertexSize.setValue(value);
		refresh();
	}// GEN-LAST:event_jTextFieldVerticesActionPerformed

	private void jTextFieldTransparencyActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jTextFieldTransparencyActionPerformed
		int value = Integer.parseInt(txtTransparency.getText());
		sldTransparency.setValue(value);
		refresh();
	}// GEN-LAST:event_jTextFieldTransparencyActionPerformed

	private void jTextFieldLineActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jTextFieldLineActionPerformed
		int value = Integer.parseInt(txtLineWidth.getText());
		sldLineWidth.setValue(value);
		refresh();
	}// GEN-LAST:event_jTextFieldLineActionPerformed

	private void jSliderVerticesStateChanged(ChangeEvent evt) {// GEN-FIRST:event_jSliderVerticesStateChanged
		int value = sldVertexSize.getValue();
		txtVertexSize.setText(String.valueOf(value));
		refresh();
	}// GEN-LAST:event_jSliderVerticesStateChanged

	private void jSliderTransparencyStateChanged(ChangeEvent evt) {// GEN-FIRST:event_jSliderTransparencyStateChanged
		int value = sldTransparency.getValue();
		txtTransparency.setText(String.valueOf(value));
		refresh();
	}// GEN-LAST:event_jSliderTransparencyStateChanged

	private void jSliderLineWidthStateChanged(ChangeEvent evt) {// GEN-FIRST:event_jSliderLineWidthStateChanged
		int value = sldLineWidth.getValue();
		txtLineWidth.setText(String.valueOf(value));
		refresh();
	}// GEN-LAST:event_jSliderLineWidthStateChanged

	private void chooseLineColor() {// GEN-FIRST:event_jButtonLineColorPickerActionPerformed
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			lblLine.setBackground(color);
		}
		refresh();

	}// GEN-LAST:event_jButtonLineColorPickerActionPerformed

	private void chooseFillColor() {
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			lblFill.setBackground(color);
			lblFill.setOpaque(true);
		}
		refresh();
	}

	private JPanel canvasPreview;
	private JButton jButtonFromCollection;
	private JButton jButtonSymbolAdd;
	private JButton jButtonSymbolDel;
	private JButton jButtonSymbolDown;
	private JButton jButtonSymbolRename;
	private JButton jButtonSymbolUp;
	private JButton btnSync;
	private JButton jButtonToCollection;
	private JCheckBox chkFill;
	private JCheckBox chkLine;
	private JLabel lblLineWidth;
	private JLabel lblTransparency;
	private JLabel lblSize;
	private JLabel lblFill;
	private JLabel lblLine;
	private JList lstSymbols;
	private JPanel jPanelButtonsCollection;
	private JPanel pnlEdition;
	private JPanel jPanelPreview;
	private JSlider sldLineWidth;
	private JSlider sldTransparency;
	private JSlider sldVertexSize;
	private JTextField txtLineWidth;
	private JTextField txtTransparency;
	private JTextField txtVertexSize;
	private JToolBar jToolBar1;

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
		return null;
	}

}