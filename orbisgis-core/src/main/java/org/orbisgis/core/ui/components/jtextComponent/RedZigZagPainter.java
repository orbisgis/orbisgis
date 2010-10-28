/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *  
 *  Lead Erwan BOCHER, scientific researcher, 
 *
 *  Developer lead : Pierre-Yves FADET, computer engineer. 
 *  
 *  User support lead : Gwendall Petit, geomatic engineer. 
 * 
 * Previous computer developer : Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
 * For more information, please consult: <http://orbisgis.cerma.archi.fr/>
 * <http://sourcesup.cru.fr/projects/orbisgis/>
 * 
 * or contact directly: 
 * erwan.bocher _at_ ec-nantes.fr 
 * Pierre-Yves.Fadet _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **/

package org.orbisgis.core.ui.components.jtextComponent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

/**
 * @author Volker Berlin
 */
public class RedZigZagPainter extends
		DefaultHighlighter.DefaultHighlightPainter {

	private static final java.awt.BasicStroke STROKE1 = new java.awt.BasicStroke(
			0.01F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10,
			new float[] { 1, 3 }, 0);
	private static final java.awt.BasicStroke STROKE2 = new java.awt.BasicStroke(
			0.01F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10,
			new float[] { 1, 1 }, 1);
	private static final java.awt.BasicStroke STROKE3 = new java.awt.BasicStroke(
			0.01F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10,
			new float[] { 1, 3 }, 2);

	public RedZigZagPainter() {
		super(Color.red);
	}

	/**
	 * {@inheritDoc}
	 */
	public Shape paintLayer(Graphics g, int i, int j, Shape shape,
			JTextComponent jtext, View view) {
		if (jtext.isEditable()) {
			g.setColor(Color.red);
			try {
				Shape sh = view.modelToView(i, Position.Bias.Forward, j,
						Position.Bias.Backward, shape);
				Rectangle rect = (sh instanceof Rectangle) ? (Rectangle) sh
						: sh.getBounds();
				drawZigZagLine(g, rect);
				return rect;
			} catch (BadLocationException badlocationexception) {
				return null;
			}
		}
		return null;
	}

	private void drawZigZagLine(Graphics g, Rectangle rect) {
		int x1 = rect.x;
		int x2 = x1 + rect.width - 1;
		int y = rect.y + rect.height - 1;
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(STROKE1);
		g2.drawLine(x1, y, x2, y);
		y--;
		g2.setStroke(STROKE2);
		g2.drawLine(x1, y, x2, y);
		y--;
		g2.setStroke(STROKE3);
		g2.drawLine(x1, y, x2, y);
	}

}
