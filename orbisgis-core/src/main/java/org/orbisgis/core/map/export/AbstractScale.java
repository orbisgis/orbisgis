package org.orbisgis.core.map.export;

public abstract class AbstractScale implements Scale {
	protected int partCount = 1;
	protected double partWidth = 1;
	protected double height = 0.3;
	protected boolean[] partsWithText = new boolean[] { false, false };
	protected boolean[] remarkedParts = new boolean[] { false, false };
	protected double scaleDenominator = 1;

	public int getPartCount() {
		return partCount;
	}

	public void setPartCount(int partCount) {
		if (partCount < 1) {
			throw new IllegalArgumentException("Cannot have so few parts");
		}
		this.partCount = partCount;
	}

	public double getPartWidth() {
		return partWidth;
	}

	public void setPartWidth(double partWidth) {
		if (partWidth <= 0) {
			throw new IllegalArgumentException("Part width should be possitive");
		}
		this.partWidth = partWidth;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		if (height <= 0) {
			throw new IllegalArgumentException(
					"Scale height should be possitive");
		}
		this.height = height;
	}

	public boolean[] getLabeledParts() {
		return partsWithText;
	}

	public void setPartsWithText(boolean[] partsWithText) {
		if (partsWithText.length != partCount + 1) {
			throw new IllegalArgumentException("Wrong number of elements. "
					+ "It should be equal to the number of parts: " + partCount);
		}
		this.partsWithText = partsWithText;
	}

	public boolean[] getRemarkedParts() {
		return remarkedParts;
	}

	public void setRemarkedParts(boolean[] remarkedParts) {
		if (remarkedParts.length != partCount + 1) {
			throw new IllegalArgumentException("Wrong number of elements. "
					+ "It should be equal to the number of parts: " + partCount);
		}
		this.remarkedParts = remarkedParts;
	}

	public double getScaleDenominator() {
		return scaleDenominator;
	}

	public void setScaleDenominator(double scaleDenominator) {
		this.scaleDenominator = scaleDenominator;
	}
}
