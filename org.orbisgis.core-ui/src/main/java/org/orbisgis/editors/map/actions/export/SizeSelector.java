package org.orbisgis.editors.map.actions.export;


public class SizeSelector extends JNumericSpinner {
	private UnitSelector unitSelector;

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

	public void setMeasure(double cm) {
		super.setValue(unitSelector.toSelectedUnit(cm));
	}
	
}
