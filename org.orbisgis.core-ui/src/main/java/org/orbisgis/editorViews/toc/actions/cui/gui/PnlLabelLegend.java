package org.orbisgis.editorViews.toc.actions.cui.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.carto.LabelLegend;
import org.orbisgis.renderer.legend.carto.LegendFactory;
import org.sif.CRFlowLayout;

public class PnlLabelLegend extends JPanel implements ILegendPanelUI {

	private LabelLegend legend;
	private LegendContext legendContext;
	private JComboBox cmbField;
	private JTextField txtSymbolSize;

	public PnlLabelLegend(LegendContext legendContext) {
		legend = LegendFactory.createLabelLegend();
		legend.setName(getLegendTypeName());
		this.legendContext = legendContext;
		init();
	}

	private void init() {

		this.setLayout(new BorderLayout());

		JPanel confPanel = new JPanel();
		confPanel.setLayout(new CRFlowLayout());

		cmbField = new JComboBox();
		cmbField.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				legend.setClassificationField((String) cmbField
						.getSelectedItem());
				syncWithLegend();
			}

		});
		confPanel.add(new JLabel("Field:"));
		confPanel.add(cmbField);

		this.add(confPanel, BorderLayout.NORTH);

		JPanel pnlConf = new JPanel();
		pnlConf.add(new JLabel("Symbol size:"));
		txtSymbolSize = new JTextField(2);
		pnlConf.add(txtSymbolSize);

		this.add(pnlConf, BorderLayout.CENTER);
	}

	public boolean acceptsGeometryType(int geometryType) {
		return true;
	}

	public Component getComponent() {
		return this;
	}

	public Legend getLegend() {
		return legend;
	}

	public String getLegendTypeName() {
		return LabelLegend.NAME;
	}

	public ILegendPanelUI newInstance(LegendContext legendContext) {
		return new PnlLabelLegend(legendContext);
	}

	public void setLegend(Legend legend) {
		this.legend = (LabelLegend) legend;
		syncWithLegend();
	}

	private void syncWithLegend() {
		try {
			SpatialDataSourceDecorator sds = legendContext.getLayer()
					.getDataSource();
			Metadata m = sds.getMetadata();
			ArrayList<String> fieldNames = new ArrayList<String>();

			for (int i = 0; i < m.getFieldCount(); i++) {
				int fieldType = m.getFieldType(i).getTypeCode();
				if ((fieldType != Type.RASTER) && (fieldType != Type.GEOMETRY)) {
					fieldNames.add(m.getFieldName(i));
				}
			}
			cmbField.setModel(new DefaultComboBoxModel(fieldNames
					.toArray(new String[0])));
			cmbField.setSelectedItem(legend.getClassificationField());

			// symbol size
			txtSymbolSize.setText("" + legend.getFontSize());

		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot access layer fields", e);
		}

	}

	public void setLegendContext(LegendContext lc) {
		this.legendContext = lc;
	}

	public String validateInput() {
		if (legend.getClassificationField() == null) {
			return "A field must be selected";
		}

		String minArea = txtSymbolSize.getText();
		try {
			Integer.parseInt(minArea);
		} catch (NumberFormatException e) {
			return "min area must be an integer";
		}

		return null;
	}

}
