package org.orbisgis.core.map.export;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

public class RectanglesScale extends AbstractScale implements Scale {

	public RectanglesScale() {
		partCount = 2;
		partWidth = 1;
		height = 0.3;
		partsWithText = new boolean[] { true, false, true };
		remarkedParts = new boolean[] { true, false, true };
		scaleDenominator = 1;
	}

	@Override
	public String getScaleTypeName() {
		return "Rectangles";
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
		// Draw the vertical lines and the text
		double posX = startLineX;
		int startingBigVerticalMark = (int) (textHeight + 1);
		int i = 0;
		while (i < partCount + 1) {
			int posY = startingBigVerticalMark;
			Font previous = g.getFont();
			if (remarkedParts[i]) {
				g.setFont(previous.deriveFont(Font.BOLD));
			}
			if (i < partCount) {
				if (i / 2 == i / 2.0) {
					g.setColor(Color.black);
				} else {
					g.setColor(Color.white);
				}
				g.fillRect((int) posX, posY, (int) partPixels, size.height
						- posY);
				g.setColor(Color.black);
				g.drawRect((int) posX, posY, (int) partPixels, size.height
						- posY);
			}

			if (partsWithText[i]) {
				String number = nf.format(scaleDenominator * partWidth * i);
				Rectangle2D bounds = fm.getStringBounds(number, g);
				g.setColor(Color.black);
				g.drawString(number, (int) (posX - bounds.getWidth() / 2),
						posY - 1);
			}
			g.setFont(previous);
			i++;
			posX += partPixels;
		}
	}

}
