package org.orbisgis.editors.map.actions.export;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.orbisgis.Services;
import org.orbisgis.map.export.MapExportManager;
import org.orbisgis.map.export.Scale;
import org.orbisgis.ui.patterns.PatternChangeListener;
import org.orbisgis.ui.patterns.PatternConfigurator;
import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class ScaleChooser extends JPanel implements UIPanel {

	private JComboBox cmbScaleType;
	private JSpinner spnPartCount;
	private JTextField txtPartSize;
	private JTextField txtHeight;
	private ScalePreview scalePreview;
	private Scale scale;
	private double scaleDenominator;
	private PatternConfigurator labelPattern;
	private PatternConfigurator markPattern;

	private boolean syncing = false;

	public ScaleChooser(double scaleDenominator) {
		this.scaleDenominator = scaleDenominator;

		this.setLayout(new BorderLayout());
		JPanel controlPanel = new JPanel();
		MapExportManager export = Services.getService(MapExportManager.class);
		String[] names = export.getScaleNames();
		controlPanel.setLayout(new CRFlowLayout(CRFlowLayout.LEFT));

		cmbScaleType = new JComboBox(names);
		cmbScaleType.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				changeScale();
			}
		});
		controlPanel.add(new JLabel("Select scale type:"));
		controlPanel.add(cmbScaleType);
		controlPanel.add(new CarriageReturn());

		controlPanel.add(new JLabel("Part count: "));
		spnPartCount = new JSpinner();
		spnPartCount.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				resizePattern(labelPattern);
				resizePattern(markPattern);
				safeSyncScale();
			}
		});
		controlPanel.add(spnPartCount);
		controlPanel.add(new CarriageReturn());

		DocumentListener synListener = new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				safeSyncScale();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				safeSyncScale();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				safeSyncScale();
			}
		};
		controlPanel.add(new JLabel("Part size (cm): "));
		txtPartSize = new JTextField(2);
		txtPartSize.getDocument().addDocumentListener(synListener);
		controlPanel.add(txtPartSize);
		controlPanel.add(new CarriageReturn());

		controlPanel.add(new JLabel("Height (cm): "));
		txtHeight = new JTextField(2);
		txtHeight.getDocument().addDocumentListener(synListener);
		controlPanel.add(txtHeight);
		controlPanel.add(new CarriageReturn());

		controlPanel.add(new JLabel("Labeled parts: "));
		PatternChangeListener patternChangeListener = new PatternChangeListener() {

			@Override
			public void patternChanged(int row, int column) {
				safeSyncScale();
			}
		};
		labelPattern = new PatternConfigurator();
		Dimension patternSize = new Dimension(100, 25);
		labelPattern.setPreferredSize(patternSize);
		labelPattern.addChangeListener(patternChangeListener);
		controlPanel.add(labelPattern);
		controlPanel.add(new CarriageReturn());

		controlPanel.add(new JLabel("Remarked parts: "));
		markPattern = new PatternConfigurator();
		markPattern.setPreferredSize(patternSize);
		markPattern.addChangeListener(patternChangeListener);
		controlPanel.add(markPattern);

		scalePreview = new ScalePreview();
		scalePreview.setPreferredSize(new Dimension(300, 50));
		this.add(scalePreview, BorderLayout.CENTER);
		this.add(controlPanel, BorderLayout.SOUTH);

		changeScale();
	}

	private void resizePattern(PatternConfigurator patternComponent) {
		try {
			int dim = Integer.parseInt(spnPartCount.getValue().toString());
			if (dim > 0) {
				patternComponent.setDimensions(1, dim);
			}
		} catch (NumberFormatException e) {
			patternComponent.setDimensions(1, 1);
		}

		this.revalidate();
		this.validate();
		this.doLayout();
	}

	private void changeScale() {
		MapExportManager export = Services.getService(MapExportManager.class);
		String scaleName = (String) cmbScaleType.getSelectedItem();
		if (scaleName != null) {
			scale = export.getScale(scaleName);
			scalePreview.setModel(scale);
			scale.setScaleDenominator(scaleDenominator);
			syncControls();
		}
	}

	private void syncControls() {
		if (!syncing) {
			syncing = true;
			try {
				txtHeight.setText(Double.toString(scale.getHeight()));
				txtPartSize.setText(Double.toString(scale.getPartWidth()));
				spnPartCount.setValue(scale.getPartCount());
				labelPattern.setRowPattern(0, scale.getLabeledParts());
				markPattern.setRowPattern(0, scale.getRemarkedParts());
			} finally {
				syncing = false;
			}
		}
	}

	private void safeSyncScale() {
		try {
			syncScale();
		} catch (NumberFormatException e) {
			// ignore
		}
	}

	private void syncScale() {
		if (!syncing) {
			syncing = true;
			try {
				scale.setHeight(Double.parseDouble(txtHeight.getText()));
				scale.setPartCount(Integer.parseInt(spnPartCount.getValue()
						.toString()));
				scale.setPartWidth(Double.parseDouble(txtPartSize.getText()));
				scale.setPartsWithText(labelPattern.getRowPattern(0));
				scale.setRemarkedParts(markPattern.getRowPattern(0));
				scalePreview.repaint();
			} finally {
				syncing = false;
			}
		}
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getTitle() {
		return "Choose scale";
	}

	@Override
	public String validateInput() {
		try {
			syncScale();
		} catch (Exception e) {
			return e.getLocalizedMessage();
		}

		return null;
	}

	public URL getIconURL() {
		return UIFactory.getDefaultIcon();
	}

	@Override
	public String getInfoText() {
		return "Choose the scale to use";
	}

	@Override
	public String initialize() {
		return null;
	}

	@Override
	public String postProcess() {
		return null;
	}

}
