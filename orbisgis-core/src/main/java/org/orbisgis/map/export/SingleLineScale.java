package org.orbisgis.map.export;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

public class SingleLineScale extends AbstractScale implements Scale {

	public SingleLineScale() {
		partCount = 3;
		partWidth = 1;
		height = 0.3;
		partsWithText = new boolean[] { true, false, true };
		remarkedParts = new boolean[] { true, false, true };
		scaleDenominator = 1;
	}

	@Override
	public void drawScale(Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();

		// Get the size of the scale in pixels
		int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
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
		int startLineX = (int) (0 + leftTextBounds.getWidth() / 2);
		int partPixels = (int) (partWidth * dpcm);
		int endLineX = (int) (startLineX + partPixels * (partCount - 1));
		g.drawLine(startLineX, size.height, endLineX, size.height);
		// Draw the vertical lines and the text
		int posX = startLineX;
		int startingBigVerticalMark = (int) (textHeight + 1);
		int startingSmallVerticalMark = size.height - 2;
		int i = 0;
		while (i < partCount) {
			int posY;
			if (remarkedParts[i]) {
				posY = startingBigVerticalMark;
			} else {
				posY = startingSmallVerticalMark;
			}
			g.drawLine(posX, posY, posX, size.height);

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
