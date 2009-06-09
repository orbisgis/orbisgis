package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.orbisgis.core.renderer.symbol.StandardLineSymbol;
import org.orbisgis.core.renderer.symbol.StandardPointSymbol;
import org.orbisgis.core.renderer.symbol.StandardPolygonSymbol;
import org.orbisgis.core.renderer.symbol.StandardSymbol;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.ui.components.preview.JNumericSpinner;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.SymbolEditorListener;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.ColorPicker;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ISymbolEditor;
import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;
import org.orbisgis.sif.UIFactory;

public class StandardSymbolEditor extends JPanel implements ISymbolEditor {

	private JButton btnSync;
	private JNumericSpinner spnLineWidth;
	private JNumericSpinner spnTransparency;
	private JNumericSpinner spnVertexSize;
	private JLabel lblFill;
	private JLabel lblLine;
	private JCheckBox chkFill;
	private JCheckBox chkLine;
	protected StandardSymbol symbol;
	private JLabel lblLineWidth;
	private JLabel lblTransparency;
	private JLabel lblSize;
	private SymbolEditorListener listener;
	protected boolean ignoreEvents = false;

	public StandardSymbolEditor() {
		CRFlowLayout flowLayout = new CRFlowLayout();
		this.setLayout(flowLayout);

		JPanel pnlTexts = getPnlTexts();
		this.add(pnlTexts);

		JPanel pnlColorChoosers = getPnlColorChoosers();
		this.add(pnlColorChoosers);
		this.add(new CarriageReturn());

		btnSync = new JButton("Synchronize colors");
		btnSync.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonSyncLineWithFillActionPerformed(evt);
			}
		});
		this.add(btnSync);
		this.add(new CarriageReturn());

		JPanel pnlSizeTexts = getPnlSizeTexts();
		this.add(pnlSizeTexts);

		JPanel pnlSizeControls = getPnlSizeControls();
		this.add(pnlSizeControls);

	}

	protected JPanel getPnlSizeControls() {
		JPanel pnlSizeControls = new JPanel();
		pnlSizeControls.setLayout(new CRFlowLayout());
		pnlSizeControls.add(new JLabel(""));
		pnlSizeControls.add(new CarriageReturn());
		spnLineWidth = getSpinner(1, Integer.MAX_VALUE);
		pnlSizeControls.add(spnLineWidth);
		pnlSizeControls.add(new CarriageReturn());

		spnTransparency = getSpinner(0, 255);
		pnlSizeControls.add(spnTransparency);

		pnlSizeControls.add(new CarriageReturn());

		spnVertexSize = getSpinner(1, Integer.MAX_VALUE);
		pnlSizeControls.add(spnVertexSize);

		pnlSizeControls.add(new CarriageReturn());
		return pnlSizeControls;
	}

	protected JNumericSpinner getSpinner(int min, int max) {
		JNumericSpinner spinner = new JNumericSpinner(4);
		spinner.setInc(1);
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				symbolChanged();
			}
		});
		spinner.setMin(min);
		spinner.setMax(max);
		spinner.setNumberFormat(NumberFormat.getIntegerInstance());
		return spinner;
	}

	protected JPanel getPnlSizeTexts() {
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

	private void jButtonSyncLineWithFillActionPerformed(ActionEvent evt) {
		lblLine.setBackground(lblFill.getBackground().darker());
		symbolChanged();
	}

	protected void symbolChanged() {
		if (!ignoreEvents) {
			int transparency = 0;
			try {
				transparency = (int) spnTransparency.getValue();
			} catch (NumberFormatException e) {
			}
			if (transparency > 255) {
				transparency = 255;
			} else if (transparency < 0) {
				transparency = 0;
			}
			if (symbol instanceof StandardLineSymbol) {
				StandardLineSymbol lineSymbol = (StandardLineSymbol) symbol;
				Color lineColor = lblLine.getBackground();
				lineColor = new Color(lineColor.getRed(), lineColor.getGreen(),
						lineColor.getBlue(), 255 - transparency);
				if (chkLine.isSelected()) {
					lineSymbol.setOutlineColor(lineColor);
				} else {
					lineSymbol.setOutlineColor(null);
				}
				int width = 1;
				try {
					width = (int) spnLineWidth.getValue();
				} catch (NumberFormatException e) {
				}
				if (width < 0) {
					width = 0;
				}
				lineSymbol.setLineWidth(width);
			}

			if (symbol instanceof StandardPolygonSymbol) {
				StandardPolygonSymbol polygonSymbol = (StandardPolygonSymbol) symbol;

				Color fillColor = lblFill.getBackground();

				fillColor = new Color(fillColor.getRed(), fillColor.getGreen(),
						fillColor.getBlue(), 255 - transparency);
				if (chkFill.isSelected()) {
					polygonSymbol.setFillColor(fillColor);
				} else {
					polygonSymbol.setFillColor(null);
				}
			}

			if (symbol instanceof StandardPointSymbol) {
				StandardPointSymbol pointSymbol = (StandardPointSymbol) symbol;
				try {
					pointSymbol.setSize((int) spnVertexSize.getValue());
				} catch (NumberFormatException e) {
				}
			}

			listener.symbolChanged();
		}
	}

	private void jCheckBoxLineActionPerformed(ActionEvent evt) {
		symbolChanged();
	}

	private void jCheckBoxFillActionPerformed(ActionEvent evt) {
		symbolChanged();
	}

	private void chooseLineColor() {
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			lblLine.setBackground(color);
		}
		symbolChanged();

	}

	private void chooseFillColor() {
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			lblFill.setBackground(color);
			lblFill.setOpaque(true);
		}
		symbolChanged();
	}

	public void setSymbol(Symbol symbol) {
		ignoreEvents = true;
		if (symbol instanceof StandardSymbol) {
			this.symbol = (StandardSymbol) symbol;
			boolean enabledFill = false;
			boolean enabledLine = false;
			boolean enabledVertex = false;

			if (symbol instanceof StandardLineSymbol) {
				enabledLine = true;
				StandardLineSymbol lineSymbol = (StandardLineSymbol) symbol;
				Color lineColor = lineSymbol.getOutlineColor();
				if (lineColor != null) {
					lblLine.setBackground(lineColor);
					chkLine.setSelected(true);
					spnTransparency.setValue(255 - lineColor.getAlpha());
				} else {
					chkLine.setSelected(false);
				}
				spnLineWidth.setValue(lineSymbol.getLineWidth());
			}

			if (symbol instanceof StandardPolygonSymbol) {
				enabledFill = true;
				enabledLine = true;
				StandardPolygonSymbol polygonSymbol = (StandardPolygonSymbol) symbol;

				Color fillColor = polygonSymbol.getFillColor();
				if (fillColor != null) {
					lblFill.setBackground(fillColor);
					chkFill.setSelected(true);
					spnTransparency.setValue(255 - fillColor.getAlpha());
				} else {
					chkFill.setSelected(false);
				}
			}

			if (symbol instanceof StandardPointSymbol) {
				enabledVertex = true;
				enabledFill = true;
				enabledLine = true;
				StandardPointSymbol pointSymbol = (StandardPointSymbol) symbol;
				spnVertexSize.setValue(pointSymbol.getSize());
			}

			spnLineWidth.setVisible(enabledLine);
			lblLineWidth.setVisible(enabledLine);

			spnLineWidth.setEnabled(chkLine.isSelected());
			lblLineWidth.setEnabled(chkLine.isSelected());

			spnVertexSize.setVisible(enabledVertex);
			lblSize.setVisible(enabledVertex);

			spnTransparency.setVisible(enabledFill || enabledLine);
			lblTransparency.setVisible(enabledFill || enabledLine);

			lblLine.setVisible(enabledLine);
			chkLine.setVisible(enabledLine);

			lblFill.setVisible(enabledFill);
			chkFill.setVisible(enabledFill);

			btnSync.setVisible(enabledFill && enabledLine);

		} else {
			ignoreEvents = false;
			throw new RuntimeException(
					"This editor doesn't accept this symbol. "
							+ "An EditablePointSymbol was expected");
		}
		ignoreEvents = false;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public boolean accepts(Symbol symbol) {
		return symbol instanceof StandardSymbol;
	}

	public Component getComponent() {
		return this;
	}

	public ISymbolEditor newInstance() {
		return new StandardSymbolEditor();
	}

	public void setSymbolEditorListener(SymbolEditorListener listener) {
		this.listener = listener;
	}
}
