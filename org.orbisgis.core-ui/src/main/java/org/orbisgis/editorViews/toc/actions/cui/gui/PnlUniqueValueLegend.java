package org.orbisgis.editorViews.toc.actions.cui.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.UniqueValueLegendTableModel;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.renderer.legend.carto.LegendFactory;
import org.orbisgis.renderer.legend.carto.UniqueValueLegend;
import org.orbisgis.renderer.symbol.Symbol;

public class PnlUniqueValueLegend extends PnlAbstractClassifiedLegend {

	public PnlUniqueValueLegend(LegendContext legendContext) {
		super(legendContext, new UniqueValueLegendTableModel(),
				LegendFactory.createUniqueValueLegend());
	}

	private UniqueValueLegend legend;
	private JComboBox cmbFields;

	@Override
	protected void addAllAction() {
		ILayer layer = legendContext.getLayer();
		SpatialDataSourceDecorator sdsd = layer.getDataSource();
		String selitem = (String) cmbFields.getSelectedItem();

		legend.clear();

		try {
			int fieldIndex = sdsd.getFieldIndexByName(selitem);

			long rowCount = sdsd.getRowCount();

			HashSet<Value> added = new HashSet<Value>();
			for (int i = 0; i < rowCount; i++) {
				if (added.size() == 32) {
					JOptionPane.showMessageDialog(this,
							"More than 32 differnt values "
									+ "found. Showing only 32");
					break;
				}

				Value val = sdsd.getFieldValue(i, fieldIndex);

				if (val.isNull()) {
					continue;
				}

				if (!added.contains(val)) {
					added.add(val);
					Symbol sym = createRandomSymbol();
					legend.addClassification(val, sym, val.toString());
				}

			}

			tableModel.setLegend(this.legend);
		} catch (DriverException e) {
			System.out.println("Driver Exception: " + e.getMessage());
		}
	}

	@Override
	protected void addOneAction() {
		int rowCount = tableModel.getRowCount();

		if (rowCount < 32) {
			Symbol sym = createRandomSymbol();
			Value val = ValueFactory.createNullValue();
			String label = "Rest of values";
			if (rowCount > 0) {
				sym = (Symbol) tableModel.getValueAt(0, 0);
				val = (Value) tableModel.getValueAt(0, 1);
				label = (String) tableModel.getValueAt(0, 2);
			}
			getTableModel().insertRow(sym, val, label);
		} else {
			JOptionPane.showMessageDialog(this,
					"Cannot have more than 32 classifications");
		}
	}

	private UniqueValueLegendTableModel getTableModel() {
		return (UniqueValueLegendTableModel) tableModel;
	}

	@Override
	protected boolean canAdd() {
		return cmbFields.getSelectedIndex() != -1;
	}

	@Override
	protected JPanel getTopPanel() {
		JPanel pnlTop = new JPanel();
		pnlTop.add(new JLabel("Classification field:"));

		cmbFields = new JComboBox();
		cmbFields.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					legend.setClassificationField((String) cmbFields
							.getSelectedItem(), legendContext.getLayer()
							.getDataSource());
				} catch (DriverException e1) {
					JOptionPane.showMessageDialog(null,
							"Cannot access the type of the field", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}

		});
		pnlTop.add(cmbFields);

		return pnlTop;
	}

	public ILegendPanelUI newInstance(LegendContext legendContext) {
		return new JPanelUniqueValueLegend(legendContext);
	}

	public String getLegendTypeName() {
		return UniqueValueLegend.NAME;
	}

}
