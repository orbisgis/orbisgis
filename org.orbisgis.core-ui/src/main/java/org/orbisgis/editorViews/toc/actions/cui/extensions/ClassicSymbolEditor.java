package org.orbisgis.editorViews.toc.actions.cui.extensions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.orbisgis.editorViews.toc.actions.cui.ISymbolEditor;
import org.orbisgis.editorViews.toc.actions.cui.SymbolEditorListener;
import org.orbisgis.editorViews.toc.actions.cui.components.ColorPicker;
import org.orbisgis.renderer.symbol.EditableLineSymbol;
import org.orbisgis.renderer.symbol.EditablePointSymbol;
import org.orbisgis.renderer.symbol.EditablePolygonSymbol;
import org.orbisgis.renderer.symbol.EditableSymbol;
import org.orbisgis.renderer.symbol.Symbol;
import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;
import org.sif.UIFactory;

public class ClassicSymbolEditor extends JPanel implements ISymbolEditor {

	private JButton btnSync;
	private JSlider sldLineWidth;
	private JTextField txtLineWidth;
	private JSlider sldTransparency;
	private JTextField txtTransparency;
	private JSlider sldVertexSize;
	private JTextField txtVertexSize;
	private JLabel lblFill;
	private JLabel lblLine;
	private JCheckBox chkFill;
	private JCheckBox chkLine;
	private EditableSymbol symbol;
	private JLabel lblLineWidth;
	private JLabel lblTransparency;
	private JLabel lblSize;
	private SymbolEditorListener listener;
	private boolean ignoreEvents = false;

	public ClassicSymbolEditor() {
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
				symbolChanged();
			}
		});
		KeyAdapter txtsKeyAdapter = new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				symbolChanged();
			}

		};
		txtLineWidth.addKeyListener(txtsKeyAdapter);
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
				symbolChanged();
			}
		});
		txtTransparency.addKeyListener(txtsKeyAdapter);

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
				symbolChanged();
			}
		});
		txtVertexSize.addKeyListener(txtsKeyAdapter);

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

	private void jButtonSyncLineWithFillActionPerformed(ActionEvent evt) {
		lblLine.setBackground(lblFill.getBackground().darker());
		symbolChanged();
	}

	private void symbolChanged() {
		if (!ignoreEvents) {
			int transparency = 0;
			try {
				transparency = Integer.parseInt(txtTransparency.getText());
			} catch (NumberFormatException e) {
			}
			if (transparency > 255) {
				transparency = 255;
			} else if (transparency < 0) {
				transparency = 0;
			}
			if (symbol instanceof EditableLineSymbol) {
				EditableLineSymbol lineSymbol = (EditableLineSymbol) symbol;
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
					width = Integer.parseInt(txtLineWidth.getText());
				} catch (NumberFormatException e) {
				}
				if (width < 0) {
					width = 0;
				}
				lineSymbol.setLineWidth(width);
			}

			if (symbol instanceof EditablePolygonSymbol) {
				EditablePolygonSymbol polygonSymbol = (EditablePolygonSymbol) symbol;

				Color fillColor = lblFill.getBackground();

				fillColor = new Color(fillColor.getRed(), fillColor.getGreen(),
						fillColor.getBlue(), 255 - transparency);
				if (chkFill.isSelected()) {
					polygonSymbol.setFillColor(fillColor);
				} else {
					polygonSymbol.setFillColor(null);
				}
			}

			if (symbol instanceof EditablePointSymbol) {
				EditablePointSymbol pointSymbol = (EditablePointSymbol) symbol;
				try {
					pointSymbol.setSize(Integer.parseInt(txtVertexSize
							.getText()));
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

	private void jSliderVerticesStateChanged(ChangeEvent evt) {
		int value = sldVertexSize.getValue();
		txtVertexSize.setText(String.valueOf(value));
		symbolChanged();
	}

	private void jSliderTransparencyStateChanged(ChangeEvent evt) {
		int value = sldTransparency.getValue();
		txtTransparency.setText(String.valueOf(value));
		symbolChanged();
	}

	private void jSliderLineWidthStateChanged(ChangeEvent evt) {
		int value = sldLineWidth.getValue();
		txtLineWidth.setText(String.valueOf(value));
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
		if (symbol instanceof EditableSymbol) {
			this.symbol = (EditableSymbol) symbol;
			boolean enabledFill = false;
			boolean enabledLine = false;
			boolean enabledVertex = false;

			if (symbol instanceof EditableLineSymbol) {
				enabledLine = true;
				EditableLineSymbol lineSymbol = (EditableLineSymbol) symbol;
				Color lineColor = lineSymbol.getOutlineColor();
				if (lineColor != null) {
					lblLine.setBackground(lineColor);
					chkLine.setSelected(true);
					sldTransparency.setValue(255 - lineColor.getAlpha());
				} else {
					chkLine.setSelected(false);
				}
				sldLineWidth.setValue(lineSymbol.getLineWidth());
			}

			if (symbol instanceof EditablePolygonSymbol) {
				enabledFill = true;
				enabledLine = true;
				EditablePolygonSymbol polygonSymbol = (EditablePolygonSymbol) symbol;

				Color fillColor = polygonSymbol.getFillColor();
				if (fillColor != null) {
					lblFill.setBackground(fillColor);
					chkFill.setSelected(true);
					sldTransparency.setValue(255 - fillColor.getAlpha());
				} else {
					chkFill.setSelected(false);
				}
			}

			if (symbol instanceof EditablePointSymbol) {
				enabledVertex = true;
				enabledFill = true;
				enabledLine = true;
				EditablePointSymbol pointSymbol = (EditablePointSymbol) symbol;
				sldVertexSize.setValue(pointSymbol.getSize());
			}

			sldLineWidth.setVisible(enabledLine);
			txtLineWidth.setVisible(enabledLine);
			lblLineWidth.setVisible(enabledLine);

			sldLineWidth.setEnabled(chkLine.isSelected());
			txtLineWidth.setEnabled(chkLine.isSelected());
			lblLineWidth.setEnabled(chkLine.isSelected());

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
		return symbol instanceof EditableSymbol;
	}

	public Component getComponent() {
		return this;
	}

	public ISymbolEditor newInstance() {
		return new ClassicSymbolEditor();
	}

	public void setSymbolEditorListener(SymbolEditorListener listener) {
		this.listener = listener;
	}
}
