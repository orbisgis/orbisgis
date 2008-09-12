package org.orbisgis.map.export;

public abstract class AbstractScale implements Scale {
	protected int partCount = 1;
	protected double partWidth = 1;
	protected double height = 0.3;
	protected boolean[] partsWithText = new boolean[] { false };
	protected boolean[] remarkedParts = new boolean[] { false };
	protected double scaleDenominator = 1;

	@Override
	public String getScaleTypeName() {
		return "Single line";
	}

	public int getPartCount() {
		return partCount;
	}

	public void setPartCount(int partCount) {
		this.partCount = partCount;
	}

	public double getPartWidth() {
		return partWidth;
	}

	public void setPartWidth(double partWidth) {
		this.partWidth = partWidth;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public boolean[] getLabeledParts() {
		return partsWithText;
	}

	public void setPartsWithText(boolean[] partsWithText) {
		this.partsWithText = partsWithText;
	}

	public boolean[] getRemarkedParts() {
		return remarkedParts;
	}

	public void setRemarkedParts(boolean[] remarkedParts) {
		this.remarkedParts = remarkedParts;
	}

	public double getScaleDenominator() {
		return scaleDenominator;
	}

	public void setScaleDenominator(double scaleDenominator) {
		this.scaleDenominator = scaleDenominator;
	}
}
