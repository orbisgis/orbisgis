package org.orbisgis.core.ui.components.preview;

public class SizeSelector extends JNumericSpinner {
	private UnitSelector unitSelector;

	/**
	 * Build a new spinner to select sizes.
	 * 
	 * @param columns
	 *            Number of columns of the text field
	 * @param unitSelector
	 *            The component that controls the units in this SizeSelector
	 */
	public SizeSelector(int columns, UnitSelector unitSelector) {
		super(columns);
		this.unitSelector = unitSelector;
		unitSelector.addSizeSelector(this);
	}

	/**
	 * Gets the measure in centimeters
	 * 
	 * @return
	 */
	public double getMeasure() {
		return unitSelector.toCM(getValue());
	}

	/**
	 * Set the measure of this component in centimeters
	 * 
	 * @param cm
	 */
	public void setMeasure(double cm) {
		super.setValue(unitSelector.toSelectedUnit(cm));
	}

}
