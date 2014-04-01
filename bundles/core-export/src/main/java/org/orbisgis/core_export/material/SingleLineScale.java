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

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

public class SingleLineScale extends AbstractScale implements Scale {

	public SingleLineScale() {
		partCount = 2;
		partWidth = 1;
		height = 0.3;
		partsWithText = new boolean[] { true, false, true };
		remarkedParts = new boolean[] { true, false, true };
		scaleDenominator = 1;
	}

	@Override
	public String getScaleTypeName() {
		return "Single line";
	}

	@Override
	public void drawScale(Graphics2D g, int dpi) {
		FontMetrics fm = g.getFontMetrics();

		// Get the size of the scale in pixels
		double dpcm = 0.01 * dpi / 0.0254;
		double scaleWidth = dpcm * partWidth * partCount;
		double scaleHeight = height * dpcm;

		// Get the text dimensions
		NumberFormat nf = NumberFormat.getIntegerInstance();
		String max = nf.format(scaleDenominator * partWidth * partCount);
		Rectangle2D rightTextBounds = fm.getStringBounds(max, g);
		if (!partsWithText[partCount - 1]) {
			rightTextBounds = new Rectangle(0, 0, 0, 0);
		}

		// Obtain the full size of the legend
		String min = "0";
		Rectangle2D leftTextBounds = fm.getStringBounds(min, g);
		if (!partsWithText[0]) {
			leftTextBounds = new Rectangle(0, 0, 0, 0);
		}

		double textHeight = fm.getStringBounds("0", g).getHeight();
		int overallHeight = (int) (scaleHeight + 1 + textHeight);
		int overallWidth = (int) (scaleWidth + leftTextBounds.getWidth() / 2 + rightTextBounds
				.getWidth() / 2);
		Rectangle size = new Rectangle(0, 0, overallWidth, overallHeight);

		// Write horizontal line
		double startLineX = 0 + leftTextBounds.getWidth() / 2;
		double partPixels = partWidth * dpcm;
		int endLineX = (int) (startLineX + partPixels * partCount);
		g.drawLine((int) startLineX, size.height, endLineX, size.height);
		// Draw the vertical lines and the text
		double posX = startLineX;
		int startingBigVerticalMark = (int) (textHeight + 1);
		int startingSmallVerticalMark = size.height - 2;
		int i = 0;
		while (i < partCount + 1) {
			int posY;
			if (remarkedParts[i]) {
				posY = startingBigVerticalMark;
			} else {
				posY = startingSmallVerticalMark;
			}
			g.drawLine((int) posX, posY, (int) posX, size.height);

			if (partsWithText[i]) {
				String number = nf.format(scaleDenominator * partWidth * i);
				Rectangle2D bounds = fm.getStringBounds(number, g);
				g.drawString(number, (int) (posX - bounds.getWidth() / 2),
						posY - 1);
			}
			i++;
			posX += partPixels;
		}
	}

}
