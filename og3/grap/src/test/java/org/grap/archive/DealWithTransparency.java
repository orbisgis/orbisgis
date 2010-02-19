/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.grap.archive;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

// http://www.javalobby.org/articles/ultimate-image/

public class DealWithTransparency {
	private BufferedImage produceColoredImage(final int width,
			final int height, final Color color) {
		final BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		final Graphics graphics = image.getGraphics();
		graphics.setColor(color);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		return image;
	}

	private BufferedImage produceDraughtboard(final int width,
			final int height, final Color color) {
		final BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		final Graphics graphics = image.getGraphics();
		final int halfWidth = image.getWidth() / 2;
		final int halfHeight = image.getHeight() / 2;

		graphics.setColor(color);
		graphics.fillRect(0, 0, halfWidth, halfHeight);
		graphics.fillRect(halfWidth, halfHeight, halfWidth, halfHeight);

		graphics.setColor(Color.BLACK);
		graphics.fillRect(halfWidth, 0, halfWidth, halfHeight);
		graphics.fillRect(0, halfHeight, halfWidth, halfHeight);
		graphics.dispose();
		return image;
	}

	private BufferedImage overlapRedBackGroundWithAnImage(
			final BufferedImage image) throws IOException {
		final BufferedImage redImage = produceColoredImage(image.getWidth(),
				image.getHeight(), Color.RED);
		final Graphics graphics = redImage.getGraphics();
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();
		System.out.println("Color model of the result (buffered image) : "
				+ redImage.getColorModel());
		return redImage;
	}

	private static BufferedImage makeColorTransparent(
			final BufferedImage image, final Color color) {
		final BufferedImage resultBI = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics2D = resultBI.createGraphics();
		graphics2D.setComposite(AlphaComposite.Src);
		graphics2D.drawImage(image, null, 0, 0);
		graphics2D.dispose();
		for (int r = 0; r < resultBI.getHeight(); r++) {
			for (int c = 0; c < resultBI.getWidth(); c++) {
				if (resultBI.getRGB(c, r) == color.getRGB()) {
					resultBI.setRGB(c, r, 0x00FFFFFF & resultBI.getRGB(c, r));
					// resultBI.setRGB(c, r, 0x8F1C1C);
				}
			}
		}
		return resultBI;
	}

	private static BufferedImage makeGlobalImageTransparent(
			final BufferedImage image, final float opacity) {
		final BufferedImage resultBI = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TRANSLUCENT);
		final Graphics2D graphics2D = resultBI.createGraphics();
		graphics2D.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, opacity));
		graphics2D.drawImage(image, null, 0, 0);
		graphics2D.dispose();
		return resultBI;
	}

	private void loadAndDisplayImage(final JFrame frame) throws IOException {
		// final BufferedImage img = produceDraughtboard(400, 400, Color.GREEN);
		 final BufferedImage img = makeColorTransparent(produceDraughtboard(400,
				400, Color.GREEN), Color.BLACK);
		// final BufferedImage img = makeGlobalImageTransparent(
		// makeColorTransparent(
		// produceDraughtboard(400, 400, Color.GREEN), Color.BLACK),
		//				0.25f);

		final BufferedImage newImg = overlapRedBackGroundWithAnImage(img);

		frame.setBounds(0, 0, newImg.getWidth(), newImg.getHeight());
		final JImagePanel panel = new JImagePanel(newImg, 0, 0);
		frame.add(panel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) throws IOException {
		final DealWithTransparency ia = new DealWithTransparency();
		final JFrame frame = new JFrame("Tutorials");
		ia.loadAndDisplayImage(frame);
	}

	private class JImagePanel extends JPanel {
		private BufferedImage image;
		int x, y;

		public JImagePanel(BufferedImage image, int x, int y) {
			super();
			this.image = image;
			this.x = x;
			this.y = y;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, x, y, null);
		}
	}
}