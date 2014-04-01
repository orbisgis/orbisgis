/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core_export.material;

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
