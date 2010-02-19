package org.orbisgis.core.ui.components.preview;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class UnitSelector extends JComboBox {

	private static final String INCH = "inch";
	private static final String MM = "mm";
	private static final String CM = "cm";
	private static String[] unitCodes = new String[3];
	private static double[] toCM = new double[3];
	private static HashMap<String, String> unitNames = new HashMap<String, String>();

	static {
		unitCodes[0] = CM;
		toCM[0] = 1;
		unitNames.put(CM, "Centimeters");
		unitCodes[1] = MM;
		toCM[1] = 0.1;
		unitNames.put(MM, "Milimeters");
		unitCodes[2] = INCH;
		toCM[2] = 2.54;
		unitNames.put(INCH, "Inches");
	}

	private int previousSelection;
	private ArrayList<JNumericSpinner> selectors = new ArrayList<JNumericSpinner>();

	public UnitSelector() {
		this.setModel(new DefaultComboBoxModel(unitCodes));
		this.setRenderer(new UnitRenderer());
		this.setSelectedIndex(0);
		previousSelection = this.getSelectedIndex();
		this.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// Transform from previous unit to current unit
				int index = getSelectedIndex();
				for (JNumericSpinner selector : selectors) {
					double cms = selector.getValue() * toCM[previousSelection];
					double newValue = cms / toCM[index];
					selector.setValue(newValue);
				}
				previousSelection = index;
			}
		});
	}

	private class UnitRenderer extends BasicComboBoxRenderer implements
			ListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);
			label.setText(unitNames.get(value));

			return label;
		}
	}

	public double toCM(double meassure) {
		return meassure * toCM[getSelectedIndex()];
	}

	public void addSizeSelector(SizeSelector selector) {
		this.selectors.add(selector);
	}

	public double toSelectedUnit(double cm) {
		return cm / toCM[getSelectedIndex()];
	}
}
