package org.orbisgis.editorViews.toc.actions.cui.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.Canvas;
import org.orbisgis.editorViews.toc.actions.cui.ui.CompositeSymbolFilter;
import org.orbisgis.editorViews.toc.actions.cui.ui.ConstraintSymbolFilter;
import org.orbisgis.editorViews.toc.actions.cui.ui.EditableSymbolFilter;
import org.orbisgis.editorViews.toc.actions.cui.ui.SymbolEditor;
import org.orbisgis.editorViews.toc.actions.cui.ui.SymbolFilter;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.carto.LegendFactory;
import org.orbisgis.renderer.legend.carto.ProportionalLegend;
import org.orbisgis.renderer.symbol.EditablePointSymbol;
import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;
import org.sif.UIFactory;

public class PnlProportionalLegend extends JPanel implements ILegendPanelUI {

	private ProportionalLegend legend;
	private LegendContext legendContext;
	private JComboBox cmbField;
	private JTextField txtMinArea;
	private Canvas canvas;

	public PnlProportionalLegend(LegendContext legendContext) {
		legend = LegendFactory.createProportionalLegend();
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
		confPanel.add(new CarriageReturn());

		confPanel.add(new JLabel("Proportional method:"));
		final JComboBox cmbMethod = new JComboBox(new String[] { "Linear",
				"logarithmic", "Square" });
		cmbMethod.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					switch (cmbMethod.getSelectedIndex()) {
					case 0:
						legend.setMethod(ProportionalLegend.LINEAR);
						break;
					case 1:
						legend.setMethod(ProportionalLegend.LOGARITHMIC);
						break;
					case 2:
						legend.setMethod(ProportionalLegend.SQUARE);
						break;
					}
				} catch (DriverException e1) {
					Services.getErrorManager().error("Cannot set the method",
							e1);
				}
			}

		});
		confPanel.add(cmbMethod);
		this.add(confPanel, BorderLayout.NORTH);

		JPanel pnlSymbol = new JPanel();
		pnlSymbol.setBorder(BorderFactory
				.createTitledBorder("Proportional symbol"));
		pnlSymbol.add(new JLabel("Symbol:"));
		canvas = new Canvas();
		pnlSymbol.add(canvas);
		canvas.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				editSymbol();
			}

		});
		canvas.setPreferredSize(new Dimension(50, 50));
		this.add(pnlSymbol, BorderLayout.CENTER);

		JPanel pnlPreview = new JPanel();
		pnlPreview.add(new JLabel("Minimum area:"));
		txtMinArea = new JTextField(5);
		pnlPreview.add(txtMinArea);

		this.add(pnlPreview, BorderLayout.SOUTH);
	}

	private void editSymbol() {
		SymbolEditor editor = new SymbolEditor(false, legendContext,
				getSymbolFilter());
		if (UIFactory.showDialog(editor)) {
			legend.setSampleSymbol((EditablePointSymbol) editor
					.getSymbolComposite().getSymbol(0));
			syncWithLegend();
		}
	}

	private SymbolFilter getSymbolFilter() {
		return new CompositeSymbolFilter(new EditableSymbolFilter(),
				new ConstraintSymbolFilter(new GeometryConstraint(
						GeometryConstraint.POINT)));
	}

	public boolean acceptsGeometryType(int geometryType) {
		return (geometryType == ILegendPanelUI.POLYGON)
				|| (geometryType == ILegendPanelUI.POINT);
	}

	public Component getComponent() {
		return this;
	}

	public Legend getLegend() {
		return legend;
	}

	public String getLegendTypeName() {
		return ProportionalLegend.NAME;
	}

	public ILegendPanelUI newInstance(LegendContext legendContext) {
		return new PnlProportionalLegend(legendContext);
	}

	public void setLegend(Legend legend) {
		this.legend = (ProportionalLegend) legend;
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
				if (fieldType == Type.BYTE || fieldType == Type.SHORT
						|| fieldType == Type.INT || fieldType == Type.LONG
						|| fieldType == Type.FLOAT || fieldType == Type.DOUBLE) {
					fieldNames.add(m.getFieldName(i));
				}
			}
			cmbField.setModel(new DefaultComboBoxModel(fieldNames
					.toArray(new String[0])));
			cmbField.setSelectedItem(legend.getClassificationField());

			// min area
			txtMinArea.setText("" + legend.getMinSymbolArea());

			// symbol
			canvas.setSymbol(legend.getSampleSymbol());
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

		String minArea = txtMinArea.getText();
		try {
			Integer.parseInt(minArea);
		} catch (NumberFormatException e) {
			return "min area must be an integer";
		}

		return null;
	}

}
